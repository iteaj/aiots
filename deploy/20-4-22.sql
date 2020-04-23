alter table t_menu
    add record_log boolean default true not null comment '是否开启此功能的日志记录';
alter table t_menu
    add log_desc varchar(64) default '' not null comment '日志说明' after icon;
