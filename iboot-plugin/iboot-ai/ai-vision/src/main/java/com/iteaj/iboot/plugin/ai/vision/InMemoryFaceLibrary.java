package com.iteaj.iboot.plugin.ai.vision;

import com.iteaj.framework.spi.ai.vision.FaceLibrary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 人员特征库内存实现
 * <p>将所有人脸特征向量缓存在 ConcurrentHashMap 中，适合人员数量在 10 万以内的场景</p>
 * <p>应用重启后数据丢失，如需持久化请实现 {@link FaceLibrary} 并替换此 Bean</p>
 */
@Component
public class InMemoryFaceLibrary implements FaceLibrary {

    /** 人脸特征存储：faceId → FaceEntry */
    private final Map<String, FaceEntry> store = new ConcurrentHashMap<>();

    @Override
    public void register(String faceId, String personId, String personName, float[] feature) {
        store.put(faceId, new FaceEntry(faceId, personId, personName, feature));
    }

    @Override
    public void remove(String faceId) {
        store.remove(faceId);
    }

    @Override
    public List<FaceEntry> listAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public MatchResult findBest(float[] feature, float threshold) {
        String bestFaceId = null;
        String bestPersonId = null;
        String bestPersonName = null;
        float bestSimilarity = -1f;

        for (FaceEntry entry : store.values()) {
            float similarity = cosineSimilarity(feature, entry.getFeature());
            if (similarity > bestSimilarity) {
                bestSimilarity = similarity;
                bestFaceId = entry.getFaceId();
                bestPersonId = entry.getPersonId();
                bestPersonName = entry.getPersonName();
            }
        }

        // 最高相似度低于阈值，认为未匹配
        if (bestFaceId == null || bestSimilarity < threshold) {
            return null;
        }
        return new MatchResult(bestFaceId, bestPersonId, bestPersonName, bestSimilarity);
    }

    /**
     * 余弦相似度计算
     * <p>公式：cos(θ) = (A·B) / (|A| × |B|)</p>
     * <p>结果范围 [-1, 1]，人脸比对通常在 [0.4, 1.0] 之间</p>
     *
     * @param a 特征向量 A
     * @param b 特征向量 B
     * @return 余弦相似度
     */
    private float cosineSimilarity(float[] a, float[] b) {
        double dot = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            dot += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        if (normA == 0.0 || normB == 0.0) {
            return 0f;
        }
        return (float) (dot / (Math.sqrt(normA) * Math.sqrt(normB)));
    }
}
