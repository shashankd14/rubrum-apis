package com.steel.product.jswone.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesOrderDetails {

    private String salesorder_id;

    private String salesorder_number;

    private String date;

    private String status;

    private String reference_number;

    private String customer_id;

    private String customer_name;

    private String customer_number;

    private String gst_no;

    private String place_of_supply;

    private String delivery_method;

    private String branch_id;

    private String branch_name;

    private String expected_shipment_date;

    private String standard_material_date;

    private String likely_material_date;

    private String remarks;

    private String cam_code;

    private BigDecimal total_quantity;

    private BigDecimal balance;
}