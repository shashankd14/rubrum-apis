package com.steel.product.application.service;

import com.steel.product.application.entity.Process;

import java.util.List;

public interface ProcessService {

    Process getById(int processId);

    List<Process> getAll();
}
