-- 2023/06/27
alter table sys_menu
    add msn char(16) null comment '所属模块';

create index sys_menu_msn_index
    on sys_menu (msn);

-- 2023/11/24
alter table iot_device_child
    add data_format enum('ABCD', 'BADC', 'DCBA', 'CDAB') null comment '数据格式' after protocol_type;

alter table iot_collect_detail
    add data_format enum('ABCD', 'BADC', 'DCBA', 'CDAB') null after collect_task_id;

-- 2024/1/4 采集点位新增精度字段
alter table iot_signal
    add accuracy int null comment '精度' after field_type;

-- 2024/1/5 采集详情新增子设备id
alter table iot_collect_detail
    add child_device_id bigint null comment '子设备id' after collect_action;
