package org.mourathi.service.s3;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class MinIOServiceImpl implements MinIOService {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MinioClient minioClient;
    @Value("${minio.put-object-part-size}")
    private Long putObjectPartSize;

    public MinIOServiceImpl(MinioClient client){
       this.minioClient = client;
    }

    @Override
    public void saveChunk(MultipartFile file, UUID uuid, String bucketName) throws Exception {
        minioClient.putObject(
                PutObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .object(uuid.toString())
                        .stream(file.getInputStream(), file.getSize(), putObjectPartSize)
                        .build()
        );
    }

    @Override
    public InputStream getInputStreamByChunk(UUID uuid, long offset, long length, String bucketName) throws Exception {
        return minioClient.getObject(
                GetObjectArgs
                        .builder()
                        .bucket(bucketName)
                        .offset(offset)
                        .length(length)
                        .object(uuid.toString())
                        .build());
    }

    @Override
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
                                    inputStream, size, putObjectPartSize)
                            .headers(headers)
                            .contentType(contentType)
                            .userMetadata(userMetadata)
                            .build());
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public GetObjectResponse getObject(String objectName, String bucketName) {
        try {
            // Read data from stream
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build());
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteObject(String objectName, String bucketName) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build());
        } catch (Exception e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
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
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Bucket> listBuckets() {
        try {
            return minioClient.listBuckets();
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeBucket(String name) {
        try {
            minioClient.removeBucket(RemoveBucketArgs.builder().bucket(name).build());
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public Bucket getBucket(String name) {
        try {
            Optional<Bucket> optionalBucket = minioClient.listBuckets().stream()
                    .filter(bucket -> bucket.name().equalsIgnoreCase(name))
                    .findFirst();
            return optionalBucket.orElse(null);
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPresignedDownloadUrl(String bucketName, String fileName){
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("response-content-disposition", "attachment; filename=\"" + fileName + "\"");
        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName).object(fileName).method(Method.GET)
                .expiry(1, TimeUnit.DAYS).extraQueryParams(queryParams).build();
        try {
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getPresignedUploadUrl(String bucketName, String fileName){
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("response-content-disposition", "attachment; filename=\"" + fileName + "\"");
        GetPresignedObjectUrlArgs getPresignedObjectUrlArgs = GetPresignedObjectUrlArgs.builder()
                .bucket(bucketName).object(fileName).method(Method.PUT)
                .expiry(1, TimeUnit.DAYS).extraQueryParams(queryParams).build();
        try {
            return minioClient.getPresignedObjectUrl(getPresignedObjectUrlArgs);
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | XmlParserException |
                 ServerException e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isBucketExists(String name) {
        try {
            return this.minioClient.bucketExists(BucketExistsArgs.builder().bucket(name).build());
        } catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidKeyException |
                 InvalidResponseException | IOException | NoSuchAlgorithmException | ServerException |
                 XmlParserException e) {
            this.logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }

}