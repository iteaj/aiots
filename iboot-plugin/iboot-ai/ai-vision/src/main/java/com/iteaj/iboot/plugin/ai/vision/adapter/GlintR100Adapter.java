package com.iteaj.iboot.plugin.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.adapter.FaceFeatureAdapter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * GlintR100 人脸特征提取适配器（亚洲人脸优化版本）
 * <p>适用于 InsightFace antelopev2 套件中的 {@code glintr100.onnx} 模型，
 * 该模型在 Glint360K 数据集（含大量亚洲人脸）上训练，对亚洲人员的识别精度更高</p>
 *
 * <p><b>输入/输出规格与 ArcFace 完全一致：</b></p>
 * <ul>
 *   <li>输入：{@code [1, 3, 112, 112]}，RGB，值域 [-1, 1]</li>
 *   <li>输出：{@code [1, 512]}，L2 归一化特征向量</li>
 * </ul>
 *
 * <p><b>支持的模型名称：</b>{@code glintr100*}、{@code glint360k*}、{@code r100*}</p>
 *
 * <p><b>优先级：</b>{@code @Order(900)}，高于 {@link ArcFaceAdapter}（1000），
 * 当模型名称匹配时优先使用本适配器</p>
 *
 * <p><b>下载地址：</b>InsightFace antelopev2 套件，包含 {@code glintr100.onnx} 和
 * {@code scrfd_10g_bnkps.onnx}（配套人脸检测模型）</p>
 */
@Component
@Order(900)
public class GlintR100Adapter extends ArcFaceAdapter {

    @Override
    public boolean supports(String modelNameOrPath) {
        String lower = modelNameOrPath.toLowerCase();
        // 匹配 glintr100*（antelopev2）、glint360k*（通用别名）、r100*（ResNet100 系列）
        return lower.contains("glintr100") || lower.contains("glint360k")
                || lower.startsWith("r100");
    }
}
