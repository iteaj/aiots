package com.iteaj.framework.spi.ai.vision.adapter;

/**
 * 人脸特征提取模型适配器接口
 * <p>适用于 ArcFace、FaceNet 等人脸特征提取模型，后处理返回归一化特征向量，用于余弦相似度比对</p>
 *
 * <p>内置实现：</p>
 * <ul>
 *   <li>{@code ArcFaceAdapter} — 匹配 {@code arcface*}、{@code w600k*}，输入 112x112，输出 512 维</li>
 *   <li>{@code GlintR100Adapter} — 匹配 {@code glintr100*}，输入 112x112，输出 512 维（亚洲人脸优化）</li>
 * </ul>
 */
public interface FaceFeatureAdapter extends ModelAdapter<float[]> {

    /**
     * 特征向量维度
     * <p>ArcFace 系列标准为 512 维，FaceNet 为 128 维或 512 维</p>
     * <p>用于结果校验，实际维度由模型输出决定</p>
     *
     * @return 特征向量维度
     */
    default int featureDim() {
        return 512;
    }
}
