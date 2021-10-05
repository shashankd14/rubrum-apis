package com.steel.product.application.entity;

import com.steel.product.application.dto.process.ProcessDto;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @OneToMany(mappedBy = "process", fetch = FetchType.LAZY)
    private Set<InstructionPlan> instructionPlans;

    public void addInstructionPlan(InstructionPlan instructionPlan) {
        if (this.instructionPlans == null) {
            this.instructionPlans = new HashSet<>();
        }
        this.getInstructionPlans().add(instructionPlan);
        instructionPlan.setProcess(this);
    }

    public void removeInstructionPlan(InstructionPlan instructionPlan) {
        this.getInstructionPlans().remove(instructionPlan);
        instructionPlan.setProcess(null);
    }

    public int getProcessId() {
        return processId;
    }

    public void setProcessId(int processId) {
        this.processId = processId;
    }

    public String getProcessName() {
        return processName;
    }

    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public List<Rates> getRates() {
        return rates;
    }

    public void setRates(List<Rates> rates) {
        this.rates = rates;
    }

    public Set<InstructionPlan> getInstructionPlans() {
        return instructionPlans;
    }

    public void setInstructionPlans(Set<InstructionPlan> instructionPlans) {
        this.instructionPlans = instructionPlans;
    }

    public static ProcessDto valueOf(Process process) {
        ProcessDto processDto = new ProcessDto();
        processDto.setProcessId(process.getProcessId());
        processDto.setProcessName(process.getProcessName());
        return processDto;
    }
}
