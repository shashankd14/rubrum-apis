package com.steel.product.application.entity;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;
import javax.persistence.Table;

import com.steel.product.application.dto.admin.AdminMenuDto;

import lombok.Data;

@Entity
@SqlResultSetMappings({ 
    @SqlResultSetMapping(name = "findMenus",
            classes = {@ConstructorResult(targetClass=AdminMenuDto.class,
            columns = {@ColumnResult(name="menuId", type = Integer.class),
                       @ColumnResult(name="parentMenuId", type = Integer.class),
                       @ColumnResult(name="menuName", type = String.class),
                       @ColumnResult(name="displayOrder", type = Integer.class),
                       @ColumnResult(name="permission", type = String.class),
                       @ColumnResult(name="menuKey", type = String.class)
            })} )
    })
@NamedNativeQueries(value = {
    
        @NamedNativeQuery(name = "findMenu", query = ""
        		+"select am.menu_id as menuId,am.parent_menu_id as parentMenuId,am.menu_name as menuName,am.menukey as menuKey,am.display_order as displayOrder,group_concat( armm.op_permission separator ',') AS permission from admin_menus am,admin_role_menu_map armm,admin_role amr,admin_user_role_mapping aurm where armm.menu_id = am.menu_id and armm.role_id = amr.role_id and amr.role_id = aurm.role_id and aurm.user_id = :userId group by am.menu_id , am.parent_menu_id, am.menu_name,am.menukey,am.display_order" ,
        resultSetMapping = "findMenus"),
        @NamedNativeQuery(name = "findAllMenu", query = ""
                +"select am.menu_id as menuId,am.menukey as menuKey,am.parent_menu_id as parentMenuId,am.menu_name as menuName,am.display_order as displayOrder,am.default_permission as permission from admin_menus am where am.active_flag = 'Y'",
                resultSetMapping = "findMenus")
        
    })
@Table(name = "ADMIN_MENUS")
@Data
public class AdminMenuEntity {

	@Id
	@Column(name = "menu_id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
	@Column(name = "parent_menu_id")
	private int parentMenuId;
	
	@Column(name = "menu_name")
	private String menuName;
	
	@Column(name = "active_flag", length = 1)
	private String activeFlag;
	
	@Column(name = "display_order")
	private int displayOrder;
	
	@Column(name="default_permission")
	private String defaultPermission;
	
	@Column(name="menukey")
	private String menukey;
	
	
	
}
