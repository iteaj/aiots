package com.iteaj.framework.spi.ai.vision.model;

import java.util.List;

/**
 * 人体姿态估计结果
 * <p>包含人体边界框及各关键点坐标，基于 COCO 17 关键点格式</p>
 */
public class PoseResult {

    /** 人体检测置信度（0~1） */
    private float confidence;

    /** 人体边界框左上角 X 坐标（像素） */
    private float x;

    /** 人体边界框左上角 Y 坐标（像素） */
    private float y;

    /** 人体边界框宽度（像素） */
    private float width;

    /** 人体边界框高度（像素） */
    private float height;

    /** 关键点列表，基于 COCO 格式共 17 个关键点，如鼻子、双眼、双肩、双肘、双腕、双髋、双膝、双踝 */
    private List<Keypoint> keypoints;

    /**
     * 获取人体检测置信度
     */
    public float getConfidence() {
        return confidence;
    }

    public PoseResult setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * 获取人体边界框左上角 X 坐标
     */
    public float getX() {
        return x;
    }

    public PoseResult setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * 获取人体边界框左上角 Y 坐标
     */
    public float getY() {
        return y;
    }

    public PoseResult setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * 获取人体边界框宽度
     */
    public float getWidth() {
        return width;
    }

    public PoseResult setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * 获取人体边界框高度
     */
    public float getHeight() {
        return height;
    }

    public PoseResult setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * 获取关键点列表
     */
    public List<Keypoint> getKeypoints() {
        return keypoints;
    }

    public PoseResult setKeypoints(List<Keypoint> keypoints) {
        this.keypoints = keypoints;
        return this;
    }
}
