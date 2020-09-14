package com.steel.product.application.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
@Table(name = "product_tblinwarddocs")
public class InwardDoc {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "docid")
	private int docId;

	@Column(name = "docurl")
	private String docUrl;

	@JsonManagedReference
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "inwardid")
	private InwardEntry inwardEntry;

	public int getDocId() {
		return docId;
	}

	public void setDocId(int docId) {
		this.docId = docId;
	}

	public String getDocUrl() {
		return docUrl;
	}

	public void setDocUrl(String docUrl) {
		this.docUrl = docUrl;
	}

	public InwardEntry getInwardEntry() {
		return inwardEntry;
	}

	public void setInwardEntry(InwardEntry inwardEntry) {
		this.inwardEntry = inwardEntry;
	}

	@Override
	public String toString() {
		return "InwardDoc [docId=" + docId + ", docUrl=" + docUrl + ", inwardEntry=" + inwardEntry + "]";
	}

}
