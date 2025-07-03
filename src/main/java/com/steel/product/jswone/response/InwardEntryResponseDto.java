package com.steel.product.jswone.response;

import lombok.Data;

@Data
public class InwardEntryResponseDto {

    private Integer inwardEntryId;

    private Integer instructionId;

    private String locationName;

    private String coilNumber;

    private String batchNumber;

    private String material;

    private String materialGrade;

    private String mmId;

    private String customerBatchId;

    private float fThickness;

    private float fLength;

    private float fQuantity;

    private Float fpresent;

    private Float inStockWeight;

}
