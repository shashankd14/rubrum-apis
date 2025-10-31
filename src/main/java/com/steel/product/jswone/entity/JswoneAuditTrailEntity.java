package com.steel.product.jswone.entity;

import org.hibernate.annotations.CreationTimestamp;
import lombok.Data;
import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "jsw_audit_trail")
public class JswoneAuditTrailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@Column(name = "process_type")
	private String processType;

	@Column(name = "po_id")
	private String poId;

	@Column(name = "po_invoice_no")
	private String poInvoiceNo;

	@Column(name = "request_obj")
	private String requestObj;

	@Column(name = "status_code")
	private String statusCode;

	@Column(name = "destination_response")
	private String destinationResponse;
	
	@Column(name = "sourceRespone")
	private String sourceRespone;
	  
	@Column(name = "request_url")
	private String requestUrl;

	@Column(name = "created_on", updatable = false)
	@CreationTimestamp
	private Date createdOn;

}