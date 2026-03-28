package com.iteaj.framework.spi.ai.vision;

import java.util.List;

/**
 * 人员特征库接口
 * <p>负责人脸特征向量的存储、查询和删除，与具体存储介质解耦</p>
 * <p>默认实现为内存版本（{@code InMemoryFaceLibrary}），可替换为数据库版本</p>
 */
public interface FaceLibrary {

    /**
     * 注册人员特征
     *
     * @param faceId    人脸唯一标识（由调用方生成，如 UUID）
     * @param personId  人员业务 ID（如工号/学号）
     * @param personName 人员姓名
     * @param feature   ArcFace 提取的特征向量（512 维）
     */
    void register(String faceId, String personId, String personName, float[] feature);

    /**
     * 删除人员特征
     *
     * @param faceId 人脸唯一标识
     */
    void remove(String faceId);

    /**
     * 查询所有已注册人员信息
     *
     * @return 人员信息列表
     */
    List<FaceEntry> listAll();

    /**
     * 与库中所有特征做余弦相似度比对，返回相似度最高的匹配结果
     * <p>若最高相似度低于 threshold，返回 null 表示未匹配</p>
     *
     * @param feature   待比对的特征向量
     * @param threshold 相似度阈值（0~1），建议 0.6~0.8
     * @return 最佳匹配结果，未匹配时返回 null
     */
    MatchResult findBest(float[] feature, float threshold);

    /**
     * 已注册人员信息条目
     */
    class FaceEntry {

        /** 人脸唯一标识 */
        private String faceId;

        /** 人员业务 ID */
        private String personId;

        /** 人员姓名 */
        private String personName;

        /** 特征向量 */
        private float[] feature;

        public FaceEntry(String faceId, String personId, String personName, float[] feature) {
            this.faceId = faceId;
            this.personId = personId;
            this.personName = personName;
            this.feature = feature;
        }

        public String getFaceId() { return faceId; }
        public String getPersonId() { return personId; }
        public String getPersonName() { return personName; }
        public float[] getFeature() { return feature; }
    }

    /**
     * 特征比对结果
     */
    class MatchResult {

        /** 匹配到的人脸唯一标识 */
        private String faceId;

        /** 匹配到的人员业务 ID */
        private String personId;

        /** 匹配到的人员姓名 */
        private String personName;

        /** 余弦相似度（0~1） */
        private float similarity;

        public MatchResult(String faceId, String personId, String personName, float similarity) {
            this.faceId = faceId;
            this.personId = personId;
            this.personName = personName;
            this.similarity = similarity;
        }

        public String getFaceId() { return faceId; }
        public String getPersonId() { return personId; }
        public String getPersonName() { return personName; }
        public float getSimilarity() { return similarity; }
    }
}
