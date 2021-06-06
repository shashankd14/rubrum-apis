package com.steel.product.application.dto.delivery;

import java.util.List;

public class DeliveryDto {

    private List<DeliveryItemDetails> deliveryItemDetails;

    private String vehicleNo;

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
}
