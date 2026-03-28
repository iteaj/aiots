package com.iteaj.framework.spi.ai.vision.model;

/**
 * 背景去除结果
 */
public class BackgroundRemovalResult {

    /** 去除背景后的图像字节数据（PNG 格式，透明背景） */
    private byte[] resultImage;

    /** 原始图像宽度（像素） */
    private int imageWidth;

    /** 原始图像高度（像素） */
    private int imageHeight;

    /**
     * 获取去除背景后的图像字节数据
     */
    public byte[] getResultImage() {
        return resultImage;
    }

    public BackgroundRemovalResult setResultImage(byte[] resultImage) {
        this.resultImage = resultImage;
        return this;
    }

    /**
     * 获取原始图像宽度
     */
    public int getImageWidth() {
        return imageWidth;
    }

    public BackgroundRemovalResult setImageWidth(int imageWidth) {
        this.imageWidth = imageWidth;
        return this;
    }

    /**
     * 获取原始图像高度
     */
    public int getImageHeight() {
        return imageHeight;
    }

    public BackgroundRemovalResult setImageHeight(int imageHeight) {
        this.imageHeight = imageHeight;
        return this;
    }
}
