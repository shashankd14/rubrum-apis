package com.steel.product.trading.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.steel.product.application.service.AWSS3Service;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.*;

@Service
public class InwardAttachmentService {
    private static final Set<String> TYPES = new HashSet<>(Arrays.asList("application/pdf", "image/jpeg", "image/png", "image/webp"));
    private final AWSS3Service storage;
    private final ObjectMapper mapper;

    public InwardAttachmentService(AWSS3Service storage, ObjectMapper mapper) {
        this.storage = storage;
        this.mapper = mapper;
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

    public String normalize(String json) {
        try {
            List<Map<String, Object>> files = read(json);
            if (files.size() > 20) throw new IllegalArgumentException("A maximum of 20 attachments is allowed.");
            for (Map<String, Object> file : files) {
                if (!(file.get("key") instanceof String) || !((String) file.get("key")).startsWith("inward_")) {
                    throw new IllegalArgumentException("Invalid inward attachment.");
                }
                file.keySet().retainAll(new HashSet<>(Arrays.asList("key", "name", "size", "type")));
            }
            return mapper.writeValueAsString(files);
        } catch (IOException e) {
            throw new IllegalArgumentException("Invalid inward attachment data.", e);
        }
    }

    public String withUrls(String json) {
        try {
            List<Map<String, Object>> files = read(json);
            for (Map<String, Object> file : files) {
                String key = (String) file.get("key");
                if (key != null && key.startsWith("inward_")) file.put("url", storage.generatePresignedUrlForTrading(key));
            }
            return mapper.writeValueAsString(files);
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read inward attachments.", e);
        }
    }

    private List<Map<String, Object>> read(String json) throws IOException {
        if (json == null || json.trim().isEmpty()) return new ArrayList<>();
        List<Map<String, Object>> files = mapper.readValue(json, new TypeReference<List<Map<String, Object>>>() {});
        if (files == null || files.stream().anyMatch(Objects::isNull)) throw new IllegalArgumentException("Invalid inward attachment data.");
        return files;
    }
}
