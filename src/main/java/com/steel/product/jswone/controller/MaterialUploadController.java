package com.steel.product.jswone.controller;

import java.io.FileNotFoundException;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.steel.product.jswone.request.MaterialUploadRequest;
import com.steel.product.jswone.service.MaterialUploadService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("materialupload")
@Tag(name = "Material Upload", description = "Material Upload")
public class MaterialUploadController {

	@Autowired
	MaterialUploadService materialUploadService;

	@PostMapping(value = "/upload", produces = "application/json")
	@Operation
	public ResponseEntity<Object> initVerify(@Valid @ModelAttribute MaterialUploadRequest request)
			throws Exception, FileNotFoundException {
		log.info("******MaterialUploadController.initVerify*****");
		return materialUploadService.upload(request);
	}

}
