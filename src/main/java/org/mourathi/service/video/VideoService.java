package org.mourathi.service.video;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface VideoService {
    UUID save(MultipartFile video, String bucketName);

    DefaultVideoService.ChunkWithMetadata fetchChunk(UUID uuid, Range range);

}
