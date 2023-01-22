package com.steel.product.application.dto.admin;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
@Data
public class CreateUserRequest {

	private int userId;

	private String userName;

	private String firstName;

	private String lastName;

	private String password;

	private String emailId;

	private String userDataVisible;

	private short enabled;

	private String mobileNo;

	private List<Integer> roleList = new ArrayList<>();

	private List<Integer> partyList = new ArrayList<>();

}
