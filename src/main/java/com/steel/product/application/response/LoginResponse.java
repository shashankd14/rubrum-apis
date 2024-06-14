/*******************************************************************************
 * Copyright (c) 2021 Techurate Systems Pvt Ltd.
 * All rights reserved. 
 * 
 * No part of this work may be reproduced, stored in a retrieval system, adopted
 * or transmitted in any form or by any means,electronic,mechanical,photographic,
 * graphic,optic recording or otherwise, translated in any language without the 
 * prior written permission of Techurate Systems Pvt Ltd.
 * 
 *******************************************************************************/

package com.steel.product.application.response;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.steel.product.application.dto.admin.AdminMenuDto;

import lombok.Builder;
import lombok.Data;

/******************************************************************************/
/**
 * Data for login response
 * 
 */
@Data
@Builder
public class LoginResponse {

	private int userId;

	private String userName;

	private Date lastLoginTime;

	private String access_token;

	private String refresh_token;

	private String token_type;

	//private String loginType;

	private long expires_in;

	private List<AdminMenuDto> menusList = new ArrayList<>();

}
