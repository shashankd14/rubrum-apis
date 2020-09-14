package com.steel.product.application.service;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

	public String uploadFile(MultipartFile multipartFile);

}
