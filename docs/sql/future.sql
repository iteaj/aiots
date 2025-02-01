-- 新特性功能sql增量脚本
-- 2025-01-30
alter table oauth2_app
    add admin_account varchar(64) null comment '管理员账号' after client_name;

create index oauth2_app_admin_account_index
    on oauth2_app (admin_account);
