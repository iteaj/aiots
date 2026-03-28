package com.iteaj.iboot.plugin.ai.vision.adapter;

import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import com.iteaj.framework.spi.ai.vision.VisionParams;
import com.iteaj.framework.spi.ai.vision.adapter.DetectionAdapter;
import com.iteaj.framework.spi.ai.vision.model.DetectionResult;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * YOLOv8 目标检测适配器
 * <p>适用于 YOLOv8 / YOLOv9 / YOLOv10 系列导出的 ONNX 模型</p>
 *
 * <p><b>输入规格：</b>{@code [1, 3, 640, 640]}，RGB，值域 [0, 1]</p>
 * <p><b>输出规格：</b>{@code [1, 84, 8400]}，其中 84 = 4（cx,cy,w,h）+ 80（COCO 类别数），
 * 8400 为候选框数量（可随模型输入尺寸变化）</p>
 *
 * <p><b>支持的模型名称前缀：</b>{@code yolov8}、{@code yolov9}、{@code yolov10}、{@code yolo}（通用兜底）</p>
 *
 * <p><b>参数说明（通过 {@link VisionParams} 传入）：</b></p>
 * <ul>
 *   <li>{@code confidence} — 置信度阈值，默认 0.5</li>
 *   <li>{@code nmsThreshold} — NMS IoU 阈值，默认 0.45</li>
 *   <li>{@code maxDetections} — 最大返回结果数，默认 100</li>
 *   <li>{@code extraParams.classNames} — 逗号分隔的类别名称列表，如 {@code "person,car,dog"}，
 *       未设置时用类别索引作为名称</li>
 * </ul>
 */
@Component
@Order(1000)
public class YoloV8DetectionAdapter extends AbstractModelAdapter<List<DetectionResult>>
        implements DetectionAdapter {

    /** 默认输入尺寸 */
    private static final int INPUT_SIZE = 640;

    @Override
    public boolean supports(String modelNameOrPath) {
        String lower = modelNameOrPath.toLowerCase();
        // 支持 yolov8*、yolov9*、yolov10* 及通用 yolo* 前缀
        return lower.contains("yolov8") || lower.contains("yolov9")
                || lower.contains("yolov10") || lower.startsWith("yolo");
    }

    @Override
    public int inputSize() {
        return INPUT_SIZE;
    }

    /**
     * 前处理：图像 Letterbox 缩放后转为归一化张量 [1, 3, 640, 640]，值域 [0, 1]
     */
    @Override
    public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
        return toNDArray(manager, image, params);
    }

    /**
     * 后处理：解析 YOLOv8 输出 [1, 84, 8400]，过滤低置信度框，执行 NMS
     *
     * <p>YOLOv8 输出格式（与 YOLOv5 不同，无 objectness 分数）：
     * <ul>
     *   <li>前 4 行：cx, cy, w, h（相对于输入尺寸的归一化坐标）</li>
     *   <li>后 80 行：每个类别的置信度分数</li>
     * </ul>
     * 每列为一个候选框，共 8400 列。</p>
     */
    @Override
    public List<DetectionResult> postprocess(NDList output, int origW, int origH, VisionParams params) {
        float confThreshold = params != null && params.getConfidence() != null
                ? params.getConfidence() : 0.5f;
        float nmsThreshold = params != null && params.getNmsThreshold() != null
                ? params.getNmsThreshold() : 0.45f;
        int maxDetections = params != null && params.getMaxDetections() != null
                ? params.getMaxDetections() : 100;

        // 解析类别名列表（可选，用于展示有意义的类别名称）
        String[] classNames = resolveClassNames(params);

        // 输出 shape: [1, numClasses+4, numBoxes]，转置为 [numBoxes, numClasses+4] 更易处理
        float[] raw = output.get(0).squeeze(0).transpose().toFloatArray();
        int numBoxes = (int) output.get(0).getShape().get(2);
        int numFields = (int) output.get(0).getShape().get(1); // 84
        int numClasses = numFields - 4;

        List<float[]> boxes = new ArrayList<>(); // [x1, y1, x2, y2, score, classIdx]

        for (int i = 0; i < numBoxes; i++) {
            int offset = i * numFields;
            // cx, cy, w, h 为相对于输入尺寸的像素坐标
            float cx = raw[offset];
            float cy = raw[offset + 1];
            float bw = raw[offset + 2];
            float bh = raw[offset + 3];

            // 找最大类别置信度
            float maxScore = 0f;
            int maxClassIdx = 0;
            for (int c = 0; c < numClasses; c++) {
                float score = raw[offset + 4 + c];
                if (score > maxScore) {
                    maxScore = score;
                    maxClassIdx = c;
                }
            }

            if (maxScore < confThreshold) continue;

            // 转换为原始图像像素坐标（反算 Letterbox 缩放）
            float scaleX = (float) origW / INPUT_SIZE;
            float scaleY = (float) origH / INPUT_SIZE;
            float x1 = (cx - bw / 2f) * scaleX;
            float y1 = (cy - bh / 2f) * scaleY;
            float x2 = (cx + bw / 2f) * scaleX;
            float y2 = (cy + bh / 2f) * scaleY;

            boxes.add(new float[]{x1, y1, x2, y2, maxScore, maxClassIdx});
        }

        // 执行 NMS 去除重叠框
        List<float[]> kept = nms(boxes, nmsThreshold);

        // 按置信度降序，截取 maxDetections 个结果
        kept.sort((a, b) -> Float.compare(b[4], a[4]));
        if (kept.size() > maxDetections) {
            kept = kept.subList(0, maxDetections);
        }

        List<DetectionResult> results = new ArrayList<>(kept.size());
        for (float[] box : kept) {
            int classIdx = (int) box[5];
            String className = (classNames != null && classIdx < classNames.length)
                    ? classNames[classIdx] : String.valueOf(classIdx);
            results.add(new DetectionResult()
                    .setX(box[0])
                    .setY(box[1])
                    .setWidth(box[2] - box[0])
                    .setHeight(box[3] - box[1])
                    .setConfidence(box[4])
                    .setClassIndex(classIdx)
                    .setClassName(className));
        }
        return results;
    }

    /**
     * NMS（非极大值抑制）：去除重叠度超过阈值的重复检测框
     *
     * @param boxes        候选框列表，每项 [x1, y1, x2, y2, score, classIdx]
     * @param iouThreshold IoU 阈值，超过此值认为是重叠框
     * @return 保留的框列表
     */
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
                float[] b = boxes.get(j);
                // 仅对同类别做 NMS（不同类别不压制）
                if ((int) a[5] != (int) b[5]) continue;
                if (iou(a, b) > iouThreshold) suppressed[j] = true;
            }
        }
        return kept;
    }

    /**
     * 计算两个框的 IoU（交并比）
     *
     * @param a 框 a [x1, y1, x2, y2, ...]
     * @param b 框 b [x1, y1, x2, y2, ...]
     * @return IoU 值（0~1）
     */
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

    /**
     * 从 extraParams 解析类别名称列表
     * <p>支持传入逗号分隔的字符串，如 {@code "person,car,dog"}</p>
     *
     * @param params 推理参数
     * @return 类别名称数组，或 null（未设置时用类别索引作为名称）
     */
    private String[] resolveClassNames(VisionParams params) {
        if (params != null && params.getExtraParams() != null) {
            Object names = params.getExtraParams().get("classNames");
            if (names instanceof String) {
                return ((String) names).split(",");
            }
            if (names instanceof String[]) {
                return (String[]) names;
            }
        }
        return null;
    }
}
