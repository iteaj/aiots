package com.iteaj.example.library;

import com.iteaj.framework.spi.ai.vision.FaceLibrary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 自定义人脸库实现示例（数据库存储）
 *
 * <p>展示如何将人脸特征持久化到数据库，替代默认的内存存储</p>
 * <p>使用 @Primary 注解覆盖默认的 InMemoryFaceLibrary</p>
 */
@Component
@Primary  // 覆盖默认的 InMemoryFaceLibrary
public class DatabaseFaceLibraryExample implements FaceLibrary {

    // @Autowired
    // private FaceFeatureMapper faceFeatureMapper;  // 你的数据库Mapper

    /**
     * 注册人脸：将特征向量存入数据库
     *
     * @param faceId     人脸唯一ID（系统自动生成）
     * @param personId   人员业务ID
     * @param personName 人员姓名
     * @param feature    512维特征向量（已L2归一化）
     */
    @Override
    public void register(String faceId, String personId, String personName, float[] feature) {
        // FaceFeatureEntity entity = new FaceFeatureEntity();
        // entity.setFaceId(faceId);
        // entity.setPersonId(personId);
        // entity.setPersonName(personName);
        // entity.setFeature(feature);  // float[] 转 JSON 或二进制存储
        // faceFeatureMapper.insert(entity);
    }

    /**
     * 注销人脸：从数据库删除
     */
    @Override
    public void unregister(String faceId) {
        // faceFeatureMapper.deleteById(faceId);
    }

    /**
     * 查找最佳匹配：计算余弦相似度，返回最相似的人脸
     *
     * @param feature   待比对的特征向量
     * @param threshold 相似度阈值（0~1），低于此值返回null
     * @return 最佳匹配结果，无匹配时返回null
     */
    @Override
    public MatchResult findBest(float[] feature, float threshold) {
        // 从数据库查询所有特征
        // List<FaceFeatureEntity> all = faceFeatureMapper.selectAll();

        MatchResult best = null;
        float bestScore = threshold;

        // for (FaceFeatureEntity entity : all) {
        //     float similarity = cosineSimilarity(feature, entity.getFeature());
        //     if (similarity > bestScore) {
        //         bestScore = similarity;
        //         best = new MatchResult();
        //         best.setFaceId(entity.getFaceId());
        //         best.setPersonId(entity.getPersonId());
        //         best.setPersonName(entity.getPersonName());
        //         best.setSimilarity(similarity);
        //     }
        // }

        return best;
    }

    /**
     * 查询所有已注册人脸
     */
    @Override
    public List<FaceInfo> listAll() {
        // return faceFeatureMapper.selectAll().stream()
        //     .map(this::convertToFaceInfo)
        //     .collect(Collectors.toList());
        return null;
    }

    /**
     * 计算余弦相似度
     *
     * <p>特征向量已L2归一化，直接点积即可得到余弦相似度</p>
     *
     * @param a 特征向量a
     * @param b 特征向量b
     * @return 相似度 0~1
     */
    private float cosineSimilarity(float[] a, float[] b) {
        float sum = 0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i] * b[i];
        }
        return sum;
    }
}
