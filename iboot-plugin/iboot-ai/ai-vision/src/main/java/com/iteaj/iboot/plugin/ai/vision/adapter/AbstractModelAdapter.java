package com.iteaj.iboot.plugin.ai.vision.adapter;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import com.iteaj.framework.spi.ai.vision.VisionParams;
import com.iteaj.framework.spi.ai.vision.adapter.ModelAdapter;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 模型适配器公共抄象基类
 * <p>在 SPI 层 {@link ModelAdapter} 之上补充 DJL 相关的抖象方法声明，
 * 并提供图像预处理的通用工具方法，避免各适配器重复实现</p>
 * <ul>
 *   <li>{@link #preprocess(NDManager, BufferedImage, VisionParams)} — 图像 → 推理输入张量（抽象方法，子类实现）</li>
 *   <li>{@link #postprocess(NDList, int, int, VisionParams)} — 模型原始输出 → 业务结果（抽象方法，子类实现）</li>
 *   <li>{@link #letterboxResize(BufferedImage, int, int)} — 保持宽高比的 Letterbox 缩放</li>
 *   <li>{@link #toNDArray(NDManager, BufferedImage, VisionParams)} — 标准归一化 [0,1] 转张量</li>
 *   <li>{@link #toNDArrayNormalized(NDManager, BufferedImage, float, float)} — 自定义区间归一化转张量</li>
 * </ul>
 *
 * @param <O> 后处理输出类型
 */
public abstract class AbstractModelAdapter<O> implements ModelAdapter<O> {

    /**
     * 默认引擎：OnnxRuntime（适合 .onnx 模型）
     */
    @Override
    public String engine() {
        return "OnnxRuntime";
    }

    // ───────────────────────── DJL 抽象方法 ─────────────────────────

    /**
     * 图像预处理：将缩放后的 {@link BufferedImage} 转为模型输入张量
     * <p>输出 shape 通常为 {@code [1, 3, H, W]}，具体取决于模型要求</p>
     *
     * @param manager NDManager，用于创建张量
     * @param image   已由框架 Letterbox 缩放到 {@link #inputSize()} 的图像
     * @param params  推理参数，可通过 extraParams 读取模型专有参数
     * @return 模型输入张量
     */
    public abstract NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params);

    /**
     * 结果后处理：将模型原始输出解析为业务结果对象
     *
     * @param output 模型原始输出（NDList）
     * @param origW  原始图像宽度（像素），用于将归一化坐标反算为像素坐标
     * @param origH  原始图像高度（像素）
     * @param params 推理参数（置信度閘値、NMS 閘値等）
     * @return 业务结果对象
     */
    public abstract O postprocess(NDList output, int origW, int origH, VisionParams params);

    /**
     * Letterbox Resize：保持宽高比缩放，不足部分用灰色（114,114,114）填充
     * <p>YOLO 系列模型标准预处理，保持目标比例不变形</p>
     *
     * @param src     原始图像
     * @param targetW 目标宽度
     * @param targetH 目标高度
     * @return 缩放+填充后的图像
     */
    @Override
    public BufferedImage letterboxResize(BufferedImage src, int targetW, int targetH) {
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

    /**
     * 图像转张量，归一化到 [0, 1]（YOLO 系列标准输入）
     * <p>使用 DJL {@link ImageFactory} 转换，内部走 native 批量操作，性能优于逐像素读取</p>
     * <p>输出 shape：[1, 3, H, W]，通道顺序 RGB，CHW 格式，FLOAT32 类型</p>
     *
     * @param manager NDManager
     * @param image   已缩放到目标尺寸的图像
     * @param params  推理参数（当前方法未使用，预留给子类覆盖时使用）
     * @return 模型输入张量
     */
    protected NDArray toNDArray(NDManager manager, BufferedImage image, VisionParams params) {
        // DJL Image 包装 BufferedImage，toNDArray 输出 HWC uint8
        // div(255) → [0,1]，transpose(2,0,1) → CHW，expandDims(0) → [1,C,H,W]
        Image djlImage = ImageFactory.getInstance().fromImage(image);
        return djlImage.toNDArray(manager, Image.Flag.COLOR)
                .div(255f)
                .transpose(2, 0, 1)
                .expandDims(0)
                .toType(DataType.FLOAT32, false);
    }

    /**
     * 图像转张量，归一化到自定义区间 [minVal, maxVal]
     * <p>先归一化到 [0, 1]，再线性映射：{@code value = minVal + pixel_01 * (maxVal - minVal)}</p>
     * <p>ArcFace 标准输入区间为 [-1, 1]，即 minVal=-1, maxVal=1</p>
     *
     * @param manager NDManager
     * @param image   已缩放到目标尺寸的图像
     * @param minVal  目标区间下界
     * @param maxVal  目标区间上界
     * @return 模型输入张量，值域 [minVal, maxVal]
     */
    protected NDArray toNDArrayNormalized(NDManager manager, BufferedImage image, float minVal, float maxVal) {
        NDArray base = toNDArray(manager, image, null);
        return base.mul(maxVal - minVal).add(minVal);
    }

    /**
     * 从 extraParams 中读取 float 参数
     *
     * @param params       推理参数
     * @param key          参数键名
     * @param defaultValue 未设置时的默认值
     * @return 参数值
     */
    protected float extraFloat(VisionParams params, String key, float defaultValue) {
        if (params != null && params.getExtraParams() != null) {
            Object val = params.getExtraParams().get(key);
            if (val instanceof Number) return ((Number) val).floatValue();
        }
        return defaultValue;
    }
}
