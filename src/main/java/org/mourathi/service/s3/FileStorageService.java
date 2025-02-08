package org.mourathi.service.s3;

import io.minio.GetObjectResponse;
import io.minio.ObjectWriteResponse;
import org.mourathi.model.FileMetadata;
import org.mourathi.repository.FileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FileStorageService implements IFileStorageService {

    Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MinIOService minIOService;

    FileRepository fileRepository;

    @Autowired
    public void setFileRepository(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @Override
    public FileMetadata uploadFile(String requestUri, String bucket, MultipartFile file) throws IOException {

        ObjectWriteResponse objectWriteResponse =  minIOService.uploadObject(file.getInputStream()
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
        return minIOService.getObject(fileName, bucket);
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
    public List<FileMetadata> getAllFileMetadataForBucket(String bucketName, int page, int size, String sortField, String sortOrder) {
        Sort sort = sortOrder.equalsIgnoreCase("desc") ? Sort.by(Sort.Order.desc(sortField)) :
                Sort.by(Sort.Order.asc(sortField)) ;

        return fileRepository.findAllByBucketName(bucketName,
                PageRequest.of(page, size, sort));
    }

    @Override
    public FileMetadata getFileMetadata(String bucketName, String fileName) {
        return fileRepository.findByNameAndBucket(fileName, bucketName);
    }

    @Override
    public FileMetadata deleteFileObject(String bucketName, String fileName){
        FileMetadata fileMetadata = fileRepository.findByNameAndBucket(fileName, bucketName);
        minIOService.deleteObject(fileMetadata.getFileName(), fileMetadata.getBucketName());
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
    public String getPresignedUploadUrl(String bucketName, String fileName) {
        return minIOService.getPresignedUploadUrl(bucketName, fileName);
    }

    @Override
    public String getPresignedDownloadUrl(String bucketName, String fileName) {
        return minIOService.getPresignedDownloadUrl(bucketName, fileName);
    }

}
