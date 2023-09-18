package com.iteaj.framework.spi.file;

import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class LocalFileService implements FileService{

    @Override
    public void upload(InputStream stream, File targetFile) throws FileException {
        try {
            if(!targetFile.exists()) {
                if(!targetFile.getParentFile().exists()) {
                    targetFile.mkdirs();
                }

                targetFile.createNewFile();
            }
            FileCopyUtils.copy(stream, new FileOutputStream(targetFile));
        } catch (IOException e) {
            throw new FileException("文件上传失败", e);
        }
    }

    @Override
    public void upload(InputStream stream, String targetFile) {
        this.upload(stream, new File(targetFile));
    }
}
