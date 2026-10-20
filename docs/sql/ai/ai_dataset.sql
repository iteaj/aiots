-- ----------------------------
-- AI数据集主表
-- ----------------------------
DROP TABLE IF EXISTS `ai_dataset`;
CREATE TABLE `ai_dataset` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(128) NOT NULL COMMENT '数据集名称',
  `config` text COMMENT '拓展配置JSON(splitRatio等)',
  `record_count` bigint(20) DEFAULT 0 COMMENT '总数据条数',
  `status` varchar(32) DEFAULT 'processing' COMMENT '状态(processing=处理中, ready=就绪, error=失败)',
  `remark` varchar(512) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI数据集表';

-- ----------------------------
-- AI数据集明细表
-- ----------------------------
DROP TABLE IF EXISTS `ai_dataset_item`;
CREATE TABLE `ai_dataset_item` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `dataset_id` bigint(20) NOT NULL COMMENT '关联主表ID',
  `name` varchar(128) NOT NULL COMMENT '数据集项名称',
  `type` varchar(32) NOT NULL COMMENT '类型(db=数据库, image=图像, text=文本)',
  `file_name` varchar(256) DEFAULT NULL COMMENT '文件名/标识',
  `file_path` varchar(512) DEFAULT NULL COMMENT '单个文件存储路径',
  `label` varchar(128) DEFAULT NULL COMMENT '标签/分类',
  `split` varchar(32) DEFAULT NULL COMMENT '数据集划分(train=训练集, val=验证集, test=测试集)',
  `record_count` bigint(20) DEFAULT 1 COMMENT '该文件包含的数据条数',
  `sort` int(11) DEFAULT 0 COMMENT '排序',
  `config` json COMMENT '拓展配置JSON(sampleRate等)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_dataset_id` (`dataset_id`),
  KEY `idx_split` (`split`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI数据集明细表';
