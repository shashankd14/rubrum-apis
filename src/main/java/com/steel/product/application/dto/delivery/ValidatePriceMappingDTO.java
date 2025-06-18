package com.steel.product.application.dto.delivery;

import java.util.List;

public class ValidatePriceMappingDTO {

    private String vehicleNo;

    private Integer packingRateId;
    
    private Integer laminationId;

    private List<DeliveryItemDetails> inwardList;

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

	public List<DeliveryItemDetails> getInwardList() {
		return inwardList;
	}

	public void setInwardList(List<DeliveryItemDetails> inwardList) {
		this.inwardList = inwardList;
	}


}

