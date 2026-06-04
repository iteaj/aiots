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


-- 2026-05-22 创建第三方社交用户绑定表
CREATE TABLE `m_user` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
  `platform` varchar(20) NOT NULL COMMENT '平台标识：WECHAT_MA-微信小程序 WECHAT_MP-微信公众号 ALIPAY-支付宝',
  `open_id` varchar(100) NOT NULL COMMENT '平台用户唯一标识',
  `union_id` varchar(100) DEFAULT NULL COMMENT '平台统一标识（微信unionId等）',
  `session_key` varchar(200) DEFAULT NULL COMMENT '会话密钥',
  `system_user_id` bigint DEFAULT NULL COMMENT '关联系统用户ID',
  `nickname` varchar(100) DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(500) DEFAULT NULL COMMENT '头像',
  `phone` varchar(20) DEFAULT NULL COMMENT '手机号',
  `gender` tinyint DEFAULT NULL COMMENT '性别：0-未知 1-男 2-女',
  `subscribe` tinyint DEFAULT '0' COMMENT '是否关注公众号：0-否 1-是',
  `subscribe_time` datetime DEFAULT NULL COMMENT '关注时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_platform_openid` (`platform`, `open_id`),
  KEY `idx_system_user` (`system_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='第三方社交用户绑定表';

-- 2026-05-25 系统用户头像字段长度改为500
ALTER TABLE sys_admin MODIFY COLUMN `avatar` varchar(500) DEFAULT '' COMMENT '头像路径';

-- 2026-05-26 创建设备指令下发日志表
CREATE TABLE `iot_device_cmd_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `uid` varchar(36) DEFAULT NULL COMMENT '设备标识',
  `device_name` varchar(64) DEFAULT NULL COMMENT '设备名称',
  `config` json DEFAULT NULL COMMENT '指令配置（含指令名称/编码/参数等）',
  `status` varchar(20) DEFAULT NULL COMMENT '执行状态：SUCCESS-成功 FAILURE-失败',
  `result_msg` varchar(128) DEFAULT NULL COMMENT '结果描述',
  `result_data` json DEFAULT NULL COMMENT '响应数据',
  `elapsed` bigint(20) DEFAULT NULL COMMENT '执行时间(毫秒)',
  `method` varchar(20) DEFAULT NULL COMMENT '下发方式：大屏/调试/脚本/场景联动/移动端',
  `cmd_type` varchar(20) DEFAULT NULL COMMENT '指令类型：控制指令/事件指令',
  `operator` varchar(100) DEFAULT NULL COMMENT '操作人',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `create_time` datetime DEFAULT NULL COMMENT '下发时间',
  PRIMARY KEY (`id`) USING BTREE,
  KEY `idx_uid` (`uid`) USING BTREE,
  KEY `idx_create_time` (`create_time`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备指令下发日志';

-- 2026-05-26 设备指令日志菜单权限

-- 设备指令日志菜单（父菜单ID 1406 是"设备维护"）
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time)
VALUES ('设备指令日志', 1406, 310, '/iot/deviceCmdLog', 'iot', 'V', 'enabled', 'iot:deviceCmdLog:view', 'iz-icon-log', '设备指令下发日志', '_self', now(), now());

-- 获取刚插入的菜单ID
set @pid = (select last_insert_id());

-- 查询权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time)
VALUES ('查询', @pid, 5, '/iot/deviceCmdLog/view', 'iot', 'A', 'enabled', 'iot:deviceCmdLog:view', '#', '查询设备指令日志', '_self', now(), now());

-- 删除权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time)
VALUES ('删除', @pid, 10, '/iot/deviceCmdLog/del', 'iot', 'A', 'enabled', 'iot:deviceCmdLog:del', '#', '删除设备指令日志', '_self', now(), now());

-- 2026-05-26 设备指令日志增加设备名称字段
ALTER TABLE iot_device_cmd_log ADD COLUMN `device_name` varchar(200) DEFAULT NULL COMMENT '设备名称' AFTER `uid`;

-- 2026-06-03
alter table iot_model_api
    add sort int default 0 null comment '序号' after id;

alter table iot_model_api
    add config json null comment '配置' after sort;

alter table iot_model_attr
    add sort int default 0 null comment '排序' after name;

alter table iot_model_attr
    add config json null comment '配置' after unit;
