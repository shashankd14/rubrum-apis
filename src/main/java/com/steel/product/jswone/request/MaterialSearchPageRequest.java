package com.steel.product.jswone.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MaterialSearchPageRequest {

	private Integer pageNo;

	private Integer pageSize;

	private String searchText;

	private String mmid;

	private Integer categoryId;

	private Integer subcategoryId;

	private Integer leafcategoryId;

	private Integer formId;

	private Integer producttypeId;

	private Integer gradeId;

	private Integer subgradeId;

	private Integer brandId;

	private Integer surfacetypeId;

	private Integer coatingtypeId;

	private Integer uomId;

}
