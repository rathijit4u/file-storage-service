package org.mourathi.dto;


public class FileDto {

    private String id;
    private String fileName;
    private String eTag;
    private String fileType;
    private long fileSize;
    private String downloadLink;
    private String bucketName;

    public FileDto() {
    }

    public FileDto(String id, String fileName, String eTag, String fileType, long fileSize, String downloadLink) {
        this.id = id;
        this.fileName = fileName;
        this.eTag = eTag;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.downloadLink = downloadLink;
    }

    public FileDto(String id, String fileName, String eTag, String fileType, long fileSize, String downloadLink, String bucketName) {
        this.id = id;
        this.fileName = fileName;
        this.eTag = eTag;
        this.fileType = fileType;
        this.fileSize = fileSize;
        this.downloadLink = downloadLink;
        this.bucketName = bucketName;
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
}
