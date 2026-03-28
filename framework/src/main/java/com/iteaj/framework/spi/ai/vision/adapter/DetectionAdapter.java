package com.iteaj.framework.spi.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.model.DetectionResult;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 目标检测模型适配器接口
 * <p>适用于 YOLO 系列等目标检测模型，后处理返回检测框列表</p>
 *
 * <p>内置实现：</p>
 * <ul>
 *   <li>{@code YoloV8DetectionAdapter} — 匹配 {@code yolov8*}、{@code yolo*}</li>
 *   <li>{@code YoloV5DetectionAdapter} — 匹配 {@code yolov5*}</li>
 * </ul>
 */
public interface DetectionAdapter extends ModelAdapter<List<DetectionResult>> {

    /**
     * Letterbox Resize：保持宽高比缩放，不足部分用灰色（114,114,114）填充
     * <p>由 VisionAnalyzer 在调用 {@link #preprocess} 前负责调用，保证输入尺寸符合模型要求</p>
     * <p>可在子接口或实现类中覆盖，使用不同的填充色或插值方式</p>
     *
     * @param src     原始图像
     * @param targetW 目标宽度
     * @param targetH 目标高度
     * @return 缩放+填充后的图像
     */
    default BufferedImage letterboxResize(BufferedImage src, int targetW, int targetH) {
        float scale = Math.min((float) targetW / src.getWidth(), (float) targetH / src.getHeight());
        int newW = (int) (src.getWidth() * scale);
        int newH = (int) (src.getHeight() * scale);
        int padX = (targetW - newW) / 2;
        int padY = (targetH - newH) / 2;

        BufferedImage result = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();
        // 灰色背景填充（YOLO 标准填充色）
        g2d.setColor(new Color(114, 114, 114));
        g2d.fillRect(0, 0, targetW, targetH);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(src.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH), padX, padY, null);
        g2d.dispose();
        return result;
    }
}
