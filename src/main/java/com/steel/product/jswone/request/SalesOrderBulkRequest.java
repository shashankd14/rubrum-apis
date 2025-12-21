package com.steel.product.jswone.request;

import java.util.List;

public class SalesOrderBulkRequest {

    private List<Integer> soIds;

    public List<Integer> getSoIds() {
        return soIds;
    }

    public void setSoIds(List<Integer> soIds) {
        this.soIds = soIds;
    }
}
