package org.mourathi.config;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Value("${minio.url}")
    private String url;

    @Value("${minio.bucket.store}")
    private String storeBucket;

    @Value("${minio.bucket.stream}")
    private String streamBucket;

    private final Logger logger = LoggerFactory.getLogger(MinioConfig.class);

    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient minioClient =
                    MinioClient.builder().endpoint(url).credentials(accessKey, secretKey).build();
            initBucket(minioClient, storeBucket);
            initBucket(minioClient, streamBucket);
            return minioClient;
        } catch (Exception ex){
            this.logger.error(ex.getMessage());
            return null;
        }

    }

    private void initBucket(MinioClient minioClient, String bucket) throws Exception {
        boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
        if (!exists) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            logger.info("successfully create bucket {} !", bucket);
        }
    }
}
