package com.steel.product.application.service;

import com.steel.product.application.dao.RatesRepository;
import com.steel.product.application.entity.Rates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatesServiceImpl implements RatesService{
    @Autowired
    private RatesRepository ratesRepository;

    @Override
    public Rates getById(int ratesId) {
        Optional<Rates> result = this.ratesRepository.findById(Integer.valueOf(ratesId));
        Rates rates = null;
        if (result.isPresent()) {
            rates = result.get();
        } else {
            throw new RuntimeException("Did not find status id - " + ratesId);
        }
        return rates;
    }

    @Override
    public List<Rates> getAll() {
        return ratesRepository.findAll();
    }
}
