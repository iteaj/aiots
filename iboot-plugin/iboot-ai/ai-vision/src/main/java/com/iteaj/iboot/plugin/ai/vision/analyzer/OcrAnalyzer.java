package com.iteaj.iboot.plugin.ai.vision.analyzer;

import com.iteaj.framework.spi.ai.vision.ModelRef;
import com.iteaj.framework.spi.ai.vision.VisionRequest;
import com.iteaj.framework.spi.ai.vision.VisionType;
import com.iteaj.framework.spi.ai.vision.model.OcrResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * OCR 文字识别分析器
 * <p>识别图像中的文字内容，返回文字内容及区域位置（含倾斜角度）</p>
 * <p>推荐模型：PaddleOCR v4（文字检测 + 识别两阶段 ONNX 模型）</p>
 */
@Component
public class OcrAnalyzer extends AbstractVisionAnalyzer<List<OcrResult>> {

    @Override
    public VisionType analyzerType() {
        return VisionType.OCR;
    }

    @Override
    public String defaultModel() {
        return "paddleocr_v4";
    }

    @Override
    public float defaultConfidence() {
        return 0.6f;
    }

    @Override
    public List<OcrResult> analyze(VisionRequest request) {
        // 获取有效推理参数
        ModelRef model = resolveModel(request);
        float confidence = resolveConfidence(request);

        // TODO：两阶段 OCR 推理流程
        // 阶段一（文字检测）：
        //   1. 加载检测模型（db_net），输出文字区域多边形坐标
        //   2. 按 confidence 阈值过滤低置信度文字区域
        //   3. 对每个区域做透视变换（矫正倾斜）
        // 阶段二（文字识别）：
        //   4. 加载识别模型（crnn），对每个矫正后的子图逐一识别
        //   5. 组装 OcrResult（text/confidence/x/y/width/height/angle）
        // 6. 按从上到下、从左到右顺序排列结果
        return Collections.emptyList();
    }
}
