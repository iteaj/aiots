package com.iteaj.iboot.plugin.ai.vision.adapter;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import com.iteaj.framework.spi.ai.vision.VisionParams;
import com.iteaj.framework.spi.ai.vision.adapter.FaceDetectionAdapter;
import com.iteaj.framework.spi.ai.vision.model.Point;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * RetinaFace 人脸检测适配器
 * <p>适用于 InsightFace 导出的 RetinaFace ONNX 模型（buffalo 系列中的检测模型）</p>
 *
 * <p><b>输入规格：</b>{@code [1, 3, H, W]}，RGB，均值减法归一化（均值 127.5，标准差 128）</p>
 * <p><b>输出规格：</b>{@code [N, 15]}，每行为一个候选框：
 * <ul>
 *   <li>索引 0~3：x1, y1, x2, y2（绝对像素坐标）</li>
 *   <li>索引 4：置信度（0~1）</li>
 *   <li>索引 5~14：5 个关键点坐标（x1,y1, x2,y2, x3,y3, x4,y4, x5,y5），顺序为
 *       左眼/右眼/鼻尖/左嘴角/右嘴角</li>
 * </ul>
 * </p>
 *
 * <p><b>支持的模型名称：</b>{@code retinaface*}、{@code det_2.5g*}（buffalo_s 系列检测模型）</p>
 *
 * <p><b>参数说明（通过 {@link VisionParams} 传入）：</b></p>
 * <ul>
 *   <li>{@code confidence} — 置信度阈值，默认 0.5</li>
 *   <li>{@code nmsThreshold} — NMS IoU 阈值，默认 0.4</li>
 *   <li>{@code maxDetections} — 最大返回人脸数，默认 50</li>
 * </ul>
 */
@Component
@Order(1000)
public class RetinaFaceAdapter extends AbstractModelAdapter<List<FaceDetectionAdapter.FaceDetection>>
        implements FaceDetectionAdapter {

    /** 默认输入尺寸（正方形） */
    private static final int INPUT_SIZE = 640;

    @Override
    public boolean supports(String modelNameOrPath) {
        String lower = modelNameOrPath.toLowerCase();
        // 匹配 retinaface* 及 buffalo_s 中的检测模型 det_2.5g
        return lower.contains("retinaface") || lower.contains("det_2.5g");
    }

    @Override
    public int inputSize() {
        return INPUT_SIZE;
    }

    /**
     * 前处理：图像转张量，减均值归一化（(pixel - 127.5) / 128），输出 [1, 3, H, W]
     * <p>RetinaFace 的标准输入归一化方式（与 YOLO 的 [0,1] 不同）</p>
     */
    @Override
    public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
        // DJL ImageFactory 读取为 HWC uint8，转置为 CHW float32，然后减均值归一化
        Image djlImage = ImageFactory.getInstance().fromImage(image);
        NDArray base = djlImage.toNDArray(manager, Image.Flag.COLOR)
                .toType(DataType.FLOAT32, false)
                .transpose(2, 0, 1)     // HWC → CHW
                .expandDims(0);         // CHW → [1, C, H, W]
        // (pixel - 127.5) / 128 等同于 pixel * (1/128) - (127.5/128)
        return base.sub(127.5f).div(128f);
    }

    /**
     * 后处理：解析 RetinaFace 输出 [N, 15]，过滤低置信度框，执行 NMS
     */
    @Override
    public List<FaceDetection> postprocess(NDList output, int origW, int origH, VisionParams params) {
        float confThreshold = params != null && params.getConfidence() != null
                ? params.getConfidence() : 0.5f;
        float nmsThreshold = params != null && params.getNmsThreshold() != null
                ? params.getNmsThreshold() : 0.4f;
        int maxDetections = params != null && params.getMaxDetections() != null
                ? params.getMaxDetections() : 50;

        // 反算 Letterbox 缩放比例，将输出坐标映射回原图
        float scaleX = (float) origW / INPUT_SIZE;
        float scaleY = (float) origH / INPUT_SIZE;

        // 输出 shape: [N, 15]
        float[] raw = output.get(0).toFloatArray();
        int numBoxes = (int) output.get(0).getShape().get(0);

        List<float[]> boxes = new ArrayList<>(); // [x1, y1, x2, y2, score, lm0x, lm0y, ..., lm4x, lm4y]

        for (int i = 0; i < numBoxes; i++) {
            int offset = i * 15;
            float score = raw[offset + 4];
            if (score < confThreshold) continue;

            // 边界框坐标（输入尺寸下的绝对像素坐标）→ 原图坐标
            float x1 = raw[offset]     * scaleX;
            float y1 = raw[offset + 1] * scaleY;
            float x2 = raw[offset + 2] * scaleX;
            float y2 = raw[offset + 3] * scaleY;

            // 5 个关键点坐标（交替 x, y）→ 原图坐标
            float[] box = new float[15];
            box[0] = x1; box[1] = y1; box[2] = x2; box[3] = y2; box[4] = score;
            for (int k = 0; k < 5; k++) {
                box[5 + k * 2]     = raw[offset + 5 + k * 2]     * scaleX; // landmark x
                box[5 + k * 2 + 1] = raw[offset + 5 + k * 2 + 1] * scaleY; // landmark y
            }
            boxes.add(box);
        }

        // NMS
        List<float[]> kept = nms(boxes, nmsThreshold);

        // 按置信度降序，截取 maxDetections 个
        kept.sort((a, b) -> Float.compare(b[4], a[4]));
        if (kept.size() > maxDetections) {
            kept = kept.subList(0, maxDetections);
        }

        // 构建结果
        List<FaceDetection> results = new ArrayList<>(kept.size());
        for (float[] box : kept) {
            List<Point> landmarks = new ArrayList<>(5);
            for (int k = 0; k < 5; k++) {
                landmarks.add(new Point(box[5 + k * 2], box[5 + k * 2 + 1]));
            }
            results.add(new FaceDetection(
                    box[0], box[1],
                    box[2] - box[0], box[3] - box[1],
                    box[4], landmarks));
        }
        return results;
    }

    // ─────────────────────────── NMS ───────────────────────────

    private List<float[]> nms(List<float[]> boxes, float iouThreshold) {
        boxes.sort(Comparator.comparingDouble((float[] b) -> b[4]).reversed());
        List<float[]> kept = new ArrayList<>();
        boolean[] suppressed = new boolean[boxes.size()];

        for (int i = 0; i < boxes.size(); i++) {
            if (suppressed[i]) continue;
            float[] a = boxes.get(i);
            kept.add(a);
            for (int j = i + 1; j < boxes.size(); j++) {
                if (suppressed[j]) continue;
                if (iou(a, boxes.get(j)) > iouThreshold) suppressed[j] = true;
            }
        }
        return kept;
    }

    private float iou(float[] a, float[] b) {
        float interX1 = Math.max(a[0], b[0]);
        float interY1 = Math.max(a[1], b[1]);
        float interX2 = Math.min(a[2], b[2]);
        float interY2 = Math.min(a[3], b[3]);
        float interW = Math.max(0f, interX2 - interX1);
        float interH = Math.max(0f, interY2 - interY1);
        float inter = interW * interH;
        if (inter == 0f) return 0f;
        float areaA = (a[2] - a[0]) * (a[3] - a[1]);
        float areaB = (b[2] - b[0]) * (b[3] - b[1]);
        return inter / (areaA + areaB - inter);
    }
}
