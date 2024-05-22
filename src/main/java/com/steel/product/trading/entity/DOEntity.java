package com.steel.product.trading.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.util.Date;

@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "trading_delivery_order")
public class DOEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "do_id")
	private Integer doId;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "enquiryid")
	private EQPEntity enquiryId;

	@Column(name = "do_no")
	private String doNo;

	@Column(name = "do_date")
	private Date doDate;

	@Column(name = "from_location_id")
	private Integer fromLocationId;

	@Column(name = "bill_to_customer_id")
	private Integer billToCustomerId;

	@Column(name = "vehicle_no")
	private String vehicleNo;

	@Column(name = "driver_number")
	private String driverNumber;

	@Column(name = "delivery_date")
	private Date deliveryDate;

	@Column(name = "dc_no")
	private String dcNo;

	@Column(name = "invoice_number")
	private String invoiceNumber;;

	@Column(name = "eway_bill_no")
	private String ewayBillNo;

	@Column(name = "dc_issued_by")
	private String dcIssuedBy;

	@Column(name = "loading_by")
	private String loadingBy;

	@Column(name = "store_loading_incharge")
	private String storeLoadingIncharge;

	@Column(name = "receiver_signatures")
	private String receiverSignatures;

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