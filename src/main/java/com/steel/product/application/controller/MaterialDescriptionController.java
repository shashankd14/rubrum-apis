package com.steel.product.application.controller;

import com.steel.product.application.dto.material.MaterialRequestDto;
import com.steel.product.application.dto.material.MaterialResponseDetailsDto;
import com.steel.product.application.entity.Material;
import com.steel.product.application.service.MaterialDescriptionService;

import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
//@CrossOrigin(origins = {"http://localhost:3001"})
//@CrossOrigin(origins = {"http://rubrum-frontend.s3-website.ap-south-1.amazonaws.com"})
@CrossOrigin
@Tag(name = "Material Description", description = "Material Description")
@RequestMapping({ "/material" })
public class MaterialDescriptionController {

	@Autowired
	private MaterialDescriptionService matDescSvc;

	@PostMapping({ "/save" })
	public ResponseEntity<Object> saveMatDesc(@RequestBody MaterialRequestDto materialRequestDto) {
		try {
			Material material = matDescSvc.findByDesc(materialRequestDto.getMaterial());
			if(material!=null && material.getDescription().equalsIgnoreCase(materialRequestDto.getMaterial())) {
				return new ResponseEntity<>("Material Desc Already exists.", HttpStatus.BAD_REQUEST);
			}
			matDescSvc.saveMatDesc(materialRequestDto);
			return new ResponseEntity<>("Material Saved Successfully", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@PutMapping({ "/update" })
	public ResponseEntity<Object> updateMaterial(@RequestBody MaterialRequestDto materialRequestDto) {
		try {
			
			Material material = matDescSvc.findByDesc(materialRequestDto.getMaterial());
			if(material!=null && materialRequestDto.getMaterial().equalsIgnoreCase(material.getDescription())  
					 && material.getMatId() != materialRequestDto.getMatId() ) {
				return new ResponseEntity<>("Material Desc Already exists.", HttpStatus.BAD_REQUEST);
			}
			
			matDescSvc.saveMatDesc(materialRequestDto);
			return new ResponseEntity<>("Material Updated Successfully", HttpStatus.OK);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>("Unknown error", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping({ "/list" })
	public List<MaterialResponseDetailsDto> getAllMatDesc() {
		return this.matDescSvc.getAllMatDesc();
	}

	@GetMapping({ "/getById/{matId}" })
	public MaterialResponseDetailsDto getMatById(@PathVariable int matId) {
		return Material.valueOfMat(this.matDescSvc.getMatById(matId));
	}
}
