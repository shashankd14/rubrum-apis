package com.steel.product.application.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "product_rates")
public class Rates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "rateid")
    private int rateId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "partyid")
    @JsonBackReference(value = "party-rates")
    private Party partyRates;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "processid")
    @JsonBackReference(value = "process-rates")
    private Process process;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "materialtype")
    @JsonBackReference(value = "material-rates")
    private Material materialType;

    @OneToMany(mappedBy = "rates")
    @JsonManagedReference(value = "instruction-rates")
    private List<Instruction> instructionRate;

    @Column(name = "thickness_min")
    private float minThickness;

    @Column(name = "thickness_max")
    private float maxThickness;

    @Column(name = "thickness_rate")
    private float thicknessRate;

    @Column(name = "packaging_charges")
    private float packagingCharges;

    @Column(name = "lamination_charges")
    private float laminationCharges;

    public int getRateId() {
        return rateId;
    }

    public void setRateId(int rateId) {
        this.rateId = rateId;
    }

    public Party getPartyRates() {
        return partyRates;
    }

    public void setPartyRates(Party partyRates) {
        this.partyRates = partyRates;
    }

    public float getMinThickness() {
        return minThickness;
    }

    public void setMinThickness(float minThickness) {
        this.minThickness = minThickness;
    }

    public float getMaxThickness() {
        return maxThickness;
    }

    public void setMaxThickness(float maxThickness) {
        this.maxThickness = maxThickness;
    }

    public float getThicknessRate() {
        return thicknessRate;
    }

    public void setThicknessRate(float thicknessRate) {
        this.thicknessRate = thicknessRate;
    }

    public float getPackagingCharges() {
        return packagingCharges;
    }

    public void setPackagingCharges(float packagingCharges) {
        this.packagingCharges = packagingCharges;
    }

    public float getLaminationCharges() {
        return laminationCharges;
    }

    public void setLaminationCharges(float laminationCharges) {
        this.laminationCharges = laminationCharges;
    }

    public List<Instruction> getInstructionRate() {
        return instructionRate;
    }

    public void setInstructionRate(List<Instruction> instructionRate) {
        this.instructionRate = instructionRate;
    }

    public Process getProcess() {
        return process;
    }

    public void setProcess(Process process) {
        this.process = process;
    }

    public Material getMaterialType() {
        return materialType;
    }

    public void setMaterialType(Material materialType) {
        this.materialType = materialType;
    }
}
