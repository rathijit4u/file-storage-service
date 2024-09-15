package org.mourathi.service;


import org.mourathi.model.FileMetadata;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IFileStorageService {
    FileMetadata uploadFile(String requestUri, String bucket, MultipartFile file) throws IOException;

    InputStream getFileStream(String objectId);

    List<FileMetadata> getAllFileMetadata(int limit);

    FileMetadata getFileMetadata(String objectId);

    FileMetadata deleteFileObject(String objectId);

    FileMetadata updateFileMetadata(FileMetadata FileMetadata, String objectId);

}
