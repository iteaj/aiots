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
alter table oauth2_app
    add admin_account varchar(64) null comment '管理员账号' after client_name;
create index oauth2_app_admin_account_index
    on oauth2_app (admin_account);

-- 2025-02-01
-- 开放平台菜单
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('北向接入', 0, 34, '#', 'iot', 'M', 'enabled', null, 'iz-icon-event-source', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:29:35');
set @pid = (select last_insert_id());
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('开放平台', @pid, 18, '/oauth2/app', 'iot', 'V', 'enabled', 'oauth2:app:view', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:53:15');
set @pid = (select last_insert_id());
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('查询', @pid, 5, '#', 'iot', 'A', 'enabled', 'oauth2:app:view', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:54:53');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('新增', @pid, 10, '#', 'iot', 'A', 'enabled', 'oauth2:app:add', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:55:05');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('修改', @pid, 15, '#', 'iot', 'A', 'enabled', 'oauth2:app:edit', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:55:20');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('删除', @pid, 20, '#', 'iot', 'A', 'enabled', 'oauth2:app:del', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:55:32');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('状态', @pid, 25, '#', 'iot', 'A', 'enabled', 'oauth2:app:status', '#', '', '_self', '2025-01-18 00:00:00', '2025-01-18 10:55:32');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('模型接口调用', @pid, 30, '#', 'iot', 'A', 'enabled', 'iot:model:invoke', '#', '', '_self', now(), '2025-01-18 10:55:32');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('模型接口调试', @pid, 35, '#', 'iot', 'A', 'enabled', 'iot:model:debug', '#', '', '_self', now(), '2025-01-18 10:55:32');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('指令集调用', @pid, 40, '#', 'iot', 'A', 'enabled', 'iot:cmd:invoke', '#', '', '_self', now(), '2025-01-18 10:55:32');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('指令集列表', @pid, 45, '#', 'iot', 'A', 'enabled', 'iot:cmd:list', '#', '', '_self', now(), '2025-01-18 10:55:32');

INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('模型接口调试', 1255, 10, '#', 'iot', 'A', 'enabled', 'iot:model:debug', '#', '', '_self', now(), '2025-01-18 10:55:32');

-- opcua菜单
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('OpcUa网关设备', 1468, 68, '/iot/driver/opcua', 'iot', 'V', 'enabled', 'iot:opcua:view', '#', '', '_self', '2024-12-16 00:00:00', '2024-12-16 15:33:17');
set @pid = (select last_insert_id());
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('新增', @pid, 8, '/iot/driver/opcua', 'iot', 'A', 'enabled', 'iot:opcua:add', '', '', '_self', '2024-12-16 15:34:04', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('删除', @pid, 18, '', 'iot', 'A', 'enabled', 'iot:opcua:del', '#', '', '_self', '2024-12-16 15:34:28', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('修改', @pid, 28, '#', 'iot', 'A', 'enabled', 'iot:opcua:edit', '#', '', '_self', '2024-12-16 15:34:48', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('浏览', @pid, 38, '#', 'iot', 'A', 'enabled', 'iot:opcua:view', '#', '', '_self', '2024-12-16 15:35:12', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('子设备', @pid, 58, '#', 'iot', 'A', 'enabled', 'iot:opcua:child', '#', '', '_self', '2024-12-16 15:35:33', null);

-- dlt645菜单
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('DLT645网关设备', 1468, 168, '/iot/driver/dlt645', 'iot', 'V', 'enabled', 'iot:dlt645:view', '#', '', '_self', '2025-01-11 00:00:00', '2025-01-11 20:42:12');
set @pid = (select last_insert_id());
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('新增', @pid, 16, '/iot/driver/dlt645', 'iot', 'A', 'enabled', 'iot:dlt645:add', '#', '', '_self', '2025-01-11 21:10:24', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('删除', @pid, 28, '', 'iot', 'A', 'enabled', 'iot:dlt645:del', '#', '', '_self', '2025-01-11 21:10:47', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('修改', @pid, 38, '#', 'iot', 'A', 'enabled', 'iot:dlt645:edit', '#', '', '_self', '2025-01-11 21:11:02', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('浏览', @pid, 46, '#', 'iot', 'A', 'enabled', 'iot:dlt645:view', '#', '', '_self', '2025-01-11 00:00:00', '2025-01-11 21:11:28');
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('子设备', @pid, 58, '#', 'iot', 'A', 'enabled', 'iot:dlt645:child', '#', '', '_self', '2025-01-11 21:11:55', null);
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('连接', @pid, 68, '', 'iot', 'A', 'enabled', 'iot:dlt645:connect', '#', '', '_self', '2025-01-11 21:12:13', null);

create table iot_northward
(
    id          bigint auto_increment primary key,
    name        varchar(64)            null comment '名称',
    type        varchar(36)            null comment '接入类型',
    status      boolean                not null default false comment '状态',
    config      json                   not null comment '接入配置',
    create_time datetime default NOW() null comment '创建时间'
) comment '北向接入';

-- 2025-2-9
alter table iot_warn_config
    add frequency int null default 0 comment '告警频率' after template;

-- 2025-04-03
create table iot_script(
    id     bigint auto_increment primary key,
    type   char(16)                 not null comment '脚本类型',
    name   varchar(64)              null comment '名称',
    script json                     null comment '脚本',
    remark varchar(1024) default '' null comment '备注',
    language char(36) null comment '语言',
    constraint iot_script_name_uindex unique (name)
) comment '脚本引擎';
alter table iot_script add create_time datetime default now() null comment '创建时间';
alter table iot_script add update_time timestamp null comment '更新时间';
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) VALUES ('数据过滤', 1421, 168, '/iot/script/filter', 'iot', 'V', 'enabled', 'iot:script:filter:view', '#', '', '_self', '2025-04-11 00:00:00', '2025-04-11 20:42:12');

-- 2025-07-17
create table isv_resource
(
    id          char(36)                           not null,
    name        varchar(64)                        null comment '名称',
    url         varchar(256)                       null comment '路径',
    type        char(16)                           null comment '资源类型',
    create_time datetime default CURRENT_TIMESTAMP null comment '创建时间'
) comment '大屏资源';

alter table isv_resource add primary key (id);
alter table isv_resource add constraint isv_resource_id_uindex unique (id);

create table isv_resource_type
(
    id   bigint auto_increment
        primary key,
    name char(24)    not null comment '名称',
    type varchar(36) not null comment '类型',
    sort int         null comment '排序'
) comment '资源类型';
-- 2025-11-09
alter table iot_device modify extend varchar(36) default null;
update iot_device set extend = null where id > 0;
alter table iot_device modify extend json null;
update iot_device set extend = '{"tags": [], "alias": ""}' where id > 0;

update iot_device set extend = JSON_SET(extend, '$.alias', 'DTU') where product_id in (select id from iot_product where protocol_code='DTU:MODBUS_TCP' or protocol_code='DTU:MODBUS_RTU');
update iot_device set extend = JSON_SET(extend, '$.alias', 'MQTT') where product_id in (select id from iot_product where protocol_code='DTU:MODBUS_OVER_MQTT');

-- 2025-12-13
alter table iot_northward
    add connect_key varchar(64) null comment '连接标识';

alter table iot_northward
    add connect_status varchar(16) null comment '连接状态';
alter table iot_northward
    add constraint iot_northward_pk_connect_key unique (connect_key);
update iot_northward set connect_status = 'offline' where id > 0;
update iot_northward set connect_key = 'mqtt_sn_0001' where id = 1;
update iot_northward set connect_key = 'mqtt_sn_0002' where id = 2;

create table isv_project
(
    id           char(36)                           not null,
    project_name varchar(64)                        null comment '项目名称',
    state        int                                null comment '状态',
    index_image  varchar(256)                       null comment '项目封面',
    remarks      varchar(256)                       null comment '项目说明',
    is_delete    int                                null comment '是否删除',
    content      json                               null comment '项目内容',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间'
)
    comment '大屏项目';

alter table isv_project add primary key (id);
