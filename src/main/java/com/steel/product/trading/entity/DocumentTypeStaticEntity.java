package com.steel.product.trading.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "trading_doctype_static")
@Data
public class DocumentTypeStaticEntity {

	@Id
	@Column(name = "doc_id")
	private Integer docId;

	@Column(name = "doc_name")
	private String docName;

}