package com.iteaj.example;

import com.iteaj.framework.spi.ai.vision.*;
import com.iteaj.framework.spi.ai.vision.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * AI 视觉模块调用示例
 *
 * <p>本示例展示如何使用 VisionService 调用各种视觉 AI 能力</p>
 */
@Service
public class AiVisionExample {

    @Autowired
    private VisionService visionService;

    /**
     * 示例1：最简单的目标检测（使用默认 yolov8n 模型）
     */
    public List<DetectionResult> detectSimple(byte[] imageBytes) {
        return visionService.detect(
                VisionRequest.of(ImageSource.of(imageBytes))
        );
    }

    /**
     * 示例2：完整参数的目标检测
     */
    public List<DetectionResult> detectWithParams(byte[] imageBytes) {
        return visionService.detect(
                VisionRequest.of(ImageSource.of(imageBytes))
                        .params(VisionParams.builder()
                                .modelName("yolov8s")              // 指定模型
                                .confidence(0.6f)                  // 置信度阈值
                                .nmsThreshold(0.5f)                // NMS阈值
                                .maxDetections(50)                 // 最大返回数量
                                .extra("classNames", "person,car") // 类别名称（可选）
                                .build())
        );
    }

    /**
     * 示例3：人脸识别（检测 + 比对）
     */
    public List<FaceResult> recognizeFace(byte[] imageBytes) {
        return visionService.recognizeFace(
                VisionRequest.of(ImageSource.of(imageBytes))
                        .params(VisionParams.builder()
                                .modelName("scrfd_10g_bnkps")       // 人脸检测模型
                                .extra("arcfaceModel", "glintr100") // 特征提取模型（亚洲优化）
                                .extra("matchThreshold", 0.75f)    // 比对阈值
                                .confidence(0.5f)                   // 检测置信度
                                .build())
        );
    }

    /**
     * 示例4：注册人脸到人员库
     */
    public FaceRegisterResult registerFace(byte[] imageBytes, String personId, String personName) {
        return visionService.registerFace(
                VisionRequest.of(ImageSource.of(imageBytes)),
                personId,    // 业务ID
                personName   // 姓名
        );
    }

    /**
     * 示例5：查询所有注册人脸
     */
    public List<FaceLibrary.FaceInfo> listAllFaces() {
        return visionService.listRegisteredFaces();
    }

    /**
     * 示例6：删除人脸
     */
    public void unregisterFace(String faceId) {
        visionService.unregisterFace(faceId);
    }

    /**
     * 示例7：使用文件路径作为图像来源
     */
    public List<DetectionResult> detectFromFile(String filePath) {
        return visionService.detect(
                VisionRequest.of(ImageSource.ofPath(filePath))
        );
    }

    /**
     * 示例8：使用输入流作为图像来源
     */
    public List<DetectionResult> detectFromStream(InputStream inputStream) throws IOException {
        return visionService.detect(
                VisionRequest.of(ImageSource.of(inputStream))
        );
    }

    /**
     * 示例9：使用绝对路径指定模型文件
     */
    public List<DetectionResult> detectWithModelPath(byte[] imageBytes) {
        return visionService.detect(
                VisionRequest.of(ImageSource.of(imageBytes))
                        .params(VisionParams.builder()
                                .modelPath("/opt/models/custom/yolov8m.onnx")  // 绝对路径
                                .confidence(0.6f)
                                .build())
        );
    }
}
