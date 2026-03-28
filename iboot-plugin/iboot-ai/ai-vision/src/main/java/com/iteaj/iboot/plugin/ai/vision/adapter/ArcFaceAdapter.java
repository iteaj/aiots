package com.iteaj.iboot.plugin.ai.vision.adapter;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import com.iteaj.framework.spi.ai.vision.VisionParams;
import com.iteaj.framework.spi.ai.vision.adapter.FaceFeatureAdapter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * ArcFace 人脸特征提取适配器
 * <p>适用于 InsightFace ArcFace/CosFace 系列 ONNX 模型（buffalo 系列 w600k_r50/mbf 等）</p>
 *
 * <p><b>输入规格：</b>{@code [1, 3, 112, 112]}，RGB，值域 [-1, 1]（标准归一化：pixel/127.5 - 1）</p>
 * <p><b>输出规格：</b>{@code [1, 512]}，L2 归一化特征向量，用于余弦相似度计算</p>
 *
 * <p><b>支持的模型名称：</b></p>
 * <ul>
 *   <li>{@code arcface*} — 通用 ArcFace 系列</li>
 *   <li>{@code w600k*} — buffalo 系列主流特征提取模型（如 w600k_r50.onnx）</li>
 *   <li>{@code mobilefacenet*}、{@code mbf*} — 轻量版移动端模型</li>
 * </ul>
 *
 * <p><b>参数说明（通过 {@link VisionParams} 传入）：</b></p>
 * <ul>
 *   <li>无额外参数，输入输出规格固定</li>
 * </ul>
 *
 * <p><b>使用场景：</b>配合 {@link RetinaFaceAdapter} / {@link ScrfdAdapter} 检测人脸并对齐后，
 * 将裁剪+仿射变换后的 112×112 人脸图像送入本适配器提取特征</p>
 */
@Component
@Order(1000)
public class ArcFaceAdapter extends AbstractModelAdapter<float[]>
        implements FaceFeatureAdapter {

    /** ArcFace 标准输入尺寸 */
    private static final int INPUT_SIZE = 112;

    @Override
    public boolean supports(String modelNameOrPath) {
        String lower = modelNameOrPath.toLowerCase();
        // 匹配 arcface*、w600k*（buffalo 系列主流特征提取模型）、mbf*/mobilefacenet*
        return lower.contains("arcface") || lower.contains("w600k")
                || lower.contains("mobilefacenet") || lower.startsWith("mbf");
    }

    @Override
    public int inputSize() {
        return INPUT_SIZE;
    }

    /**
     * 前处理：图像转张量，归一化到 [-1, 1]
     * <p>公式：{@code (pixel / 127.5) - 1}，等同于 {@code (pixel - 127.5) / 127.5}</p>
     * <p>输出 shape：{@code [1, 3, 112, 112]}</p>
     */
    @Override
    public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
        Image djlImage = ImageFactory.getInstance().fromImage(image);
        NDArray base = djlImage.toNDArray(manager, Image.Flag.COLOR)
                .toType(DataType.FLOAT32, false)
                .transpose(2, 0, 1)     // HWC → CHW
                .expandDims(0);         // CHW → [1, C, H, W]
        // 归一化到 [-1, 1]：pixel / 127.5 - 1
        return base.div(127.5f).sub(1.0f);
    }

    /**
     * 后处理：提取 512 维特征向量并执行 L2 归一化
     * <p>L2 归一化后的向量可直接用余弦相似度（点积）计算人脸相似度</p>
     *
     * @return L2 归一化后的 512 维特征向量
     */
    @Override
    public float[] postprocess(NDList output, int origW, int origH, VisionParams params) {
        NDArray feat = output.get(0).squeeze(); // [1, 512] → [512]
        // L2 归一化：向量除以自身的 L2 范数
        float norm = (float) Math.sqrt(feat.mul(feat).sum().toFloatArray()[0]);
        if (norm > 1e-6f) {
            feat = feat.div(norm);
        }
        return feat.toFloatArray();
    }

    /**
     * 中心裁剪缩放：将任意尺寸图像缩放到 112×112（用于对齐后的人脸图像）
     * <p>覆盖父类的 letterboxResize，ArcFace 直接拉伸到固定尺寸即可（人脸已对齐，无需 letterbox）</p>
     */
    @Override
    public BufferedImage letterboxResize(BufferedImage src, int targetW, int targetH) {
        // 人脸特征提取：直接缩放，无需保持宽高比（输入已是裁剪对齐的人脸图像）
        BufferedImage result = new BufferedImage(targetW, targetH, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(src.getScaledInstance(targetW, targetH, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();
        return result;
    }
}
