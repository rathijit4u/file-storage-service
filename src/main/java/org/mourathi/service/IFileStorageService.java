package org.mourathi.service;


import io.minio.GetObjectResponse;
import org.mourathi.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IFileStorageService {
    FileMetadata uploadFile(String requestUri, String bucket, MultipartFile file) throws IOException;

    GetObjectResponse getFileStream(String fileName, String bucket);

    List<FileMetadata> getAllFileMetadata(int limit);

    List<FileMetadata> getAllFileMetadataForBucket(String bucketName, int page, int size, String sort, String sortOrder);

    FileMetadata getFileMetadata(String bucketName, String fileName);

    FileMetadata deleteFileObject(String bucketName, String fileName);

    FileMetadata updateFileMetadata(FileMetadata FileMetadata, String objectId);

    String getPresignedUploadUrl(String bucketName, String fileName);

    String getPresignedDownloadUrl(String bucketName, String fileName);
}
