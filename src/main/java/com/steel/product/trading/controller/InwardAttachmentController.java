package com.steel.product.trading.controller;

import com.steel.product.trading.service.InwardAttachmentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Collections;

@RestController
@CrossOrigin
public class InwardAttachmentController {
    private final InwardAttachmentService service;

    public InwardAttachmentController(InwardAttachmentService service) { this.service = service; }

    @PostMapping(value = "/inward/attachments", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(service.upload(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", e.getMessage()));
        } catch (IOException | RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("message", "Unable to upload attachment. Please retry."));
        }
    }
}
