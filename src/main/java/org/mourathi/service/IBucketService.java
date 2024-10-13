package org.mourathi.service;

import io.minio.messages.Bucket;
import org.mourathi.dto.BucketDto;

import java.util.List;

public interface IBucketService {
    void createBucket(String name);

    void deleteBucket(String name);

    List<BucketDto> getAllBuckets();
}
