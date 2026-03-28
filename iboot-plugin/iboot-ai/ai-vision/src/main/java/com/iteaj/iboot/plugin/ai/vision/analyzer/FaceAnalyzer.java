package com.iteaj.iboot.plugin.ai.vision.analyzer;

import ai.djl.Model;
import ai.djl.inference.Predictor;
import ai.djl.ndarray.NDArray;
import ai.djl.ndarray.NDList;
import ai.djl.ndarray.NDManager;
import ai.djl.translate.Batchifier;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorContext;
import com.iteaj.framework.spi.ai.vision.FaceLibrary;
import com.iteaj.framework.spi.ai.vision.ModelRef;
import com.iteaj.framework.spi.ai.vision.VisionRequest;
import com.iteaj.framework.spi.ai.vision.VisionType;
import com.iteaj.framework.spi.ai.vision.adapter.FaceDetectionAdapter;
import com.iteaj.framework.spi.ai.vision.adapter.FaceDetectionAdapter.FaceDetection;
import com.iteaj.framework.spi.ai.vision.adapter.FaceFeatureAdapter;
import com.iteaj.framework.spi.ai.vision.model.FaceRegisterResult;
import com.iteaj.framework.spi.ai.vision.model.FaceResult;
import com.iteaj.framework.spi.ai.vision.model.Point;
import com.iteaj.iboot.plugin.ai.vision.adapter.AbstractModelAdapter;
import com.iteaj.iboot.plugin.ai.vision.adapter.ModelAdapterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 人脸识别分析器
 * <p>两阶段流程：</p>
 * <ol>
 *   <li>检测阶段：由 {@link FaceDetectionAdapter} 定位人脸框及 5 个关键点（双眼/鼻尖/左右嘴角）</li>
 *   <li>识别阶段：由 {@link FaceFeatureAdapter} 对对齐后的人脸子图提取 512 维特征向量，
 *       与人员库做余弦相似度比对</li>
 * </ol>
 *
 * <p><b>检测模型：</b>RetinaFace（{@code retinaface*}）/ SCRFD（{@code scrfd*}）</p>
 * <p><b>特征提取模型：</b>ArcFace（{@code arcface*}、{@code w600k*}）/
 *     GlintR100（{@code glintr100*}，亚洲人脸优化）</p>
 *
 * <p><b>参数示例：</b></p>
 * <pre>{@code
 * VisionParams.builder()
 *     .modelName("scrfd_10g_bnkps")                                // 人脸检测模型名称
 *     .extra("arcfaceModel", "glintr100")                          // ArcFace 模型名称（按名称）
 *     // 或 .extra("arcfaceModelPath", "/models/glintr100.onnx")   // ArcFace 模型路径（按路径）
 *     .extra("matchThreshold", 0.75f)                              // 人脸比对相似度阈值（默认 0.6）
 *     .confidence(0.5f)                                            // 人脸检测置信度阈值（默认 0.7）
 * }</pre>
 */
@Component
public class FaceAnalyzer extends AbstractVisionAnalyzer<List<FaceResult>> {

    /** ArcFace 标准对齐目标关键点位置（112×112 画布）：左眼/右眼/鼻尖/左嘴角/右嘴角 */
    private static final double[][] ARCFACE_DST_LANDMARKS = {
            {38.29, 51.70},
            {73.53, 51.70},
            {56.02, 71.74},
            {41.55, 92.36},
            {70.73, 92.36}
    };

    /** ArcFace 输入图像边长 */
    private static final int ARCFACE_INPUT_SIZE = 112;

    /** 人脸特征库，由 Spring 注入 */
    private final FaceLibrary faceLibrary;

    public FaceAnalyzer(FaceLibrary faceLibrary) {
        this.faceLibrary = faceLibrary;
    }

    @Autowired
    @Override
    public void setAdapterRegistry(ModelAdapterRegistry adapterRegistry) {
        super.setAdapterRegistry(adapterRegistry);
    }

    @Override
    public VisionType analyzerType() {
        return VisionType.FACE;
    }

    @Override
    public String defaultModel() {
        return "retinaface";
    }

    @Override
    public float defaultConfidence() {
        return 0.7f;
    }

    // ============================================================
    // 主流程：识别（检测 + 比对）
    // ============================================================

    @Override
    public List<FaceResult> analyze(VisionRequest request) {
        try {
            // 1. 解析检测模型引用（路径 > 名称 > 默认 retinaface）
            ModelRef detModelRef = resolveModel(request);
            // 2. 人脸比对相似度阈值（extraParams.matchThreshold，默认 0.6）
            float matchThreshold = resolveExtraFloat(request, "matchThreshold", 0.6f);
            // 3. 解析 ArcFace 特征提取模型引用
            ModelRef arcfaceModelRef = resolveArcfaceModel(request);

            // 4. 检测人脸，获取检测框和 5 关键点
            BufferedImage image = loadImage(request.getSource());
            List<FaceDetection> detections = detectFaces(image, detModelRef, request);

            // 5. 对每张人脸：对齐 → 提特征 → 库比对
            List<FaceResult> results = new ArrayList<>();
            for (FaceDetection det : detections) {
                BufferedImage alignedFace = alignFace(image, det.landmarks);
                float[] feature = extractFeature(alignedFace, arcfaceModelRef, request);
                FaceLibrary.MatchResult match = faceLibrary.findBest(feature, matchThreshold);

                FaceResult faceResult = new FaceResult()
                        .setConfidence(det.confidence)
                        .setX(det.x)
                        .setY(det.y)
                        .setWidth(det.width)
                        .setHeight(det.height)
                        .setLandmarks(det.landmarks);

                if (match != null) {
                    faceResult.setFaceId(match.getFaceId())
                              .setPersonName(match.getPersonName());
                }
                results.add(faceResult);
            }
            return results;
        } catch (Exception e) {
            throw new RuntimeException("人脸识别失败：" + e.getMessage(), e);
        }
    }

    // ============================================================
    // 人脸注册：提取特征并存入人员库
    // ============================================================

    /**
     * 注册人脸到人员库
     * <p>要求图像中有且仅有一张人脸，否则返回失败</p>
     *
     * @param request    包含注册照片的分析请求
     * @param personId   人员业务 ID
     * @param personName 人员姓名
     * @return 注册结果
     */
    public FaceRegisterResult register(VisionRequest request, String personId, String personName) {
        try {
            // 检测模型：由 params.modelName / params.modelPath 控制，默认 retinaface
            ModelRef detModelRef = resolveModel(request);
            // ArcFace 特征提取模型
            ModelRef arcfaceModelRef = resolveArcfaceModel(request);

            BufferedImage image = loadImage(request.getSource());
            List<FaceDetection> detections = detectFaces(image, detModelRef, request);

            if (detections.isEmpty()) {
                return new FaceRegisterResult()
                        .setSuccess(false)
                        .setDetectedFaceCount(0)
                        .setErrorMessage("图像中未检测到人脸，请确保图像清晰且包含人脸");
            }
            if (detections.size() > 1) {
                return new FaceRegisterResult()
                        .setSuccess(false)
                        .setDetectedFaceCount(detections.size())
                        .setErrorMessage("图像中包含 " + detections.size()
                                + " 张人脸，注册要求图像中有且仅有一张人脸");
            }

            FaceDetection det = detections.get(0);
            BufferedImage alignedFace = alignFace(image, det.landmarks);
            float[] feature = extractFeature(alignedFace, arcfaceModelRef, request);

            String faceId = UUID.randomUUID().toString().replace("-", "");
            faceLibrary.register(faceId, personId, personName, feature);

            return new FaceRegisterResult()
                    .setSuccess(true)
                    .setFaceId(faceId)
                    .setDetectedFaceCount(1);
        } catch (Exception e) {
            throw new RuntimeException("人脸注册失败：" + e.getMessage(), e);
        }
    }

    // ============================================================
    // 阶段一：人脸检测（委托给 FaceDetectionAdapter）
    // ============================================================

    /**
     * 通过适配器检测人脸，返回检测框和 5 个关键点列表
     * <p>适配器由 {@link ModelAdapterRegistry} 按模型名/路径自动路由</p>
     */
    private List<FaceDetection> detectFaces(BufferedImage image, ModelRef modelRef,
                                            VisionRequest request) throws Exception {
        // 匹配人脸检测适配器
        FaceDetectionAdapter adapter = adapterRegistry.match(modelRef.getValue(),
                FaceDetectionAdapter.class);
        AbstractModelAdapter<?> djlAdapter = (AbstractModelAdapter<?>) adapter;

        final int origW = image.getWidth();
        final int origH = image.getHeight();

        // Letterbox 缩放（由适配器 inputSize() 决定）
        int size = adapter.inputSize();
        BufferedImage resized = adapter.letterboxResize(image, size, size);

        // 加载模型 + 推理
        try (Model model = loadModel(modelRef, adapter.engine());
             NDManager manager = NDManager.newBaseManager();
             Predictor<BufferedImage, NDList> predictor = model.newPredictor(
                     new RawTranslator(manager, djlAdapter, request.getParams()))) {

            NDList output = predictor.predict(resized);
            @SuppressWarnings("unchecked")
            List<FaceDetection> detResult = (List<FaceDetection>) djlAdapter.postprocess(
                    output, origW, origH, request.getParams());
            return detResult;
        }
    }

    // ============================================================
    // 阶段二：人脸对齐（仿射变换）
    // ============================================================

    /**
     * 基于 5 个关键点对人脸子图做仿射变换，输出标准 112×112 对齐图
     * <p>ArcFace 标准目标关键点位置（112×112 画布）：
     * 左眼(38.29,51.70)、右眼(73.53,51.70)、鼻尖(56.02,71.74)、
     * 左嘴角(41.55,92.36)、右嘴角(70.73,92.36)</p>
     *
     * @param image     原始图像
     * @param landmarks 5 个人脸关键点
     * @return 112×112 对齐后的人脸子图
     */
    private BufferedImage alignFace(BufferedImage image, List<Point> landmarks) {
        double[][] dst = ARCFACE_DST_LANDMARKS;

        double srcEyeLeft_x  = landmarks.get(0).getX();
        double srcEyeLeft_y  = landmarks.get(0).getY();
        double srcEyeRight_x = landmarks.get(1).getX();
        double srcEyeRight_y = landmarks.get(1).getY();
        double dstEyeLeft_x  = dst[0][0];
        double dstEyeLeft_y  = dst[0][1];
        double dstEyeRight_x = dst[1][0];
        double dstEyeRight_y = dst[1][1];

        // 基于双眼向量计算旋转角度和缩放比例
        double srcAngle = Math.atan2(srcEyeRight_y - srcEyeLeft_y, srcEyeRight_x - srcEyeLeft_x);
        double dstAngle = Math.atan2(dstEyeRight_y - dstEyeLeft_y, dstEyeRight_x - dstEyeLeft_x);
        double angle = dstAngle - srcAngle;

        double srcDist = Math.sqrt(Math.pow(srcEyeRight_x - srcEyeLeft_x, 2)
                + Math.pow(srcEyeRight_y - srcEyeLeft_y, 2));
        double dstDist = Math.sqrt(Math.pow(dstEyeRight_x - dstEyeLeft_x, 2)
                + Math.pow(dstEyeRight_y - dstEyeLeft_y, 2));
        double scale = dstDist / srcDist;

        double srcCx = (srcEyeLeft_x + srcEyeRight_x) / 2.0;
        double srcCy = (srcEyeLeft_y + srcEyeRight_y) / 2.0;
        double dstCx = (dstEyeLeft_x + dstEyeRight_x) / 2.0;
        double dstCy = (dstEyeLeft_y + dstEyeRight_y) / 2.0;

        BufferedImage result = new BufferedImage(ARCFACE_INPUT_SIZE, ARCFACE_INPUT_SIZE,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = result.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // 仿射变换：以目标中心为原点，先缩放+旋转，再平移到原图对应位置
        AffineTransform at = new AffineTransform();
        at.translate(dstCx, dstCy);
        at.scale(scale, scale);
        at.rotate(angle);
        at.translate(-srcCx, -srcCy);
        g2d.setTransform(at);
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        return result;
    }

    // ============================================================
    // 阶段三：人脸特征提取（委托给 FaceFeatureAdapter）
    // ============================================================

    /**
     * 通过适配器提取人脸 512 维特征向量（L2 归一化）
     * <p>适配器由 {@link ModelAdapterRegistry} 按模型名/路径自动路由</p>
     *
     * @param alignedFace    112×112 对齐后的人脸子图
     * @param arcfaceModelRef ArcFace 模型引用
     * @param request        分析请求（用于读取 VisionParams）
     * @return L2 归一化后的 512 维特征向量
     */
    private float[] extractFeature(BufferedImage alignedFace, ModelRef arcfaceModelRef,
                                   VisionRequest request) throws Exception {
        // 匹配特征提取适配器
        FaceFeatureAdapter adapter = adapterRegistry.match(arcfaceModelRef.getValue(),
                FaceFeatureAdapter.class);
        AbstractModelAdapter<?> djlAdapter = (AbstractModelAdapter<?>) adapter;

        // 特征提取输入尺寸（通常为 112）
        int size = adapter.inputSize();
        // ArcFaceAdapter 覆盖了 letterboxResize，直接做拉伸（不做 letterbox）
        BufferedImage resized = adapter.letterboxResize(alignedFace, size, size);

        try (Model model = loadModel(arcfaceModelRef, adapter.engine());
             NDManager manager = NDManager.newBaseManager();
             Predictor<BufferedImage, NDList> predictor = model.newPredictor(
                     new RawTranslator(manager, djlAdapter, request.getParams()))) {

            NDList output = predictor.predict(resized);
            @SuppressWarnings("unchecked")
            float[] featResult = (float[]) djlAdapter.postprocess(
                    output, alignedFace.getWidth(), alignedFace.getHeight(),
                    request.getParams());
            return featResult;
        }
    }

    // ============================================================
    // ArcFace 模型参数解析
    // ============================================================

    /**
     * 从请求参数中解析 ArcFace 特征提取模型引用
     *
     * <p>参数优先级（由高到低）：</p>
     * <ol>
     *   <li>{@code extraParams.arcfaceModelPath}：ArcFace 模型的绝对文件路径，
     *       例如 {@code /opt/models/arcface_r100.onnx}</li>
     *   <li>{@code extraParams.arcfaceModel}：ArcFace 模型名称，
     *       从默认模型目录（{@code vision.model.dir} JVM 属性）加载，
     *       例如 {@code arcface_r100}</li>
     *   <li>JVM 属性 {@code vision.arcface.model}：全局默认模型名称配置</li>
     *   <li>内置默认值 {@code arcface_r100}</li>
     * </ol>
     *
     * <p>示例：</p>
     * <pre>{@code
     * // 按名称（从模型目录加载）
     * VisionParams.builder().extra("arcfaceModel", "arcface_r100")
     *
     * // 按绝对路径
     * VisionParams.builder().extra("arcfaceModelPath", "/opt/models/arcface_r100.onnx")
     * }</pre>
     *
     * @param request 视觉分析请求
     * @return ArcFace 模型引用
     */
    private ModelRef resolveArcfaceModel(VisionRequest request) {
        if (request.getParams() != null && request.getParams().getExtraParams() != null) {
            // 优先按路径加载
            Object path = request.getParams().getExtraParams().get("arcfaceModelPath");
            if (path instanceof String && !((String) path).isEmpty()) {
                return ModelRef.ofPath((String) path);
            }
            // 其次按名称加载
            Object name = request.getParams().getExtraParams().get("arcfaceModel");
            if (name instanceof String && !((String) name).isEmpty()) {
                return ModelRef.ofName((String) name);
            }
        }
        // 兜底：读取 JVM 属性，再无则使用内置默认值
        return ModelRef.ofName(System.getProperty("vision.arcface.model", "arcface_r100"));
    }

    /**
     * 从 extraParams 读取 float 参数
     */
    private float resolveExtraFloat(VisionRequest request, String key, float defaultValue) {
        if (request.getParams() != null && request.getParams().getExtraParams() != null) {
            Object val = request.getParams().getExtraParams().get(key);
            if (val instanceof Number) return ((Number) val).floatValue();
        }
        return defaultValue;
    }

    // ─────────────────────────── 内部 Translator ───────────────────────────

    /**
     * 原始直传 Translator：通过适配器将 BufferedImage 转为 NDArray 输入，输出原始 NDList
     */
    private static class RawTranslator implements Translator<BufferedImage, NDList> {

        private final NDManager manager;
        private final AbstractModelAdapter<?> adapter;
        private final com.iteaj.framework.spi.ai.vision.VisionParams params;

        RawTranslator(NDManager manager,
                      AbstractModelAdapter<?> adapter,
                      com.iteaj.framework.spi.ai.vision.VisionParams params) {
            this.manager = manager;
            this.adapter = adapter;
            this.params = params;
        }

        @Override
        public NDList processInput(TranslatorContext ctx, BufferedImage input) {
            NDArray tensor = adapter.preprocess(manager, input, params);
            return new NDList(tensor);
        }

        @Override
        public NDList processOutput(TranslatorContext ctx, NDList list) {
            return list;
        }

        @Override
        public Batchifier getBatchifier() {
            return null;
        }
    }
}
