---
name: ai-vision-architecture
description: AI视觉模块开发指南。面向开发人员的快速上手指南，包含如何调用现有能力、如何扩展新模型、如何新增场景。
---

# AI 视觉模块开发指南

## 快速开始

### 1. 调用现有能力（3分钟上手）

**目标检测**：

完整示例代码见 [AiVisionExample.java](AiVisionExample.java)

```java
@Autowired
private VisionService visionService;

// 最简单调用 - 使用默认 yolov8n 模型
List<DetectionResult> results = visionService.detect(
    VisionRequest.of(ImageSource.of(imageBytes))
);

// 完整参数调用
List<DetectionResult> results = visionService.detect(
    VisionRequest.of(ImageSource.of(imageBytes))
        .params(VisionParams.builder()
            .modelName("yolov8s")              // 指定模型
            .confidence(0.6f)                  // 置信度阈值
            .nmsThreshold(0.5f)                // NMS阈值
            .maxDetections(50)                 // 最大返回数量
            .extra("classNames", "person,car") // 类别名称（可选）
            .build())
);
```

**人脸识别**（检测+比对）：
```java
// 识别图中所有人脸，并比对人员库
List<FaceResult> faces = visionService.recognizeFace(
    VisionRequest.of(ImageSource.of(imageBytes))
        .params(VisionParams.builder()
            .modelName("scrfd_10g_bnkps")      // 检测模型
            .extra("arcfaceModel", "glintr100") // 特征模型（亚洲优化）
            .extra("matchThreshold", 0.75f)    // 比对阈值
            .build())
);

// 注册人脸到人员库
FaceRegisterResult result = visionService.registerFace(
    VisionRequest.of(ImageSource.of(imageBytes)),
    "person-001",    // 业务ID
    "张三"           // 姓名
);
```

**支持的图像来源**：
```java
ImageSource.of(byte[] imageBytes)           // 字节数组
ImageSource.of(InputStream imageStream)     // 输入流
ImageSource.ofPath("/path/to/image.jpg")    // 文件路径
```

> 更多调用示例（文件路径、输入流、模型路径指定等）见 [AiVisionExample.java](AiVisionExample.java)

---

## 核心概念（必须理解）

### 三层架构

```
┌─────────────────────────────────────────────────────────────┐
│  第1层：VisionService（门面接口）                              │
│  - 提供 detect() / recognizeFace() / ocr() 等场景方法         │
│  - 直接调用，无需关心底层实现                                  │
├─────────────────────────────────────────────────────────────┤
│  第2层：VisionAnalyzer（场景分析器）                           │
│  - DetectionAnalyzer / FaceAnalyzer / OcrAnalyzer 等         │
│  - 负责场景编排：参数解析 → 适配器匹配 → 推理 → 后处理          │
├─────────────────────────────────────────────────────────────┤
│  第3层：ModelAdapter（模型适配器）                             │
│  - YoloV8DetectionAdapter / RetinaFaceAdapter / ArcFaceAdapter │
│  - 封装特定模型的预处理/后处理逻辑                              │
└─────────────────────────────────────────────────────────────┘
```

### 模型自动路由机制

**问题**：换模型文件时，如何自动匹配对应的预处理/后处理？

**解决方案**：
```java
// 1. 适配器声明支持的模型名称模式
@Component
@Order(1000)
public class YoloV8DetectionAdapter implements DetectionAdapter {
    @Override
    public boolean supports(String modelNameOrPath) {
        String lower = modelNameOrPath.toLowerCase();
        return lower.contains("yolov8") || lower.contains("yolov9") 
            || lower.contains("yolov10") || lower.startsWith("yolo");
    }
}

// 2. 分析器通过 ModelAdapterRegistry 自动匹配
DetectionAdapter adapter = adapterRegistry.match("yolov8s.onnx", DetectionAdapter.class);
// 返回 YoloV8DetectionAdapter 实例
```

**优先级控制**：`@Order(数值)` 越小越优先，内置适配器默认 1000，自定义可设 100。

---

## 扩展开发指南

### 场景一：接入新的 ONNX 模型（新增适配器）

**适用情况**：已有 ONNX 模型，需要接入系统（如新的 YOLO 版本、新的人脸检测模型）。

**完整示例代码**：[MyYoloAdapterExample.java](MyYoloAdapterExample.java)

**步骤**：

1. **创建适配器类**（在 `ai-vision/adapter` 包下）：

```java
@Component
@Order(100)  // 优先级高于内置适配器
public class MyYoloAdapter extends AbstractModelAdapter<List<DetectionResult>>
        implements DetectionAdapter {

    @Override
    public boolean supports(String modelNameOrPath) {
        // 匹配模型文件名特征
        return modelNameOrPath.toLowerCase().contains("myyolo");
    }

    @Override
    public int inputSize() {
        return 640;  // 模型输入尺寸（正方形边长）
    }

    @Override
    public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
        // 图像 → 张量转换
        Image djlImage = ImageFactory.getInstance().fromImage(image);
        return djlImage.toNDArray(manager, Image.Flag.COLOR)
                .div(255f)                    // 归一化到 [0,1]
                .transpose(2, 0, 1)           // HWC → CHW
                .expandDims(0)                // 加 batch 维度 [1,3,H,W]
                .toType(DataType.FLOAT32, false);
    }

    @Override
    public List<DetectionResult> postprocess(NDList output, int origW, int origH, 
                                              VisionParams params) {
        // 从 VisionParams 读取阈值参数
        float confThreshold = params != null && params.getConfidence() != null 
                ? params.getConfidence() : 0.5f;
        
        // 解析模型输出 NDList → 业务对象
        List<DetectionResult> results = new ArrayList<>();
        // ... 解析逻辑（参考 YoloV8DetectionAdapter）
        return results;
    }
}
```

2. **放置模型文件**：
```
models/
└── myyolo.onnx    # 模型文件放入 models 目录
```

3. **调用测试**：
```java
List<DetectionResult> results = visionService.detect(
    VisionRequest.of(ImageSource.of(imageBytes))
        .params(VisionParams.builder()
            .modelName("myyolo")    // 匹配 supports() 方法
            .build())
);
```

**关键要点**：
- 继承 `AbstractModelAdapter<O>` 获得图像处理工具方法
- 实现对应场景的 SPI 接口（如 `DetectionAdapter`）
- `preprocess()` 负责：BufferedImage → NDArray（模型输入）
- `postprocess()` 负责：NDList（原始输出）→ 业务结果对象
- 通过 `VisionParams` 读取用户传入的参数

> 完整代码和详细注释见 [MyYoloAdapterExample.java](MyYoloAdapterExample.java)

---

### 场景二：新增视觉场景（新增分析器）

**适用情况**：需要支持全新的视觉任务（如新的检测类型、新的识别任务）。

**完整示例代码**：[MyAnalyzerExample.java](MyAnalyzerExample.java)

**步骤**：

1. **定义结果模型**（在 `framework/spi/ai/vision/model` 下）
2. **定义 SPI 接口**（在 `framework/spi/ai/vision/adapter` 下）
3. **创建适配器实现**（在 `ai-vision/adapter` 下）
4. **创建分析器**（在 `ai-vision/analyzer` 下）：

```java
@Component
public class MyAnalyzer extends AbstractVisionAnalyzer<MyResult> {

    @Autowired
    @Override
    public void setAdapterRegistry(ModelAdapterRegistry adapterRegistry) {
        super.setAdapterRegistry(adapterRegistry);
    }

    @Override
    public VisionType analyzerType() { 
        return VisionType.MY_TYPE;  // 需在 VisionType 枚举中添加
    }

    @Override
    public String defaultModel() { return "mymodel"; }

    @Override
    public MyResult analyze(VisionRequest request) {
        // 1. 解析模型引用
        ModelRef modelRef = resolveModel(request);
        
        // 2. 匹配适配器
        MyAdapter adapter = adapterRegistry.match(modelRef.getValue(), MyAdapter.class);
        AbstractModelAdapter<?> djlAdapter = (AbstractModelAdapter<?>) adapter;
        
        // 3. 加载图像
        BufferedImage image = loadImage(request.getSource());
        
        // 4. 缩放
        BufferedImage resized = adapter.letterboxResize(image, adapter.inputSize(), 
                                                         adapter.inputSize());
        
        // 5. 推理
        try (Model model = loadModel(modelRef, adapter.engine());
             NDManager manager = NDManager.newBaseManager();
             Predictor<BufferedImage, NDList> predictor = model.newPredictor(
                     new RawTranslator(manager, djlAdapter, request.getParams()))) {
            
            NDList output = predictor.predict(resized);
            return (MyResult) djlAdapter.postprocess(output, 
                    image.getWidth(), image.getHeight(), request.getParams());
        }
    }
}
```

5. **添加到 VisionService**（在 `VisionService` 中添加方法，`VisionServiceImpl` 中实现）

> 完整代码和详细步骤见 [MyAnalyzerExample.java](MyAnalyzerExample.java)

---

### 场景三：自定义人脸库实现

**适用情况**：需要将人脸特征持久化到数据库（而非默认的内存存储）。

**完整示例代码**：[DatabaseFaceLibraryExample.java](DatabaseFaceLibraryExample.java)

**步骤**：

1. **实现 FaceLibrary 接口**：

```java
@Component
@Primary  // 覆盖默认的 InMemoryFaceLibrary
public class DatabaseFaceLibrary implements FaceLibrary {
    
    @Autowired
    private FaceFeatureMapper faceFeatureMapper;
    
    @Override
    public void register(String faceId, String personId, String personName, float[] feature) {
        // 将特征向量存入数据库
    }
    
    @Override
    public MatchResult findBest(float[] feature, float threshold) {
        // 查询所有特征，计算余弦相似度，返回最佳匹配
    }
    
    private float cosineSimilarity(float[] a, float[] b) {
        // 特征向量已L2归一化，直接点积即可
        float sum = 0;
        for (int i = 0; i < a.length; i++) sum += a[i] * b[i];
        return sum;
    }
}
```

> 完整代码见 [DatabaseFaceLibraryExample.java](DatabaseFaceLibraryExample.java)

---

## 内置适配器速查表

| 适配器 | 匹配规则 | 输入尺寸 | 归一化 | 输出格式 | 适用场景 |
|--------|----------|----------|--------|----------|----------|
| `YoloV8DetectionAdapter` | yolov8/9/10/yolo* | 640 | [0,1] | List<DetectionResult> | 通用目标检测 |
| `RetinaFaceAdapter` | retinaface*/det_2.5g* | 640 | (p-127.5)/128 | List<FaceDetection> | 人脸检测（轻量） |
| `ScrfdAdapter` | scrfd*/det_10g* | 640 | (p-127.5)/128 | List<FaceDetection> | 人脸检测（高精度） |
| `ArcFaceAdapter` | arcface*/w600k* | 112 | [-1,1] | float[512] | 人脸特征提取 |
| `GlintR100Adapter` | glintr100*/r100* | 112 | [-1,1] | float[512] | 亚洲人脸优化 |

**优先级**：GlintR100/Scrfd @Order(900)，其他 @Order(1000)

---

## 常见问题

### Q1: 如何指定模型文件路径而非名称？

```java
VisionParams.builder()
    .modelPath("/opt/models/custom/yolov8m.onnx")  // 绝对路径
    .build()
```

### Q2: 如何为不同场景配置不同的默认模型？

```java
// 在 Analyzer 中覆盖 defaultModel()
@Override
public String defaultModel() {
    return System.getProperty("vision.detection.model", "yolov8n");
}
```

### Q3: 模型输出格式与内置适配器不匹配怎么办？

创建新的适配器类，在 `postprocess()` 中按实际格式解析：
```java
@Override
public List<DetectionResult> postprocess(NDList output, int origW, int origH, VisionParams params) {
    // 你的模型输出格式可能是 [N, 6] 或其他格式
    // 按实际格式解析即可
}
```

### Q4: 如何调试预处理/后处理逻辑？

```java
// 在 preprocess/postprocess 中添加日志
@Override
public NDArray preprocess(NDManager manager, BufferedImage image, VisionParams params) {
    System.out.println("Input image size: " + image.getWidth() + "x" + image.getHeight());
    NDArray tensor = ...;
    System.out.println("Output tensor shape: " + tensor.getShape());
    return tensor;
}
```

---

## 模型下载来源

| 模型类型 | 来源 | 说明 |
|----------|------|------|
| YOLOv8/v9/v10 | [Ultralytics](https://github.com/ultralytics/ultralytics) | 官方导出 ONNX |
| RetinaFace/SCRFD/ArcFace | [InsightFace](https://github.com/deepinsight/insightface) | buffalo/antelopev2 套件 |

**buffalo 包规格**：
- `buffalo_s`：轻量版（det_2.5g + w600k_mbf）
- `buffalo_m`：中配版（det_10g + w600k_r50）
- `buffalo_l`：完整版（det_10g + w600k_r50 + 其他）

---

## 关键设计原则

1. **SPI 层无 DJL 依赖**：framework 模块不引入 DJL，避免依赖传递
2. **自动路由**：通过 `supports()` + `@Order` 实现模型自动匹配，无需配置
3. **参数继承链**：VisionParams → 分析器默认值 → 适配器专有参数（extraParams）
4. **L2 归一化**：人脸特征向量自动归一化，直接使用余弦相似度比对
