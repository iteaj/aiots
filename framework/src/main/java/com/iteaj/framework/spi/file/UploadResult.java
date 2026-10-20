package com.iteaj.framework.spi.file;

import java.io.File;

public class UploadResult {

    /**
     * 访问地址
     */
    private String url;

    /**
     * 文件
     */
    private File file;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件基础访问地址
     * @see com.iteaj.framework.autoconfigure.FrameworkProperties#getFileServerDomain()
     */
    private String fileBasicUrl;

    /**
     * 原文件名
     */
    private String originalFilename;

    public UploadResult(String originalFilename) {
        this.originalFilename = originalFilename;
    }

    public UploadResult(String fileName, String originalFilename) {
        this.fileName = fileName;
        this.originalFilename = originalFilename;
    }

    public UploadResult(String url, File file, String fileName, String originalFilename) {
        this.url = url;
        this.file = file;
        this.fileName = fileName;
        this.originalFilename = originalFilename;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileBasicUrl() {
        return fileBasicUrl;
    }

    public UploadResult setFileBasicUrl(String fileBasicUrl) {
        this.fileBasicUrl = fileBasicUrl;
        return this;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public void setOriginalFilename(String originalFilename) {
        this.originalFilename = originalFilename;
    }
}
