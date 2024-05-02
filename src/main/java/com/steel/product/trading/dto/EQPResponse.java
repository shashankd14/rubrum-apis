package com.steel.product.trading.dto;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.steel.product.trading.entity.EQPTermsEntity;

import lombok.Data;

@Data
public class EQPResponse {

	private Integer enquiryId;

	private Integer enqCustomerId;

	private String enqCustomerName;

	private String enqEnquiryFrom;

	private String enqEnquiryDate;;

	private Integer enqQty;

	private BigDecimal enqValue;

	private Integer quoteCustomerId;

	private String quoteCustomerName;

	private String quoteEnquiryFrom;

	private String quoteEnquiryDate;;

	private Integer quoteQty;

	private BigDecimal quoteValue;

	private String status;

	private List<EQPChildResponse> itemsList;

	private EQPTermsEntity terms = new EQPTermsEntity();

}