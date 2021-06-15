package com.steel.product.application.service;

import com.steel.product.application.dao.RatesRepository;
import com.steel.product.application.entity.Material;
import com.steel.product.application.entity.Party;
import com.steel.product.application.entity.Process;
import com.steel.product.application.entity.Rates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RatesServiceImpl implements RatesService{
    @Autowired
    private RatesRepository ratesRepository;

    @Autowired
    private PartyDetailsService partyDetailsService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private MaterialDescriptionService materialDescriptionService;


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

    @Override
    public Rates save(Rates rates) {
        try{
            Rates savedRates = new Rates();

            if(rates.getRateId() != 0)
                savedRates = getById(rates.getRateId());

            Party party = new Party();
            Process process = new Process();
            Material material = new Material();

            party = partyDetailsService.getPartyById(rates.getPartyRates().getnPartyId());
            process = processService.getById(rates.getProcess().getProcessId());
            material = materialDescriptionService.getMatById(rates.getMaterialType().getMatId());

            savedRates.setPartyRates(party);
            savedRates.setProcess(process);
            savedRates.setMaterialType(material);

            savedRates.setMinThickness(rates.getMinThickness());
            savedRates.setMaxThickness(rates.getMaxThickness());
            savedRates.setThicknessRate(rates.getThicknessRate());
            savedRates.setPackagingCharges(rates.getPackagingCharges());
            savedRates.setLaminationCharges(rates.getLaminationCharges());

            return ratesRepository.save(savedRates);


        }catch (Exception e){
            return null;
        }

    }
}
