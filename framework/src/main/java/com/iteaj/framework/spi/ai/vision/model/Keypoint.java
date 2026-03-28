package com.iteaj.framework.spi.ai.vision.model;

/**
 * 姿态估计关键点
 * <p>用于人体姿态估计结果中的单个关键点信息</p>
 */
public class Keypoint {

    /** 关键点名称，如 left_eye、right_knee（COCO 格式） */
    private String name;

    /** X 坐标（像素） */
    private float x;

    /** Y 坐标（像素） */
    private float y;

    /** 关键点置信度（0~1） */
    private float confidence;

    public Keypoint() {}

    public Keypoint(String name, float x, float y, float confidence) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.confidence = confidence;
    }

    /**
     * 获取关键点名称
     */
    public String getName() {
        return name;
    }

    public Keypoint setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * 获取 X 坐标
     */
    public float getX() {
        return x;
    }

    public Keypoint setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * 获取 Y 坐标
     */
    public float getY() {
        return y;
    }

    public Keypoint setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * 获取关键点置信度
     */
    public float getConfidence() {
        return confidence;
    }

    public Keypoint setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }
}
