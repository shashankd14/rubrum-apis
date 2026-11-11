package com.steel.product.trading.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.service.AWSS3Service;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
@Log4j2
public class InwardAttachmentService {
    public static final String DOCUMENT = "DOCUMENT";
    public static final String QC_CERTIFICATE = "QC_CERTIFICATE";
    public static final String ITEM_IMAGE = "ITEM_IMAGE";
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList("application/pdf", "image/jpeg", "image/png", "image/webp"));
    private final AWSS3Service storage;
    private final ObjectMapper mapper;
    private final JdbcTemplate jdbc;

    public InwardAttachmentService(AWSS3Service storage, ObjectMapper mapper, JdbcTemplate jdbc) {
        this.storage = storage;
        this.mapper = mapper;
        this.jdbc = jdbc;
    }

    public Map<String, Object> upload(MultipartFile file) throws IOException {
        if (file.isEmpty() || file.getSize() > 10 * 1024 * 1024 || !TYPES.contains(file.getContentType())) {
            throw new IllegalArgumentException("Choose a PDF, JPG, PNG or WebP file up to 10 MB.");
        }
        String name = Optional.ofNullable(file.getOriginalFilename()).orElse("attachment");
        String key = storage.persistTradingFiles("", "inward_" + UUID.randomUUID(), file);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("key", key);
        result.put("name", name);
        result.put("size", file.getSize());
        result.put("type", file.getContentType());
        result.put("url", storage.generatePresignedUrlForTrading(key));
        return result;
    }

    public List<Map<String, Object>> normalize(String json) {
        try {
            List<Map<String, Object>> files = read(json);
            if (files.size() > 20) throw new IllegalArgumentException("A maximum of 20 attachments is allowed.");
            for (Map<String, Object> file : files) {
                if (!(file.get("key") instanceof String) || !((String) file.get("key")).startsWith("inward_")) {
                    throw new IllegalArgumentException("Invalid inward attachment.");
                }
                file.keySet().retainAll(new HashSet<>(Arrays.asList("key", "name", "size", "type")));
            }
            return files;
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid inward attachment data.", e);
        }
    }

    public void replace(int inwardId, Integer itemchildId, String type, List<Map<String, Object>> files) {
        if (itemchildId == null) {
            jdbc.update("DELETE FROM trading_inward_attachment WHERE inward_id = ? AND itemchild_id IS NULL AND attachment_type = ?", inwardId, type);
        } else {
            jdbc.update("DELETE FROM trading_inward_attachment WHERE itemchild_id = ? AND attachment_type = ?", itemchildId, type);
        }
        if (files == null || files.isEmpty()) return;
        List<Object[]> rows = new ArrayList<>();
        for (int i = 0; i < files.size(); i++) {
            Map<String, Object> file = files.get(i);
            Object size = file.get("size");
            rows.add(new Object[] { inwardId, itemchildId, type, file.get("key"), file.get("name"),
                size instanceof Number ? ((Number) size).longValue() : null, file.get("type"), i + 1 });
        }
        jdbc.batchUpdate("INSERT INTO trading_inward_attachment (inward_id, itemchild_id, attachment_type, file_key, file_name, file_size, mime_type, sort_order) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)", rows);
    }

    public Map<String, String> loadWithUrls(Collection<Integer> inwardIds) {
        Map<String, List<Map<String, Object>>> grouped = new HashMap<>();
        if (inwardIds == null || inwardIds.isEmpty()) return new HashMap<>();
        new NamedParameterJdbcTemplate(jdbc).query("SELECT * FROM trading_inward_attachment WHERE inward_id IN (:ids) "
            + "ORDER BY inward_id, itemchild_id, attachment_type, sort_order, attachment_id",
            new MapSqlParameterSource("ids", inwardIds), rs -> {
                Map<String, Object> file = new LinkedHashMap<>();
                String key = rs.getString("file_key");
                file.put("key", key);
                file.put("name", rs.getString("file_name"));
                Number size = (Number) rs.getObject("file_size");
                file.put("size", size == null ? null : size.longValue());
                file.put("type", rs.getString("mime_type"));
                if (key != null && key.startsWith("inward_")) file.put("url", storage.generatePresignedUrlForTrading(key));
                Number itemchildId = (Number) rs.getObject("itemchild_id");
                grouped.computeIfAbsent(lookupKey(rs.getInt("inward_id"), itemchildId == null ? null : itemchildId.intValue(),
                    rs.getString("attachment_type")), k -> new ArrayList<>()).add(file);
            });
        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, List<Map<String, Object>>> entry : grouped.entrySet()) {
            try {
                result.put(entry.getKey(), mapper.writeValueAsString(entry.getValue()));
            } catch (IOException e) {
                throw new IllegalStateException("Unable to read inward attachments.", e);
            }
        }
        return result;
    }

    public static String lookupKey(Integer inwardId, Integer itemchildId, String type) {
        return type + ":" + inwardId + ":" + (itemchildId == null ? "" : itemchildId);
    }

    public int migrateLegacy() {
        int migrated = 0;
        List<String> skipped = new ArrayList<>();
        for (String[] column : new String[][] { { "documents_json", DOCUMENT }, { "qc_certificates_json", QC_CERTIFICATE } }) {
            List<Map<String, Object>> rows = jdbc.queryForList("SELECT i.inward_id, i." + column[0] + " AS files FROM trading_inward i "
                + "WHERE i." + column[0] + " IS NOT NULL AND i." + column[0] + " <> '' AND i." + column[0] + " <> '[]' AND NOT EXISTS "
                + "(SELECT 1 FROM trading_inward_attachment a WHERE a.inward_id = i.inward_id AND a.itemchild_id IS NULL AND a.attachment_type = ?)", column[1]);
            for (Map<String, Object> row : rows) {
                List<Map<String, Object>> files = legacy(row.get("files"));
                if (files == null) { skipped.add("inward " + row.get("inward_id")); continue; }
                replace(((Number) row.get("inward_id")).intValue(), null, column[1], files);
                migrated++;
            }
        }
        List<Map<String, Object>> images = jdbc.queryForList("SELECT c.inwardid, c.itemchild_id, c.images_json AS files FROM trading_inward_items c "
            + "WHERE c.images_json IS NOT NULL AND c.images_json <> '' AND c.images_json <> '[]' AND NOT EXISTS "
            + "(SELECT 1 FROM trading_inward_attachment a WHERE a.itemchild_id = c.itemchild_id AND a.attachment_type = ?)", ITEM_IMAGE);
        for (Map<String, Object> row : images) {
            List<Map<String, Object>> files = legacy(row.get("files"));
            if (files == null) { skipped.add("item " + row.get("itemchild_id")); continue; }
            replace(((Number) row.get("inwardid")).intValue(), ((Number) row.get("itemchild_id")).intValue(), ITEM_IMAGE, files);
            migrated++;
        }
        if (!skipped.isEmpty()) log.warn("Inward attachments not migrated, invalid legacy JSON for {}", skipped);
        return migrated;
    }

    private List<Map<String, Object>> legacy(Object json) {
        try {
            return read(json == null ? null : json.toString());
        } catch (IOException | IllegalArgumentException e) {
            return null;
        }
    }

    private List<Map<String, Object>> read(String json) throws IOException {
        if (json == null || json.trim().isEmpty()) return new ArrayList<>();
        List<Map<String, Object>> files = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        if (files == null || files.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Invalid inward attachment data.");
        return files;
    }
}
