package com.steel.product.application.service;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

	public String uploadFile(MultipartFile multipartFile);

	public String uploadPDFFileToS3Bucket(String bucketName, File file,  String partDetailsId);

	public String generatePresignedUrl(String fileName);

	String persistFiles(String applicationJarPath, String stageName, String templateName, MultipartFile file)
			throws IOException;

}
