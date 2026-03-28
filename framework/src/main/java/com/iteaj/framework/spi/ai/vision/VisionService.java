package com.iteaj.framework.spi.ai.vision;

import com.iteaj.framework.spi.ai.vision.model.*;
import com.iteaj.framework.spi.ai.vision.model.FaceRegisterResult;

import java.util.List;
import java.util.function.Consumer;

/**
 * 视觉 AI 统一服务接口
 * <p>作为所有视觉场景能力的统一入口，屏蔽底层推理引擎差异</p>
 * <p>各方法均以 {@link VisionRequest} 作为统一入参，通过 {@link VisionParams} 可按需微调模型和推理参数</p>
 *
 * <p>典型用法：</p>
 * <pre>{@code
 * // 1. 构造图像来源
 * ImageSource source = ImageSource.of(imageBytes);
 *
 * // 2. 可选：自定义推理参数（不设置则使用分析器默认值）
 * VisionParams params = VisionParams.builder()
 *     .modelName("yolov8n")
 *     .confidence(0.6f)
 *     .maxDetections(50);
 *
 * // 3. 构造请求并调用
 * List<DetectionResult> results = visionService.detect(VisionRequest.of(source).params(params));
 * }</pre>
 *
 * <p>通过 Spring 构造器注入使用：</p>
 * <pre>{@code
 * private final VisionService visionService;
 *
 * public XxxService(VisionService visionService) {
 *     this.visionService = visionService;
 * }
 * }</pre>
 */
public interface VisionService {

    // ============================================================
    // 基础检测与识别
    // ============================================================

    /**
     * 目标检测
     * <p>检测图像中的所有目标，返回类别、置信度和边界框坐标</p>
     * <p>典型场景：安防监控、工业缺陷、交通违章、货架商品等</p>
     *
     * @param request 分析请求（含图像来源和推理参数）
     * @return 检测到的目标列表，按置信度降序排列
     */
    List<DetectionResult> detect(VisionRequest request);

    /**
     * 图像分类
     * <p>对整张图像进行分类，返回最可能的类别及 Top-N 候选</p>
     * <p>典型场景：商品类别识别、农作物病害识别、图片内容分类等</p>
     *
     * @param request 分析请求
     * @return 分类结果，含最高置信度类别及 Top-N 候选列表
     */
    ClassificationResult classify(VisionRequest request);

    /**
     * OCR 文字识别
     * <p>识别图像中的文字内容，返回文字内容和位置信息</p>
     * <p>典型场景：证件识别、票据识别、工业铭牌、文档数字化等</p>
     *
     * @param request 分析请求
     * @return 识别到的文字区域列表，按从上到下、从左到右排列
     */
    List<OcrResult> ocr(VisionRequest request);

    /**
     * 人脸识别
     * <p>检测图像中的人脸并与人员库进行比对，返回人脸位置及匹配信息</p>
     * <p>典型场景：门禁考勤、访客登记、安防布控等</p>
     *
     * @param request 分析请求
     * @return 识别到的人脸列表，含边界框、关键点及匹配人员信息
     */
    List<FaceResult> recognizeFace(VisionRequest request);

    /**
     * 图像语义分割
     * <p>对图像进行像素级分割，返回每个分割区域的类别和掩码</p>
     * <p>典型场景：自动驾驶路面分割、医疗影像病灶分割、遥感图像分析等</p>
     *
     * @param request 分析请求
     * @return 分割结果，含所有分割区域列表及原图尺寸
     */
    SegmentationResult segment(VisionRequest request);

    /**
     * 人体姿态估计
     * <p>检测图像中的人体关键点，返回关节坐标（基于 COCO 17 关键点格式）</p>
     * <p>典型场景：健身动作矫正、工人疲劳检测、运动员动作分析等</p>
     *
     * @param request 分析请求
     * @return 人体姿态列表，每个元素包含边界框和 17 个关键点
     */
    List<PoseResult> estimatePose(VisionRequest request);

    // ============================================================
    // 图像处理与增强
    // ============================================================

    /**
     * 背景去除
     * <p>去除图像背景，保留前景主体，输出透明背景 PNG 图像</p>
     * <p>典型场景：商品图换背景、证件照处理、图像合成等</p>
     *
     * @param request 分析请求
     * @return 背景去除结果，含带透明背景的图像字节数据
     */
    BackgroundRemovalResult removeBackground(VisionRequest request);

    /**
     * 图像增强（超分辨率 / 降噪）
     * <p>对低分辨率或模糊图像进行超分辨率放大或降噪处理</p>
     * <p>典型场景：监控画面清晰化、老照片修复、小图片放大等</p>
     *
     * @param request 分析请求（可通过 extraParams 传入 scaleFactor 放大倍数）
     * @return 图像增强结果，含处理后的图像字节数据和尺寸信息
     */
    ImageEnhanceResult superResolve(VisionRequest request);

    /**
     * 图像特征提取
     * <p>提取图像的深度特征向量，用于以图搜图、相似度计算等</p>
     * <p>典型场景：以图搜图、商品相似推荐、重复图片检测等</p>
     *
     * @param request 分析请求
     * @return 特征提取结果，含特征向量数据
     */
    FeatureResult extractFeature(VisionRequest request);

    /**
     * 图像内容审核
     * <p>检测图像是否包含违规内容（色情、暴力、广告等），返回安全判断及各类别置信度</p>
     * <p>典型场景：UGC 内容平台审核、电商商品图审查等</p>
     *
     * @param request 分析请求
     * @return 审核结果，含是否安全及各违规类别的置信度
     */
    ModerationResult moderate(VisionRequest request);

    // ============================================================
    // 视频流处理
    // ============================================================

    /**
     * 视频流实时目标检测
     * <p>逐帧解码视频流并进行目标检测，通过回调推送每帧的检测结果</p>
     * <p>典型场景：安防监控实时布控、工厂流水线实时质检、交通闯红灯抓拍等</p>
     *
     * <p>使用示例：</p>
     * <pre>{@code
     * // videoSource：本地文件路径 / RTSP / RTMP / HTTP 视频流 URL
     * visionService.detectStream(VisionRequest.of(ImageSource.ofPath("rtsp://...")),
     *     result -> {
     *         // result.getDetections() 当前帧所有目标
     *         // result.getFrameImage() 已画框的帧图像（可推送到前端）
     *         webSocket.send(result.getFrameImage());
     *     });
     * }</pre>
     *
     * @param request  分析请求（imagePath 填写视频流地址或本地文件路径）
     * @param callback 每帧检测结果回调，在后台线程中调用，需自行保证线程安全
     */
    void detectStream(VisionRequest request, Consumer<FrameDetectionResult> callback);

    // ============================================================
    // 人员库管理
    // ============================================================

    /**
     * 注册人脸到人员库
     * <p>要求图像中有且仅有一张人脸，否则注册失败</p>
     * <p>流程：检测人脸 → 关键点对齐 → ArcFace 提取特征 → 存入人员库</p>
     *
     * @param request    包含注册照片的分析请求
     * @param personId   人员业务 ID（如工号/学号），由调用方自行管理
     * @param personName 人员姓名
     * @return 注册结果，含 faceId 或失败原因
     */
    FaceRegisterResult registerFace(VisionRequest request, String personId, String personName);

    /**
     * 从人员库中删除人脸
     *
     * @param faceId 注册时返回的人脸唯一标识
     */
    void removeFace(String faceId);

    /**
     * 查询人员库中已注册的所有人员信息
     *
     * @return 人员信息列表
     */
    List<FaceLibrary.FaceEntry> listFaces();

    // ============================================================
    // 泛型扩展入口
    // ============================================================

    /**
     * 自定义场景分析
     * <p>通过指定 {@link VisionType} 路由到对应的 {@link VisionAnalyzer} 实现</p>
     * <p>适用于内置方法未覆盖的定制化场景</p>
     *
     * @param type    场景类型枚举，与 {@link VisionAnalyzer#analyzerType()} 对应
     * @param request 分析请求
     * @param <O>     分析结果类型
     * @return 分析结果
     */
    <O> O analyze(VisionType type, VisionRequest request);
}
