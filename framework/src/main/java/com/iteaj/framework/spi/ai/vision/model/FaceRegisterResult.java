package com.iteaj.framework.spi.ai.vision.model;

/**
 * 人脸注册结果
 * <p>注册成功时返回分配的 faceId，注册失败时返回错误原因</p>
 */
public class FaceRegisterResult {

    /** 是否注册成功 */
    private boolean success;

    /** 注册成功后分配的人脸唯一标识 */
    private String faceId;

    /** 注册失败时的原因描述，成功时为 null */
    private String errorMessage;

    /** 图像中检测到的人脸数量（为 0 或 > 1 时注册失败） */
    private int detectedFaceCount;

    /**
     * 是否注册成功
     */
    public boolean isSuccess() {
        return success;
    }

    public FaceRegisterResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    /**
     * 获取注册成功后分配的人脸唯一标识
     */
    public String getFaceId() {
        return faceId;
    }

    public FaceRegisterResult setFaceId(String faceId) {
        this.faceId = faceId;
        return this;
    }

    /**
     * 获取注册失败原因
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    public FaceRegisterResult setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    /**
     * 获取图像中检测到的人脸数量
     */
    public int getDetectedFaceCount() {
        return detectedFaceCount;
    }

    public FaceRegisterResult setDetectedFaceCount(int detectedFaceCount) {
        this.detectedFaceCount = detectedFaceCount;
        return this;
    }
}
