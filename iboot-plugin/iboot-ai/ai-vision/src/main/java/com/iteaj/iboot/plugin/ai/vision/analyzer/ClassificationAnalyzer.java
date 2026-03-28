package com.iteaj.iboot.plugin.ai.vision.analyzer;

import com.iteaj.framework.spi.ai.vision.ModelRef;
import com.iteaj.framework.spi.ai.vision.VisionRequest;
import com.iteaj.framework.spi.ai.vision.VisionType;
import com.iteaj.framework.spi.ai.vision.model.ClassificationResult;
import org.springframework.stereotype.Component;

/**
 * 图像分类分析器
 * <p>对整张图像进行类别识别，返回最高置信度类别及 Top-N 候选列表</p>
 * <p>推荐模型：ResNet50 / EfficientNet-B0（ImageNet 1000 类）</p>
 */
@Component
public class ClassificationAnalyzer extends AbstractVisionAnalyzer<ClassificationResult> {

    @Override
    public VisionType analyzerType() {
        return VisionType.CLASSIFICATION;
    }

    @Override
    public String defaultModel() {
        return "resnet50";
    }

    @Override
    public ClassificationResult analyze(VisionRequest request) {
        // 获取有效推理参数
        ModelRef model = resolveModel(request);
        float confidence = resolveConfidence(request);

        // TODO：加载分类模型 → 图像预处理（Resize/归一化） → DJL 推理 → Softmax → 排序取 Top-N
        // 1. 通过 model 加载 ONNX 分类模型
        // 2. 将图像 Resize 到模型输入尺寸，归一化
        // 3. 执行推理，获取各类别 logits
        // 4. 应用 Softmax 转换为概率
        // 5. 按概率降序排列，构建 TopN 候选列表
        // 6. 返回 ClassificationResult（className/classIndex/confidence/topN）
        return new ClassificationResult();
    }
}
