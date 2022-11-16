package com.steel.product.application.entity;

import com.steel.product.application.dto.status.StatusDto;
import javax.persistence.*;

@Entity
@Table(name = "product_status")
public class Status {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "statusid")
	private int statusId;

	@Column(name = "statusname")
	private String statusName;

	public int getStatusId() {
		return this.statusId;
	}

	public void setStatusId(int statusId) {
		this.statusId = statusId;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
	}

	public static StatusDto valueOf(Status status){
		StatusDto statusDto = new StatusDto();
		statusDto.setStatusId(status.getStatusId());
		statusDto.setStatusName(status.getStatusName());
		return statusDto;
	}

}
