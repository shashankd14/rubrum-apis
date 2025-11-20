package com.steel.product.application.service;

import java.io.File;
import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AWSS3Service {

	String uploadFile(MultipartFile multipartFile, String fileExtension);

	String uploadFileToS3Bucket(String bucketName, File file, String fileExtension);

	String uploadPDFFileToS3Bucket(String bucketName, File file, String partDetailsId);

	String generatePresignedUrl(String fileName);

	String persistFiles(String applicationJarPath, String stageName, String templateName, MultipartFile file)
			throws IOException;

	String persistQualityReportFiles(String applicationJarPath, String stageName, String templateName,
			MultipartFile file);

	String persistTradingFiles(String applicationJarPath, String itemCode, MultipartFile file) throws IOException;

	String generatePresignedUrlForTrading(String fileName);

	String downloadS3toLocalFile(String s3Key, String localPath);
}
