package com.steel.product.application.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class AWSS3ServiceImpl implements AWSS3Service {
 
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.url}")
    private String url;
    
    @Value("${aws.s3.bucketPDFs}")
    private String bucketPDFs;
    
    @Value("${aws.s3.tradingFiles}")
    private String tradingBucket;
    
    @Value("${aws.s3.bucket}")
    private String bucketName;
    
    @Value("${aws.access_key_id}")
    private String accessKey;
    
    @Value("${aws.secret_access_key}")
    private String secretKey;
    
    @Value("${aws.s3.region}")
    private String region;
    
    @Override
    // @Async annotation ensures that the method is executed in a different background thread 
    // but not consume the main thread.
    @Async
    public String uploadFile(final MultipartFile multipartFile) {
    	 String fileUrl="";
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket(bucketName, file);
            file.delete();  // To remove the file locally created in the project folder.
            fileUrl = url + "/" + bucketName + "/" + file;
        } catch (final AmazonServiceException ex) {
            ex.printStackTrace();
        }
        
        return fileUrl;
    }
 
    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(multipartFile.getOriginalFilename());
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (final IOException ex) {
            ex.printStackTrace();
        }
        return file;
    }
 
    public void uploadFileToS3Bucket(final String bucketName, final File file) {
        final String uniqueFileName = LocalDateTime.now() + "_" + file.getName();
        final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
        amazonS3.putObject(putObjectRequest);
    }
    
	@Override
	@Async
	public String uploadPDFFileToS3Bucket(final String bucketName, final File file, String partDetailsId) {
		String fileUrl ="";
		
		try {
			String uniqueFileName = file.getName();
			if (partDetailsId != null && partDetailsId.length() > 0) {
				uniqueFileName = partDetailsId;
			}
			final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
			amazonS3.putObject(putObjectRequest);
			fileUrl = url + "/" + bucketName + "/" + uniqueFileName;
		} catch (Exception e) {
			log.info("getMessage == "+e.getMessage());
		}
		return fileUrl;
	}

	@Override
	public String generatePresignedUrl(String fileName) {

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		
		final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(region).build();

		// Set the expiry time
		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketPDFs, fileName)
				.withMethod(HttpMethod.GET).withExpiration(expiration);
		//
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		//System.out.println("Pre-Signed URL: " + url.toString());

		return url.toString();
	}

	@Override
	public String persistFiles(String applicationJarPath, String stageName, String templateName,
			MultipartFile file) throws IOException {
		String path = applicationJarPath + File.separator + stageName + File.separator+ templateName;
		String modifiedFileName = templateName+"_"+stageName+"_"+ file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		String jsonFile = path + File.separator + modifiedFileName;
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
		
		uploadPDFFileToS3Bucket(bucketPDFs, new File(jsonFile), modifiedFileName);

		return modifiedFileName;
	}

	@Override
	public String persistQualityReportFiles(String applicationJarPath, String stageName, String templateName,
			MultipartFile file) {
		String modifiedFileName="";
		try {
			String path = applicationJarPath + File.separator + stageName + File.separator+ templateName;
			modifiedFileName = templateName+"_"+stageName+"_"+ file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
			String jsonFile = path + File.separator + modifiedFileName;
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
			
			uploadPDFFileToS3Bucket(bucketPDFs+"/QualityReports", new File(jsonFile), modifiedFileName);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return modifiedFileName;
	}
	
	public static void mainss(String[] args) {
		System.out.println("Hi aknak ==");
		String kk = new BCryptPasswordEncoder().encode("5699e5cb-8aac-4a39-bb61-413f18653051") ;
		System.out.println("Hi aknak =="+kk);
		kk = new BCryptPasswordEncoder().encode("admin@123") ;
		System.out.println("Hi aknak22 =="+kk);		
	}

	@Override
	public String persistTradingFiles(String applicationJarPath, String itemCode, 
			MultipartFile file) throws IOException {
		String path = applicationJarPath + File.separator + itemCode ;
		String modifiedFileName = itemCode+"_"+ file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
		String jsonFile = path + File.separator + modifiedFileName;
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
		
		uploadPDFFileToS3Bucket(tradingBucket, new File(jsonFile), modifiedFileName);

		return modifiedFileName;
	}

	@Override
	public String generatePresignedUrlForTrading(String fileName) {

		BasicAWSCredentials awsCreds = new BasicAWSCredentials(accessKey, secretKey);
		
		final AmazonS3 s3Client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(awsCreds)).withRegion(region).build();

		// Set the expiry time
		java.util.Date expiration = new java.util.Date();
		long expTimeMillis = expiration.getTime();
		expTimeMillis += 1000 * 60 * 60;
		expiration.setTime(expTimeMillis);

		GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(tradingBucket, fileName).withMethod(HttpMethod.GET).withExpiration(expiration);
		URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);
		return url.toString();
	}

	
}