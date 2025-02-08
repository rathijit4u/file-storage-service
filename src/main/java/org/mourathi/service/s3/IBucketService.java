package org.mourathi.service.s3;

import org.mourathi.dto.BucketDto;

import java.util.List;

public interface IBucketService {
    BucketDto createBucket(String name) throws Exception;

    void deleteBucket(String name);

    List<BucketDto> getAllBuckets();

    BucketDto getBucket(String name) throws Exception;
}
