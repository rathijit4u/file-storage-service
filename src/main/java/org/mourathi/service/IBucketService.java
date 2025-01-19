package org.mourathi.service;

import org.jetbrains.annotations.NotNull;
import org.mourathi.dto.BucketDto;

import java.util.List;

public interface IBucketService {
    BucketDto createBucket(String name) throws Exception;

    void deleteBucket(String name);

    List<BucketDto> getAllBuckets();

    BucketDto getBucket(String name) throws Exception;
}
