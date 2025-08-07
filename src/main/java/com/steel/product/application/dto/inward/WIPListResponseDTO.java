package com.steel.product.application.dto.inward;

import com.steel.product.application.dto.instruction.WIPChildListResponseDTO;
import lombok.Data;
import java.util.List;

@Data
public class WIPListResponseDTO {

	private int inwardEntryId;

	private int coilAge;

	private String coilNumber;

	private String batchNumber;

	private String customerBatchId;

	private String customerCoilId;

	private String customerInvoiceNo;

	private String partyName;

	private String materialGrade;

	private String materialSubGrade;

	private String materialDesc;

	private String inwardStatus;

	private String mmId;

	private Float fWidth;

	private Float fThickness;

	private Float fLength;

	private Float grossWeight;

	private Float fpresent;

	private List<WIPChildListResponseDTO> instruction;

}
