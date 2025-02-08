package org.mourathi.service.s3;

import io.minio.GetObjectResponse;
import io.minio.ObjectWriteResponse;
import io.minio.messages.Bucket;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.UUID;

public interface MinIOService {
    void saveChunk(MultipartFile file, UUID uuid, String bucketName) throws Exception;

    InputStream getInputStreamByChunk(UUID uuid, long offset, long length, String bucketName) throws Exception;

    ObjectWriteResponse uploadObject(InputStream inputStream, String fileName, String contentType, long size, String bucketName);

    GetObjectResponse getObject(String objectName, String bucketName);

    void deleteObject(String objectName, String bucketName);

    void makeBucket(String name);

    List<Bucket> listBuckets();

    void removeBucket(String name);

    Bucket getBucket(String name);

    String getPresignedDownloadUrl(String bucketName, String fileName);

    String getPresignedUploadUrl(String bucketName, String fileName);

    boolean isBucketExists(String name);
}
