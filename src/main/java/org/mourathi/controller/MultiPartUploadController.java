package org.mourathi.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.mourathi.dto.UploadDto;
import org.mourathi.service.FileStorageAsyncService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/multipart")
public class MultiPartUploadController {
    private final FileStorageAsyncService fileStorageAsyncService;

    public MultiPartUploadController(FileStorageAsyncService fileStorageAsyncService) {
        this.fileStorageAsyncService = fileStorageAsyncService;
    }

    @PostMapping("/{bucket}/init")
    public ResponseEntity<Map<String, Object>> initFileUpload(HttpServletRequest request, @PathVariable("bucket") String bucket
                                    , @RequestBody UploadDto uploadDto)
            throws IOException {
        Map<String, Object> result = fileStorageAsyncService.initMultiPartUpload(uploadDto.getFileName()
                , bucket, uploadDto.getPartCount()
                                , uploadDto.getContentType());
        return ResponseEntity.ok().body(result);
    }
}
