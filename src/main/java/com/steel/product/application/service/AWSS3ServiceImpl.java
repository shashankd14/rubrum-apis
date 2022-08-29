package com.steel.product.application.service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
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

@Service
public class AWSS3ServiceImpl implements AWSS3Service {
 
    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.url}")
    private String url;
    
    @Value("${aws.s3.bucketPDFs}")
    private String bucketPDFs;
    
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
		String uniqueFileName = file.getName();
		if (partDetailsId != null && partDetailsId.length() > 0) {
			uniqueFileName = partDetailsId;
		}
		final PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uniqueFileName, file);
		amazonS3.putObject(putObjectRequest);
		String fileUrl = url + "/" + bucketName + "/" + uniqueFileName;
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

}