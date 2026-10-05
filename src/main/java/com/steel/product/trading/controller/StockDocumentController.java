package com.steel.product.trading.controller;

import com.steel.product.trading.dto.StockDocument;
import com.steel.product.trading.service.StockDocumentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Stock Documents", description = "Stock audit and adjustment documents")
public class StockDocumentController {
    private final StockDocumentService service;
    public StockDocumentController(StockDocumentService service) { this.service = service; }
    @GetMapping("/stock/documents/{id}")
    public StockDocument get(@PathVariable String id) { return service.get(id); }
    @PostMapping("/stock/documents")
    public StockDocument save(@RequestBody StockDocument request) { return service.save(request); }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalid(IllegalArgumentException error) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", error.getMessage()));
    }
}
