package com.example.document.adapter.out.storage;

import com.example.document.application.port.out.DocumentStoragePort;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.InputStream;

/**
 * Adapter sortant : implementera DocumentStoragePort au-dessus d'AWS S3.
 * <p>
 * NE PAS IMPLEMENTER S3 ICI. Structure preparee uniquement.
 * <p>
 * Flux cible :
 * <pre>
 * DocumentApplicationService
 *           |
 *           v
 * DocumentStoragePort
 *           |
 *           v
 * S3DocumentStorageAdapter
 *           |
 *           v
 *         AWS S3
 * </pre>
 * TODO — business implementation : a decouvrir et ecrire soi-meme —
 * AWS SDK (software.amazon.awssdk:s3), S3Client, credentials, region,
 * bucket, permissions IAM, upload / download / delete d'objets.
 */
@Component
public class S3DocumentStorageAdapter implements DocumentStoragePort {

    @Value("${document.storage.aws-region:}")
    private String awsRegion;

    @Value("${document.storage.aws-s3-bucket:}")
    private String awsS3Bucket;

    @Override
    public String store(String filename, InputStream content, long sizeInBytes) {
        throw new UnsupportedOperationException("TODO — business implementation (AWS S3)");
    }

    @Override
    public InputStream retrieve(String storageKey) {
        throw new UnsupportedOperationException("TODO — business implementation (AWS S3)");
    }

    @Override
    public void delete(String storageKey) {
        throw new UnsupportedOperationException("TODO — business implementation (AWS S3)");
    }
}
