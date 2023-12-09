package com.steel.product.application.dto.pdf;

import com.steel.product.application.entity.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InwardEntryPdfDto {

    private Integer inwardEntryId;

    private String partyName;

    private String partyCgst;

    private String coilNumber;

    private String batchNumber;

    private Date dReceivedDate;

    private Date dBillDate;

    private String vLorryNo;

    private Date dInvoiceDate;

    private String customerCoilId;

    private String customerInvoiceNo;

    private String customerBatchId;

    private String purposeType;

    private String testCertificateNumber;

    private String testCertificateFileUrl;

    private String vInvoiceNo;

    private String matDescription;

    private String materialGradeName;

    private float fWidth;

    private float fThickness;

    private float fLength;

    private float fQuantity;

    private float grossWeight;

    private Status status;

    private String vProcess;

    private Float fpresent;

    private Float valueOfGoods;

    private float billedWeight;

    private String parentCoilNumber;

    private int vParentBundleNumber;

    private String remarks;

    private AdminUserEntity createdBy;

    private AdminUserEntity updatedBy;

    private Date createdOn;

    private Date updatedOn;

    private Boolean isDeleted;

    private List<InwardDoc> docs;

    private List<InstructionResponsePdfDto> instructions;

    private Map<Float, List<InstructionResponsePdfDto>> instructionsMap;

    private Map<Float, List<InstructionResponsePdfDto>> instructionsSlitMap;

    private Map<Long, List<InstructionResponsePdfDto>> instructionsCutMap;

    private Map<PartDetailsPdfResponse, List<InstructionResponsePdfDto>> partDetailsSlitMap;

    private Map<PartDetailsPdfResponse, List<InstructionResponsePdfDto>> partDetailsCutMap;

    private Float totalWeight;

    private Float totalWeightSlit;

    private Float totalWeightCut;

    private Float inStockWeight;

    private String partDetailsId;
    
    private Map<Integer, String> kqpParamsList = new HashMap<>();

}
