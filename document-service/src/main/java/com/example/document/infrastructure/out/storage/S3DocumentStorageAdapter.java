package com.example.document.infrastructure.out.storage;

import com.example.document.application.port.out.DocumentStoragePort;
import com.example.document.domain.exception.DocumentStorageException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * Adapter sortant : implemente DocumentStoragePort au-dessus d'AWS S3.
 */
@Component
@RequiredArgsConstructor
public class S3DocumentStorageAdapter implements DocumentStoragePort {

    private final S3Client s3Client;

    @Value("${document.storage.aws-s3-bucket}")
    private String awsS3Bucket;


    @Override
    public String store(String filename, InputStream content, long sizeInBytes) {
        String storageKey = UUID.randomUUID() + "-" + filename;

        try (InputStream in = content) {
            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(awsS3Bucket)
                    .key(storageKey)
                    .contentLength(sizeInBytes)
                    .build();

            s3Client.putObject(request, RequestBody.fromInputStream(in, sizeInBytes));

            return storageKey;
        } catch (IOException e) {
            throw new DocumentStorageException("Failed to read file content for upload: " + filename, e);
        } catch (S3Exception e) {
            throw new DocumentStorageException("Failed to store file in S3: " + filename, e);
        }
    }

    @Override
    public InputStream retrieve(String storageKey) {
        try {
            GetObjectRequest request = GetObjectRequest.builder()
                    .bucket(awsS3Bucket)
                    .key(storageKey)
                    .build();

            return s3Client.getObject(request);
        } catch (S3Exception e) {
            throw new DocumentStorageException("Failed to retrieve file from S3: " + storageKey, e);
        }
    }

    @Override
    public void delete(String storageKey) {
        try {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                    .bucket(awsS3Bucket)
                    .key(storageKey)
                    .build();

            s3Client.deleteObject(request);
        } catch (S3Exception e) {
            throw new DocumentStorageException("Failed to delete file from S3: " + storageKey, e);
        }
    }
}
