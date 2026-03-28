package com.iteaj.iboot.plugin.ai.vision.analyzer;

import com.iteaj.framework.spi.ai.vision.ModelRef;
import com.iteaj.framework.spi.ai.vision.VisionRequest;
import com.iteaj.framework.spi.ai.vision.VisionType;
import com.iteaj.framework.spi.ai.vision.model.DetectionResult;
import com.iteaj.framework.spi.ai.vision.model.FrameDetectionResult;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * 视频流目标检测分析器
 * <p>逐帧解码视频流，对每一帧执行目标检测，通过回调推送帧级检测结果</p>
 * <p>支持本地视频文件 / RTSP / RTMP / HTTP 视频流</p>
 * <p>推荐模型：yolov8n.onnx（实时推理，轻量）</p>
 * <p>可通过 {@code VisionParams.extra("skipFrames", 2)} 设置跳帧数以提升性能</p>
 */
@Component
public class StreamDetectionAnalyzer extends AbstractVisionAnalyzer<List<DetectionResult>> {

    /** 默认跳帧数（每隔 N 帧推理一次），0 表示不跳帧 */
    private static final int DEFAULT_SKIP_FRAMES = 0;

    @Override
    public VisionType analyzerType() {
        return VisionType.STREAM_DETECTION;
    }

    @Override
    public String defaultModel() {
        return "yolov8n";
    }

    /**
     * 对单帧图像执行目标检测（实现自 {@link com.iteaj.framework.spi.ai.vision.VisionAnalyzer}）
     * <p>用于单帧检测场景；视频流场景请使用 {@link #detectStream(VisionRequest, Consumer)}</p>
     *
     * @param request 单帧图像分析请求
     * @return 当前帧中检测到的目标列表
     */
    @Override
    public List<DetectionResult> analyze(VisionRequest request) {
        ModelRef model = resolveModel(request);
        float confidence = resolveConfidence(request);
        float nmsThreshold = resolveNmsThreshold(request);
        int maxDetections = resolveMaxDetections(request);

        // TODO：复用 DetectionAnalyzer 的推理逻辑对单帧图像执行检测
        return Collections.emptyList();
    }

    /**
     * 视频流实时目标检测（带逐帧回调）
     * <p>在独立后台线程中持续解码视频流并推理，每帧结果通过 callback 推送</p>
     *
     * @param request  分析请求（imagePath 填写视频流地址或本地文件路径）
     * @param callback 每帧检测结果回调，在后台线程中调用，需自行保证线程安全
     */
    public void detectStream(VisionRequest request, Consumer<FrameDetectionResult> callback) {
        ModelRef model = resolveModel(request);
        float confidence = resolveConfidence(request);
        float nmsThreshold = resolveNmsThreshold(request);
        int maxDetections = resolveMaxDetections(request);

        // 从 extraParams 中读取跳帧数，未设置则使用默认值
        int skipFrames = DEFAULT_SKIP_FRAMES;
        if (request.getParams() != null && request.getParams().getExtraParams() != null) {
            Object skip = request.getParams().getExtraParams().get("skipFrames");
            if (skip instanceof Number) {
                skipFrames = ((Number) skip).intValue();
            }
        }

        final int finalSkipFrames = skipFrames;
        String videoPath = request.getSource().getImagePath();

        // TODO：视频流逐帧检测流程（在后台线程中执行）
        // Thread.startVirtualThread(() -> {
        //     try (FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(videoPath)) {
        //         grabber.start();
        //         long frameIndex = 0;
        //         Frame frame;
        //         Java2DFrameConverter converter = new Java2DFrameConverter();
        //         while ((frame = grabber.grab()) != null) {
        //             if (frame.image == null) continue;
        //             // 跳帧处理：仅对指定间隔帧执行推理
        //             if (finalSkipFrames > 0 && frameIndex % (finalSkipFrames + 1) != 0) {
        //                 frameIndex++;
        //                 continue;
        //             }
        //             // 将帧转为 byte[] → 复用 analyze() 执行推理
        //             BufferedImage bi = converter.convert(frame);
        //             List<DetectionResult> detections = doDetect(bi, model, confidence, nmsThreshold, maxDetections);
        //             // 绘制检测框（可选）
        //             byte[] frameImage = drawBoundingBoxes(bi, detections);
        //             // 构建帧结果并回调
        //             FrameDetectionResult result = new FrameDetectionResult()
        //                 .setFrameIndex(frameIndex)
        //                 .setTimestamp(grabber.getTimestamp() / 1000)
        //                 .setDetections(detections)
        //                 .setFrameImage(frameImage);
        //             callback.accept(result);
        //             frameIndex++;
        //         }
        //     }
        // });
    }
}
