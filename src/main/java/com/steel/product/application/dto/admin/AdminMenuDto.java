package com.steel.product.application.dto.admin;

import lombok.Data;

@Data
public class AdminMenuDto {
	
	private int menuId;
	private String menuName;
	private int parentMenuId;
	private String permission;
	private int displayOrder;
	private String menuKey;
	
	public AdminMenuDto() {}
	
	public AdminMenuDto(Integer menuId, Integer parentMenuId, String menuName,  Integer displayOrder, String permission, String menuKey) {
		this.menuId = menuId;
		this.parentMenuId = parentMenuId;
		this.menuName = menuName;
		this.permission = permission;
		this.displayOrder = displayOrder;
		this.menuKey = menuKey;
	}
	
	public AdminMenuDto(Integer menuId, Integer parentMenuId, String menuName,  Integer displayOrder) {
		this.menuId = menuId;
		this.parentMenuId = parentMenuId;
		this.menuName = menuName;
		this.displayOrder = displayOrder;
	}
	
	
	

}
