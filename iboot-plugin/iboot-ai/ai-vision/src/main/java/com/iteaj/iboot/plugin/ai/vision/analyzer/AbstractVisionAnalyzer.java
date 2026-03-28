package com.iteaj.iboot.plugin.ai.vision.analyzer;

import ai.djl.Model;
import com.iteaj.framework.spi.ai.vision.*;
import com.iteaj.iboot.plugin.ai.vision.adapter.ModelAdapterRegistry;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * VisionAnalyzer 公共抽象基类
 * <p>提供推理参数合并工具方法：用户传入参数优先，未传入时使用分析器默认值</p>
 * <p>子类只需实现 {@link #analyzerType()} 和 {@link #analyze(VisionRequest)} 两个方法</p>
 * <p>走适配器流程时通过 {@link ModelAdapterRegistry} 自动匹配对应的预处理/后处理逻辑</p>
 *
 * @param <O> 分析结果类型
 */
public abstract class AbstractVisionAnalyzer<O> implements VisionAnalyzer<O> {

    /** 适配器注册中心（由 Spring 注入，走适配器流程的子类需要此字段） */
    protected ModelAdapterRegistry adapterRegistry;

    /**
     * 注入适配器注册中心（setter 注入，非必须，无适配器场景可不注入）
     */
    public void setAdapterRegistry(ModelAdapterRegistry adapterRegistry) {
        this.adapterRegistry = adapterRegistry;
    }

    // ─────────────────────────── 图像加载 ───────────────────────────

    /**
     * 从 ImageSource 中加载 BufferedImage
     * <p>支持 byte[]、InputStream、文件路径三种来源，自动识别类型</p>
     *
     * @param source 图像来源
     * @return BufferedImage
     * @throws IOException 读取失败时抛出
     */
    protected BufferedImage loadImage(ImageSource source) throws IOException {
        if (source.getImageBytes() != null) {
            return ImageIO.read(new ByteArrayInputStream(source.getImageBytes()));
        }
        if (source.getImageStream() != null) {
            return ImageIO.read(source.getImageStream());
        }
        if (source.getImagePath() != null) {
            return ImageIO.read(Files.newInputStream(Paths.get(source.getImagePath())));
        }
        throw new IllegalArgumentException(
                "ImageSource 未设置任何图像数据（imageBytes / imageStream / imagePath）");
    }

    // ─────────────────────────── 模型加载 ───────────────────────────

    /**
     * 加载 DJL ONNX 模型
     * <p>支持按名称（从模型目录查找）或按完整路径两种方式加载</p>
     *
     * @param modelRef 模型引用
     * @param engine   推理引擎名称（如 OnnxRuntime），传 null 时 DJL 自动推断
     * @return 加载完成的 DJL Model 实例
     * @throws Exception 加载失败时抛出
     */
    protected Model loadModel(ModelRef modelRef, String engine) throws Exception {
        Model model = (engine != null)
                ? Model.newInstance(modelRef.getValue(), engine)
                : Model.newInstance(modelRef.getValue());
        if (modelRef.isPath()) {
            model.load(Paths.get(modelRef.getValue()));
        } else {
            // 未指定路径时，从 vision.model.dir 系统属性对应目录加载（默认 models/）
            String modelDir = System.getProperty("vision.model.dir", "models");
            model.load(Paths.get(modelDir), modelRef.getValue());
        }
        return model;
    }

    // ─────────────────────────── 参数解析 ───────────────────────────

    /**
     * 从请求中获取有效置信度阈值
     * <p>优先使用 {@link VisionParams#getConfidence()}，未设置时退回 {@link #defaultConfidence()}</p>
     *
     * @param request 分析请求
     * @return 有效置信度阈值
     */
    protected float resolveConfidence(VisionRequest request) {
        VisionParams params = request.getParams();
        if (params != null && params.getConfidence() != null) {
            return params.getConfidence();
        }
        return defaultConfidence();
    }

    /**
     * 从请求中获取有效 NMS 阈值
     * <p>优先使用 {@link VisionParams#getNmsThreshold()}，未设置时退回 {@link #defaultNmsThreshold()}</p>
     *
     * @param request 分析请求
     * @return 有效 NMS 阈值
     */
    protected float resolveNmsThreshold(VisionRequest request) {
        VisionParams params = request.getParams();
        if (params != null && params.getNmsThreshold() != null) {
            return params.getNmsThreshold();
        }
        return defaultNmsThreshold();
    }

    /**
     * 从请求中获取有效最大检测数量
     * <p>优先使用 {@link VisionParams#getMaxDetections()}，未设置时退回 {@link #defaultMaxDetections()}</p>
     *
     * @param request 分析请求
     * @return 有效最大检测数量
     */
    protected int resolveMaxDetections(VisionRequest request) {
        VisionParams params = request.getParams();
        if (params != null && params.getMaxDetections() != null) {
            return params.getMaxDetections();
        }
        return defaultMaxDetections();
    }

    /**
     * 从请求中解析有效模型引用
     * <p>优先级：{@link VisionParams#getModelPath()}（完整路径）
     * → {@link VisionParams#getModelName()}（名称）
     * → {@link #defaultModel()}（分析器默认名称）</p>
     *
     * <p>子类使用示例：</p>
     * <pre>{@code
     * ModelRef model = resolveModel(request);
     * if (model.isPath()) {
     *     // 直接按路径加载模型文件
     *     loadModelFromPath(model.getValue());
     * } else {
     *     // 按名称从模型目录中查找
     *     loadModelByName(model.getValue());
     * }
     * }</pre>
     *
     * @param request 分析请求
     * @return 携带类型信息的模型引用
     */
    protected ModelRef resolveModel(VisionRequest request) {
        VisionParams params = request.getParams();
        if (params != null) {
            if (params.getModelPath() != null && !params.getModelPath().isEmpty()) {
                return ModelRef.ofPath(params.getModelPath());
            }
            if (params.getModelName() != null && !params.getModelName().isEmpty()) {
                return ModelRef.ofName(params.getModelName());
            }
        }
        return ModelRef.ofName(defaultModel());
    }

    // ─────────────────────────── 模型引用 ───────────────────────────
}
