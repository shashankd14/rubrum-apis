package com.steel.product.application.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.log4j.Log4j2;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

@Log4j2
@Service
public class AWSS3ServiceImpl implements AWSS3Service {

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

    private S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    private S3Presigner s3Presigner() {
        return S3Presigner.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();
    }

    private File convertMultiPartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        }
        return file;
    }

    @Async
    public String uploadFile(MultipartFile multipartFile, String uniqueFileName) {
        String fileUrl = "";
        try {
            File file = convertMultiPartFileToFile(multipartFile);
            uploadFileToS3Bucket(bucketName, file, uniqueFileName);
            file.delete();
            fileUrl = url + "/" + bucketName + "/" + uniqueFileName;
        } catch (IOException | S3Exception e) {
            log.error("Error uploading file", e);
        }
        return fileUrl;
    }

    public String uploadFileToS3Bucket(String bucketName, File file, String uniqueFileName) {
        //String uniqueFileName = fileExtension+ "_" + file.getName();
        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(uniqueFileName)
                .build();
        s3Client().putObject(putRequest, RequestBody.fromFile(file));
        return uniqueFileName;
    }

    @Async
    public String uploadPDFFileToS3Bucket(String bucketName, File file, String partDetailsId) {
        String fileUrl = "";
        try {
            String uniqueFileName = (partDetailsId != null && !partDetailsId.isEmpty()) ? partDetailsId : file.getName();
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .build();
            s3Client().putObject(putRequest, RequestBody.fromFile(file));
            fileUrl = url + "/" + bucketName + "/" + uniqueFileName;
        } catch (S3Exception e) {
            log.error("Error uploading PDF file", e);
        }
        return fileUrl;
    }

    public String generatePresignedUrl(String fileName) {
        S3Presigner presigner = s3Presigner();
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(bucketPDFs)
                .key(fileName)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getRequest)
                .signatureDuration(Duration.ofHours(1))
                .build();
        return presigner.presignGetObject(presignRequest).url().toString();
    }
    
    public String persistFiles(String applicationJarPath, String stageName, String templateName, MultipartFile file) throws IOException {
        String path = applicationJarPath + File.separator + stageName + File.separator + templateName;
        String modifiedFileName = templateName + "_" + stageName + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        File jsonFile = new File(path + File.separator + modifiedFileName);

        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outStream = new FileOutputStream(jsonFile)) {

            byte[] buffer = new byte[1024]; // 1KB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }

        // Upload to S3
        uploadPDFFileToS3Bucket(bucketPDFs, jsonFile, modifiedFileName);

        return modifiedFileName;
    }
    public String persistQualityReportFiles(String applicationJarPath, String stageName, String templateName, MultipartFile file) {
        String modifiedFileName = "";
        try {
            String path = applicationJarPath + File.separator + stageName + File.separator + templateName;
            modifiedFileName = templateName + "_" + stageName + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();

            File jsonFile = new File(path + File.separator + modifiedFileName);

            try (InputStream inputStream = file.getInputStream();
                 FileOutputStream outStream = new FileOutputStream(jsonFile)) {

                byte[] buffer = new byte[1024]; // 1KB buffer
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outStream.write(buffer, 0, bytesRead);
                }
            }

            // Upload to S3 using SDK v2 method
            uploadPDFFileToS3Bucket(bucketPDFs + "/QualityReports", jsonFile, modifiedFileName);

        } catch (Exception e) {
            log.error("Error persisting quality report file", e);
        }

        return modifiedFileName;
    }
    public String persistTradingFiles(String applicationJarPath, String itemCode, MultipartFile file) throws IOException {
        String path = applicationJarPath + File.separator + itemCode;
        String modifiedFileName = itemCode + "_" + file.getOriginalFilename().replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

        File dir = new File(path);
        if (!dir.exists()) dir.mkdirs();

        File jsonFile = new File(path + File.separator + modifiedFileName);

        try (InputStream inputStream = file.getInputStream();
             FileOutputStream outStream = new FileOutputStream(jsonFile)) {

            byte[] buffer = new byte[1024]; // 1KB buffer
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }

        // Upload to S3 using SDK v2 method
        uploadPDFFileToS3Bucket(tradingBucket, jsonFile, modifiedFileName);

        return modifiedFileName;
    }

    public String generatePresignedUrlForTrading(String fileName) {
        S3Presigner presigner = s3Presigner();
        GetObjectRequest getRequest = GetObjectRequest.builder()
                .bucket(tradingBucket)
                .key(fileName)
                .build();
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .getObjectRequest(getRequest)
                .signatureDuration(Duration.ofHours(1))
                .build();
        return presigner.presignGetObject(presignRequest).url().toString();
    }
    
    public String downloadS3toLocalFile(String s3Key, String localPath) {
        S3Client s3 = s3Client(); // Your existing method to get S3Client
        try {
            s3.headObject(builder -> builder.bucket(bucketName).key(s3Key));
        } catch (software.amazon.awssdk.services.s3.model.NoSuchKeyException e) {
            //log.error("S3 object not found: " + s3Key, e);
            return null; // or throw a custom exception
        } catch (software.amazon.awssdk.services.s3.model.S3Exception e) {
            //log.error("Error accessing S3 for key: " + s3Key, e);
            return null; // or throw a custom exception
        }

        // If object exists, download it
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .build();

            s3.getObject(getRequest, Paths.get(localPath));
            log.info("File downloaded successfully: " + localPath);
        }  catch (SdkClientException e) {
            //System.err.println("SDK client error: " + e.getMessage());
            return localPath;
        } catch (Exception e) {
           // log.error("Error downloading file from S3: " + s3Key, e.getMessage());
            return localPath;
		}
        return localPath;
    }
    public String downloadPdfAsBase64(String fileName) {
        try {
            GetObjectRequest getRequest = GetObjectRequest.builder()
                    .bucket(bucketPDFs)
                    .key(fileName)
                    .build();

            try (InputStream inputStream = s3Client().getObject(getRequest);
                 ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

                byte[] data = new byte[8192];
                int bytesRead;

                while ((bytesRead = inputStream.read(data)) != -1) {
                    buffer.write(data, 0, bytesRead);
                }

                return Base64.getEncoder().encodeToString(buffer.toByteArray());
            }

        } catch (Exception e) {
            log.info("Error downloading PDF from S3 and converting to Base64. File: {}", fileName, e.getMessage());
            return null;
        }
    }
    
	public static void mainaa(String[] args) {
		String kk = new BCryptPasswordEncoder().encode("vIkraNt#mankar") ;
		System.out.println("Hi encoded value    "+kk);
		//kk = new BCryptPasswordEncoder().encode("admin@123") ;
		//System.out.println("Hi aknak22 =="+kk);		
	}

}
