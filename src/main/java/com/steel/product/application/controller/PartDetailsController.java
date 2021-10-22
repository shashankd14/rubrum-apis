package com.steel.product.application.controller;

import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.service.PartDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/partdetails")
public class PartDetailsController {

    private PartDetailsService partDetailsService;

    @Autowired
    public PartDetailsController(PartDetailsService partDetailsService) {
        this.partDetailsService = partDetailsService;
    }

    @GetMapping("{partDetailsId}")
    public List<PartDetailsResponse> getPartDetailsResponse(@PathVariable("partDetailsId") String partDetailsId) {
        return partDetailsService.getByPartDetailsId(partDetailsId);
    }

}
