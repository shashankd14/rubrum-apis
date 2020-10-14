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

import com.steel.product.application.entity.IndividualCut;
import com.steel.product.application.service.IndividualCutService;

@RestController
@RequestMapping("/api/cuttingInstruction")
public class IndividualCutController {
	
	@Autowired
	private IndividualCutService individualCutService;
	
	@GetMapping("/list")
	public ResponseEntity<Object> findAll(){
		
		try{
			
			List<IndividualCut> cutList = individualCutService.findAll();
			
			return new ResponseEntity<Object>(cutList, HttpStatus.OK);
			
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getById")
	public ResponseEntity<Object> findById(@RequestParam("cutId")int theId) {

		try {

			IndividualCut theCut = individualCutService.findById(theId);
			return new ResponseEntity<Object>(theCut, HttpStatus.OK);

		} catch (Exception e) {

			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@PostMapping("/save")
	public ResponseEntity<Object> save(
			@RequestParam("parentCoilId") int parentCoilId,
			@RequestParam("noOfPieces") int noOfPieces,
			@RequestParam("weight") float weight,
			@RequestParam("wastage") float wastage,
			@RequestParam("damages") float damages) {

		IndividualCut individualCut = new IndividualCut();
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		try {

			individualCut.setParentCoilId(parentCoilId);
			individualCut.setNoOfPieces(noOfPieces);
			individualCut.setWeight(weight);
			individualCut.setWastage(wastage);
			individualCut.setDamages(damages);

			individualCut.setCreatedBy(1);
			individualCut.setUpdatedBy(1);
			individualCut.setCreatedOn(timestamp);
			individualCut.setUpdatedOn(timestamp);
			individualCut.setIsDeleted(false);

			individualCutService.save(individualCut);
			return new ResponseEntity<Object>("save success!!", HttpStatus.OK);
		} catch (Exception e) {

			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
	
	@DeleteMapping("/deleteById")
	public ResponseEntity<Object> deleteById(@RequestParam("cutId")int id) {
		
		try {
			
			individualCutService.deleteById(id);
			return new ResponseEntity<Object>("delete success!!", HttpStatus.OK);
		}catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}

}
