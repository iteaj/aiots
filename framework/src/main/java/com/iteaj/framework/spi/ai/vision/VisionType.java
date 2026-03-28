package com.iteaj.framework.spi.ai.vision;

/**
 * 视觉分析场景类型枚举
 * <p>用于 {@link VisionAnalyzer#analyzerType()} 的返回值，标识当前分析器所属的视觉场景</p>
 * <p>{@link VisionService} 通过此枚举值路由到对应的分析器实现</p>
 */
public enum VisionType {

    /** 目标检测：检测图像中的所有目标，返回类别和边界框 */
    DETECTION,

    /** 图像分类：对整张图像进行类别识别，返回 Top-N 候选 */
    CLASSIFICATION,

    /** OCR 文字识别：识别图像中的文字内容和位置 */
    OCR,

    /** 人脸识别：检测人脸并与人员库比对 */
    FACE,

    /** 图像语义分割：像素级区域分割 */
    SEGMENTATION,

    /** 人体姿态估计：检测人体关节关键点 */
    POSE,

    /** 背景去除：去除图像背景，输出透明背景图 */
    BACKGROUND_REMOVAL,

    /** 图像增强（超分辨率 / 降噪）：提升图像质量 */
    IMAGE_ENHANCE,

    /** 图像特征提取：提取深度特征向量，用于以图搜图等 */
    FEATURE_EXTRACT,

    /** 内容审核：检测违规内容（色情、暴力、广告等） */
    MODERATION,

    /** 视频流目标检测：对视频流逐帧进行目标检测 */
    STREAM_DETECTION
}
