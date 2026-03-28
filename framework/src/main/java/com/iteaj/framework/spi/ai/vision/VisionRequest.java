package com.iteaj.framework.spi.ai.vision;

/**
 * 视觉分析请求
 * <p>组合 {@link ImageSource}（图像来源）和 {@link VisionParams}（推理参数），作为 {@link VisionAnalyzer} 的统一入参</p>
 * <p>支持链式调用构建</p>
 */
public class VisionRequest {

    /** 图像来源（必填） */
    private ImageSource source;

    /** 推理参数（可选，不设置则使用分析器默认值） */
    private VisionParams params;

    private VisionRequest() {}

    /**
     * 创建请求，仅指定图像来源，参数使用分析器默认值
     * @param source 图像来源
     * @return VisionRequest
     */
    public static VisionRequest of(ImageSource source) {
        VisionRequest request = new VisionRequest();
        request.source = source;
        return request;
    }

    /**
     * 设置推理参数（覆盖分析器默认值）
     * @param params 推理参数
     * @return VisionRequest
     */
    public VisionRequest params(VisionParams params) {
        this.params = params;
        return this;
    }

    /**
     * 获取图像来源
     */
    public ImageSource getSource() {
        return source;
    }

    /**
     * 获取推理参数，未设置时返回 null（调用方应使用分析器默认值）
     */
    public VisionParams getParams() {
        return params;
    }
}
