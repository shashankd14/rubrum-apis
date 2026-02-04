package com.steel.product.jswone.entity;

public enum StatusType {

	SO_CREATED("SO_CREATED"), SO_APPROVED("SO_APPROVED"), SO_HOLD("SO_HOLD"), SO_REJECTED("SO_REJECTED"),
	CP_PLAN_DRAFT("CP_PLAN_DRAFT"), CP_PLAN_ISSUED("CP_PLAN_ISSUED"), CP_PLAN_INPROGRESS("CP_PLAN_INPROGRESS"),
	CP_PAN_COMPLETED("CP_PAN_COMPLETED"),;

	private String type;

	private StatusType(String type) {
		this.setType(type);
	}

	public String value() {
		return name();
	}

	public static StatusType fromValue(String v) {
		return valueOf(v);
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
}
