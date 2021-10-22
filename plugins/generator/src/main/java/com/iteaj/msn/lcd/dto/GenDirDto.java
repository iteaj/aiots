package com.iteaj.msn.lcd.dto;

import lombok.Data;

@Data
public class GenDirDto {

    /**
     * 自定义生成目录
     */
    private String cusGenDir;

    /**
     * 项目根目录
     */
    private String projectRoot;

    public GenDirDto(String cusGenDir, String projectRoot) {
        this.cusGenDir = cusGenDir;
        this.projectRoot = projectRoot;
    }
}
