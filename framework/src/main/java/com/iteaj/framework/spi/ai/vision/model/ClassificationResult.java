package com.iteaj.framework.spi.ai.vision.model;

import java.util.List;

/**
 * 图像分类结果
 */
public class ClassificationResult {

    /** 最高置信度的类别名称 */
    private String className;

    /** 最高置信度的类别索引 */
    private int classIndex;

    /** 最高置信度（0~1） */
    private float confidence;

    /** Top-N 候选结果列表，按置信度降序排列 */
    private List<ClassItem> topN;

    /**
     * 获取最高置信度类别名称
     */
    public String getClassName() {
        return className;
    }

    public ClassificationResult setClassName(String className) {
        this.className = className;
        return this;
    }

    /**
     * 获取最高置信度类别索引
     */
    public int getClassIndex() {
        return classIndex;
    }

    public ClassificationResult setClassIndex(int classIndex) {
        this.classIndex = classIndex;
        return this;
    }

    /**
     * 获取最高置信度
     */
    public float getConfidence() {
        return confidence;
    }

    public ClassificationResult setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * 获取 Top-N 候选结果列表
     */
    public List<ClassItem> getTopN() {
        return topN;
    }

    public ClassificationResult setTopN(List<ClassItem> topN) {
        this.topN = topN;
        return this;
    }
}
