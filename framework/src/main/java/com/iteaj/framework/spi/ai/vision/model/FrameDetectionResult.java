package com.iteaj.framework.spi.ai.vision.model;

import java.util.List;

/**
 * 视频帧目标检测结果
 * <p>在 {@link DetectionResult} 单目标结果基础上，增加帧序号和时间戳，用于视频流检测场景</p>
 */
public class FrameDetectionResult {

    /** 当前帧序号（从 0 开始） */
    private long frameIndex;

    /** 当前帧时间戳（毫秒） */
    private long timestamp;

    /** 当前帧中检测到的所有目标列表 */
    private List<DetectionResult> detections;

    /** 已绘制检测框的帧图像字节数据（JPEG 格式，按需返回，可为 null） */
    private byte[] frameImage;

    /**
     * 获取帧序号
     */
    public long getFrameIndex() {
        return frameIndex;
    }

    public FrameDetectionResult setFrameIndex(long frameIndex) {
        this.frameIndex = frameIndex;
        return this;
    }

    /**
     * 获取帧时间戳（毫秒）
     */
    public long getTimestamp() {
        return timestamp;
    }

    public FrameDetectionResult setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * 获取当前帧中检测到的目标列表
     */
    public List<DetectionResult> getDetections() {
        return detections;
    }

    public FrameDetectionResult setDetections(List<DetectionResult> detections) {
        this.detections = detections;
        return this;
    }

    /**
     * 获取已绘制检测框的帧图像字节数据
     */
    public byte[] getFrameImage() {
        return frameImage;
    }

    public FrameDetectionResult setFrameImage(byte[] frameImage) {
        this.frameImage = frameImage;
        return this;
    }
}
