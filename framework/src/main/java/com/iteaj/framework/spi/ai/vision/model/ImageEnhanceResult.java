package com.iteaj.framework.spi.ai.vision.model;

/**
 * 图像增强结果（超分辨率 / 降噪等）
 */
public class ImageEnhanceResult {

    /** 增强后的图像字节数据（JPEG/PNG 格式） */
    private byte[] resultImage;

    /** 增强后图像宽度（像素） */
    private int imageWidth;

    /** 增强后图像高度（像素） */
    private int imageHeight;

    /** 放大倍数，超分辨率场景适用，如 2、4 */
    private int scaleFactor;

    /**
     * 获取增强后的图像字节数据
     */
    public byte[] getResultImage() {
        return resultImage;
    }

    public ImageEnhanceResult setResultImage(byte[] resultImage) {
        this.resultImage = resultImage;
        return this;
    }

    /**
     * 获取增强后图像宽度
     */
    public int getImageWidth() {
        return imageWidth;
    }

    public ImageEnhanceResult setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
        return this;
    }

    /**
     * 获取增强后图像高度
     */
    public int getImageHeight() {
        return imageHeight;
    }

    public ImageEnhanceResult setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
        return this;
    }

    /**
     * 获取放大倍数
     */
    public int getScaleFactor() {
        return scaleFactor;
    }

    public ImageEnhanceResult setScaleFactor(int scaleFactor) {
        this.scaleFactor = scaleFactor;
        return this;
    }
}
