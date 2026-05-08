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