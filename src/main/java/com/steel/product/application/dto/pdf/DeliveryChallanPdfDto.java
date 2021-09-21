package com.steel.product.application.dto.pdf;

import com.steel.product.application.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DeliveryChallanPdfDto {

    private String companyName;
    private String gstN;
    private Address addressBranch;
    private Address addressOffice;
    private String email;
    private List<InwardEntryPdfDto> inwardPdfDtos;
    private Float totalDeliveryWeight;
    private Float totalValueOfGoods;

    public DeliveryChallanPdfDto() {
    }

    public DeliveryChallanPdfDto(CompanyDetails companyDetails, List<InwardEntry> inwardEntries){
        this.companyName = companyDetails.getCompanyName();
        this.gstN = companyDetails.getGstN();
        this.addressBranch = companyDetails.getAddressBranch();
        this.addressOffice = companyDetails.getAddressOffice();
        this.email = companyDetails.getEmail();
        if(this.inwardPdfDtos == null){
            this.inwardPdfDtos = new ArrayList<>();
        }
        this.inwardPdfDtos = inwardEntries.stream().map(inw -> InwardEntry.valueOf(inw,inw.getInstructions()
                .stream().map(ins -> Instruction.valueOfInstructionPdf(ins)).collect(Collectors.toList()))).collect(Collectors.toList());
        this.totalDeliveryWeight = inwardPdfDtos.stream().flatMap(inw -> inw.getInstructions().stream())
                .reduce(0f,(sum,ins) -> sum + ins.getActualWeight(), Float::sum);
        this.totalValueOfGoods = inwardPdfDtos.stream().flatMap(inw -> inw.getInstructions().stream())
                .reduce(0f,(sum,ins) -> sum+ins.getValueOfGoods(),Float::sum);

    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getGstN() {
        return gstN;
    }

    public void setGstN(String gstN) {
        this.gstN = gstN;
    }

    public Address getAddressBranch() {
        return addressBranch;
    }

    public void setAddressBranch(Address addressBranch) {
        this.addressBranch = addressBranch;
    }

    public Address getAddressOffice() {
        return addressOffice;
    }

    public void setAddressOffice(Address addressOffice) {
        this.addressOffice = addressOffice;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<InwardEntryPdfDto> getInwardPdfDtos() {
        return inwardPdfDtos;
    }

    public void setInwardPdfDtos(List<InwardEntryPdfDto> inwardPdfDtos) {
        this.inwardPdfDtos = inwardPdfDtos;
    }

    public Float getTotalDeliveryWeight() {
        return totalDeliveryWeight;
    }

    public void setTotalDeliveryWeight(Float totalDeliveryWeight) {
        this.totalDeliveryWeight = totalDeliveryWeight;
    }

    public Float getTotalValueOfGoods() {
        return totalValueOfGoods;
    }

    public void setTotalValueOfGoods(Float totalValueOfGoods) {
        this.totalValueOfGoods = totalValueOfGoods;
    }
}
