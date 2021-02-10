package com.steel.product.application.controller;

import com.steel.product.application.entity.Rates;
import com.steel.product.application.service.RatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/rates")
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
    @GetMapping("/getAll")
    public ResponseEntity<Object> getAll(){
        try {
            List<Rates> ratesList = ratesService.getAll();
            return new ResponseEntity(ratesList, HttpStatus.OK);

        }catch (Exception e){
            return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
