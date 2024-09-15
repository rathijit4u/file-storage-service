package org.mourathi.service;

import io.minio.messages.Bucket;

import java.util.List;

public interface IBucketService {
    void createBucket(String name);

    void deleteBucket(String name);

    List<Bucket> getAllBuckets();
}
