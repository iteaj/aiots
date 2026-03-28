package com.iteaj.framework.spi.ai.vision.model;

import java.util.List;

/**
 * 图像分割结果
 */
public class SegmentationResult {

    /** 分割区域列表，每个元素对应图像中一个分割对象 */
    private List<SegmentItem> segments;

    /** 原始图像宽度（像素） */
    private int imageWidth;

    /** 原始图像高度（像素） */
    private int imageHeight;

    /**
     * 获取分割区域列表
     */
    public List<SegmentItem> getSegments() {
        return segments;
    }

    public SegmentationResult setSegments(List<SegmentItem> segments) {
        this.segments = segments;
        return this;
    }

    /**
     * 获取原始图像宽度
     */
    public int getImageWidth() {
        return imageWidth;
    }

    public SegmentationResult setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
        return this;
    }

    /**
     * 获取原始图像高度
     */
    public int getImageHeight() {
        return imageHeight;
    }

    public SegmentationResult setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
        return this;
    }
}
