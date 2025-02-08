package org.mourathi.service.video;

import jakarta.transaction.Transactional;
import org.mourathi.exception.StorageException;
import org.mourathi.model.FileMetadata;
import org.mourathi.repository.FileRepository;
import org.mourathi.service.s3.MinIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;

@Service
public class DefaultVideoService implements VideoService{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private MinIOService storageService;
    @Autowired
    private FileRepository fileMetadataRepository;

    @Override
    @Transactional
    public UUID save(MultipartFile video, String bucketName) {
        try {

            FileMetadata metadata = new FileMetadata(video.getOriginalFilename(), ""
                    , video.getContentType(), video.getSize(), bucketName);
            metadata.setChunkSize(video.getSize());
            metadata.setHttpContentType(video.getContentType());
            fileMetadataRepository.save(metadata);
            UUID fileUuid = UUID.fromString(metadata.getId());
            storageService.saveChunk(video, fileUuid, bucketName);
            return fileUuid;
        } catch (Exception ex) {
            logger.error("Exception occurred when trying to save the file", ex);
            throw new StorageException(ex);
        }
    }

    @Override
    public ChunkWithMetadata fetchChunk(UUID uuid, Range range) {
        FileMetadata fileMetadata = fileMetadataRepository.findById(uuid.toString()).orElseThrow();
        return new ChunkWithMetadata(fileMetadata, readChunk(uuid, range, fileMetadata.getFileSize(), fileMetadata.getBucketName()));

    }

    private byte[] readChunk(UUID uuid, Range range, long fileSize, String bucketName) {
        long startPosition = range.getRangeStart();
        long endPosition = range.getRangeEnd(fileSize);
        int chunkSize = (int) (endPosition - startPosition + 1);
        try(InputStream inputStream = storageService.getInputStreamByChunk(uuid, startPosition, chunkSize, bucketName)) {
            return inputStream.readAllBytes();
        } catch (Exception exception) {
            logger.error("Exception occurred when trying to read file with ID = {}", uuid);
            throw new StorageException(exception);
        }
    }

    public record ChunkWithMetadata(
            FileMetadata metadata,
            byte[] chunk
    ) {}

}
