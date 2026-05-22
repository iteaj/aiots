-- 2026-04-27 添加频率标签字段

-- 新增字段
ALTER TABLE iot_model_api ADD COLUMN collect_tag VARCHAR(50) COMMENT '频率标签（默认、低频、中频、高频）' DEFAULT 'DEFAULT';
ALTER TABLE iot_event_source ADD COLUMN frequency_tags JSON COMMENT '频率标签范围（多选）' DEFAULT ('["DEFAULT"]');

-- 初始化现有数据默认值
UPDATE iot_model_api SET collect_tag = 'DEFAULT' WHERE collect_tag IS NULL;
UPDATE iot_event_source SET frequency_tags = CAST('["DEFAULT"]' AS JSON) WHERE frequency_tags IS NULL;


-- 2026-05-05 新增产品config字段配置

-- 初始化现有产品的calcScript和calcStatus配置
UPDATE iot_product
SET config = JSON_SET(IFNULL(config, JSON_OBJECT()), '$.calcScript', '', '$.calcLanguage', 'disabled')
WHERE config IS NULL OR JSON_EXTRACT(config, '$.calcScript') IS NULL OR JSON_EXTRACT(config, '$.calcLanguage') IS NULL;


-- 2026-05-22 创建微信用户绑定表
CREATE TABLE `wechat_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `user_type` tinyint NOT NULL COMMENT '应用类型：1-公众号 2-小程序',
  `open_id` varchar(100) NOT NULL COMMENT '微信openId',
  `union_id` varchar(100) DEFAULT NULL COMMENT '微信unionId',
  `session_key` varchar(200) DEFAULT NULL COMMENT '小程序sessionKey',
  `system_user_id` bigint DEFAULT NULL COMMENT '关联系统用户ID',
  `user_category` tinyint DEFAULT NULL COMMENT '用户类别：1-管理端 2-会员端',
  `nickname` varchar(100) DEFAULT NULL COMMENT '微信昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0-未知 1-男 2-女',
  `subscribe` tinyint DEFAULT '0' COMMENT '是否关注公众号：0-否 1-是',
  `subscribe_time` datetime DEFAULT NULL COMMENT '关注时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_openid` (`user_type`, `open_id`),
  KEY `idx_system_user` (`system_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='微信用户绑定表';