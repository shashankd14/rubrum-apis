package com.steel.product.application.service;

import com.steel.product.application.entity.Rates;

import java.util.List;

public interface RatesService {

    Rates getById (int ratesId);
    List<Rates> getAll();
}
