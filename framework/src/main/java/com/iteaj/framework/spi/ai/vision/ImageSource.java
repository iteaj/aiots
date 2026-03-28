package com.iteaj.framework.spi.ai.vision;

import java.io.InputStream;

/**
 * 图像来源
 * <p>封装视觉分析的图像数据，支持 byte[]、InputStream、本地文件路径三种来源，三选一</p>
 */
public class ImageSource {

    /** 图像字节数据（与 imageStream、imagePath 三选一） */
    private byte[] imageBytes;

    /** 图像输入流（与 imageBytes、imagePath 三选一） */
    private InputStream imageStream;

    /** 图像文件本地路径（与 imageBytes、imageStream 三选一） */
    private String imagePath;

    private ImageSource() {}

    /**
     * 从字节数组创建图像来源
     * @param imageBytes 图像字节数据
     * @return ImageSource
     */
    public static ImageSource of(byte[] imageBytes) {
        ImageSource source = new ImageSource();
        source.imageBytes = imageBytes;
        return source;
    }

    /**
     * 从输入流创建图像来源
     * @param imageStream 图像输入流
     * @return ImageSource
     */
    public static ImageSource of(InputStream imageStream) {
        ImageSource source = new ImageSource();
        source.imageStream = imageStream;
        return source;
    }

    /**
     * 从本地文件路径创建图像来源
     * @param imagePath 图像文件路径，如 /images/test.jpg
     * @return ImageSource
     */
    public static ImageSource ofPath(String imagePath) {
        ImageSource source = new ImageSource();
        source.imagePath = imagePath;
        return source;
    }

    /**
     * 获取图像字节数据
     */
    public byte[] getImageBytes() {
        return imageBytes;
    }

    /**
     * 获取图像输入流
     */
    public InputStream getImageStream() {
        return imageStream;
    }

    /**
     * 获取图像文件路径
     */
    public String getImagePath() {
        return imagePath;
    }
}
