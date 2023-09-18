package com.iteaj.framework.spi.file;

import java.io.File;
import java.io.InputStream;

public interface FileService {

    /**
     * 上传文件
     * @param stream
     * @param targetFile
     */
    void upload(InputStream stream, File targetFile) throws FileException;

    /**
     * 上传文件
     * @param stream
     * @param targetFile
     */
    void upload(InputStream stream, String targetFile) throws FileException;
}
