package com.iteaj.framework.spi.ai.vision.model;

import java.util.List;

/**
 * 人脸识别结果
 * <p>包含人脸检测框、关键点及人员匹配信息</p>
 */
public class FaceResult {

    /** 人脸唯一标识，与人员库比对后返回，未比对则为 null */
    private String faceId;

    /** 匹配到的人员姓名，未匹配则为 null */
    private String personName;

    /** 检测或匹配置信度（0~1） */
    private float confidence;

    /** 人脸框左上角 X 坐标（像素） */
    private float x;

    /** 人脸框左上角 Y 坐标（像素） */
    private float y;

    /** 人脸框宽度（像素） */
    private float width;

    /** 人脸框高度（像素） */
    private float height;

    /** 人脸关键点列表（通常包含双眼、鼻尖、嘴角共 5 个点） */
    private List<Point> landmarks;

    /**
     * 获取人脸唯一标识
     */
    public String getFaceId() {
        return faceId;
    }

    public FaceResult setFaceId(String faceId) {
        this.faceId = faceId;
        return this;
    }

    /**
     * 获取匹配到的人员姓名
     */
    public String getPersonName() {
        return personName;
    }

    public FaceResult setPersonName(String personName) {
        this.personName = personName;
        return this;
    }

    /**
     * 获取置信度
     */
    public float getConfidence() {
        return confidence;
    }

    public FaceResult setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * 获取人脸框左上角 X 坐标
     */
    public float getX() {
        return x;
    }

    public FaceResult setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * 获取人脸框左上角 Y 坐标
     */
    public float getY() {
        return y;
    }

    public FaceResult setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * 获取人脸框宽度
     */
    public float getWidth() {
        return width;
    }

    public FaceResult setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * 获取人脸框高度
     */
    public float getHeight() {
        return height;
    }

    public FaceResult setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * 获取人脸关键点列表
     */
    public List<Point> getLandmarks() {
        return landmarks;
    }

    public FaceResult setLandmarks(List<Point> landmarks) {
        this.landmarks = landmarks;
        return this;
    }
}
