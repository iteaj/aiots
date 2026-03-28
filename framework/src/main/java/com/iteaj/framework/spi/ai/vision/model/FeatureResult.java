package com.iteaj.framework.spi.ai.vision.model;

/**
 * 图像特征提取结果
 * <p>用于以图搜图、相似度计算等场景</p>
 */
public class FeatureResult {

    /** 特征向量，可用于余弦相似度或欧式距离计算 */
    private float[] feature;

    /** 特征向量维度 */
    private int dimension;

    /**
     * 获取特征向量
     */
    public float[] getFeature() {
        return feature;
    }

    public FeatureResult setFeature(float[] feature) {
        this.feature = feature;
        if (feature != null) {
            this.dimension = feature.length;
        }
        return this;
    }

    /**
     * 获取特征向量维度
     */
    public int getDimension() {
        return dimension;
    }

    public FeatureResult setDimension(int dimension) {
        this.dimension = dimension;
        return this;
    }
}
