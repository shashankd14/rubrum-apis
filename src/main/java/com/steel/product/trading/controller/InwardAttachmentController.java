package com.steel.product.trading.controller;

import com.steel.product.trading.exception.TradingErrors;
import com.steel.product.trading.service.InwardAttachmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@RestController
@CrossOrigin
public class InwardAttachmentController {
    private final InwardAttachmentService service;

    public InwardAttachmentController(InwardAttachmentService service) { this.service = service; }

    @PostMapping(value = "/inward/attachments", consumes = "multipart/form-data", produces = "application/json")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(service.upload(file));
        } catch (IOException | RuntimeException e) {
            return TradingErrors.failure("upload the attachment", e);
        }
    }
}
