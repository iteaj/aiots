package com.iteaj.framework.spi.file;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.exception.ServiceException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class LocalUploadService implements UploadService, InitializingBean {

    private File rootDir; // 上传的跟目录
    private String uploadRootUri;
    private final FrameworkProperties properties;
    private String commonSubPath = File.separator + "file" + File.separator; // 默认的子路径

    public LocalUploadService(FrameworkProperties properties) {
        this.properties = properties;
    }

    @Override
    public UploadResult upload(InputStream stream, String originalFilename) throws FileException {
        return this.upload(stream, originalFilename, this.commonSubPath);
    }

    @Override
    public UploadResult upload(InputStream stream, String originalFilename, String subPath) throws FileException {
        return this.upload(stream, originalFilename, null, subPath);
    }

    @Override
    public UploadResult upload(InputStream stream, String originalFilename, String fileName, String subPath) throws FileException {
        try {
            fileName = StrUtil.isNotBlank(fileName) ? fileName :  UUID.randomUUID().toString();
            String filePath = genFilePath(originalFilename, fileName, subPath);

            File file = new File(rootDir, filePath);
            FileUtil.writeFromStream(stream, file);
            return new UploadResult(uploadRootUri + filePath, file, fileName, originalFilename)
                    .setFileBasicUrl(properties.getFileServerDomain());
        } catch (IORuntimeException e) {
            throw new FileException(e);
        }
    }

    /**
     * 生成文件路径, 使用指定的文件名
     * @param originalFilename 上传的文件名, 用来获取文件类型
     * @param fileName // 要创建的文件的文件名
     * @param subPath // 生成文件路径的子路径
     * @return
     */
    private String genFilePath(String originalFilename, String fileName, String subPath) {
        // 生成文件名
        String fileType = getFileType(originalFilename);
        // 文件名包含了文件后缀
        fileName = fileName.contains(fileType) ? fileName : fileName + fileType;

        // 生成文件路径
        if(!subPath.endsWith("/")) {
            subPath = subPath + "/";
        }

        return subPath + fileName;
    }

    /**
     * 删除上传根目录下指定子路径的文件/目录(路径越界防护: 禁止删除根目录自身)
     * @param subPath 子路径(如 /ai/agent/admin/1)
     * @return 是否删除成功(子路径不存在或为空返回false)
     */
    @Override
    public boolean delete(String subPath) {
        if (StrUtil.isBlank(subPath) || this.rootDir == null) {
            return false;
        }
        File target = new File(this.rootDir, subPath.replace("/", File.separator));
        if (!target.getAbsolutePath().startsWith(this.rootDir.getAbsolutePath() + File.separator)) {
            return false; // 路径越界防护
        }
        if (!target.exists()) {
            return false;
        }
        return FileUtil.del(target);
    }

    /**
     * 读取已上传文件(按访问URL反查本地磁盘文件): url = uploadRootUri + filePath, 剥离前缀拼rootDir定位
     * @param url upload返回的访问地址(如 /profile/ai/attachment/x.png)
     * @return 文件输入流(调用方负责关闭)
     * @throws FileException URL为空/根目录未就绪/文件不存在或路径越界
     */
    @Override
    public File readFile(String url) throws FileException {
        if(StrUtil.isBlank(url)) {
            throw new FileException("无效的url: " + url);
        }
        if(url.startsWith("http")) {
            if(url.startsWith(properties.getFileServerDomain())) {
                url = url.substring(properties.getFileServerDomain().length());
            } else {
                throw new FileException("不支持的url, 必须以平台基础url开头(" + properties.getFileServerDomain() + ")");
            }
        }

        if (this.rootDir == null || StrUtil.isBlank(this.uploadRootUri)) {
            throw new FileException("无法读取文件, URL或上传根目录未就绪: " + url);
        }
        // 剥离访问前缀得相对路径, 拼上传根目录定位本地文件
        String filePath = url.startsWith(this.uploadRootUri) ? url.substring(this.uploadRootUri.length()) : url;
        File file = new File(this.rootDir, filePath.replace("/", File.separator));
        // 路径越界防护: 禁止读取上传根目录之外的文件
        if (!file.getAbsolutePath().startsWith(this.rootDir.getAbsolutePath() + File.separator)
                || !file.exists() || !file.isFile()) {
            throw new FileException("文件不存在或路径非法: " + url);
        }

        return file;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        FrameworkProperties.Upload upload = properties.getWeb().getUpload();
        String uploadRootDir = upload.getLocation();
        if(!StrUtil.isBlank(uploadRootDir)) {
            uploadRootDir = uploadRootDir.contains("file:") ?
                    uploadRootDir.split("file:")[1]
                    : uploadRootDir.split("classpath:")[1];

            if(uploadRootDir.endsWith(File.separator)) {
                uploadRootDir = uploadRootDir.substring(0, uploadRootDir.length()-1);
            }

            this.rootDir = new File(uploadRootDir);
            if(!this.rootDir.exists()) {
                this.rootDir.mkdirs();
            }

            if(!this.rootDir.isDirectory()) {
                throw new ServiceException("文件的上传根路径必须是目录："+uploadRootDir);
            }
        }
        String uploadRootUri = upload.getPattern();
        if(!StrUtil.isBlank(uploadRootUri)) {
            if(uploadRootUri.contains("*")) {
                uploadRootUri = uploadRootUri.replaceAll("\\*", "");
            }

            if(uploadRootUri.endsWith("/")) {
                uploadRootUri = uploadRootUri.substring(0, uploadRootUri.length()-1);
            }

            this.uploadRootUri = uploadRootUri;
        } else {
            throw new ServiceException("未配置上传文件的访问pattern[framework.web.upload.pattern]");
        }
    }
}
