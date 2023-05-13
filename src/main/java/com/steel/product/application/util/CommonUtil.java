package com.steel.product.application.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.steel.product.application.dao.UserRepository;
import com.steel.product.application.entity.AdminUserEntity;
import com.steel.product.application.service.AWSS3Service;

@Service
public class CommonUtil {

	static Logger logger = LoggerFactory.getLogger(CommonUtil.class);
	
    @Value("${aws.s3.bucketPDFs}")
    private String bucketName;

	private AWSS3Service awsS3Service; 

    @Autowired
    private UserRepository userDetailsRepository;
	
	public String persistFiles(String applicationJarPath, String stageName, String templateName,
			MultipartFile file) throws IOException {
		String path = applicationJarPath + File.separator + stageName + File.separator+ templateName;
		String jsonFile = path + File.separator + file.getOriginalFilename();
		File dir = new File(path);
		if (!dir.exists())
			dir.mkdirs();
		InputStream inputStream = file.getInputStream();
		FileOutputStream outStream = new FileOutputStream(new File(jsonFile));
		byte[] contents = new byte[inputStream.available()];
		int length;
		while ((length = inputStream.read(contents)) > 0) {
			outStream.write(contents, 0, length);
		}
		inputStream.close();
		outStream.close();
		
		String fileUrl = awsS3Service.uploadPDFFileToS3Bucket(bucketName, dir, file.getOriginalFilename());

		return fileUrl;
	}

	public int getUserId() {
		int userId = 0;
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		UserDetails userDetail = (UserDetails) authentication.getPrincipal();
		Optional<AdminUserEntity> userEntity = userDetailsRepository.findByUserName(userDetail.getUsername());
		if (userEntity.isPresent()) {
			userId = userEntity.get().getUserId();
		}

		return userId;
	}

}