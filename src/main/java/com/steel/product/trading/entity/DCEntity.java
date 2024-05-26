package com.steel.product.trading.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.util.Date;

@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "trading_delivery_chalan")
public class DCEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "dc_id")
	private Integer dcId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "enquiryid")
	private EQPEntity enquiryId;

	@Column(name = "dispatch_from_id")
	private Integer dispatchFromId;

	@Column(name = "bill_to")
	private Integer billTo;

	@Column(name = "ship_to")
	private Integer shipTo;

	@Column(name = "vehicle_no")
	private String vehicleNo;

	@Column(name = "transport_doc")
	private String transportDoc;

	@Column(name = "delivery_date")
	private Date deliveryDate;

	@Column(name = "eway_bill_no")
	private String ewayBillNo;

	@Column(name = "place_of_supply")
	private String placeOfSupply;

	@Column(name = "weigh_bridge")
	private BigDecimal weighBridge;

	@Column(name = "gross_weight")
	private BigDecimal grossWeight;

	@Column(name = "tare_weight")
	private BigDecimal tareWeight;

	@Column(name = "net_weight")
	private BigDecimal netWeight;

	@Column(name = "sac1")
	private String sac1;

	@Column(name = "sac2")
	private String sac2;

	@Column(name = "sac3")
	private String sac3;

	@Column(name = "sac4")
	private String sac4;

	@Column(name = "loading_taxable")
	private BigDecimal loadingTaxable;

	@Column(name = "transport_taxable")
	private BigDecimal transportTaxable;

	@Column(name = "other_charges_taxable")
	private BigDecimal otherChargesTaxable;

	@Column(name = "taxable_amount")
	private BigDecimal taxableAmount;

	@Column(name = "sgst")
	private BigDecimal sgst;

	@Column(name = "cgst")
	private BigDecimal cgst;

	@Column(name = "total")
	private BigDecimal total;

	@Column(name = "other_charges1_non_taxable")
	private BigDecimal otherCharges1NonTaxable;

	@Column(name = "other_charges2_non_taxable")
	private BigDecimal otherCharges2NonTaxable;

	@Column(name = "grand_total")
	private BigDecimal grandTotal;

	@Column(name = "dc_issued_by")
	private String dcIssuedBy;

	@Column(name = "store_loading_incharge")
	private String storeLoadingIncharge;

	@Column(name = "receiver_signatures")
	private String receiverSignatures;

	@Column(name = "to_be_billed_to")
	private Integer toBeBilledTo;

	@Column(name = "purpose")
	private String purpose;

	@Column(name = "amount_in_words")
	private String amountInWords;

	@Column(name = "e_oe")
	private String eOe;

	@Column(name = "dc_version")
	private String dcVersion;

	@Column(name = "is_deleted", columnDefinition = "BIT")
	private Boolean isDeleted;

	@Column(name = "created_by")
	private Integer createdBy;

	@Column(name = "updated_by")
	private Integer updatedBy;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

	@Column(name = "updated_on")
	@UpdateTimestamp
	private Date updatedOn;

}