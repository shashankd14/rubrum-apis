package com.steel.product.application.controller;

import com.steel.product.application.entity.MaterialGrade;
import com.steel.product.application.service.MaterialGradeService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@RequestMapping({"/api/materialGrade"})
public class MaterialGradeController {
	@Autowired
	private MaterialGradeService materialGradeService;

	@GetMapping("/getById")
	public ResponseEntity<Object> getById(int gradeId) {
		try {
			MaterialGrade materialGrade = this.materialGradeService.getById(gradeId);
			return new ResponseEntity(materialGrade, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/getAll")
	public ResponseEntity<Object> getAll() {
		try {
			List<MaterialGrade> matGradeList = this.materialGradeService.getAll();
			return new ResponseEntity<Object>(matGradeList, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getByMaterialId")
	public ResponseEntity<Object> getByMaterialId(@RequestParam int materialId){
		
		try {
			
			List<MaterialGrade> gradeList = materialGradeService.getByMaterialId(materialId);
			return new ResponseEntity<Object>(gradeList, HttpStatus.OK);
			
		} catch(Exception e) {
			
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
