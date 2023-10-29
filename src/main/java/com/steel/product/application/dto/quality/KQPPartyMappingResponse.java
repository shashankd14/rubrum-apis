package com.steel.product.application.dto.quality;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KQPPartyMappingResponse {

	private Integer id;

	private Integer partyId;

	private String partyName;

	private String stageName;

	private String kqpName;

	private Integer kqpId;

	private Integer userId;

	private String endUserTagIdList;

	private String thicknessList;

	private String widthList;

	private String lengthList;

	private String matGradeIdList;

	private Integer createdBy;

	private Integer updatedBy;

	private String anyPartyFlag;
	
	private String anyMatgradeFlag;
	
	private String anyEndusertagFlag;
	
	private String anyWidthFlag;
	
	private String anyLengthFlag;
	
	private String anyThicknessFlag;

}
