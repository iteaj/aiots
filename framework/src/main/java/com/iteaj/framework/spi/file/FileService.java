package com.iteaj.framework.spi.file;

import java.io.IOException;

public interface FileService {

    /**
     * 写文件
     * @param bytes
     * @throws IOException
     */
    void writer(byte[] bytes) throws IOException;
}
