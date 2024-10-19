package org.mourathi.utils;

import com.google.common.collect.Multimap;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Part;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class CustomMinioClient extends MinioAsyncClient {
    protected CustomMinioClient(MinioAsyncClient client) {
        super(client);
    }

    public String initMultiPartUpload(String bucket, String region, String object,
                                      Multimap<String, String> headers, Multimap<String, String> extraQueryParams)
            throws InternalException, InsufficientDataException, NoSuchAlgorithmException, IOException
            , InvalidKeyException, XmlParserException, ExecutionException, InterruptedException {

        CompletableFuture<CreateMultipartUploadResponse> response = this.createMultipartUploadAsync(
                bucket, region, object, headers, extraQueryParams);
        return response.get().result().uploadId();
    }

    public ObjectWriteResponse mergeMultipartUpload(String bucketName, String region, String objectName
            , String uploadId, Part[] parts, Multimap<String, String> extraHeaders
            , Multimap<String, String> extraQueryParams)
            throws IOException, InvalidKeyException, NoSuchAlgorithmException, InsufficientDataException
            , InternalException, XmlParserException, ExecutionException, InterruptedException {

        return this.completeMultipartUploadAsync(bucketName, region, objectName, uploadId
                , parts, extraHeaders, extraQueryParams).get();
    }

    public ListPartsResponse listMultipart(String bucketName, String region, String objectName, Integer maxParts
            , Integer partNumberMarker, String uploadId, Multimap<String, String> extraHeaders
            , Multimap<String, String> extraQueryParams)
            throws NoSuchAlgorithmException, InsufficientDataException, IOException, InvalidKeyException
            , XmlParserException,InternalException, ExecutionException, InterruptedException {
        return this.listPartsAsync(bucketName, region, objectName, maxParts, partNumberMarker
                , uploadId, extraHeaders, extraQueryParams).get();
    }
}
