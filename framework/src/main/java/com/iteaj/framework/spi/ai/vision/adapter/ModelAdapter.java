package com.iteaj.framework.spi.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.VisionParams;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * 模型适配器 SPI 顶层接口
 * <p>将「模型名称/路径」与「推理引擎 + 预处理 + 后处理」三件套绑定为一个可插拔单元，
 * 实现模型与解析逻辑的统一封装，支持运行时按模型名称自动路由。</p>
 *
 * <p><b>核心职责：</b></p>
 * <ol>
 *   <li>{@link #supports(String)} — 声明本适配器支持哪些模型（按名称或路径前缀匹配）</li>
 *   <li>{@link #engine()} — 指定推理引擎（OnnxRuntime / PyTorch），null 时自动推断</li>
 *   <li>{@link #inputSize()} — 声明模型期望的输入图像尺寸，由框架负责缩放</li>
 *   <li>{@link #letterboxResize(BufferedImage, int, int)} — Letterbox 缩放（可覆盖）</li>
 *   <li>preprocess / postprocess — 在 ai-vision 层的 {@code AbstractModelAdapter} 中定义，
 *       依赖 DJL NDArray 等框架类型，SPI 层不引入此依赖</li>
 * </ol>
 *
 * <p><b>扩展示例（自定义检测模型，在 ai-vision 实现层）：</b></p>
 * <pre>{@code
 * @Component
 * @Order(100)   // 数值越小优先级越高，内置适配器优先级为 1000
 * public class MyDetectionAdapter extends AbstractModelAdapter<List<DetectionResult>>
 *         implements DetectionAdapter {
 *
 *     @Override
 *     public boolean supports(String model) {
 *         return model.startsWith("mymodel");
 *     }
 *
 *     @Override
 *     public int inputSize() { return 640; }
 *
 *     @Override
 *     public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
 *         // 自定义前处理逻辑
 *     }
 *
 *     @Override
 *     public List<DetectionResult> postprocess(NDList output, int origW, int origH, VisionParams params) {
 *         // 自定义输出解析逻辑
 *     }
 * }
 * }</pre>
 *
 * @param <O> 后处理输出的业务结果类型
 */
public interface ModelAdapter<O> {

    /**
     * 是否支持该模型
     * <p>框架按 Spring {@code @Order} 顺序遍历所有适配器，返回 true 的第一个被使用</p>
     * <p>建议按模型名称前缀匹配，如 {@code model.startsWith("yolov8")}</p>
     *
     * @param modelNameOrPath 模型名称（如 {@code yolov8n}）或完整路径（如 {@code /models/yolov8n.onnx}）
     * @return true 表示本适配器支持该模型
     */
    boolean supports(String modelNameOrPath);

    /**
     * 推理引擎名称
     * <p>对应 DJL 引擎标识：{@code OnnxRuntime} / {@code PyTorch} / {@code TensorFlow}</p>
     * <p>返回 {@code null} 时由 DJL 根据模型文件格式自动推断</p>
     *
     * @return 引擎名称，或 null（自动推断）
     */
    default String engine() {
        return null;
    }

    /**
     * 模型期望的输入图像边长（宽=高的正方形）
     * <p>框架在 preprocess 前会先将图像 Letterbox 缩放到此尺寸</p>
     * <p>常见值：YOLO 系列 640，ArcFace 112，FaceNet 160</p>
     *
     * @return 输入图像边长，单位像素
     */
    int inputSize();

    /**
     * Letterbox Resize：保持宽高比缩放，不足部分用灰色（114,114,114）填充
     * <p>YOLO/ArcFace 等多数视觉模型的标准预处理方式，保持目标比例不变形</p>
     * <p>子接口/实现类可覆盖，使用不同的填充色或插值方式</p>
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
        g2d.setColor(new Color(114, 114, 114));
        g2d.fillRect(0, 0, targetW, targetH);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(src.getScaledInstance(newW, newH, java.awt.Image.SCALE_SMOOTH), padX, padY, null);
        g2d.dispose();
        return result;
    }
}
