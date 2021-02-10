package com.steel.product.application.service;

import com.steel.product.application.dao.ProcessRepository;
import com.steel.product.application.entity.Process;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProcessServiceImpl implements ProcessService{

    @Autowired
    private ProcessRepository processRepo;

    @Override
    public Process getById(int processId) {
        Optional<Process> result = this.processRepo.findById(Integer.valueOf(processId));
        Process process = null;
        if (result.isPresent()) {
            process = result.get();
        } else {
            throw new RuntimeException("Did not find status id - " + processId);
        }
        return process;
    }

    @Override
    public List<Process> getAll() {
        return processRepo.findAll();
    }
}
