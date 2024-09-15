package org.mourathi.controller;

import io.minio.messages.Bucket;
import org.mourathi.service.IBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {

    @Autowired
    IBucketService iBucketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createBucket(@RequestParam("name") String name){
        iBucketService.createBucket(name);
    }

    @DeleteMapping("/{objectName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBucket(@PathVariable("objectName") String name){
        iBucketService.deleteBucket(name);
    }

    @GetMapping
    public ResponseEntity<List<Bucket>> getAllBuckets(){
        return ResponseEntity.ok(iBucketService.getAllBuckets());
    }
}
