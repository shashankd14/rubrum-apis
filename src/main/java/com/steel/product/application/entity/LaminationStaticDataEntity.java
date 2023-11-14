package com.steel.product.application.entity;

import javax.persistence.*;

@Entity
@Table(name = "lamination_charges_static_data")
public class LaminationStaticDataEntity {

	@Id
	@Column(name = "lamination_details_id")
	private Integer laminationDetailsId;

	@Column(name = "lamination_desc")
	private String laminationDetailsDesc;

	public Integer getLaminationDetailsId() {
		return laminationDetailsId;
	}

	public void setLaminationDetailsId(Integer laminationDetailsId) {
		this.laminationDetailsId = laminationDetailsId;
	}

	public String getLaminationDetailsDesc() {
		return laminationDetailsDesc;
	}

	public void setLaminationDetailsDesc(String laminationDetailsDesc) {
		this.laminationDetailsDesc = laminationDetailsDesc;
	}

}
