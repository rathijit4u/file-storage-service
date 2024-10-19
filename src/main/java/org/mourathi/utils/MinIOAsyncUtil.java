package org.mourathi.utils;

import com.google.common.collect.HashMultimap;
import io.minio.*;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.XmlParserException;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Part;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class MinIOAsyncUtil {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String MINIO_SERVER_URL = "http://localhost:9000";
    private final CustomMinioClient minioAsyncClient;

    public MinIOAsyncUtil() {
        this.minioAsyncClient = new CustomMinioClient(MinioAsyncClient.builder()
                .endpoint(MINIO_SERVER_URL)
                .credentials(System.getenv("accessKey"), System.getenv("secretKey"))
                .build());
    }

    public CompletableFuture<ObjectWriteResponse> uploadObject(InputStream inputStream, String fileName, String contentType, long size, String bucketName) {
        try {
            // Upload input stream with headers and user metadata.
            Map<String, String> headers = new HashMap<>();
            headers.put("X-Amz-Storage-Class", "REDUCED_REDUNDANCY");
            Map<String, String> userMetadata = new HashMap<>();
            userMetadata.put("File-Type", contentType);
            this.makeBucket(bucketName);
            return minioAsyncClient.putObject(
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

    public void downloadObject(String objectName, String outputFileName, String bucketName) {
        // Download object given the bucket, object name and output file name
        try {
            CompletableFuture<Void> future = minioAsyncClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .filename(outputFileName)
                            .build());
            future.get();
        } catch (Exception e) {
            this.logger.error(e.getLocalizedMessage());
            throw new RuntimeException(e);
        }
    }

    public InputStream getFileStream(String objectName, String bucketName) {
        try {
            // Read data from stream
            return minioAsyncClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .build()).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteObject(String objectName, String bucketName) {
        try {
            minioAsyncClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build()).get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public void makeBucket(String name)  {
        try {
            boolean found = this.isBucketExists(name);
            if (!found) {
                minioAsyncClient.makeBucket(MakeBucketArgs.builder().bucket(name).build());
            } else {
                this.logger.info("Bucket '{}' already exists.", name);
            }
        } catch (XmlParserException | InsufficientDataException | InternalException |
                 InvalidKeyException | IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Bucket> listBuckets() {
        try {
            return minioAsyncClient.listBuckets().get();
        } catch (XmlParserException | NoSuchAlgorithmException | IOException | InvalidKeyException | InternalException |
                 InsufficientDataException | ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void removeBucket(String name) {
        try {
            minioAsyncClient.removeBucket(RemoveBucketArgs.builder().bucket(name).build());
        } catch (XmlParserException | NoSuchAlgorithmException |
                 IOException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isBucketExists(String name) {
        try {
            return minioAsyncClient.bucketExists(BucketExistsArgs.builder().bucket(name).build()).get();
        } catch (XmlParserException | NoSuchAlgorithmException |
                 IOException | InvalidKeyException | InternalException |
                 InsufficientDataException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    public String getUploadObjectUrl(String objectName, String bucketName, Map<String, String> reqParams) {

        try {
            return minioAsyncClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(1, TimeUnit.DAYS)
                            .extraQueryParams(reqParams)
                            .build());
        } catch (Exception e) {
            this.logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public Map<String, Object> initMultiPartUpload(String objectName, String bucketName, int partCount, String contentType) {
        Map<String, Object> result = new HashMap<>();
        try {
            if (contentType.isBlank()) {
                contentType = "application/octet-stream";
            }
            HashMultimap<String, String> headers = HashMultimap.create();
            headers.put("Content-Type", contentType);
            String uploadId = minioAsyncClient.initMultiPartUpload(bucketName, null
                    , objectName, headers, null);

            result.put("uploadId", uploadId);
            List<String> partList = new ArrayList<>();

            Map<String, String> reqParams = new HashMap<>();
            //reqParams.put("response-content-type", "application/json");
            reqParams.put("uploadId", uploadId);
            for (int i = 1; i <= partCount; i++) {
                reqParams.put("partNumber", String.valueOf(i));
                String uploadUrl = minioAsyncClient.getPresignedObjectUrl(
                        GetPresignedObjectUrlArgs.builder()
                                .method(Method.PUT)
                                .bucket(bucketName)
                                .object(objectName)
                                .expiry(1, TimeUnit.DAYS)
                                .extraQueryParams(reqParams)
                                .build());
                partList.add(uploadUrl);
            }
            result.put("uploadUrls", partList);
        } catch (Exception e) {
            this.logger.error(e.getLocalizedMessage());
            return null;
        }

        return result;
    }

    public boolean mergeMultipartUpload(String objectName, String bucketName, String uploadId) {
        try {

            Part[] parts = new Part[1000];
            ListPartsResponse partResult = minioAsyncClient.listMultipart(bucketName, null, objectName, 1000, 0, uploadId, null, null);
            int partNumber = 0;
            for (Part part : partResult.result().partList()) {
                parts[partNumber] = new Part(++partNumber, part.etag());
            }
            minioAsyncClient.mergeMultipartUpload(bucketName, null, objectName, uploadId, parts, null, null);
        } catch (Exception e) {
            this.logger.error(e.getLocalizedMessage());
            return false;
        }

        return true;
    }

}
