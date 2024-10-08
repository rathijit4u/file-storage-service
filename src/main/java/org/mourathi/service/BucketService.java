package org.mourathi.service;

import io.minio.messages.Bucket;
import org.mourathi.utils.MinIOStorageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BucketService implements IBucketService {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private final MinIOStorageUtil minIOStorageUtil = new MinIOStorageUtil();

    @Override
    public void createBucket(String name){
        minIOStorageUtil.makeBucket(name);
    }

    @Override
    public void deleteBucket(String name){
        minIOStorageUtil.removeBucket(name);
    }

    @Override
    public List<Bucket> getAllBuckets(){
        return minIOStorageUtil.listBuckets();
    }
}
