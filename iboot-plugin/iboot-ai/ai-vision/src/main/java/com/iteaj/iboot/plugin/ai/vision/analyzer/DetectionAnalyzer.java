package com.iteaj.iboot.plugin.ai.vision.analyzer;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.iteaj.framework.spi.ai.vision.ModelRef;
import com.iteaj.framework.spi.ai.vision.VisionRequest;
import com.iteaj.framework.spi.ai.vision.VisionType;
import com.iteaj.iboot.plugin.ai.vision.adapter.AbstractModelAdapter;
import com.iteaj.framework.spi.ai.vision.model.DetectionResult;
import com.iteaj.framework.spi.ai.vision.adapter.DetectionAdapter;
import com.iteaj.iboot.plugin.ai.vision.adapter.ModelAdapterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.List;

/**
 * 目标检测分析器
 * <p>基于 YOLO 系列 ONNX 模型，检测图像中的所有目标，返回类别、置信度和边界框</p>
 *
 * <p><b>推荐模型：</b>yolov8n.onnx / yolov8s.onnx（速度与精度均衡）</p>
 *
 * <p><b>工作流程：</b></p>
 * <ol>
 *   <li>通过 {@link ModelAdapterRegistry} 按模型名/路径自动匹配 {@link DetectionAdapter}</li>
 *   <li>适配器的 {@code inputSize()} 决定 Letterbox 缩放目标尺寸</li>
 *   <li>适配器的 {@code preprocess()} 将图像转为模型输入张量</li>
 *   <li>DJL 调用推理引擎执行推理</li>
 *   <li>适配器的 {@code postprocess()} 解析原始输出，过滤低置信度并执行 NMS</li>
 * </ol>
 *
 * <p><b>使用示例：</b></p>
 * <pre>{@code
 * // 使用默认 yolov8n 模型
 * List<DetectionResult> results = visionService.detect(
 *     VisionRequest.of(ImageSource.of(imageBytes))
 * );
 *
 * // 指定模型名称（适配器按名称自动匹配）
 * List<DetectionResult> results = visionService.detect(
 *     VisionRequest.of(ImageSource.of(imageBytes))
 *         .params(VisionParams.builder()
 *             .modelName("yolov8s")       // 按名称指定模型
 *             .confidence(0.6f)           // 置信度阈值
 *             .nmsThreshold(0.5f)         // NMS 阈值
 *             .maxDetections(50)          // 最大检测数量
 *             .extra("classNames", "person,car,dog") // 类别名称（可选）
 *             .build())
 * );
 * }</pre>
 */
@Component
public class DetectionAnalyzer extends AbstractVisionAnalyzer<List<DetectionResult>> {

    @Autowired
    @Override
    public void setAdapterRegistry(ModelAdapterRegistry adapterRegistry) {
        super.setAdapterRegistry(adapterRegistry);
    }

    @Override
    public VisionType analyzerType() {
        return VisionType.DETECTION;
    }

    @Override
    public String defaultModel() {
        return "yolov8n";
    }

    @Override
    public List<DetectionResult> analyze(VisionRequest request) {
        try {
            // 1. 解析模型引用（路径 > 名称 > 默认值）
            ModelRef modelRef = resolveModel(request);

            // 2. 匹配适配器（按模型名/路径前缀自动路由到对应的 DetectionAdapter 实现）
            DetectionAdapter adapter = adapterRegistry.match(modelRef.getValue(), DetectionAdapter.class);
            AbstractModelAdapter<?> djlAdapter = (AbstractModelAdapter<?>) adapter;

            // 3. 加载图像
            BufferedImage image = loadImage(request.getSource());
            final int origW = image.getWidth();
            final int origH = image.getHeight();

            // 4. Letterbox 缩放（由适配器 inputSize() 决定目标尺寸，保持宽高比）
            int size = adapter.inputSize();
            BufferedImage resized = adapter.letterboxResize(image, size, size);

            // 5. 加载模型并执行推理
            try (Model model = loadModel(modelRef, adapter.engine());
                 NDManager manager = NDManager.newBaseManager();
                 Predictor<BufferedImage, NDList> predictor = model.newPredictor(
                         new RawTranslator(manager, djlAdapter, request.getParams()))) {

                NDList output = predictor.predict(resized);

                // 6. 后处理：解析输出、NMS 去重、返回 DetectionResult 列表
                @SuppressWarnings("unchecked")
                List<DetectionResult> detResult = (List<DetectionResult>) djlAdapter.postprocess(
                        output, origW, origH, request.getParams());
                return detResult;
            }
        } catch (Exception e) {
            throw new RuntimeException("目标检测推理失败：" + e.getMessage(), e);
        }
    }

    // ─────────────────────────── 内部 Translator ───────────────────────────

    /**
     * 原始直传 Translator：将 BufferedImage 通过适配器转为 NDArray 输入，输出原始 NDList
     * <p>不做任何额外封装，后处理由 {@link DetectionAdapter#postprocess} 负责</p>
     */
    private static class RawTranslator implements Translator<BufferedImage, NDList> {

        private final NDManager manager;
        private final AbstractModelAdapter<?> adapter;
        private final com.iteaj.framework.spi.ai.vision.VisionParams params;

        RawTranslator(NDManager manager, AbstractModelAdapter<?> adapter,
                      com.iteaj.framework.spi.ai.vision.VisionParams params) {
            this.manager = manager;
            this.adapter = adapter;
            this.params = params;
        }

        @Override
        public NDList processInput(TranslatorContext ctx, BufferedImage input) {
            // 调用适配器预处理，转为模型输入张量
            NDArray tensor = adapter.preprocess(manager, input, params);
            return new NDList(tensor);
        }

        @Override
        public NDList processOutput(TranslatorContext ctx, NDList list) {
            // 直接返回原始输出，后处理由 DetectionAdapter#postprocess 负责
            return list;
        }

        @Override
        public Batchifier getBatchifier() {
            return null; // 单张推理，不使用 Batchifier
        }
    }
}
