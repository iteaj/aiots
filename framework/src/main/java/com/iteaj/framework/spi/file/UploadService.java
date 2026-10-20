package com.iteaj.framework.spi.file;

import com.iteaj.framework.autoconfigure.FrameworkProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 文件上传服务
 */
public interface UploadService {

    /**
     * 上传文件
     * @param stream
     * @param originalFilename 原文件名
     * @return 访问地址
     */
    UploadResult upload(InputStream stream, String originalFilename) throws FileException;

    /**
     * 上传文件
     * @param stream
     * @param originalFilename 原文件名
     * @param subPath 子路径
     * @return 访问地址
     */
    UploadResult upload(InputStream stream, String originalFilename, String subPath) throws FileException;

    /**
     * 上传文件
     * @param stream
     * @param originalFilename 原文件名
     * @param fileName 自定义文件名
     * @param subPath 子路径
     * @return 访问地址
     */
    UploadResult upload(InputStream stream, String originalFilename, String fileName, String subPath) throws FileException;

    /**
     * 返回文件类型
     * @param fileName
     * @return e.g .jpg
     */
    default String getFileType(String fileName) {
        return fileName.replaceFirst(".*\\.", ".");
    }

    /**
     * 删除上传根目录下指定子路径的文件/目录(用于业务关联文件的级联清理, 如删除会话时清理其媒体文件)
     * <p>子路径不存在或删除失败返回false; 默认实现不删除, 由具体上传服务覆盖</p>
     * @param subPath 子路径(如 /ai/agent/admin/1)
     * @return 是否删除成功
     */
    default boolean delete(String subPath) {
        return false;
    }

    /**
     * 读取已上传文件的输入流
     * <p>默认不支持(抛FileException), 由具体上传服务实现: 本地存储按URL反查磁盘文件, 对象存储按URL拉取</p>
     * @param url upload返回的访问地址, 支持：{@link FrameworkProperties#getFileServerDomain()/{subPath}/{fileName}}, /{subPath}/{fileName}
     * @return 文件输入流(调用方负责关闭)
     * @throws FileException 不支持读取或文件不存在
     */
    default InputStream read(String url) throws FileException {
        File file = readFile(url);
        try {
            return new FileInputStream(file);
        } catch (IOException e) {
            throw new FileException("文件读取失败: " + url, e);
        }
    }

    File readFile(String url) throws FileException;
}
