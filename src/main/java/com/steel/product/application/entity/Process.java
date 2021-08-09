package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_process")
public class Process {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "processid")
    private int processId;

    @Column(name = "processname")
    private String processName;

    @OneToMany(mappedBy = "process")
    private List<Rates> rates;

    public int getProcessId() {
        return processId;
    }

    @OneToMany(mappedBy = "status", cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.DETACH,
            CascadeType.REFRESH })
    private List<Instruction> instruction;

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public List<Instruction> getInstruction() {
        return instruction;
    }

    public void setInstruction(List<Instruction> instruction) {
        this.instruction = instruction;
    }

    public List<Rates> getRates() {
        return rates;
    }

    public void setRates(List<Rates> rates) {
        this.rates = rates;
    }
}
