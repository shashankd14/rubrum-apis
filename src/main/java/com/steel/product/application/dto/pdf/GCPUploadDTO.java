package com.steel.product.application.dto.pdf;

import com.steel.product.application.dto.instruction.WIPChildListResponseDTO;
import com.steel.product.application.entity.*;
import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class GCPUploadDTO {

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

	private String subGradeName;

	private float fWidth;

	private float fThickness;

	private float fLength;

	private float fQuantity;

	private float grossWeight;

	private Status status;

	private String statusDesc;

	private String vProcess;

	private Float fpresent;

	private Float valueOfGoods;

	private float billedWeight;

	private String parentCoilNumber;

	private String tdcNo;
	
	private int coilage;

	private int vParentBundleNumber;

	private String remarks;

	private AdminUserEntity createdBy;

	private AdminUserEntity updatedBy;

	private Date createdOn;

	private Date updatedOn;

	private Date processedDate;

	private Boolean isDeleted;

	private Float ys;

	private Float uts;

	private Float el;

	private String mmid;

	private List<WIPChildListResponseDTO> instructions;

	private Float totalWeight;

	private Float totalWeightSlit;

	private Float totalWeightCut;

	private Float inStockWeight;

	private String partDetailsId;

	private String plannedYieldLossRatio;
}
