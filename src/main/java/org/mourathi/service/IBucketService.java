package org.mourathi.service;

import org.jetbrains.annotations.NotNull;
import org.mourathi.dto.BucketDto;

import java.util.List;

public interface IBucketService {
    void createBucket(String name);

    void deleteBucket(String name);

    List<BucketDto> getAllBuckets();
}
