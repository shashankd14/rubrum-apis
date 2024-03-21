package com.steel.product.trading.request;

import java.io.Serializable;
import lombok.Data;

@Data
public class SearchRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private String searchText;

	private Integer pageNo;

	private Integer pageSize;

}
