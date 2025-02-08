package org.mourathi.controller;

import lombok.RequiredArgsConstructor;
import org.mourathi.service.video.DefaultVideoService;
import org.mourathi.service.video.Range;
import org.mourathi.service.video.VideoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpHeaders;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/videos")
public class VideoController {
    private final VideoService videoService;

    @Value("${photon.streaming.default-chunk-size}")
    public Integer defaultChunkSize;

    @PostMapping
    public ResponseEntity<UUID> save(@RequestParam("file") MultipartFile file, @RequestParam("bucket_name") String bucketName) {
        UUID fileUuid = videoService.save(file, bucketName);
        return ResponseEntity.ok(fileUuid);
    }



    @GetMapping("/{id}")
    public ResponseEntity<byte[]> readChunk(
            @RequestHeader(value = HttpHeaders.RANGE, required = false) String range,
            @PathVariable("id") String id
    ) {
        Range parsedRange = Range.parseHttpRangeString(range, defaultChunkSize);
        DefaultVideoService.ChunkWithMetadata chunkWithMetadata = videoService.fetchChunk(UUID.fromString(id), parsedRange);
        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .header(HttpHeaders.CONTENT_TYPE, chunkWithMetadata.metadata().getHttpContentType())
                .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                .header(HttpHeaders.CONTENT_LENGTH, calculateContentLengthHeader(parsedRange, chunkWithMetadata.metadata().getFileSize()))
                .header(HttpHeaders.CONTENT_RANGE, constructContentRangeHeader(parsedRange, chunkWithMetadata.metadata().getFileSize()))
                .body(chunkWithMetadata.chunk());
    }

    private String calculateContentLengthHeader(Range range, long fileSize) {
        return String.valueOf(range.getRangeEnd(fileSize) - range.getRangeStart() + 1);
    }

    private String constructContentRangeHeader(Range range, long fileSize) {
        return  "bytes " + range.getRangeStart() + "-" + range.getRangeEnd(fileSize) + "/" + fileSize;
    }
}
