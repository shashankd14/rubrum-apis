package com.steel.product.application.dto.delivery;

import java.util.List;

public class ValidatePriceMappingDTO {

    private String vehicleNo;

    private Integer packingRateId;
    
    private Integer laminationId;

    private List<Integer> inwardList;

	public String getVehicleNo() {
		return vehicleNo;
	}

	public void setVehicleNo(String vehicleNo) {
		this.vehicleNo = vehicleNo;
	}

	public Integer getPackingRateId() {
		return packingRateId;
	}

	public void setPackingRateId(Integer packingRateId) {
		this.packingRateId = packingRateId;
	}

	public Integer getLaminationId() {
		return laminationId;
	}

	public void setLaminationId(Integer laminationId) {
		this.laminationId = laminationId;
	}

	public List<Integer> getInwardList() {
		return inwardList;
	}

	public void setInwardList(List<Integer> inwardList) {
		this.inwardList = inwardList;
	}


}

