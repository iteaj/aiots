# 2025-5-27
INSERT INTO `sys_menu` VALUES (1480,'产品数据',1421,88,'/iot/collectData/product','iot','V','enabled','iot:collectData:product','iz-icon-container-default','','_self','2025-05-26 00:00:00',NULL);

# 2025-06-04
create table isv_project (
    id char(36),
    project_name varchar(64) null comment '项目名称',
    state int null comment '状态',
    index_image varchar(256) null comment '项目封面',
    remarks varchar(256) null comment '项目说明',
    is_delete int null comment '是否删除',
    content json null comment '项目内容',
    create_time datetime default now() not null comment '创建时间',
    constraint isv_project_pk primary key (id)
) comment '大屏项目';

alter table iot_collect_data
    add product_code varchar(64) null comment '产品编码';

alter table iot_collect_data
    add protocol_code varchar(64) null comment '协议编码';

alter table iot_collect_data
    add device_group_id bigint null comment '所属组';

create index iot_collect_data_device_group_id_index
    on iot_collect_data (device_group_id);

create index iot_collect_data_protocol_code_index
    on iot_collect_data (protocol_code);

create index iot_collect_data_product_code_index
    on iot_collect_data (product_code);
-- 添加缺失字段
ALTER TABLE iot_event_source
    ADD COLUMN period_type VARCHAR(20) NOT NULL DEFAULT 'cron' COMMENT '采集类型(cron:定时,millisecond:周期)',
    ADD COLUMN period_ms BIGINT COMMENT '采集周期毫秒数',
    ADD COLUMN rate_type TINYINT(1)  COMMENT '速率类型(1:固定速率, 0:固定延迟)';
# 2025-11-29
alter table iot_collect_data
    change signal_id field_id bigint not null comment '字段id';

alter table iot_collect_data
    change collect_task_id task_id bigint null comment '任务id';

drop index iot_collect_data_signal_id_index on iot_collect_data;

create index iot_collect_data_field_id_index
    on iot_collect_data (field_id);

alter table iot_collect_data
    add device_sn varchar(36) null comment '设备编号';

alter table iot_collect_data
    add device_name varchar(64) null comment '设备名称';

alter table iot_collect_data
    add field_name varchar(36) null comment '字段名称';