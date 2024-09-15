package org.mourathi.model;

public class FileObjectMetadata {
    private String fileName;
    private String eTag;
    private String fileType;
    private long fileSize;
    private String download_link;

    public FileObjectMetadata() {
    }

    public FileObjectMetadata(String fileName, String eTag, String fileType, long fileSize) {
        this.fileName = fileName;
        this.seteTag(eTag);
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public FileObjectMetadata(String fileName, String eTag, String fileType, long fileSize, String downloadLink) {
        this(fileName, eTag, fileType, fileSize);
        this.download_link = downloadLink;
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

        this.eTag = eTag.replaceAll("^\"|\"$", "");
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

    public String getDownload_link() {
        return download_link;
    }

    public void setDownload_link(String download_link) {
        this.download_link = download_link;
    }
}
