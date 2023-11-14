package com.steel.product.application.dto.delivery;

import java.util.Date;
import java.util.List;

public class DeliveryDto {

    private List<DeliveryItemDetails> deliveryItemDetails;

    private String vehicleNo;

    private Integer deliveryId;

    private String taskType;

    private String customerInvoiceNo;

    private Date customerInvoiceDate;
    
    private Integer packingRateId;
    
    private Integer laminationId;

    public List<DeliveryItemDetails> getDeliveryItemDetails() {
        return deliveryItemDetails;
    }

    public void setDeliveryItemDetails(List<DeliveryItemDetails> deliveryItemDetails) {
        this.deliveryItemDetails = deliveryItemDetails;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }

    public String getCustomerInvoiceNo() {
        return customerInvoiceNo;
    }

    public void setCustomerInvoiceNo(String customerInvoiceNo) {
        this.customerInvoiceNo = customerInvoiceNo;
    }

    public Date getCustomerInvoiceDate() {
        return customerInvoiceDate;
    }

    public void setCustomerInvoiceDate(Date customerInvoiceDate) {
        this.customerInvoiceDate = customerInvoiceDate;
    }

    public Integer getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Integer deliveryId) {
        this.deliveryId = deliveryId;
    }

	public String getTaskType() {
		return taskType;
	}

	public void setTaskType(String taskType) {
		this.taskType = taskType;
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


}


