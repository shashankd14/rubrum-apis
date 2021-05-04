package com.steel.product.application.dto.delivery;

import java.util.List;

public class DeliveryDto {

    private List<DeliveryRemarks> packetRemarks;

    private String vehicleNo;

    public List<DeliveryRemarks> getPacketRemarks() {
        return packetRemarks;
    }

    public void setPacketRemarks(List<DeliveryRemarks> packetRemarks) {
        this.packetRemarks = packetRemarks;
    }

    public String getVehicleNo() {
        return vehicleNo;
    }

    public void setVehicleNo(String vehicleNo) {
        this.vehicleNo = vehicleNo;
    }
}
