package com.iteaj.develop.gen;

/**
 * create time: 2020/7/2
 *
 * @author iteaj
 * @since 1.0
 */
public class GenConfig {

    /**
     * 模块别名, 默认为项目名称
     *  模块别名将作为包路径、url路径、权限路径的一部分
     */
    private String alias;

    /**
     * 项目名称
     */
    private String project;

    /**
     * 是否直接生成文件到项目
     */
    private boolean write;

    public String getAlias() {
        return alias;
    }

    public GenConfig setAlias(String alias) {
        this.alias = alias;
        return this;
    }

    public String getProject() {
        return project;
    }

    public GenConfig setProject(String project) {
        this.project = project;
        return this;
    }

    public boolean isWrite() {
        return write;
    }

    public GenConfig setWrite(boolean write) {
        this.write = write;
        return this;
    }
}
