alter table sys_menu
    add msn char(16) null comment '所属模块';

create index sys_menu_msn_index
    on sys_menu (msn);