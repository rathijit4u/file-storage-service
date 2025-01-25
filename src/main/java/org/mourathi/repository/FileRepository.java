package org.mourathi.repository;

import org.mourathi.model.FileMetadata;
import org.mourathi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<FileMetadata, String> {
    @Query("select fm from file_metadata_info fm where fm.fileName = ?1 and fm.bucketName=?2")
    FileMetadata findByNameAndBucket(String name, String bucketName);

    @Query("select fm from file_metadata_info fm where fm.bucketName = ?1")
    List<FileMetadata> findAllByBucketName(String bucketName, Pageable pageable);
}
