package com.steel.product.jswone.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.jswone.request.SearchRequest;
import com.steel.product.jswone.response.BranchMasterDTO;
import com.steel.product.jswone.service.BranchMasterService;

import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@CrossOrigin
@Tag(name = "Branch Master", description = "Branch Master")
@RequestMapping({ "/branch" })
public class BranchMasterController {

	@Autowired
	private BranchMasterService branchMasterService;

	@PostMapping({ "/list" })
	public ResponseEntity<Object> getCategoryList(@RequestBody SearchRequest searchPageRequest) {
		List<BranchMasterDTO> pageResult = branchMasterService.getList(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	@PostMapping({ "/location" })
	public ResponseEntity<Object> getLocationList(@RequestBody SearchRequest searchPageRequest) {
		List<BranchMasterDTO> pageResult = branchMasterService.getList(searchPageRequest);
		return new ResponseEntity<Object>(pageResult, HttpStatus.OK);
	}

	
}
