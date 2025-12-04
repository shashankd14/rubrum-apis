package com.steel.product.jswone.request;

import java.io.Serializable;
import lombok.Data;

@Data
public class SearchRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer id;

	private Integer categoryId;

	private Integer gradeId;

	private Integer brandId;

	private Integer productId;

	private Integer branchId;

	private Integer subcategoryId;

	private Integer leafcategoryId;

	private String searchText;

	private Integer pageNo;

	private Integer pageSize;

}
