package com.iteaj.iboot.plugin.ai.vision;

import com.iteaj.framework.spi.ai.vision.*;
import com.iteaj.framework.spi.ai.vision.model.*;
import com.iteaj.framework.spi.ai.vision.model.FaceRegisterResult;
import com.iteaj.iboot.plugin.ai.vision.analyzer.FaceAnalyzer;
import com.iteaj.iboot.plugin.ai.vision.analyzer.StreamDetectionAnalyzer;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 视觉 AI 统一服务实现
 * <p>在 Spring 容器启动时自动收集所有 {@link VisionAnalyzer} 实现，按 {@link VisionType} 建立路由表</p>
 * <p>各场景方法通过路由表找到对应分析器后委托执行</p>
 */
@Service
public class VisionServiceImpl implements VisionService {

    /** 场景路由表：VisionType → VisionAnalyzer 实现 */
    private final Map<VisionType, VisionAnalyzer<?>> analyzerMap;

    /** 视频流检测分析器（单独持有，便于调用 detectStream 方法） */
    private final StreamDetectionAnalyzer streamDetectionAnalyzer;

    /** 人脸识别分析器（单独持有，便于调用 register 方法） */
    private final FaceAnalyzer faceAnalyzer;

    /** 人员特征库 */
    private final FaceLibrary faceLibrary;

    /**
     * 通过构造器注入所有 VisionAnalyzer 实现，自动构建路由表
     *
     * @param analyzers               所有注册到 Spring 容器的 VisionAnalyzer 实现列表
     * @param streamDetectionAnalyzer 视频流检测分析器
     * @param faceAnalyzer            人脸识别分析器
     * @param faceLibrary             人员特征库
     */
    public VisionServiceImpl(List<VisionAnalyzer<?>> analyzers,
                              StreamDetectionAnalyzer streamDetectionAnalyzer,
                              FaceAnalyzer faceAnalyzer,
                              FaceLibrary faceLibrary) {
        this.streamDetectionAnalyzer = streamDetectionAnalyzer;
        this.faceAnalyzer = faceAnalyzer;
        this.faceLibrary = faceLibrary;
        this.analyzerMap = analyzers.stream()
                .collect(Collectors.toMap(VisionAnalyzer::analyzerType, a -> a));
    }

    @Override
    public List<DetectionResult> detect(VisionRequest request) {
        return (List<DetectionResult>) getAnalyzer(VisionType.DETECTION).analyze(request);
    }

    @Override
    public ClassificationResult classify(VisionRequest request) {
        return (ClassificationResult) getAnalyzer(VisionType.CLASSIFICATION).analyze(request);
    }

    @Override
    public List<OcrResult> ocr(VisionRequest request) {
        return (List<OcrResult>) getAnalyzer(VisionType.OCR).analyze(request);
    }

    @Override
    public List<FaceResult> recognizeFace(VisionRequest request) {
        return (List<FaceResult>) getAnalyzer(VisionType.FACE).analyze(request);
    }

    @Override
    public SegmentationResult segment(VisionRequest request) {
        return (SegmentationResult) getAnalyzer(VisionType.SEGMENTATION).analyze(request);
    }

    @Override
    public List<PoseResult> estimatePose(VisionRequest request) {
        return (List<PoseResult>) getAnalyzer(VisionType.POSE).analyze(request);
    }

    @Override
    public BackgroundRemovalResult removeBackground(VisionRequest request) {
        return (BackgroundRemovalResult) getAnalyzer(VisionType.BACKGROUND_REMOVAL).analyze(request);
    }

    @Override
    public ImageEnhanceResult superResolve(VisionRequest request) {
        return (ImageEnhanceResult) getAnalyzer(VisionType.IMAGE_ENHANCE).analyze(request);
    }

    @Override
    public FeatureResult extractFeature(VisionRequest request) {
        return (FeatureResult) getAnalyzer(VisionType.FEATURE_EXTRACT).analyze(request);
    }

    @Override
    public ModerationResult moderate(VisionRequest request) {
        return (ModerationResult) getAnalyzer(VisionType.MODERATION).analyze(request);
    }

    @Override
    public void detectStream(VisionRequest request, Consumer<FrameDetectionResult> callback) {
        streamDetectionAnalyzer.detectStream(request, callback);
    }

    @Override
    public FaceRegisterResult registerFace(VisionRequest request, String personId, String personName) {
        return faceAnalyzer.register(request, personId, personName);
    }

    @Override
    public void removeFace(String faceId) {
        faceLibrary.remove(faceId);
    }

    @Override
    public List<FaceLibrary.FaceEntry> listFaces() {
        return faceLibrary.listAll();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <O> O analyze(VisionType type, VisionRequest request) {
        return (O) getAnalyzer(type).analyze(request);
    }

    /**
     * 从路由表中获取分析器，未找到时抛出异常
     *
     * @param type 场景类型枚举
     * @return 对应的分析器实现
     * @throws IllegalArgumentException 未找到对应分析器时抛出
     */
    private VisionAnalyzer<?> getAnalyzer(VisionType type) {
        VisionAnalyzer<?> analyzer = analyzerMap.get(type);
        if (analyzer == null) {
            throw new IllegalArgumentException("未找到场景类型 [" + type + "] 对应的 VisionAnalyzer 实现");
        }
        return analyzer;
    }
}
