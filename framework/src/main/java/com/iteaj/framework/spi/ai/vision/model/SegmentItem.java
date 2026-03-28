package com.iteaj.framework.spi.ai.vision.model;

import java.util.List;

/**
 * 图像分割区域
 * <p>用于图像分割结果中的单个分割对象信息</p>
 */
public class SegmentItem {

    /** 类别名称 */
    private String className;

    /** 类别索引 */
    private int classIndex;

    /** 置信度（0~1） */
    private float confidence;

    /** 掩码数据（与原图等尺寸的二值图，1 表示前景，0 表示背景） */
    private byte[] mask;

    /** 轮廓点坐标列表 */
    private List<Point> contour;

    /**
     * 获取类别名称
     */
    public String getClassName() {
        return className;
    }

    public SegmentItem setClassName(String className) {
        this.className = className;
        return this;
    }

    /**
     * 获取类别索引
     */
    public int getClassIndex() {
        return classIndex;
    }

    public SegmentItem setClassIndex(int classIndex) {
        this.classIndex = classIndex;
        return this;
    }

    /**
     * 获取置信度
     */
    public float getConfidence() {
        return confidence;
    }

    public SegmentItem setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * 获取掩码数据
     */
    public byte[] getMask() {
        return mask;
    }

    public SegmentItem setMask(byte[] mask) {
        this.mask = mask;
        return this;
    }

    /**
     * 获取轮廓点列表
     */
    public List<Point> getContour() {
        return contour;
    }

    public SegmentItem setContour(List<Point> contour) {
        this.contour = contour;
        return this;
    }
}
