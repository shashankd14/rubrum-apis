package com.steel.product.application.controller;

import com.steel.product.application.dto.partDetails.PartDetailsResponse;
import com.steel.product.application.service.PartDetailsService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Part Details", description = "Part Details")
@RequestMapping("/partdetails")
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
