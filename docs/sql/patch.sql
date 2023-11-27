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
