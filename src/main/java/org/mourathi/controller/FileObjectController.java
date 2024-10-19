package org.mourathi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.mourathi.dto.FileDto;
import org.mourathi.model.FileMetadata;
import org.mourathi.service.IFileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/files")
public class FileObjectController {

    @Autowired
    IFileStorageService fileStorageService;


    @PostMapping("/{bucket}/upload")
    public ResponseEntity<FileDto> uploadSingleFile(HttpServletRequest request, @PathVariable("bucket") String bucket
            , @RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.accepted()
                .body(getFileDto(request, bucket, file));
    }

    @PostMapping("/{bucket}/bulkupload")
    public ResponseEntity<List<FileDto>> uploadMultipleFiles(HttpServletRequest request
            , @PathVariable("bucket") String bucket, @RequestParam("files") MultipartFile[] files) throws IOException {
        List<FileDto> responses = new ArrayList<>();
        for(MultipartFile file :files){
            responses.add(getFileDto(request, bucket, file));
        }
        return ResponseEntity.ok()
                .body(responses);
    }

    @GetMapping("/{objectId}/file")
    public ResponseEntity<Resource> downloadFile(@PathVariable("objectId") String objectId) throws IOException {
        InputStreamResource resource = new InputStreamResource(fileStorageService.getFileStream(objectId));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + objectId)
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<FileDto>> getAllFileMetadata(HttpServletRequest request,
                                                            @RequestParam("limit") Optional<Integer> optionalLimit)
            throws IOException {
        return ResponseEntity.ok()
                .body(convertFileMetadataList(request,
                        fileStorageService.getAllFileMetadata(optionalLimit.orElse(Integer.MAX_VALUE))));
    }

    @GetMapping("/{objectId}")
    public ResponseEntity<FileDto> getFileMetadata(HttpServletRequest request, @PathVariable("objectId") String objectId) {
        return ResponseEntity.ok().body(convertFileMetaDataToFileDto(request
                , fileStorageService.getFileMetadata(objectId)));
    }

    @PutMapping("/{objectId}")
    public ResponseEntity<FileDto> updateFileMetadata(HttpServletRequest request, @RequestBody FileDto fileDto,
                                                      @PathVariable("objectId") String objectId) {
        FileMetadata fileMetadata = fileStorageService.getFileMetadata(fileDto.getId());
        fileMetadata.setFileType(fileDto.getFileType());
        fileMetadata.setFileName(fileDto.getFileName());
        fileMetadata.setFileSize(fileDto.getFileSize());
        fileMetadata.setDownloadLink(fileDto.getDownloadLink());
        fileMetadata.setBucketName(fileDto.getBucketName());
        return ResponseEntity.ok().body(convertFileMetaDataToFileDto(request
                , fileStorageService.updateFileMetadata(fileMetadata, objectId)));
    }


    @DeleteMapping("/{objectId}")
    public ResponseEntity<FileDto> deleteFileObject(HttpServletRequest request
            , @PathVariable("objectId") String objectId){

        return ResponseEntity.ok()
                .body(convertFileMetaDataToFileDto(request, fileStorageService.deleteFileObject(objectId)));
    }


    @NotNull
    private FileDto getFileDto(HttpServletRequest request, String bucket, MultipartFile file) throws IOException {
        return convertFileMetaDataToFileDto(request, fileStorageService.uploadFile(getBaseUrl(request), bucket, file));
    }

    @NotNull
    private static String getBaseUrl(HttpServletRequest request) {
        return ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .build()
                .toUriString();
    }

    private List<FileDto> convertFileMetadataList(HttpServletRequest request, List<FileMetadata> fileMetadataList){
        List<FileDto> response = new ArrayList<>();

        for(FileMetadata fileMetadata: fileMetadataList){
            response.add(convertFileMetaDataToFileDto(request, fileMetadata));
        }
        return response;
    }

    private FileDto convertFileMetaDataToFileDto(HttpServletRequest request, FileMetadata fileMetadata){
        String baseUrl = getBaseUrl(request);
        return new FileDto(fileMetadata.getId(), fileMetadata.getFileName()
                , fileMetadata.getFileType(), fileMetadata.getFileSize()
                , baseUrl + fileMetadata.getDownloadLink(), fileMetadata.getBucketName());
    }
}
