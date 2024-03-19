package com.steel.product.trading.request;

import java.io.Serializable;
import lombok.Data;

@Data
public class MaterialMasterSearch implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer itemId;

	private String searchText;

	private Integer pageNo;

	private Integer pageSize;

}
