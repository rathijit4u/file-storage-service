package org.mourathi.service;

import io.minio.GetObjectResponse;
import io.minio.ObjectWriteResponse;
import org.mourathi.exception.FileObjectNotFoundException;
import org.mourathi.model.FileMetadata;
import org.mourathi.repository.FileRepository;
import org.mourathi.utils.MinIOStorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class FileStorageService implements IFileStorageService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MinIOStorageUtil minIOStorageUtil = new MinIOStorageUtil();
    @Autowired
    FileRepository fileRepository;

    @Override
    public FileMetadata uploadFile(String requestUri, String bucket, MultipartFile file) throws IOException {

        ObjectWriteResponse objectWriteResponse =  minIOStorageUtil.uploadObject(file.getInputStream()
                , file.getOriginalFilename(), file.getContentType(), file.getSize(), bucket);

        FileMetadata fileMetadata = fileRepository.findByNameAndBucket(file.getOriginalFilename(), bucket);
        if(fileMetadata != null){
            fileMetadata.setFileSize(file.getSize());
            fileMetadata.setFileType(file.getContentType());
            fileMetadata.seteTag(objectWriteResponse.etag());
            fileMetadata.setUpdated(new Date());
            fileMetadata.setBucketName(bucket);
            //fileMetadata.setDownloadLink(requestUri + fileMetadata.getDownloadLink());
        } else{
            fileMetadata = new FileMetadata(objectWriteResponse.object(), objectWriteResponse.etag()
                    , file.getContentType(), file.getSize(), bucket);
        }

        return fileRepository.save(fileMetadata);
    }

    @Override
    public GetObjectResponse getFileStream(String fileName, String bucket){
        return minIOStorageUtil.getObject(fileName, bucket);
    }

    @Override
    public List<FileMetadata> getAllFileMetadata(int limit){
        List<FileMetadata> responses = new ArrayList<>();
        int counter = 0;

        for(FileMetadata fileMetadata: fileRepository.findAll()){
            if(counter > limit){
                break;
            }
            responses.add(fileMetadata);
            counter++;
        }
        return responses;
    }

    @Override
    public List<FileMetadata> getAllFileMetadataForBucket(String bucketName, int limit) {
        List<FileMetadata> responses = new ArrayList<>();
        int counter = 0;

        for(FileMetadata fileMetadata: fileRepository.findAllByBucket(bucketName)){
            if(counter > limit){
                break;
            }
            responses.add(fileMetadata);
            counter++;
        }
        return responses;
    }

    @Override
    public FileMetadata getFileMetadata(String bucketName, String fileName) {
        return fileRepository.findByNameAndBucket(fileName, bucketName);
    }

    @Override
    public FileMetadata deleteFileObject(String bucketName, String fileName){
        FileMetadata fileMetadata = fileRepository.findByNameAndBucket(fileName, bucketName);
        minIOStorageUtil.deleteObject(fileMetadata.getFileName(), fileMetadata.getBucketName());
        fileRepository.deleteById(fileMetadata.getId());
        return fileMetadata;
    }

    @Override
    public FileMetadata updateFileMetadata(FileMetadata fileMetadata, String objectId) {
        fileMetadata.setUpdated(new Date());
        fileRepository.save(fileMetadata);
        return fileMetadata;
    }

    @Override
    public String getPresignedUrl(String bucketName, String fileName) {
        return minIOStorageUtil.getPresignedUrl(bucketName, fileName);
    }
}
