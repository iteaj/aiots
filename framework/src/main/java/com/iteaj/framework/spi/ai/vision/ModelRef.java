package com.iteaj.framework.spi.ai.vision;

/**
 * 模型引用
 * <p>携带模型标识字符串及其类型（名称 or 完整路径），避免裸字符串语义不明确的问题</p>
 *
 * <p>使用示例：</p>
 * <pre>{@code
 * // 按名称，从 vision.model.dir 目录查找对应文件
 * ModelRef.ofName("yolov8n")
 *
 * // 按绝对路径
 * ModelRef.ofPath("/opt/models/yolov8n.onnx")
 * }</pre>
 */
public class ModelRef {

    /** 模型引用类型 */
    public enum Type {
        /** 模型名称，由推理框架从模型目录中查找对应文件 */
        NAME,
        /** 模型文件完整路径，直接加载 */
        PATH
    }

    /** 模型标识字符串（名称或路径） */
    private final String value;

    /** 引用类型 */
    private final Type type;

    private ModelRef(String value, Type type) {
        this.value = value;
        this.type = type;
    }

    /**
     * 创建名称类型的模型引用
     *
     * @param name 模型名称，如 {@code yolov8n}、{@code retinaface}
     * @return 名称类型的 ModelRef
     */
    public static ModelRef ofName(String name) {
        return new ModelRef(name, Type.NAME);
    }

    /**
     * 创建路径类型的模型引用
     *
     * @param path 模型文件完整路径，如 {@code /models/yolov8n.onnx}
     * @return 路径类型的 ModelRef
     */
    public static ModelRef ofPath(String path) {
        return new ModelRef(path, Type.PATH);
    }

    /**
     * 从 {@link VisionParams} 中解析模型引用
     * <p>优先级：modelPath（完整路径）→ modelName（名称）→ defaultModel（默认值）</p>
     *
     * @param params       推理参数，可为 null
     * @param defaultModel 未指定时的默认模型名称
     * @return 解析后的 ModelRef
     */
    public static ModelRef resolve(VisionParams params, String defaultModel) {
        if (params != null) {
            if (params.getModelPath() != null && !params.getModelPath().isEmpty()) {
                return ModelRef.ofPath(params.getModelPath());
            }
            if (params.getModelName() != null && !params.getModelName().isEmpty()) {
                return ModelRef.ofName(params.getModelName());
            }
        }
        return ModelRef.ofName(defaultModel);
    }

    /**
     * 获取模型标识字符串（名称或路径）
     */
    public String getValue() {
        return value;
    }

    /**
     * 获取引用类型
     */
    public Type getType() {
        return type;
    }

    /**
     * 是否为路径类型
     */
    public boolean isPath() {
        return type == Type.PATH;
    }

    /**
     * 是否为名称类型
     */
    public boolean isName() {
        return type == Type.NAME;
    }

    @Override
    public String toString() {
        return type.name() + "(" + value + ")";
    }
}
