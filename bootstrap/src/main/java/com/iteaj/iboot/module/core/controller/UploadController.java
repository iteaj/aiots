package com.iteaj.iboot.module.core.controller;

import cn.hutool.core.util.StrUtil;
import com.iteaj.framework.BaseController;
import com.iteaj.framework.autoconfigure.FrameworkProperties;
import com.iteaj.framework.logger.Logger;
import com.iteaj.framework.result.Result;
import com.iteaj.framework.security.SecurityUtil;
import com.iteaj.iboot.module.core.entity.Admin;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 通用的文件上传
 * @author iteaj
 * @since 1.0
 */
@RestController
@RequestMapping("/common/upload")
public class UploadController extends BaseController implements InitializingBean {

    private File rootDir; // 上传的跟目录
    private String uploadRootUri;
    private String commonSubPath = "/common/"; // 默认的子路径
    private final FrameworkProperties properties;

    public UploadController(FrameworkProperties properties) {
        this.properties = properties;
    }

    /**
     * 文件上传
     * todo url
     * @param file
     * @return
     */
    @Logger("上传单文件")
    @PostMapping
    public Result<String> upload(MultipartFile file, String subPath) {
        try {
            String fileUrl = this.createFile(file, subPath);
            return success(fileUrl, "上传成功");
        } catch (IOException e) {
            return fail("上传失败");
        }
    }

    /**
     * 文件批量上传
     * @param file
     * @return
     */
    @Logger("批量上传文件")
    @PostMapping("/batch")
    public Result<List<String>> batch(MultipartFile[] file, String subPath) {
        List<String> urls = new ArrayList<>();

        try {
            Arrays.asList(file).forEach(item -> {
                try {
                    urls.add(this.createFile(item, subPath));
                } catch (IOException e) {
                    throw new RuntimeException(e.getMessage(), e);
                }

            });
        } catch (Exception e) {
            return fail("上传失败");
        }

        return success(urls);
    }

    /**
     * 上传用户头像
     * @param avatar
     * @return
     */
    @PostMapping("avatar")
    public Result avatar(MultipartFile avatar) {
        try {
            Admin admin = SecurityUtil.getLoginUser().map(item -> (Admin) item).get();
            String fileUrl = this.createFile(avatar, "avatar_"+admin.getId(), "/avatar");
            return success(fileUrl, "上传成功");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            return fail("上传失败");
        }
    }

    private String createFile(MultipartFile file, String subPath) throws IOException {
        String filePath = genFilePath(file.getOriginalFilename(), subPath);

        InputStream inputStream = file.getInputStream();

        File outFile = new File(this.rootDir, filePath);
        if(!outFile.getParentFile().exists())
            outFile.mkdirs();

        FileCopyUtils.copy(inputStream, new FileOutputStream(outFile));

        return uploadRootUri + filePath;
    }

    /**
     * 创建文件
     * @param file
     * @param fileName
     * @param subPath
     * @return
     * @throws IOException
     */
    private String createFile(MultipartFile file, String fileName, String subPath) throws IOException {
        String filePath = genFilePath(file.getOriginalFilename(), fileName, subPath);

        InputStream inputStream = file.getInputStream();

        File outFile = new File(this.rootDir, filePath);
        if(!outFile.getParentFile().exists())
            outFile.mkdirs();

        // 文件不存在创建
        if(!outFile.exists()) outFile.createNewFile();

        FileCopyUtils.copy(inputStream, new FileOutputStream(outFile));

        return uploadRootUri + filePath;
    }

    /**
     * 生成文件路径, 文件名使用uuid
     * @param originalFilename
     * @param subPath
     * @return
     */
    private String genFilePath(String originalFilename, String subPath) {
        return this.genFilePath(originalFilename, null, subPath);
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
        fileName = StrUtil.isNotBlank(fileName) ? fileName + fileType
                :  UUID.randomUUID() + fileType;

        // 生成文件路径
        if(StrUtil.isBlank(subPath)) subPath = commonSubPath;
        else if(!subPath.endsWith("/")) subPath = subPath + "/";

        return subPath + fileName;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        FrameworkProperties.Upload upload = properties.getWeb().getUpload();
        String uploadRootDir = upload.getLocation();
        if(!StrUtil.isBlank(uploadRootDir)) {
            uploadRootDir = uploadRootDir.contains("file:") ?
                    uploadRootDir.split("file:")[1]
                    : uploadRootDir.split("classpath:")[1];

            if(uploadRootDir.endsWith("/")) {
                uploadRootDir = uploadRootDir.substring(0, uploadRootDir.length()-1);
            }

            this.rootDir = new File(uploadRootDir);
            if(!this.rootDir.exists()) {
                this.rootDir.mkdirs();
            }

            if(!this.rootDir.isDirectory()) {
                logger.warn("文件的上传根路径必须是目录："+uploadRootDir);
                return;
            }
        }
        String uploadRootUri = upload.getPattern();
        if(!StrUtil.isBlank(uploadRootUri)) {
            if(uploadRootUri.contains("*")) {
                uploadRootUri = uploadRootUri.replaceAll("\\*", "");
            }

            if(uploadRootUri.endsWith("/")) {
                this.uploadRootUri = uploadRootUri.substring(0, uploadRootUri.length()-1);
            }

            this.uploadRootUri = uploadRootUri;
        } else {
            logger.error("未配置上传文件的访问pattern[framework.upload-pattern]");
        }
    }

    public String getFileType(String fileName) {
        return fileName.replaceFirst(".*\\.", ".");
    }
}
