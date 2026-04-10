package com.iteaj.example.adapter;

import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.ImageFactory;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.ndarray.types.DataType;
import com.iteaj.framework.spi.ai.vision.VisionParams;
import com.iteaj.framework.spi.ai.vision.adapter.DetectionAdapter;
import com.iteaj.framework.spi.ai.vision.model.DetectionResult;
import com.iteaj.iboot.plugin.ai.vision.adapter.AbstractModelAdapter;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义 YOLO 模型适配器示例
 *
 * <p>展示如何接入新的 ONNX 目标检测模型</p>
 * <p>使用方式：将模型文件放入 models 目录，通过 modelName("myyolo") 调用</p>
 */
@Component
@Order(100)  // 优先级高于内置适配器（内置为1000）
public class MyYoloAdapterExample extends AbstractModelAdapter<List<DetectionResult>>
        implements DetectionAdapter {

    /**
     * 声明本适配器支持的模型名称模式
     * 当用户传入的模型名包含 "myyolo" 时，自动路由到此适配器
     */
    @Override
    public boolean supports(String modelNameOrPath) {
        return modelNameOrPath.toLowerCase().contains("myyolo");
    }

    /**
     * 模型输入尺寸（正方形边长）
     * 框架会自动将图像 Letterbox 缩放到此尺寸
     */
    @Override
    public int inputSize() {
        return 640;
    }

    /**
     * 图像预处理：BufferedImage → NDArray（模型输入张量）
     *
     * @param manager DJL 的 NDManager，用于创建张量
     * @param image   已由框架缩放到 inputSize() 的图像
     * @param params  用户传入的推理参数
     * @return 模型输入张量，shape 通常为 [1, 3, H, W]
     */
    @Override
    public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
        // 使用 DJL ImageFactory 进行标准化处理
        Image djlImage = ImageFactory.getInstance().fromImage(image);

        return djlImage.toNDArray(manager, Image.Flag.COLOR)
                .div(255f)                    // 归一化到 [0,1]
                .transpose(2, 0, 1)           // HWC → CHW
                .expandDims(0)                // 加 batch 维度 [1,3,H,W]
                .toType(DataType.FLOAT32, false);
    }

    /**
     * 结果后处理：NDList（模型原始输出）→ 业务对象
     *
     * @param output 模型原始输出
     * @param origW  原始图像宽度（用于坐标反算）
     * @param origH  原始图像高度
     * @param params 用户传入的推理参数（置信度阈值、NMS阈值等）
     * @return 检测结果列表
     */
    @Override
    public List<DetectionResult> postprocess(NDList output, int origW, int origH,
                                              VisionParams params) {
        // 从 VisionParams 读取阈值参数，使用默认值兜底
        float confThreshold = params != null && params.getConfidence() != null
                ? params.getConfidence() : 0.5f;
        float nmsThreshold = params != null && params.getNmsThreshold() != null
                ? params.getNmsThreshold() : 0.45f;

        List<DetectionResult> results = new ArrayList<>();

        // TODO: 根据实际模型输出格式解析
        // YOLOv8 输出格式示例：[1, 84, 8400]
        // 84 = 4(框坐标) + 80(COCO类别数)，8400 = 候选框数量

        // float[] raw = output.get(0).squeeze(0).transpose().toFloatArray();
        // ... 解析逻辑：过滤低置信度 → NMS去重 → 构建 DetectionResult

        return results;
    }
}
