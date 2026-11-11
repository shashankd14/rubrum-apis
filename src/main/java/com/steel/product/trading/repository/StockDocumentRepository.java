package com.steel.product.trading.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.trading.dto.StockDocument;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class StockDocumentRepository {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    public StockDocumentRepository(JdbcTemplate jdbc, ObjectMapper mapper) { this.jdbc = jdbc; this.mapper = mapper; }

    private RowMapper<StockDocument> rowMapper() {
        return (rs, i) -> {
            StockDocument d = new StockDocument();
            d.setKind(rs.getString("kind"));
            d.setId(("AUDIT".equals(d.getKind()) ? "AUD-" : "ADJ-") + rs.getLong("document_id"));
            d.setVersion(rs.getInt("version_no")); d.setDate(rs.getDate("document_date").toLocalDate());
            d.setLocationId(rs.getInt("location_id")); d.setLocation(rs.getString("location_name"));
            d.setAuditor(rs.getString("auditor")); d.setConsignment(rs.getString("consignment"));
            return d;
        };
    }

    private static final String LINE_COLUMNS = "document_id, line_no, material_id, item_name, heat_no, system_weight, system_pieces, " +
        "physical_weight, physical_pieces, before_weight, pieces, verified, add_weight, deduct_weight, remarks";

    private static long documentId(StockDocument d) {
        return Long.parseLong(d.getId().substring(d.getId().indexOf('-') + 1));
    }

    private static Integer integer(java.sql.ResultSet rs, String column) throws java.sql.SQLException {
        Number value = (Number) rs.getObject(column);
        return value == null ? null : value.intValue();
    }

    private List<StockDocument> withLines(List<StockDocument> docs) {
        if (docs.isEmpty()) return docs;
        Map<Long, List<StockDocument.Line>> lines = new HashMap<>();
        new NamedParameterJdbcTemplate(jdbc).query("SELECT * FROM trading_stock_document_line WHERE document_id IN (:ids) ORDER BY document_id, line_no",
            new MapSqlParameterSource("ids", docs.stream().map(StockDocumentRepository::documentId).collect(Collectors.toList())), rs -> {
                StockDocument.Line line = new StockDocument.Line();
                line.setMaterialId(rs.getString("material_id")); line.setName(rs.getString("item_name"));
                line.setHeat(rs.getString("heat_no"));
                line.setSystemWeight(rs.getBigDecimal("system_weight")); line.setSystemPieces(integer(rs, "system_pieces"));
                line.setPhysicalWeight(rs.getBigDecimal("physical_weight")); line.setPhysicalPieces(integer(rs, "physical_pieces"));
                line.setBefore(rs.getBigDecimal("before_weight")); line.setPieces(integer(rs, "pieces"));
                line.setVerified(rs.getBigDecimal("verified")); line.setAdd(rs.getBigDecimal("add_weight"));
                line.setDeduct(rs.getBigDecimal("deduct_weight")); line.setRemarks(rs.getString("remarks"));
                lines.computeIfAbsent(rs.getLong("document_id"), k -> new ArrayList<>()).add(line);
            });
        docs.forEach(d -> d.setLines(lines.getOrDefault(documentId(d), new ArrayList<>())));
        return docs;
    }

    private void saveLines(long documentId, List<StockDocument.Line> lines) {
        jdbc.update("DELETE FROM trading_stock_document_line WHERE document_id = ?", documentId);
        if (lines == null || lines.isEmpty()) return;
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            StockDocument.Line l = lines.get(i);
            rows.add(new Object[] { documentId, i + 1, l.getMaterialId(), l.getName(), l.getHeat(), l.getSystemWeight(), l.getSystemPieces(),
                l.getPhysicalWeight(), l.getPhysicalPieces(), l.getBefore(), l.getPieces(), l.getVerified(), l.getAdd(), l.getDeduct(), l.getRemarks() });
        }
        jdbc.batchUpdate("INSERT INTO trading_stock_document_line (" + LINE_COLUMNS + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", rows);
    }

    public int migrateLegacyLines() {
        List<Map<String, Object>> legacy = jdbc.queryForList("SELECT d.document_id, d.lines_json FROM trading_stock_document d " +
            "WHERE d.lines_json IS NOT NULL AND NOT EXISTS (SELECT 1 FROM trading_stock_document_line l WHERE l.document_id = d.document_id)");
        int migrated = 0;
        List<Object> skipped = new ArrayList<>();
        for (Map<String, Object> row : legacy) {
            List<StockDocument.Line> lines;
            try { lines = mapper.readValue(String.valueOf(row.get("lines_json")), new TypeReference<List<StockDocument.Line>>() {}); }
            catch (Exception e) { skipped.add(row.get("document_id")); continue; }
            if (lines == null || lines.isEmpty()) continue;
            saveLines(((Number) row.get("document_id")).longValue(), lines);
            migrated++;
        }
        if (!skipped.isEmpty()) log.warn("Stock document lines not migrated, invalid legacy JSON for document ids {}", skipped);
        return migrated;
    }

    private String filters(String kind, String search, Integer locationId, List<Object> args) {
        String where = " WHERE kind = ?";
        args.add(kind);
        if (locationId != null) {
            where += " AND location_id = ?";
            args.add(locationId);
        }
        if (!search.isEmpty()) {
            where += " AND (LOWER(CONCAT(CASE WHEN kind = 'AUDIT' THEN 'AUD-' ELSE 'ADJ-' END, " +
                "document_id, ' ', auditor, ' ', location_name, ' ', consignment)) LIKE ? ESCAPE '!' OR EXISTS (SELECT 1 FROM trading_stock_document_line l " +
                "WHERE l.document_id = trading_stock_document.document_id AND LOWER(CONCAT_WS(' ', l.material_id, l.item_name, l.heat_no, l.remarks)) LIKE ? ESCAPE '!'))";
            String pattern = "%" + search.toLowerCase(Locale.ROOT).replace("!", "!!").replace("%", "!%").replace("_", "!_") + "%";
            args.add(pattern);
            args.add(pattern);
        }
        return where;
    }

    public List<StockDocument> list(String kind, String search, Integer locationId, int page, int size, String sort) {
        List<Object> args = new ArrayList<>();
        String where = filters(kind, search, locationId, args);
        String order = sort.isEmpty() ? "document_id DESC" :
            "document_date " + ("asc".equals(sort) ? "ASC" : "DESC") + ", document_id DESC";
        args.add(size);
        args.add((long) (page - 1) * size);
        return withLines(jdbc.query("SELECT * FROM trading_stock_document" + where + " ORDER BY " + order + " LIMIT ? OFFSET ?",
            rowMapper(), args.toArray()));
    }
    public long count(String kind, String search, Integer locationId) {
        List<Object> args = new ArrayList<>();
        String where = filters(kind, search, locationId, args);
        return jdbc.queryForObject("SELECT COUNT(*) FROM trading_stock_document" + where, Long.class, args.toArray());
    }
    public StockDocument get(long id, boolean lock) {
        List<StockDocument> rows = jdbc.query("SELECT * FROM trading_stock_document WHERE document_id = ?" +
            (lock ? " FOR UPDATE" : ""), rowMapper(), id);
        if (rows.isEmpty()) throw new IllegalArgumentException("Stock document not found.");
        return withLines(rows).get(0);
    }
    public Long beginRequest(String requestId, String hash) {
        jdbc.update("INSERT INTO trading_stock_document_request (request_id, payload_hash) VALUES (?, ?) " +
            "ON DUPLICATE KEY UPDATE request_id = request_id", requestId, hash);
        return jdbc.queryForObject("SELECT * FROM trading_stock_document_request WHERE request_id = ?", (rs, i) -> {
            if (!hash.equals(rs.getString("payload_hash"))) throw new IllegalArgumentException("Request already used with different values. Reload the record.");
            Number id = (Number) rs.getObject("document_id");
            return id == null ? null : id.longValue();
        }, requestId);
    }
    public void finishRequest(String requestId, long id) {
        jdbc.update("UPDATE trading_stock_document_request SET document_id = ? WHERE request_id = ?", id, requestId);
    }
    public long save(StockDocument d, Long id) {
        if (id != null) {
            jdbc.update("UPDATE trading_stock_document SET version_no = ?, document_date = ?, auditor = ?, consignment = ?, lines_json = NULL WHERE document_id = ?",
                d.getVersion(), java.sql.Date.valueOf(d.getDate()), d.getAuditor(), d.getConsignment(), id);
            saveLines(id, d.getLines());
            return id;
        }
        GeneratedKeyHolder key = new GeneratedKeyHolder();
        jdbc.update(connection -> {
            java.sql.PreparedStatement statement = connection.prepareStatement("INSERT INTO trading_stock_document " +
                "(kind, version_no, document_date, location_id, location_name, auditor, consignment) VALUES (?, ?, ?, ?, ?, ?, ?)",
                new String[] { "document_id" });
            statement.setString(1, d.getKind()); statement.setInt(2, d.getVersion());
            statement.setDate(3, java.sql.Date.valueOf(d.getDate())); statement.setInt(4, d.getLocationId());
            statement.setString(5, d.getLocation()); statement.setString(6, d.getAuditor());
            statement.setString(7, d.getConsignment()); return statement;
        }, key);
        long documentId = key.getKey().longValue();
        saveLines(documentId, d.getLines());
        return documentId;
    }
    public void linkEffect(long documentId, String requestId) {
        jdbc.update("INSERT INTO trading_stock_document_effect (request_id, document_id) VALUES (?, ?)", requestId, documentId);
    }
    public List<Map<String, Object>> legacyCorrections() {
        return jdbc.queryForList("SELECT c.* FROM trading_stock_correction c WHERE NOT EXISTS " +
            "(SELECT 1 FROM trading_stock_document_effect e WHERE e.request_id = c.request_id) ORDER BY correction_id FOR UPDATE");
    }
}
