package com.iteaj.framework.spi.ai.vision;

import java.util.HashMap;
import java.util.Map;

/**
 * 视觉推理参数
 * <p>用于指定模型来源及推理过程中的关键参数，所有字段均为可选，不填则使用 {@link VisionAnalyzer} 的默认值</p>
 * <p>支持链式调用构建</p>
 */
public class VisionParams {

    /** 模型名称，与 modelPath 二选一。由系统从模型目录中查找对应文件，如 yolov8n */
    private String modelName;

    /** 模型文件完整路径，与 modelName 二选一，如 /models/yolov8n.onnx */
    private String modelPath;

    /** 置信度阈值（0~1），低于此值的结果将被过滤 */
    private Float confidence;

    /** NMS（非极大值抑制）阈值（0~1），用于过滤重叠检测框，适用于检测类场景 */
    private Float nmsThreshold;

    /** 单次推理返回的最大目标数量，适用于检测类场景 */
    private Integer maxDetections;

    /** 次要扩展参数，用于传递场景专有的非通用参数 */
    private Map<String, Object> extraParams;

    private VisionParams() {}

    /**
     * 创建空参数对象，后续通过链式调用设置参数
     */
    public static VisionParams builder() {
        return new VisionParams();
    }

    /**
     * 指定模型名称（由系统从模型目录中查找对应文件）
     * @param modelName 模型名称，如 yolov8n
     */
    public VisionParams modelName(String modelName) {
        this.modelName = modelName;
        return this;
    }

    /**
     * 指定模型文件完整路径
     * @param modelPath 模型文件路径，如 /models/yolov8n.onnx
     */
    public VisionParams modelPath(String modelPath) {
        this.modelPath = modelPath;
        return this;
    }

    /**
     * 设置置信度阈值
     * @param confidence 置信度（0~1），低于此值的结果将被过滤
     */
    public VisionParams confidence(float confidence) {
        this.confidence = confidence;
        return this;
    }

    /**
     * 设置 NMS 阈值
     * @param nmsThreshold NMS 阈值（0~1），用于过滤重叠检测框
     */
    public VisionParams nmsThreshold(float nmsThreshold) {
        this.nmsThreshold = nmsThreshold;
        return this;
    }

    /**
     * 设置最大检测数量
     * @param maxDetections 单次推理返回的最大目标数
     */
    public VisionParams maxDetections(int maxDetections) {
        this.maxDetections = maxDetections;
        return this;
    }

    /**
     * 添加次要扩展参数
     * @param key   参数名
     * @param value 参数值
     */
    public VisionParams extra(String key, Object value) {
        if (this.extraParams == null) {
            this.extraParams = new HashMap<>();
        }
        this.extraParams.put(key, value);
        return this;
    }

    /**
     * 获取模型名称
     */
    public String getModelName() {
        return modelName;
    }

    /**
     * 获取模型文件完整路径
     */
    public String getModelPath() {
        return modelPath;
    }

    /**
     * 获取置信度阈值
     */
    public Float getConfidence() {
        return confidence;
    }

    /**
     * 获取 NMS 阈值
     */
    public Float getNmsThreshold() {
        return nmsThreshold;
    }

    /**
     * 获取最大检测数量
     */
    public Integer getMaxDetections() {
        return maxDetections;
    }

    /**
     * 获取次要扩展参数
     */
    public Map<String, Object> getExtraParams() {
        return extraParams;
    }
}
