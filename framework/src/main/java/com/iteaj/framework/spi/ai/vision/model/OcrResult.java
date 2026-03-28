package com.iteaj.framework.spi.ai.vision.model;

/**
 * OCR 文字识别结果
 * <p>对应图像中识别到的单个文字区域</p>
 */
public class OcrResult {

    /** 识别出的文字内容 */
    private String text;

    /** 识别置信度（0~1） */
    private float confidence;

    /** 文字区域左上角 X 坐标（像素） */
    private float x;

    /** 文字区域左上角 Y 坐标（像素） */
    private float y;

    /** 文字区域宽度（像素） */
    private float width;

    /** 文字区域高度（像素） */
    private float height;

    /** 文字旋转角度（度），用于倾斜文字场景 */
    private float angle;

    /**
     * 获取识别出的文字内容
     */
    public String getText() {
        return text;
    }

    public OcrResult setText(String text) {
        this.text = text;
        return this;
    }

    /**
     * 获取识别置信度
     */
    public float getConfidence() {
        return confidence;
    }

    public OcrResult setConfidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * 获取文字区域左上角 X 坐标
     */
    public float getX() {
        return x;
    }

    public OcrResult setX(float x) {
        this.x = x;
        return this;
    }

    /**
     * 获取文字区域左上角 Y 坐标
     */
    public float getY() {
        return y;
    }

    public OcrResult setY(float y) {
        this.y = y;
        return this;
    }

    /**
     * 获取文字区域宽度
     */
    public float getWidth() {
        return width;
    }

    public OcrResult setWidth(float width) {
        this.width = width;
        return this;
    }

    /**
     * 获取文字区域高度
     */
    public float getHeight() {
        return height;
    }

    public OcrResult setHeight(float height) {
        this.height = height;
        return this;
    }

    /**
     * 获取文字旋转角度
     */
    public float getAngle() {
        return angle;
    }

    public OcrResult setAngle(float angle) {
        this.angle = angle;
        return this;
    }
}
