package com.iteaj.framework.spi.ai.vision.adapter;

import com.iteaj.framework.spi.ai.vision.model.Point;

import java.util.List;

/**
 * 人脸检测模型适配器接口
 * <p>适用于 RetinaFace、SCRFD 等人脸检测模型，后处理除边界框外额外返回 5 个关键点坐标</p>
 *
 * <p>内置实现：</p>
 * <ul>
 *   <li>{@code RetinaFaceAdapter} — 匹配 {@code retinaface*}，输出 [N, 15] 格式</li>
 *   <li>{@code ScrfdAdapter} — 匹配 {@code scrfd*}、{@code det_*}，多输出头格式</li>
 * </ul>
 */
public interface FaceDetectionAdapter extends ModelAdapter<List<FaceDetectionAdapter.FaceDetection>> {

    /**
     * 人脸检测中间结果
     * <p>包含检测框、置信度和 5 个关键点（左眼/右眼/鼻尖/左嘴角/右嘴角）</p>
     */
    class FaceDetection {

        /** 边界框左上角 X 坐标（像素） */
        public final float x;

        /** 边界框左上角 Y 坐标（像素） */
        public final float y;

        /** 边界框宽度（像素） */
        public final float width;

        /** 边界框高度（像素） */
        public final float height;

        /** 检测置信度（0~1） */
        public final float confidence;

        /** 5 个人脸关键点：左眼/右眼/鼻尖/左嘴角/右嘴角 */
        public final List<Point> landmarks;

        public FaceDetection(float x, float y, float width, float height,
                             float confidence, List<Point> landmarks) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.confidence = confidence;
            this.landmarks = landmarks;
        }
    }
}
