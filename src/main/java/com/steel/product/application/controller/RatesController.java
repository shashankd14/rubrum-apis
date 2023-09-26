package com.steel.product.application.controller;

import com.steel.product.application.entity.Rates;
import com.steel.product.application.service.RatesService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@Tag(name = "Rates", description = "Rates")
@RequestMapping("/rates")
public class RatesController {

    @Autowired
    private RatesService ratesService;

    @GetMapping("/getById/{ratesId}")
    public ResponseEntity<Object> getById (@PathVariable int ratesId){
        try{
            Rates rates = ratesService.getById(ratesId);
            return new ResponseEntity(rates, HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @GetMapping("/list")
    public ResponseEntity<Object> getAll(){
        try {
            List<Rates> ratesList = ratesService.getAll();
            return new ResponseEntity(ratesList, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping({"/save"})
    public ResponseEntity<Object> saveRates(@ModelAttribute Rates rates) {
        try{
            rates.setRateId(0);
            ratesService.save(rates);
            return new ResponseEntity("Rates saved successfully", HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping({"/update"})
    public ResponseEntity<Object> updateRates(@ModelAttribute Rates rates) {
        try{
            //rates.setRateId(0);
            ratesService.save(rates);
            return new ResponseEntity("Rates updated successfully", HttpStatus.OK);

        }catch(Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
