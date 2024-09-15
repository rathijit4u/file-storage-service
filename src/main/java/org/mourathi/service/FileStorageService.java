package org.mourathi.service;

import io.minio.ObjectWriteResponse;
import io.minio.messages.Item;
import org.mourathi.dto.FileDto;
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
import java.io.InputStream;
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

        FileMetadata fileMetadata = null;

        ObjectWriteResponse objectWriteResponse =  minIOStorageUtil.uploadObject(file.getInputStream()
                , file.getOriginalFilename(), file.getContentType(), file.getSize(), bucket);

        fileMetadata = fileRepository.findByNameAndBucket(file.getOriginalFilename(), bucket);
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
    public InputStream getFileStream(String id){
        FileMetadata fileMetadata = this.getFileById(id);
        return minIOStorageUtil.getObject(fileMetadata.getFileName(), fileMetadata.getBucketName());
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
    public FileMetadata getFileMetadata(String id) {
        return this.getFileById(id);
    }

    @Override
    public FileMetadata deleteFileObject(String objectId){
        FileMetadata fileMetadata = this.getFileById(objectId);
        minIOStorageUtil.deleteObject(fileMetadata.getFileName(), fileMetadata.getBucketName());
        fileRepository.deleteById(objectId);
        return fileMetadata;
    }

    @Override
    public FileMetadata updateFileMetadata(FileMetadata fileMetadata, String objectId) {
        fileRepository.save(fileMetadata);
        return fileMetadata;
    }




    private FileMetadata getFileById(String id){
        Optional<FileMetadata> optionalFileMetadata = fileRepository.findById(id);
        return optionalFileMetadata
                .orElseThrow(()-> new FileObjectNotFoundException("No file object found with id - " + id));
    }
}
