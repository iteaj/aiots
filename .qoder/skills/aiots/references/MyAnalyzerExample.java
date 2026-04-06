package com.iteaj.example.analyzer;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.iteaj.framework.spi.ai.vision.*;
import com.iteaj.framework.spi.ai.vision.adapter.ModelAdapter;
import com.iteaj.iboot.plugin.ai.vision.adapter.AbstractModelAdapter;
import com.iteaj.iboot.plugin.ai.vision.adapter.ModelAdapterRegistry;
import com.iteaj.iboot.plugin.ai.vision.analyzer.AbstractVisionAnalyzer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

/**
 * 自定义视觉场景分析器示例
 *
 * <p>展示如何新增一个全新的视觉分析场景</p>
 * <p>步骤：1.定义结果模型 2.定义SPI接口 3.创建适配器 4.创建分析器（本类）</p>
 */
@Component
public class MyAnalyzerExample extends AbstractVisionAnalyzer<MyResult> {

    @Autowired
    @Override
    public void setAdapterRegistry(ModelAdapterRegistry adapterRegistry) {
        super.setAdapterRegistry(adapterRegistry);
    }

    /**
     * 返回分析器类型（需要在 VisionType 枚举中添加对应类型）
     */
    @Override
    public VisionType analyzerType() {
        // return VisionType.MY_TYPE;  // 需要在 VisionType 枚举中添加
        return null;
    }

    /**
     * 默认模型名称
     */
    @Override
    public String defaultModel() {
        return "mymodel";
    }

    /**
     * 核心分析逻辑
     */
    @Override
    public MyResult analyze(VisionRequest request) {
        try {
            // 1. 解析模型引用（从 request.params 读取 modelName/modelPath）
            ModelRef modelRef = resolveModel(request);

            // 2. 匹配适配器（根据模型名自动路由到对应的 ModelAdapter）
            // MyAdapter 是你自定义的 SPI 接口
            // MyAdapter adapter = adapterRegistry.match(modelRef.getValue(), MyAdapter.class);
            // AbstractModelAdapter<?> djlAdapter = (AbstractModelAdapter<?>) adapter;

            // 3. 加载图像（支持 byte[] / InputStream / 文件路径）
            BufferedImage image = loadImage(request.getSource());
            final int origW = image.getWidth();
            final int origH = image.getHeight();

            // 4. 图像缩放（Letterbox 保持宽高比）
            // int size = adapter.inputSize();
            // BufferedImage resized = adapter.letterboxResize(image, size, size);

            // 5. 加载模型并执行推理
            // try (Model model = loadModel(modelRef, adapter.engine());
            //      NDManager manager = NDManager.newBaseManager();
            //      Predictor<BufferedImage, NDList> predictor = model.newPredictor(
            //              new RawTranslator(manager, djlAdapter, request.getParams()))) {
            //
            //     NDList output = predictor.predict(resized);
            //
            //     // 6. 后处理：解析模型输出 → 业务结果
            //     return (MyResult) djlAdapter.postprocess(output, origW, origH, request.getParams());
            // }

            return new MyResult();

        } catch (Exception e) {
            throw new RuntimeException("MyAnalyzer 推理失败：" + e.getMessage(), e);
        }
    }

    /**
     * 内部 Translator 类：连接 DJL Predictor 与自定义适配器
     */
    private static class RawTranslator implements Translator<BufferedImage, NDList> {

        private final NDManager manager;
        private final AbstractModelAdapter<?> adapter;
        private final VisionParams params;

        RawTranslator(NDManager manager, AbstractModelAdapter<?> adapter, VisionParams params) {
            this.manager = manager;
            this.adapter = adapter;
            this.params = params;
        }

        @Override
        public NDList processInput(TranslatorContext ctx, BufferedImage input) {
            // 调用适配器的预处理
            return new NDList(adapter.preprocess(manager, input, params));
        }

        @Override
        public NDList processOutput(TranslatorContext ctx, NDList list) {
            // 直接返回原始输出，后处理由适配器负责
            return list;
        }

        @Override
        public Batchifier getBatchifier() {
            return null;  // 单张推理，不使用 Batchifier
        }
    }

    /**
     * 示例结果模型（实际应定义在 framework/spi/ai/vision/model 下）
     */
    public static class MyResult {
        private float confidence;
        private String label;
        // ... getter/setter
    }
}
