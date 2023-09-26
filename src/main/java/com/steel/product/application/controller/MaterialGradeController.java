package com.steel.product.application.controller;

import com.steel.product.application.entity.MaterialGrade;
import com.steel.product.application.service.MaterialGradeService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@Tag(name = "Material Grade", description = "Material Grade")
@RequestMapping({"/materialGrade"})
public class MaterialGradeController {
	@Autowired
	private MaterialGradeService materialGradeService;

	@GetMapping("/getById/{gradeId}")
	public ResponseEntity<Object> getById(@PathVariable int gradeId) {
		try {
			MaterialGrade materialGrade = this.materialGradeService.getById(gradeId);
			return new ResponseEntity(MaterialGrade.valueOf(materialGrade), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping("/list")
	public ResponseEntity<Object> getAll() {
		try {
			List<MaterialGrade> matGradeList = this.materialGradeService.getAll();
			return new ResponseEntity<Object>(matGradeList.stream().map(g -> MaterialGrade.valueOf(g)).collect(Collectors.toList()), HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping("/getByMaterialId/{materialId}")
	public ResponseEntity<Object> getByMaterialId(@PathVariable int materialId){
		try {
			List<MaterialGrade> gradeList = materialGradeService.getByMaterialId(materialId);
			return new ResponseEntity<Object>(gradeList.stream().map(g -> MaterialGrade.valueOf(g)).collect(Collectors.toList()), HttpStatus.OK);
			
		} catch(Exception e) {
			return new ResponseEntity<Object>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
}
