package org.mourathi.repository;

import org.mourathi.model.FileMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends CrudRepository<FileMetadata, String> {
    @Query("select fm from file_metadata_info fm where fm.fileName = ?1 and fm.bucketName=?2")
    FileMetadata findByNameAndBucket(String name, String bucketName);

}
