package org.mourathi.controller;

import io.minio.messages.Bucket;
import org.mourathi.dto.BucketDto;
import org.mourathi.service.FSUserDetailsService;
import org.mourathi.service.IBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/buckets")
public class BucketController {

    @Autowired
    IBucketService iBucketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<BucketDto> createBucket(@RequestParam("name") String name) throws Exception {
        BucketDto bucketDto = iBucketService.createBucket(name);
        return ResponseEntity.ok(bucketDto
                .add(linkTo(methodOn(BucketController.class).getBucket(name)).withSelfRel()));
    }

    @DeleteMapping("/{objectName}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteBucket(@PathVariable("objectName") String name){
        iBucketService.deleteBucket(name);
    }

    @GetMapping("/{objectName}")
    public ResponseEntity<BucketDto> getBucket(@PathVariable("objectName") String name) {

        try {
            BucketDto bucketDto = iBucketService.getBucket(name);
            return ResponseEntity.ok(bucketDto
                    .add(linkTo(methodOn(BucketController.class).getBucket(name)).withSelfRel()));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public CollectionModel<BucketDto> getAllBuckets(){
        List<BucketDto> bucketDtos = iBucketService.getAllBuckets().stream()
                .map(bucketDto -> {
                    try {
                        return bucketDto.add(
                                linkTo(methodOn(BucketController.class).getBucket(bucketDto.getName())).withSelfRel());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }).toList();
        return CollectionModel.of(bucketDtos, linkTo(methodOn(BucketController.class).getAllBuckets()).withSelfRel());
    }

}
