package com.steel.product.jswone.request;

import lombok.Data;

@Data
public class SalesOrderCustomFields {

    private String cf_jopl_sales_order_reference;

    private String cf_biz_segment;

    private String cf_e_commerce;

    private String cf_supply_source;

    private String cf_payment_mode;

    private String cf_incoming_payment;

    private String cf_delivery_method;

    private String cf_freight_value;

    private String cf_expected_delivery_date;
}

