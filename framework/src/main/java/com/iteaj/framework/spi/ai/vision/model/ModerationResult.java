package com.iteaj.framework.spi.ai.vision.model;

/**
 * 图像内容审核结果
 * <p>用于违规内容检测（色情、暴力、广告等），返回是否合规及详细分类置信度</p>
 */
public class ModerationResult {

    /** 是否通过审核（true=安全，false=违规） */
    private boolean safe;

    /** 审核未通过时的违规类型描述，如 pornography、violence */
    private String violationType;

    /** 违规置信度（0~1） */
    private float violationConfidence;

    /** 色情内容置信度（0~1） */
    private float pornConfidence;

    /** 暴力内容置信度（0~1） */
    private float violenceConfidence;

    /** 广告内容置信度（0~1） */
    private float adConfidence;

    /**
     * 是否通过审核
     */
    public boolean isSafe() {
        return safe;
    }

    public ModerationResult setSafe(boolean safe) {
        this.safe = safe;
        return this;
    }

    /**
     * 获取违规类型描述
     */
    public String getViolationType() {
        return violationType;
    }

    public ModerationResult setViolationType(String violationType) {
        this.violationType = violationType;
        return this;
    }

    /**
     * 获取违规置信度
     */
    public float getViolationConfidence() {
        return violationConfidence;
    }

    public ModerationResult setViolationConfidence(float violationConfidence) {
        this.violationConfidence = violationConfidence;
        return this;
    }

    /**
     * 获取色情内容置信度
     */
    public float getPornConfidence() {
        return pornConfidence;
    }

    public ModerationResult setPornConfidence(float pornConfidence) {
        this.pornConfidence = pornConfidence;
        return this;
    }

    /**
     * 获取暴力内容置信度
     */
    public float getViolenceConfidence() {
        return violenceConfidence;
    }

    public ModerationResult setViolenceConfidence(float violenceConfidence) {
        this.violenceConfidence = violenceConfidence;
        return this;
    }

    /**
     * 获取广告内容置信度
     */
    public float getAdConfidence() {
        return adConfidence;
    }

    public ModerationResult setAdConfidence(float adConfidence) {
        this.adConfidence = adConfidence;
        return this;
    }
}
