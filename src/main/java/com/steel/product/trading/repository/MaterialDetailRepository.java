package com.steel.product.trading.repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.steel.product.trading.entity.MaterialMasterEntity;
import java.math.BigDecimal;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Repository;

@Repository
@Log4j2
public class MaterialDetailRepository {
    private static final String CUSTOM_PARAMETERS = "customParameters";
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    public MaterialDetailRepository(JdbcTemplate jdbc, ObjectMapper mapper) {
        this.jdbc = jdbc;
        this.mapper = mapper;
    }

    public static class Details {
        public final ObjectNode technicalSpecs;
        public final ArrayNode unitWeights;
        public final ArrayNode applicableProcesses;

        Details(ObjectMapper mapper) {
            technicalSpecs = mapper.createObjectNode();
            unitWeights = mapper.createArrayNode();
            applicableProcesses = mapper.createArrayNode();
        }
    }

    public void replace(int itemId, String technicalSpecs, String unitWeights, String applicableProcesses) {
        jdbc.update("UPDATE trading_material_master SET technical_specs = NULL, unit_weights = NULL, applicable_processes = NULL, "
            + "additional_params = NULL WHERE item_id = ?", itemId);
        replaceSpecs(itemId, read(technicalSpecs));
        replaceUnitWeights(itemId, read(unitWeights));
        replaceProcesses(itemId, read(applicableProcesses));
    }

    private void replaceSpecs(int itemId, JsonNode specs) {
        jdbc.update("DELETE FROM trading_material_spec WHERE item_id = ?", itemId);
        if (specs == null || !specs.isObject()) return;
        List<Object[]> rows = new ArrayList<>();
        Iterator<Map.Entry<String, JsonNode>> fields = specs.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> field = fields.next();
            if (CUSTOM_PARAMETERS.equals(field.getKey())) continue;
            JsonNode spec = field.getValue();
            JsonNode value = spec != null && spec.isObject() ? spec.get("value") : spec;
            String unit = spec != null && spec.isObject() ? text(spec.get("unit")) : null;
            if (isEmpty(value)) continue;
            rows.add(specRow(itemId, field.getKey(), null, value, unit, rows.size() + 1));
        }
        JsonNode custom = specs.get(CUSTOM_PARAMETERS);
        if (custom != null && custom.isArray()) {
            for (JsonNode parameter : custom) {
                String name = text(parameter.get("name"));
                if (name == null || name.trim().isEmpty() || isEmpty(parameter.get("value"))) continue;
                rows.add(specRow(itemId, null, name.trim(), parameter.get("value"), text(parameter.get("unit")), rows.size() + 1));
            }
        }
        if (rows.isEmpty()) return;
        jdbc.batchUpdate("INSERT INTO trading_material_spec (item_id, spec_key, spec_name, value_number, value_text, unit, sort_order) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?)", rows);
    }

    private Object[] specRow(int itemId, String key, String name, JsonNode value, String unit, int order) {
        BigDecimal number = value.isNumber() ? value.decimalValue() : null;
        return new Object[] { itemId, key, name, number, number == null ? value.asText() : null, unit, order };
    }

    private void replaceUnitWeights(int itemId, JsonNode weights) {
        jdbc.update("DELETE FROM trading_material_unit_weight WHERE item_id = ?", itemId);
        if (weights == null || !weights.isArray()) return;
        List<Object[]> rows = new ArrayList<>();
        for (JsonNode weight : weights) {
            BigDecimal value = decimal(weight.get("value"));
            String basis = text(weight.get("basis"));
            if (value == null || basis == null) continue;
            boolean active = !weight.has("active") || weight.get("active").asBoolean(true);
            rows.add(new Object[] { itemId, basis, value, active, rows.size() + 1 });
        }
        if (rows.isEmpty()) return;
        jdbc.batchUpdate("INSERT INTO trading_material_unit_weight (item_id, basis, weight_value, is_active, sort_order) VALUES (?, ?, ?, ?, ?)", rows);
    }

    private void replaceProcesses(int itemId, JsonNode processes) {
        jdbc.update("DELETE FROM trading_material_process WHERE item_id = ?", itemId);
        if (processes == null || !processes.isArray()) return;
        List<Object[]> rows = new ArrayList<>();
        for (JsonNode process : processes) {
            String name = text(process);
            if (name == null || name.trim().isEmpty()) continue;
            rows.add(new Object[] { itemId, name.trim(), rows.size() + 1 });
        }
        if (rows.isEmpty()) return;
        jdbc.batchUpdate("INSERT INTO trading_material_process (item_id, process_name, sort_order) VALUES (?, ?, ?)", rows);
    }

    public Map<Integer, Details> load(Collection<Integer> itemIds) {
        Map<Integer, Details> details = new HashMap<>();
        Set<Integer> ids = new LinkedHashSet<>(itemIds);
        ids.remove(null);
        if (ids.isEmpty()) return details;
        NamedParameterJdbcTemplate named = new NamedParameterJdbcTemplate(jdbc);
        MapSqlParameterSource params = new MapSqlParameterSource("ids", ids);
        named.query("SELECT * FROM trading_material_spec WHERE item_id IN (:ids) ORDER BY item_id, sort_order, spec_id", params, rs -> {
            Details d = details.computeIfAbsent(rs.getInt("item_id"), k -> new Details(mapper));
            ObjectNode spec = mapper.createObjectNode();
            String name = rs.getString("spec_name");
            if (rs.getString("spec_key") == null) spec.put("name", name);
            BigDecimal number = rs.getBigDecimal("value_number");
            if (number != null) spec.put("value", number.stripTrailingZeros().scale() <= 0 ? number.stripTrailingZeros().setScale(0) : number.stripTrailingZeros());
            else spec.put("value", rs.getString("value_text"));
            spec.put("unit", rs.getString("unit"));
            if (rs.getString("spec_key") != null) {
                d.technicalSpecs.set(rs.getString("spec_key"), spec);
            } else {
                JsonNode custom = d.technicalSpecs.get(CUSTOM_PARAMETERS);
                (custom == null ? d.technicalSpecs.putArray(CUSTOM_PARAMETERS) : (ArrayNode) custom).add(spec);
            }
        });
        named.query("SELECT * FROM trading_material_unit_weight WHERE item_id IN (:ids) ORDER BY item_id, sort_order, unit_weight_id", params, rs -> {
            ObjectNode weight = details.computeIfAbsent(rs.getInt("item_id"), k -> new Details(mapper)).unitWeights.addObject();
            weight.put("value", rs.getBigDecimal("weight_value").stripTrailingZeros());
            weight.put("basis", rs.getString("basis"));
            weight.put("active", rs.getBoolean("is_active"));
        });
        named.query("SELECT * FROM trading_material_process WHERE item_id IN (:ids) ORDER BY item_id, sort_order, material_process_id", params, rs -> {
            details.computeIfAbsent(rs.getInt("item_id"), k -> new Details(mapper)).applicableProcesses.add(rs.getString("process_name"));
        });
        return details;
    }

    public void populate(Collection<MaterialMasterEntity> materials) {
        List<Integer> ids = new ArrayList<>();
        materials.forEach(m -> ids.add(m.getItemId()));
        Map<Integer, Details> details = load(ids);
        for (MaterialMasterEntity material : materials) {
            Details d = details.getOrDefault(material.getItemId(), new Details(mapper));
            material.setTechnicalSpecs(d.technicalSpecs.toString());
            material.setUnitWeights(d.unitWeights.toString());
            material.setApplicableProcesses(d.applicableProcesses.toString());
            material.setAdditionalParams(null);
        }
    }

    public int migrateLegacy() {
        List<Map<String, Object>> rows = jdbc.queryForList("SELECT m.item_id, m.technical_specs, m.unit_weights, m.applicable_processes, m.additional_params "
            + "FROM trading_material_master m WHERE NOT EXISTS (SELECT 1 FROM trading_material_spec s WHERE s.item_id = m.item_id) "
            + "AND NOT EXISTS (SELECT 1 FROM trading_material_unit_weight w WHERE w.item_id = m.item_id) "
            + "AND NOT EXISTS (SELECT 1 FROM trading_material_process p WHERE p.item_id = m.item_id) "
            + "AND (m.technical_specs IS NOT NULL OR m.unit_weights IS NOT NULL OR m.applicable_processes IS NOT NULL OR m.additional_params IS NOT NULL)");
        int migrated = 0;
        List<Integer> skipped = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            int itemId = ((Number) row.get("item_id")).intValue();
            try {
                JsonNode additional = read(row.get("additional_params"));
                JsonNode specs = read(row.get("technical_specs"));
                JsonNode weights = read(row.get("unit_weights"));
                if ((specs == null || specs.size() == 0) && additional != null) specs = additional.get("technicalSpecs");
                if ((weights == null || weights.size() == 0) && additional != null) weights = additional.get("unitWeights");
                replaceSpecs(itemId, specs);
                replaceUnitWeights(itemId, weights);
                replaceProcesses(itemId, read(row.get("applicable_processes")));
                migrated++;
            } catch (IllegalArgumentException e) {
                skipped.add(itemId);
            }
        }
        if (!skipped.isEmpty()) log.warn("Material details not migrated, invalid legacy JSON for item ids {}", skipped);
        return migrated;
    }

    private JsonNode read(Object value) {
        if (value == null) return null;
        String text = value instanceof byte[] ? new String((byte[]) value, java.nio.charset.StandardCharsets.UTF_8) : value.toString();
        if (text.trim().isEmpty()) return null;
        try {
            JsonNode node = mapper.readTree(text);
            return node != null && node.isTextual() ? mapper.readTree(node.asText()) : node;
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid material detail data.", e);
        }
    }

    private static boolean isEmpty(JsonNode value) {
        return value == null || value.isNull() || value.isContainerNode() || value.asText().trim().isEmpty();
    }

    private static String text(JsonNode value) {
        return value == null || value.isNull() ? null : value.asText();
    }

    private static BigDecimal decimal(JsonNode value) {
        if (value == null || value.isNull()) return null;
        try {
            return value.isNumber() ? value.decimalValue() : new BigDecimal(value.asText().trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
