-- ----------------------------
-- AI数据集菜单和权限
-- ----------------------------

-- 数据集管理菜单（父菜单ID 1517 是"智能模型"）
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) 
VALUES ('数据集管理', 1517, 18, '/ai/dataset', 'ai', 'V', 'enabled', 'ai:dataset:view', 'iz-icon-dataset', 'AI数据集管理', '_self', now(), now());

-- 获取刚插入的菜单ID
set @pid = (select last_insert_id());

-- 查询权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) 
VALUES ('查询', @pid, 5, '/ai/dataset/view', 'ai', 'A', 'enabled', 'ai:dataset:view', '#', '查询数据集列表', '_self', now(), now());

-- 新增权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) 
VALUES ('新增', @pid, 10, '/ai/dataset/add', 'ai', 'A', 'enabled', 'ai:dataset:add', '#', '新增数据集', '_self', now(), now());

-- 修改权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) 
VALUES ('修改', @pid, 15, '/ai/dataset/edit', 'ai', 'A', 'enabled', 'ai:dataset:edit', '#', '修改数据集', '_self', now(), now());

-- 删除权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) 
VALUES ('删除', @pid, 20, '/ai/dataset/del', 'ai', 'A', 'enabled', 'ai:dataset:del', '#', '删除数据集', '_self', now(), now());

-- 详情权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) 
VALUES ('详情', @pid, 25, '/ai/dataset/detail', 'ai', 'A', 'enabled', 'ai:dataset:detail', '#', '查看数据集详情', '_self', now(), now());

-- 列表权限
INSERT INTO sys_menu (name, pid, sort, url, msn, type, status, perms, icon, remark, target, create_time, update_time) 
VALUES ('列表', @pid, 30, '/ai/dataset/list', 'ai', 'A', 'enabled', 'ai:dataset:list', '#', '数据集列表查询', '_self', now(), now());
