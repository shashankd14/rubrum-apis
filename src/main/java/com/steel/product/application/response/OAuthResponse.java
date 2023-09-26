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

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

/******************************************************************************/
/**
 * Data for oauth response
 * 
 */
@Data
public class OAuthResponse {

	@JsonAlias("access_token")
	private String accessToken;

	@JsonAlias("token_type")
	private String tokenType;

	@JsonAlias("refresh_token")
	private String refreshToken;

	@JsonAlias("expires_in")
	private long expiresIn;

	@JsonAlias("scope")
	private String scope;

}
