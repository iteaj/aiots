-- 2024-10-18更新
alter table iot_collect_data
    modify cid bigint null comment '任务执行标识';

alter table iot_collect_data
    change signal_id field_id bigint not null comment '信号id';

alter table iot_collect_data
    change collect_task_id task_id bigint null comment '任务id';

alter table iot_collect_data
    add product_code varchar(64) null comment '产品编码';

alter table iot_collect_data
    add protocol_code varchar(64) null comment '协议编码';

alter table iot_collect_data
    add device_group_Id bigint null comment '设备组';

alter table iot_collect_data
    add device_name varchar(64) null comment '设备名称';

alter table iot_collect_data
    add device_sn varchar(64) null comment '设备编号';

alter table iot_collect_data
    add field_name varchar(64) null comment '字段名称';

drop index iot_collect_data_signal_id_index on iot_collect_data;

create index iot_collect_data_field_id_index
    on iot_collect_data (field_id);

-- 2025-01-18更新
alter table oauth2_app
    add status boolean default true null comment '应用状态' after contract_scope;
alter table oauth2_app
    add remark varchar(256) null comment '备注' after client_name;
create index oauth2_app_client_id_index
    on oauth2_app (client_id);

-- 2025-02-01
-- 开放平台菜单
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('北向接入', 0, 34, '#', 'iot', 'M', 'enabled', null, 'iz-icon-event-source', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:29:35');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('开放平台', 1485, 18, '/oauth2/app', 'iot', 'V', 'enabled', 'oauth2:app:view', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:53:15');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('查询', 1486, 5, '/oauth2/app/view', 'iot', 'A', 'enabled', 'oauth2:app:view', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:54:53');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('新增', 1486, 10, '/oauth2/app/add', 'iot', 'A', 'enabled', 'oauth2:app:add', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:55:05');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('修改', 1486, 15, '/oauth2/app/edit', 'iot', 'A', 'enabled', 'oauth2:app:edit', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:55:20');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('删除', 1486, 20, '/oauth2/app/del', 'iot', 'A', 'enabled', 'oauth2:app:del', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:55:32');
-- opcua菜单
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('OpcUa网关设备', 1468, 68, '/iot/driver/opcua', 'iot', 'V', 'enabled', 'iot:opcua:view', '#', '', '_self', '2024-12-16 00:00:00', '2024-12-16 15:33:17');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('新增', 1485, 8, '/iot/driver/opcua', 'iot', 'A', 'enabled', 'iot:opcua:add', '', '', '_self', '2024-12-16 15:34:04', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('删除', 1485, 18, '', 'iot', 'A', 'enabled', 'iot:opcua:del', '#', '', '_self', '2024-12-16 15:34:28', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('修改', 1485, 28, '#', 'iot', 'A', 'enabled', 'iot:opcua:edit', '#', '', '_self', '2024-12-16 15:34:48', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('浏览', 1485, 38, '#', 'iot', 'A', 'enabled', 'iot:opcua:view', '#', '', '_self', '2024-12-16 15:35:12', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('子设备', 1485, 58, '#', 'iot', 'A', 'enabled', 'iot:opcua:child', '#', '', '_self', '2024-12-16 15:35:33', null);
-- dlt645菜单
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('DLT645网关设备', 1468, 168, '/iot/driver/dlt645', 'iot', 'V', 'enabled', 'iot:dlt645:view', '#', '', '_self', '2025-01-11 00:00:00', '2025-01-11 20:42:12');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('新增', 1491, 16, '/iot/driver/dlt645', 'iot', 'A', 'enabled', 'iot:dlt645:add', '#', '', '_self', '2025-01-11 21:10:24', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('删除', 1491, 28, '', 'iot', 'A', 'enabled', 'iot:dlt645:del', '#', '', '_self', '2025-01-11 21:10:47', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('修改', 1491, 38, '#', 'iot', 'A', 'enabled', 'iot:dlt645:edit', '#', '', '_self', '2025-01-11 21:11:02', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('浏览', 1491, 46, '#', 'iot', 'A', 'enabled', 'iot:dlt645:view', '#', '', '_self', '2025-01-11 00:00:00', '2025-01-11 21:11:28');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('子设备', 1491, 58, '#', 'iot', 'A', 'enabled', 'iot:dlt645:child', '#', '', '_self', '2025-01-11 21:11:55', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('连接', 1491, 68, '', 'iot', 'A', 'enabled', 'iot:dlt645:connect', '#', '', '_self', '2025-01-11 21:12:13', null);