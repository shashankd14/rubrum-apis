package com.steel.product.application.entity;

import javax.persistence.*;

import lombok.Data;

@Data
@Entity
@Table(name = "fg_label_dtls")
public class FGLabelEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "fg_id")
	private Integer fgId;

	@Column(name = "inwardentryid")
	private Integer inwardentryid;

	@Column(name = "label_seq")
	private Integer labelSeq;

	@Column(name = "label_name")
	private String labelName;

	@Column(name = "label_s3_url")
	private String labelS3Url;

}
