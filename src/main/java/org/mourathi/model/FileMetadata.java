package org.mourathi.model;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PostLoad;
import org.hibernate.annotations.UuidGenerator;

import java.util.Date;

@Entity(name = "file_metadata_info")
public class FileMetadata {

    @Id
    @UuidGenerator
    private String id;
    private String fileName;
    private String eTag;
    private String fileType;
    private long fileSize;
    private String downloadLink;
    private String bucketName;
    private Date created;
    private Date updated;

    public FileMetadata(){}

    public FileMetadata(String fileName, String eTag, String fileType, long fileSize, String bucketName) {
        this.fileName = fileName;
        this.eTag = eTag;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.bucketName = bucketName;
        this.created = new Date();
        this.updated = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String eTag) {
        this.eTag = eTag;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    @PostLoad
    private void postLoad(){
        this.setDownloadLink("/api/files/" + this.getId() + "/file");
    }
}
