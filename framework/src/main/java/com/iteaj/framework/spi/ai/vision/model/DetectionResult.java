package com.iteaj.framework.spi.ai.vision.model;

/**
 * 目标检测结果
 * <p>对应图像中检测到的单个目标，包含类别信息和边界框坐标</p>
 */
public class DetectionResult {

    /** 类别名称，如 person、car */
    private String className;

    /** 类别索引 */
    private int classIndex;

    /** 置信度（0~1） */
    private float confidence;

    /** 边界框左上角 X 坐标（像素） */
    private float x;

    /** 边界框左上角 Y 坐标（像素） */
    private float y;

    /** 边界框宽度（像素） */
    private float width;

    /** 边界框高度（像素） */
    private float height;

    /**
     * 获取类别名称
     */
    public String getClassName() {
        return className;
    }

    public DetectionResult setClassName(String className) {
        this.className = className;
        return this;
    }

    /**
     * 获取类别索引
     */
    public int getClassIndex() {
        return classIndex;
    }

    public DetectionResult setClassIndex(int classIndex) {
        this.classIndex = classIndex;
        return this;
    }

    /**
     * 获取置信度
     */
    public float getConfidence() {
        return confidence;
    }

    public DetectionResult setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * 获取边界框左上角 X 坐标
     */
    public float getX() {
        return x;
    }

    public DetectionResult setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * 获取边界框左上角 Y 坐标
     */
    public float getY() {
        return y;
    }

    public DetectionResult setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * 获取边界框宽度
     */
    public float getWidth() {
        return width;
    }

    public DetectionResult setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * 获取边界框高度
     */
    public float getHeight() {
        return height;
    }

    public DetectionResult setHeight(float height) {
        this.height = height;
        return this;
    }
}
