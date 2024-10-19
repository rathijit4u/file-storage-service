package org.mourathi.service;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.minio.ObjectWriteResponse;
import org.mourathi.exception.FileObjectNotFoundException;
import org.mourathi.model.FileMetadata;
import org.mourathi.repository.FileRepository;
import org.mourathi.utils.MinIOAsyncUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class FileStorageAsyncService implements IFileStorageService{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MinIOAsyncUtil minIOAsyncUtil = new MinIOAsyncUtil();
    @Autowired
    FileRepository fileRepository;
    @Override
    public FileMetadata uploadFile(String requestUri, String bucket, MultipartFile file) throws IOException {
        FileMetadata fileMetadata;
        fileMetadata = fileRepository.findByNameAndBucket(file.getOriginalFilename(), bucket);
        CompletableFuture<ObjectWriteResponse> objectWriteResponse =  minIOAsyncUtil.uploadObject(file.getInputStream()
                , file.getOriginalFilename(), file.getContentType(), file.getSize(), bucket);

        if(fileMetadata != null){
            fileMetadata.setFileSize(file.getSize());
            fileMetadata.setFileType(file.getContentType());
            fileMetadata.setUpdated(new Date());
            fileMetadata.setBucketName(bucket);
            //fileMetadata.setDownloadLink(requestUri + fileMetadata.getDownloadLink());
        } else{
            fileMetadata = new FileMetadata(file.getOriginalFilename(), null
                    , file.getContentType(), file.getSize(), bucket);
        }

        FileMetadata finalFileMetadata = fileMetadata;
        objectWriteResponse.whenComplete((res, error) ->{
            if(error != null){
                this.logger.error(error.getLocalizedMessage());
                throw new RuntimeException(error);
            } else if (res != null){
                finalFileMetadata.seteTag(res.etag());
                fileRepository.save(finalFileMetadata);
                this.logger.info("File - " + finalFileMetadata.getFileName() + " has been successfully uploaded!!!");
            }
        });
        return fileRepository.save(finalFileMetadata);
    }

    @Override
    public InputStream getFileStream(String objectId) {
        FileMetadata fileMetadata = this.getFileById(objectId);
        return minIOAsyncUtil.getFileStream(fileMetadata.getFileName(), fileMetadata.getBucketName());
    }

    @Override
    public List<FileMetadata> getAllFileMetadata(int limit) {
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
    public FileMetadata getFileMetadata(String objectId) {
        return this.getFileById(objectId);
    }

    @Override
    public FileMetadata deleteFileObject(String objectId) {
        FileMetadata fileMetadata = this.getFileById(objectId);
        minIOAsyncUtil.deleteObject(fileMetadata.getFileName(), fileMetadata.getBucketName());
        fileRepository.deleteById(objectId);
        return fileMetadata;
    }

    @Override
    public FileMetadata updateFileMetadata(FileMetadata fileMetadata, String objectId) {
        fileMetadata.setUpdated(new Date());
        fileRepository.save(fileMetadata);
        return fileMetadata;
    }


    public Map<String, Object> initMultiPartUpload(String filename, String bucketName
            , Integer partCount, String contentType) {

        Map<String, Object> result;
        if (partCount == 1) {
            String uploadObjectUrl = minIOAsyncUtil.getUploadObjectUrl(filename, bucketName, null);
            result = ImmutableMap.of("uploadUrls", ImmutableList.of(uploadObjectUrl));
        } else {
            result = minIOAsyncUtil.initMultiPartUpload(filename, bucketName, partCount, contentType);
        }
        return result;
    }


    public boolean mergeMultipartUpload(String objectName, String bucketName, String uploadId) {
        return minIOAsyncUtil.mergeMultipartUpload(objectName, bucketName, uploadId);
    }

    private FileMetadata getFileById(String id){
        Optional<FileMetadata> optionalFileMetadata = fileRepository.findById(id);
        return optionalFileMetadata
                .orElseThrow(()-> new FileObjectNotFoundException("No file object found with id - " + id));
    }
}
