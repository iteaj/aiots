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
 * SCRFD 人脸检测适配器
 * <p>适用于 InsightFace SCRFD 系列模型（buffalo_l 中的 det_10g / antelopev2 中的 scrfd_10g_bnkps）</p>
 *
 * <p><b>输入规格：</b>{@code [1, 3, H, W]}，RGB，均值减法归一化（均值 127.5，标准差 128）</p>
 *
 * <p><b>输出规格（多头，9 个输出张量）：</b>stride 8/16/32 各 3 个输出（score/bbox/kps），共 9 个：
 * <ul>
 *   <li>stride 8：score_8 [N1,2], bbox_8 [N1,4], kps_8 [N1,10]</li>
 *   <li>stride 16：score_16 [N2,2], bbox_16 [N2,4], kps_16 [N2,10]</li>
 *   <li>stride 32：score_32 [N3,2], bbox_32 [N3,4], kps_32 [N3,10]</li>
 * </ul>
 * N1/N2/N3 = (H/stride * W/stride * 2)，输入 640×640 时分别为 6400/1600/400 锚点</p>
 *
 * <p><b>支持的模型名称：</b>{@code scrfd*}、{@code det_10g*}（buffalo_l/m 检测模型）、
 * {@code det_500m*}（buffalo_s/sc 轻量检测模型）</p>
 *
 * <p><b>参数说明（通过 {@link VisionParams} 传入）：</b></p>
 * <ul>
 *   <li>{@code confidence} — 置信度阈值，默认 0.5</li>
 *   <li>{@code nmsThreshold} — NMS IoU 阈值，默认 0.4</li>
 *   <li>{@code maxDetections} — 最大返回人脸数，默认 50</li>
 * </ul>
 */
@Component
@Order(900)
public class ScrfdAdapter extends AbstractModelAdapter<List<FaceDetectionAdapter.FaceDetection>>
        implements FaceDetectionAdapter {

    /** 默认输入尺寸 */
    private static final int INPUT_SIZE = 640;

    /** SCRFD 三种 stride（锚点生成步长） */
    private static final int[] STRIDES = {8, 16, 32};

    /** 每个位置生成 2 个锚点（SCRFD 默认 num_anchors=2） */
    private static final int NUM_ANCHORS = 2;

    @Override
    public boolean supports(String modelNameOrPath) {
        String lower = modelNameOrPath.toLowerCase();
        // 匹配 scrfd* 及 buffalo_l/m 中的 det_10g、antelopev2 中的 scrfd_10g_bnkps
        return lower.contains("scrfd") || lower.contains("det_10g")
                || lower.contains("det_500m") || lower.contains("det_2.5g")
                        && lower.contains("bnkps");
    }

    @Override
    public int inputSize() {
        return INPUT_SIZE;
    }

    /**
     * 前处理：图像转张量，减均值归一化（(pixel - 127.5) / 128），输出 [1, 3, H, W]
     */
    @Override
    public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
        Image djlImage = ImageFactory.getInstance().fromImage(image);
        NDArray base = djlImage.toNDArray(manager, Image.Flag.COLOR)
                .toType(DataType.FLOAT32, false)
                .transpose(2, 0, 1)     // HWC → CHW
                .expandDims(0);         // CHW → [1, C, H, W]
        return base.sub(127.5f).div(128f);
    }

    /**
     * 后处理：解析 SCRFD 9 个输出头，生成候选框和关键点，过滤低置信度框，执行 NMS
     *
     * <p>输出张量顺序（固定）：
     * score_8, score_16, score_32, bbox_8, bbox_16, bbox_32, kps_8, kps_16, kps_32</p>
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

        List<float[]> boxes = new ArrayList<>(); // [x1,y1,x2,y2,score, lm0x,lm0y,...,lm4x,lm4y]

        // 遍历三个 stride 的输出头（输出张量顺序：score*3, bbox*3, kps*3）
        for (int si = 0; si < STRIDES.length; si++) {
            int stride = STRIDES[si];

            // score: [fH*fW*numAnchors, 2]，取索引 1（前景置信度）
            float[] scores   = output.get(si).toFloatArray();
            float[] bboxRaw  = output.get(si + 3).toFloatArray();
            float[] kpsRaw   = output.get(si + 6).toFloatArray();

            int fH = INPUT_SIZE / stride;
            int fW = INPUT_SIZE / stride;
            int numAnchors = fH * fW * NUM_ANCHORS;

            // 预先生成锚点中心（cx, cy）
            float[][] anchors = generateAnchors(fH, fW, stride);

            for (int ai = 0; ai < numAnchors; ai++) {
                // score shape: [numAnchors, 2]，前景置信度在第 1 列（softmax 后最大值）
                float score = scores[ai * 2 + 1];
                if (score < confThreshold) continue;

                float cx = anchors[ai][0];
                float cy = anchors[ai][1];

                // bbox 解码：[dx1, dy1, dx2, dy2]（相对于锚点中心的偏移，乘以 stride）
                float x1 = (cx - bboxRaw[ai * 4]     * stride) * scaleX;
                float y1 = (cy - bboxRaw[ai * 4 + 1] * stride) * scaleY;
                float x2 = (cx + bboxRaw[ai * 4 + 2] * stride) * scaleX;
                float y2 = (cy + bboxRaw[ai * 4 + 3] * stride) * scaleY;

                float[] box = new float[15];
                box[0] = x1; box[1] = y1; box[2] = x2; box[3] = y2; box[4] = score;

                // kps 解码：5 个关键点，每点 (dx, dy) 相对于锚点中心
                for (int k = 0; k < 5; k++) {
                    box[5 + k * 2]     = (cx + kpsRaw[ai * 10 + k * 2]     * stride) * scaleX;
                    box[5 + k * 2 + 1] = (cy + kpsRaw[ai * 10 + k * 2 + 1] * stride) * scaleY;
                }
                boxes.add(box);
            }
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

    // ─────────────────────────── 锚点生成 ───────────────────────────

    /**
     * 生成特征图上所有锚点的中心坐标（输入尺寸下的像素坐标）
     * <p>SCRFD 每个位置生成 {@value #NUM_ANCHORS} 个锚点，中心相同</p>
     *
     * @param fH     特征图高度（= INPUT_SIZE / stride）
     * @param fW     特征图宽度
     * @param stride 步长
     * @return 所有锚点中心 [fH*fW*numAnchors, 2]，每项 [cx, cy]
     */
    private float[][] generateAnchors(int fH, int fW, int stride) {
        float[][] anchors = new float[fH * fW * NUM_ANCHORS][2];
        int idx = 0;
        for (int y = 0; y < fH; y++) {
            for (int x = 0; x < fW; x++) {
                float cx = (x + 0.5f) * stride;
                float cy = (y + 0.5f) * stride;
                for (int a = 0; a < NUM_ANCHORS; a++) {
                    anchors[idx][0] = cx;
                    anchors[idx][1] = cy;
                    idx++;
                }
            }
        }
        return anchors;
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
