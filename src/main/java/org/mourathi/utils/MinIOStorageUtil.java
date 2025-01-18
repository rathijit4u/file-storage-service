package org.mourathi.utils;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class MinIOStorageUtil {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    public static final String MINIO_SERVER_URL = "http://localhost:9000";

    private final MinioClient minioClient;

    public MinIOStorageUtil(){
        this.minioClient =
                MinioClient.builder()
                        .endpoint(MINIO_SERVER_URL)
                        .credentials(System.getenv("accessKey"), System.getenv("secretKey"))
                        .build();
    }

    public ObjectWriteResponse uploadObject(InputStream inputStream, String fileName, String contentType, long size, String bucketName) {

        try {
            // Upload input stream with headers and user metadata.
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Amz-Storage-Class", "REDUCED_REDUNDANCY");
            Map<String, String> userMetadata = new HashMap<>();
            userMetadata.put("File-Type", contentType);
            this.makeBucket(bucketName);
            return minioClient.putObject(
                    PutObjectArgs.builder().bucket(bucketName).object(fileName).stream(
                                    inputStream, size, -1)
                            .headers(headers)
                            .contentType(contentType)
                            .userMetadata(userMetadata)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public GetObjectResponse getObject(String objectName, String bucketName) {
        try {
            // Read data from stream
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteObject(String objectName, String bucketName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void makeBucket(String name)  {
        try {
            boolean found = this.isBucketExists(name);
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
            } else {
                this.logger.info("Bucket '{}' already exists.", name);
            }
        } catch (ErrorResponseException | XmlParserException | InsufficientDataException | InternalException |
                 InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
                 ServerException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBucket(String name) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(name).build());
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    public Bucket getBucket(String name) {
        try {
            Optional<Bucket> optionalBucket = minioClient.listBuckets().stream()
                    .filter(bucket -> bucket.name().equalsIgnoreCase(name))
                    .findFirst();
            return optionalBucket.orElse(null);
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPresignedUrl(String bucketName, String fileName){
        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName).object(fileName).method(Method.PUT)
                .expiry(1, TimeUnit.DAYS).build();
        try {
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            throw new RuntimeException(e);
        }

    }
    private boolean isBucketExists(String name) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }
}