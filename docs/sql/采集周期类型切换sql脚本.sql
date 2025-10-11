-- 添加缺失字段
ALTER TABLE iot_event_source
    ADD COLUMN period_type VARCHAR(20) NOT NULL DEFAULT 'cron' COMMENT '采集类型(cron:定时,millisecond:周期)',
ADD COLUMN period_ms BIGINT COMMENT '采集周期毫秒数',
ADD COLUMN rate_type TINYINT(1)  COMMENT '速率类型(1:固定速率, 0:固定延迟)';