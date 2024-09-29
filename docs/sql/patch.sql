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