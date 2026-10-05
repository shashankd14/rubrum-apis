package com.steel.product.trading.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.steel.product.trading.request.CommercialTermRequest;
import com.steel.product.trading.request.CommercialTermSearchRequest;
import com.steel.product.trading.request.DeleteRequest;
import com.steel.product.trading.service.CommercialTermMasterService;

@RestController
@CrossOrigin
@Tag(name = "Commercial Term Master", description = "Commercial term master")
public class CommercialTermMasterController {
    @Autowired
    private CommercialTermMasterService service;

    @PostMapping("/commercial-term/list")
    public ResponseEntity<Object> list(@RequestBody CommercialTermSearchRequest request) {
        return ResponseEntity.ok(service.list(request));
    }

    @PostMapping("/commercial-term/save")
    public ResponseEntity<Object> save(@RequestBody CommercialTermRequest request) {
        return service.save(request);
    }

    @PutMapping("/commercial-term/update")
    public ResponseEntity<Object> update(@RequestBody CommercialTermRequest request) {
        return service.save(request);
    }

    @PostMapping("/commercial-term/delete")
    public ResponseEntity<Object> delete(@RequestBody DeleteRequest request) {
        return service.delete(request);
    }
}
