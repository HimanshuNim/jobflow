package com.himanshu.jobflow.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.InputStream;

@Service
public class BackblazeStorageService {

    private final S3Client s3Client;

    @Value("${b2.bucket-name}")
    private String bucketName;

    public BackblazeStorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public void uploadFile(
            String objectKey,
            MultipartFile file) throws IOException {

        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .contentType(file.getContentType())
                .build();

        s3Client.putObject(
                request,
                RequestBody.fromInputStream(
                        file.getInputStream(),
                        file.getSize()
                )
        );
    }

    public InputStream downloadFile(String objectKey) {

        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        return s3Client.getObject(request);
    }

    public void deleteFile(String objectKey) {

        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(objectKey)
                .build();

        s3Client.deleteObject(request);
    }
}