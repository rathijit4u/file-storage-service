package org.mourathi.controller;

import io.minio.GetObjectResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.mourathi.dto.BucketDto;
import org.mourathi.dto.FileDto;
import org.mourathi.model.FileMetadata;
import org.mourathi.service.IFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/files")
public class FileObjectController {

    @Autowired
    IFileStorageService fileStorageService;


    @PostMapping("/{bucket}/upload")
    @ResponseStatus(HttpStatus.CREATED)
    public FileDto uploadSingleFile(HttpServletRequest request, @PathVariable("bucket") String bucket
            , @RequestParam("file") MultipartFile file) throws Exception {
        return getFileDto(request, bucket, file);
    }

    @PostMapping("/{bucket}/bulkupload")
    public ResponseEntity<List<FileDto>> uploadMultipleFiles(HttpServletRequest request
            , @PathVariable("bucket") String bucket, @RequestParam("files") MultipartFile[] files) throws Exception {
        List<FileDto> responses = new ArrayList<>();
        for(MultipartFile file :files){
            responses.add(getFileDto(request, bucket, file));
        }
        return ResponseEntity.ok()
                .body(responses);
    }

    @GetMapping("/presignedurl/upload")
    public ResponseEntity<String> getPresignedUrl(@RequestParam("bucket_name") String bucketName,
                                @RequestParam("file_name") String fileName) throws IOException {
       return ResponseEntity.ok().body(fileStorageService.getPresignedUrl(bucketName, fileName));
    }

    @GetMapping("/{bucket}/{fileName}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable("fileName") String fileName,
                                                 @PathVariable("bucket") String bucket) throws IOException {
        GetObjectResponse getObjectResponse = fileStorageService.getFileStream(fileName, bucket);
        InputStreamResource resource = new InputStreamResource(getObjectResponse);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="
                        + fileName)
                .body(resource);
    }

    @GetMapping("/{bucket}")
    public CollectionModel<FileDto> getAllFileMetadata(HttpServletRequest request
                                                    ,@PathVariable("bucket") String bucket
                                        ,@RequestParam("limit") Optional<Integer> optionalLimit)
            throws Exception {
        return CollectionModel.of((convertFileMetadataList(request,
                        fileStorageService.getAllFileMetadataForBucket(bucket, optionalLimit.orElse(Integer.MAX_VALUE)))),
                linkTo(methodOn(FileObjectController.class).getAllFileMetadata(request, bucket, optionalLimit)).withSelfRel());
    }

    @GetMapping("/{bucket}/{objectId}")
    public ResponseEntity<FileDto> getFileMetadata(HttpServletRequest request
                                    ,@PathVariable("bucket") String bucket
                                    , @PathVariable("objectId") String objectId) throws Exception {
        FileMetadata fileMetadata = fileStorageService.getFileMetadata(bucket, objectId);
        if(fileMetadata == null)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok().body(convertFileMetaDataToFileDto(request
                , fileMetadata));
    }

    @PutMapping("/{objectId}")
    public ResponseEntity<FileDto> updateFileMetadata(HttpServletRequest request, @RequestBody FileDto fileDto,
                                                      @PathVariable("objectId") String objectId) throws Exception {

        FileMetadata fileMetadata = fileStorageService.getFileMetadata(fileDto.getBucket().getName(), fileDto.getId());
        fileMetadata.setFileType(fileDto.getFileType());
        fileMetadata.seteTag(fileDto.geteTag());
        fileMetadata.setFileName(fileDto.getFileName());
        fileMetadata.setFileSize(fileDto.getFileSize());
        fileMetadata.setDownloadLink(fileDto.getDownloadLink());
        fileMetadata.setBucketName(fileDto.getBucket().getName());
        return ResponseEntity.ok().body(convertFileMetaDataToFileDto(request
                , fileStorageService.updateFileMetadata(fileMetadata, objectId)));
    }


    @DeleteMapping("/{bucket}/{objectId}")
    public ResponseEntity<FileDto> deleteFileObject(HttpServletRequest request
            ,@PathVariable("bucket") String bucket
            , @PathVariable("objectId") String objectId) throws Exception {

        return ResponseEntity.ok()
                .body(convertFileMetaDataToFileDto(request, fileStorageService.deleteFileObject(bucket, objectId)));
    }


    @NotNull
    private FileDto getFileDto(HttpServletRequest request, String bucket, MultipartFile file) throws Exception {
        return convertFileMetaDataToFileDto(request, fileStorageService.uploadFile(getBaseUrl(request), bucket, file));
    }

    @NotNull
    private static String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }

    private List<FileDto> convertFileMetadataList(HttpServletRequest request, List<FileMetadata> fileMetadataList) throws Exception {
        List<FileDto> response = new ArrayList<>();

        for(FileMetadata fileMetadata: fileMetadataList){
            response.add(convertFileMetaDataToFileDto(request, fileMetadata));
        }
        return response;
    }

    private FileDto convertFileMetaDataToFileDto(HttpServletRequest request, FileMetadata fileMetadata) throws Exception {
        String baseUrl = getBaseUrl(request);
        BucketDto bucketDto = new BucketDto(fileMetadata.getBucketName(), null)
                .add(linkTo(methodOn(BucketController.class)
                        .getBucket(fileMetadata.getBucketName())).withSelfRel());
        return new FileDto(fileMetadata.getId(), fileMetadata.getFileName(), fileMetadata.geteTag()
                , fileMetadata.getFileType(), fileMetadata.getFileSize()
                , baseUrl + fileMetadata.getDownloadLink(), bucketDto)
                .add(linkTo(methodOn(FileObjectController.class)
                        .getFileMetadata(request, fileMetadata.getBucketName(), fileMetadata.getId())).withSelfRel());
    }
}
