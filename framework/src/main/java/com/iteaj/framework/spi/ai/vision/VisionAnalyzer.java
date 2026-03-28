package com.iteaj.framework.spi.ai.vision;

/**
 * 视觉场景分析器接口
 * <p>每个视觉场景（目标检测、OCR、人脸识别等）对应一个实现类</p>
 * <p>实现类负责：图像前处理 → 调用推理引擎 → 结果后处理</p>
 * <p>通过 Spring @Component 注册后，由 {@link VisionService} 自动发现并路由</p>
 *
 * @param <O> 分析结果类型
 */
public interface VisionAnalyzer<O> {

    /**
     * 场景类型标识，用于 VisionService 路由分发
     *
     * @return 分析器对应的视觉场景枚举
     */
    VisionType analyzerType();

    /**
     * 默认使用的模型名称
     * <p>当 {@link VisionParams} 未指定模型时使用此值</p>
     */
    String defaultModel();

    /**
     * 默认置信度阈值（0~1）
     * <p>当 {@link VisionParams} 未指定 confidence 时使用此值，默认 0.5</p>
     */
    default float defaultConfidence() {
        return 0.5f;
    }

    /**
     * 默认 NMS 阈值（0~1）
     * <p>当 {@link VisionParams} 未指定 nmsThreshold 时使用此值，默认 0.45</p>
     */
    default float defaultNmsThreshold() {
        return 0.45f;
    }

    /**
     * 默认最大检测数量
     * <p>当 {@link VisionParams} 未指定 maxDetections 时使用此值，默认 100</p>
     */
    default int defaultMaxDetections() {
        return 100;
    }

    /**
     * 执行视觉分析
     * <p>{@link VisionRequest#getParams()} 中指定的参数优先级高于分析器默认值</p>
     *
     * @param request 分析请求（含图像来源和推理参数）
     * @return 分析结果
     */
    O analyze(VisionRequest request);
}
