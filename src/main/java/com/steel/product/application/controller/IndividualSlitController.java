package com.steel.product.application.controller;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.application.entity.IndividualSlit;
import com.steel.product.application.service.IndividualSlitService;

@RestController
@RequestMapping("/api/slittingInstruction")
public class IndividualSlitController {

	@Autowired
	private IndividualSlitService individualSlitService;
	
	
	@GetMapping("/list")
	public ResponseEntity<Object> findAll(){
		
		try {
			
			List<IndividualSlit> slits = individualSlitService.findAll();
			return new ResponseEntity<Object>(slits, HttpStatus.OK);
			
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	@GetMapping("/getById")
	public ResponseEntity<Object> findById(@RequestParam("slitId")int theId) {
		
		try {
			
			IndividualSlit slit = individualSlitService.findById(theId);
			return new ResponseEntity<Object>(slit, HttpStatus.OK);
			
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@PostMapping("/save")
	public ResponseEntity<Object> save(
			@RequestParam("parentCoilId") int parentCoilId,
			@RequestParam("noOfPieces") int noOfPieces,
			@RequestParam("weight") float weight,
			@RequestParam("wastage") float wastage,
			@RequestParam("damages") float damages) {
		
		
		IndividualSlit individualSlit = new IndividualSlit();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		try {
			
			individualSlit.setParentCoilId(parentCoilId);
			individualSlit.setNoOfPieces(noOfPieces);
			individualSlit.setWeight(weight);
			individualSlit.setWastage(wastage);
			individualSlit.setDamages(damages);
			
			individualSlit.setCreatedBy(1);
			individualSlit.setUpdatedBy(1);
			individualSlit.setCreatedOn(timestamp);
			individualSlit.setUpdatedOn(timestamp);
			individualSlit.setIsDeleted(false);
			
			individualSlitService.save(individualSlit);
			
			return new ResponseEntity<Object>("save success!!", HttpStatus.INTERNAL_SERVER_ERROR);
			
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@DeleteMapping("/deleteById")
	public ResponseEntity<Object> deleteById(@RequestParam("slitId")int theId) {
		
		try {
			
			individualSlitService.deleteById(theId);
			return new ResponseEntity<Object>("delete success!!", HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
