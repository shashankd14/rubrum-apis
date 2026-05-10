package com.steel.product.jswone.request;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class SalesOrderLineItem {

    private String line_item_id;

    private String item_id;

    private String warehouse_id;

    private String warehouse_name;

    private String sku;

    private String name;

    private Double rate;

    private BigDecimal quantity;

    private String unit;

    private String tax_id;

    private String tax_name;

    private BigDecimal tax_percentage;

    private Double item_total;

    private String item_type;

    private String hsn_or_sac;

    private String cf_material_form_1;

    private String cf_form;

    private String cf_child_sku;
}
