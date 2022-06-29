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

package com.steel.product.application.dto;

import lombok.Data;

/******************************************************************************/
/**
 * Data class for login request
 *
 */
@Data
public class LoginRequest {
	
	private String userName;

	private String password;
}
