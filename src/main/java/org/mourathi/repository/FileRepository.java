package org.mourathi.repository;

import org.mourathi.model.FileMetadata;
import org.mourathi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends CrudRepository<FileMetadata, String> {
    @Query("select fm from file_metadata_info fm where fm.fileName = ?1 and fm.bucketName=?2")
    FileMetadata findByNameAndBucket(String name, String bucketName);

    @Query("select fm from file_metadata_info fm where fm.bucketName = :bucketName")
    List<FileMetadata> findAllByBucket(@Param("bucketName") String bucketName);

}
