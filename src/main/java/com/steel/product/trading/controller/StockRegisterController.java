package com.steel.product.trading.controller;

import com.steel.product.trading.dto.StockRegisterResponse;
import com.steel.product.trading.request.OpeningStockRequest;
import com.steel.product.trading.request.StockCorrectionRequest;
import com.steel.product.trading.request.StockSearchRequest;
import com.steel.product.trading.service.StockDocumentService;
import com.steel.product.trading.service.StockRegisterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
@Tag(name = "Stock Register", description = "Stock register, verification, and adjustments")
public class StockRegisterController {
    private final StockRegisterService service;

    private final StockDocumentService documents;

    public StockRegisterController(StockRegisterService service, StockDocumentService documents) {
        this.service = service;
        this.documents = documents;
    }

    @PostMapping("/stock/register/list")
    public StockRegisterResponse list(@RequestBody StockSearchRequest request) {
        return service.list(request.getSearchText() == null ? "" : request.getSearchText(),
            request.getLocationId(), request.getPageNo(), request.getPageSize(),
            request.getSort() == null ? "" : request.getSort());
    }

    @PostMapping("/stock/verification/list")
    public Map<String, Object> verifications(@RequestBody StockSearchRequest request) {
        return documents.list("AUDIT", request);
    }

    @PostMapping("/stock/adjustment/list")
    public Map<String, Object> adjustments(@RequestBody StockSearchRequest request) {
        return documents.list("ADJUSTMENT", request);
    }

    @GetMapping("/stock/locations")
    public List<Map<String, Object>> locations() { return service.locations(); }

    @PostMapping("/stock/opening")
    public Map<String, String> save(@RequestBody OpeningStockRequest request) {
        return service.saveOpening(request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> invalid(IllegalArgumentException exception) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("message", exception.getMessage()));
    }

    @PostMapping("/stock/corrections")
    public Map<String, String> correct(@RequestBody StockCorrectionRequest request) {
        return service.correct(request);
    }
}
