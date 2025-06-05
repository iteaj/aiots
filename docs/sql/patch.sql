# 2025-5-27
INSERT INTO `sys_menu` VALUES (1480,'产品数据',1421,88,'/iot/collectData/product','iot','V','enabled','iot:collectData:product','iz-icon-container-default','','_self','2025-05-26 00:00:00',NULL);

# 2025-06-04
create table isv_project (
    id bigint auto_increment,
    project_name varchar(64) null comment '项目名称',
    state int null comment '状态',
    index_image varchar(256) null comment '项目封面',
    remarks varchar(256) null comment '项目说明',
    is_delete int null comment '是否删除',
    content json null comment '项目内容',
    create_time datetime default now() not null comment '创建时间',
    constraint isv_project_pk
        primary key (id)
) comment '大屏项目';