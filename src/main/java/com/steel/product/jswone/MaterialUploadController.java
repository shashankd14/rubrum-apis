package com.steel.product.jswone;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@RequestMapping("fileUpload")
@Tag(name = "File Upload", description = "File Upload")
public class MaterialUploadController {
/*
	@PostMapping(value = "/initVerify", produces = "application/json")
	@Operation
	public ResponseEntity<Object> initVerify(@Valid @ModelAttribute FileUploadInitiateRequest request)
			throws Exception, FileNotFoundException {
		log.info("******FileUploadController.initVerify*****");
		return null;//initiateService.initVerify(request);
	} */
}
