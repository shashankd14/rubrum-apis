package com.steel.product.jswone.request;

import lombok.Data;

/**
 * One row of the FG (finished goods) stock upload file.
 * Columns: CoilNumber, CustomerBatchId, Finishing Date, Current Date, Coil Age(No'of Days),
 * No'of Pieces, MaterialDesc, MaterialGrade, Mother Coil No, TDC No, Packet Id, Thickness,
 * Actual Width, Actual Length, Actual Weight, Classification Tag, End User Tag
 */
@Data
public class FGFileDataDTO {

	private String coilNumber;
	private String customerBatchId;
	private String finishingDate;
	private String currentDate;
	private String coilAge;
	private String noOfPieces;
	private String materialDesc;
	private String materialGrade;
	private String motherCoilNo;
	private String tdcNo;
	private String packetId;
	private String thickness;
	private String actualWidth;
	private String actualLength;
	private String actualWeight;
	private String classificationTag;
	private String endUserTag;
}
