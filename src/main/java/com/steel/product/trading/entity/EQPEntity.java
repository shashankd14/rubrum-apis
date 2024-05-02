package com.steel.product.trading.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
@Entity
@Table(name = "trading_eqp")
public class EQPEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "enquiry_id")
	private Integer enquiryId;

	@Column(name = "enq_customer_id")
	private Integer enqCustomerId;

	@Column(name = "enq_enquiry_from")
	private String enqEnquiryFrom;

	@Column(name = "enq_enquiry_date")
	private Date enqEnquiryDate;;

	@Column(name = "enq_qty")
	private Integer enqQty;

	@Column(name = "enq_value")
	private BigDecimal enqValue;
	
	@Column(name = "quote_customer_id")
	private Integer quoteCustomerId;

	@Column(name = "quote_enquiry_from")
	private String quoteEnquiryFrom;

	@Column(name = "quote_enquiry_date")
	private Date quoteEnquiryDate;;

	@Column(name = "quote_qty")
	private Integer quoteQty;

	@Column(name = "quote_value")
	private BigDecimal quoteValue;
	
	@Column(name = "status")
	private String status;

	@OneToMany(mappedBy = "enquiryId", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private Set<EQPChildEntity> itemsList;
	
	@OneToMany(mappedBy = "enquiryId", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private Set<EQPTermsEntity> terms;

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

	@Column(name = "quote_created_by")
	private Integer quoteCreatedBy;

	@Column(name = "quote_updated_by")
	private Integer quoteUpdatedBy;

	@Column(name = "quote_created_on", updatable = false)
	@CreationTimestamp
	private Date quoteCreatedOn;

	@Column(name = "quote_updated_on")
	@UpdateTimestamp
	private Date quoteUpdatedOn;

	public void addItem(EQPChildEntity item) {
		if (this.itemsList == null) {
			this.itemsList = new LinkedHashSet<>();
		}
		this.itemsList.add(item);
		item.setEnquiryId(this);
	}
	
	public void addTerms(EQPTermsEntity eqpTermsEntity) {
		if (this.terms == null) {
			this.terms = new LinkedHashSet<>();
		}
		this.terms.add(eqpTermsEntity);
		eqpTermsEntity.setEnquiryId(this);
	}

	public Integer getEnquiryId() {
		return enquiryId;
	}

	public void setEnquiryId(Integer enquiryId) {
		this.enquiryId = enquiryId;
	}

	public Integer getEnqCustomerId() {
		return enqCustomerId;
	}

	public void setEnqCustomerId(Integer enqCustomerId) {
		this.enqCustomerId = enqCustomerId;
	}

	public String getEnqEnquiryFrom() {
		return enqEnquiryFrom;
	}

	public void setEnqEnquiryFrom(String enqEnquiryFrom) {
		this.enqEnquiryFrom = enqEnquiryFrom;
	}

	public Date getEnqEnquiryDate() {
		return enqEnquiryDate;
	}

	public void setEnqEnquiryDate(Date enqEnquiryDate) {
		this.enqEnquiryDate = enqEnquiryDate;
	}

	public Integer getEnqQty() {
		return enqQty;
	}

	public void setEnqQty(Integer enqQty) {
		this.enqQty = enqQty;
	}

	public BigDecimal getEnqValue() {
		return enqValue;
	}

	public void setEnqValue(BigDecimal enqValue) {
		this.enqValue = enqValue;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Set<EQPChildEntity> getItemsList() {
		return itemsList;
	}

	public void setItemsList(Set<EQPChildEntity> itemsList) {
		this.itemsList = itemsList;
	}

	public Boolean getIsDeleted() {
		return isDeleted;
	}

	public void setIsDeleted(Boolean isDeleted) {
		this.isDeleted = isDeleted;
	}

	public Integer getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Integer createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getUpdatedBy() {
		return updatedBy;
	}

	public void setUpdatedBy(Integer updatedBy) {
		this.updatedBy = updatedBy;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public Date getUpdatedOn() {
		return updatedOn;
	}

	public void setUpdatedOn(Date updatedOn) {
		this.updatedOn = updatedOn;
	}

	public Integer getQuoteCustomerId() {
		return quoteCustomerId;
	}

	public void setQuoteCustomerId(Integer quoteCustomerId) {
		this.quoteCustomerId = quoteCustomerId;
	}

	public String getQuoteEnquiryFrom() {
		return quoteEnquiryFrom;
	}

	public void setQuoteEnquiryFrom(String quoteEnquiryFrom) {
		this.quoteEnquiryFrom = quoteEnquiryFrom;
	}

	public Date getQuoteEnquiryDate() {
		return quoteEnquiryDate;
	}

	public void setQuoteEnquiryDate(Date quoteEnquiryDate) {
		this.quoteEnquiryDate = quoteEnquiryDate;
	}

	public Integer getQuoteQty() {
		return quoteQty;
	}

	public void setQuoteQty(Integer quoteQty) {
		this.quoteQty = quoteQty;
	}

	public BigDecimal getQuoteValue() {
		return quoteValue;
	}

	public void setQuoteValue(BigDecimal quoteValue) {
		this.quoteValue = quoteValue;
	}

	public Integer getQuoteCreatedBy() {
		return quoteCreatedBy;
	}

	public void setQuoteCreatedBy(Integer quoteCreatedBy) {
		this.quoteCreatedBy = quoteCreatedBy;
	}

	public Integer getQuoteUpdatedBy() {
		return quoteUpdatedBy;
	}

	public void setQuoteUpdatedBy(Integer quoteUpdatedBy) {
		this.quoteUpdatedBy = quoteUpdatedBy;
	}

	public Date getQuoteCreatedOn() {
		return quoteCreatedOn;
	}

	public void setQuoteCreatedOn(Date quoteCreatedOn) {
		this.quoteCreatedOn = quoteCreatedOn;
	}

	public Date getQuoteUpdatedOn() {
		return quoteUpdatedOn;
	}

	public void setQuoteUpdatedOn(Date quoteUpdatedOn) {
		this.quoteUpdatedOn = quoteUpdatedOn;
	}


	public Set<EQPTermsEntity> getTerms() {
		return terms;
	}


	public void setTerms(Set<EQPTermsEntity> terms) {
		this.terms = terms;
	}

}