create schema if not exists izone collate utf8mb4_0900_ai_ci;

create table if not exists t_admin
(
	id bigint auto_increment comment '用户ID'
		primary key,
	dept_id bigint null comment '部门ID',
	account char(24) not null comment '登录账号',
	nick varchar(30) not null comment '用户昵称',
	email varchar(50) default '' null comment '用户邮箱',
	phone varchar(11) default '' null comment '手机号码',
	sex enum('男', '女', '未知') default '未知' null comment '用户性别（0男 1女 2未知）',
	avatar varchar(100) default '' null comment '头像路径',
	password varchar(50) default '' null comment '密码',
	salt varchar(20) default '' null comment '盐加密',
	status enum('正常', '禁用') default '正常' not null comment '帐号状态（0正常 1停用）',
	login_ip varchar(50) default '' null comment '最后登陆IP',
	login_date datetime null comment '最后登陆时间',
	remark varchar(500) default '' null comment '备注',
	create_by varchar(64) default '' null comment '创建者',
	create_time datetime null comment '创建时间',
	update_by varchar(64) default '' null comment '更新者',
	update_time timestamp null comment '更新时间',
	constraint t_admin_account_uindex
		unique (account)
)
comment '管理员信息表';

create table if not exists t_admin_role
(
	aid bigint not null comment '管理员id',
	rid bigint null comment '角色id'
)
comment '管理员角色表';

create table if not exists t_config
(
	id int(5) auto_increment comment '参数主键'
		primary key,
	name varchar(100) default '' null comment '参数名称',
	value varchar(100) default '' null comment '参数键值',
	type tinyint(1) default 0 null comment '系统内置（Y是 N否）',
	remark varchar(500) null comment '备注',
	create_by varchar(64) default '' null comment '创建者',
	create_time datetime null comment '创建时间',
	update_by varchar(64) default '' null comment '更新者',
	update_time datetime null comment '更新时间'
)
comment '参数配置表';

create table if not exists t_dept
(
	id bigint auto_increment comment '部门id'
		primary key,
	pid bigint default 0 null comment '父部门id',
	level int(3) default 1 null comment '属于第几级',
	name varchar(30) default '' null comment '部门名称',
	sort int(4) default 0 null comment '显示顺序',
	leader varchar(20) null comment '负责人',
	phone varchar(11) null comment '联系电话',
	email varchar(50) null comment '邮箱',
	status enum('正常', '禁用') default '正常' not null comment '部门状态（0正常 1停用）',
	create_by varchar(64) default '' null comment '创建者',
	create_time datetime null comment '创建时间',
	update_by varchar(64) default '' null comment '更新者',
	update_time datetime null comment '更新时间'
)
comment '部门表';

create table if not exists t_dict_data
(
	id bigint auto_increment comment '字典id'
		primary key,
	sort int(4) default 0 null comment '字典排序',
	label varchar(100) default '' null comment '字典标签',
	value varchar(100) default '' null comment '字典键值',
	sn varchar(100) default '' null comment '字典编码',
	css_class varchar(100) null comment '样式属性（其他样式扩展）',
	list_class varchar(100) null comment '表格回显样式',
	`default` tinyint(1) default 0 null comment '是否默认（Y是 N否）',
	status enum('正常', '禁用') default '正常' not null comment '状态（0正常 1停用）',
	remark varchar(500) null comment '备注',
	create_by varchar(64) default '' null comment '创建者',
	create_time datetime null comment '创建时间',
	update_by varchar(64) default '' null comment '更新者',
	update_time datetime null comment '更新时间'
)
comment '字典数据表';

create table if not exists t_dict_type
(
	id bigint auto_increment comment '字典主键'
		primary key,
	sn varchar(100) default '' null comment '字典编码',
	name varchar(100) default '' null comment '字典名称',
	status enum('正常', '禁用') default '正常' not null comment '状态（0正常 1停用）',
	remark varchar(500) null comment '备注',
	create_by varchar(64) default '' null comment '创建者',
	create_time datetime null comment '创建时间',
	update_by varchar(64) default '' null comment '更新者',
	update_time datetime null comment '更新时间',
	constraint sn
		unique (sn)
)
comment '字典类型表';

create table if not exists t_menu
(
	id bigint auto_increment comment '菜单ID'
		primary key,
	msn char(32) not null comment '模块编码, 用来标识此权限属于哪个模块',
	name varchar(50) not null comment '菜单名称',
	pid bigint default 0 null comment '父菜单ID',
	sort int(4) default 0 null comment '显示顺序',
	url varchar(200) default '#' null comment '请求地址',
	type enum('功能', '菜单', '目录') not null comment '菜单类型',
	function_type enum('View', 'Add', 'Edit', 'Del', 'Import', 'Export') null,
	view tinyint(1) default 1 null comment '菜单状态（true显示 false隐藏）',
	perms varchar(100) null comment '权限标识',
	icon varchar(100) default '#' null comment '菜单图标',
	position enum('M', 'MM', 'T', 'TM') null comment '功能点位置 M.主操作栏 MM.主操作栏更多 T.表操作 TM.表操作更多',
	remark varchar(500) default '' null comment '备注',
	create_by varchar(64) default '' null comment '创建者',
	create_time datetime null comment '创建时间',
	update_by varchar(64) default '' null comment '更新者',
	update_time datetime null comment '更新时间'
)
comment '菜单权限表';

create table if not exists t_module
(
	id int auto_increment
		primary key,
	msn char(32) not null comment '模块编码',
	name char(32) not null comment '应用名称',
	version char(16) null comment '版本号',
	remark varchar(256) null comment '模块说明',
	create_time datetime default CURRENT_TIMESTAMP not null,
	update_time timestamp default CURRENT_TIMESTAMP not null,
	constraint t_app_app_id_uindex
		unique (msn),
	constraint t_app_app_name_uindex
		unique (name)
)
comment '应用模块表';

create table if not exists t_opera_log
(
	id bigint auto_increment comment '日志主键'
		primary key,
	title varchar(50) default '' null comment '模块标题',
	type int(2) default 0 null comment '业务类型（0其它 1新增 2修改 3删除）',
	method varchar(100) default '' null comment '方法名称',
	user_id bigint default 0 null comment '操作人员',
	user_name varchar(50) default '' null comment '操作人员',
	url varchar(255) default '' null comment '请求URL',
	ip varchar(50) default '' null comment '主机地址',
	status int(1) default 0 null comment '操作状态（0正常 1异常）',
	message varchar(2000) default '' null comment '错误消息',
	create_time datetime null comment '操作时间'
)
comment '操作日志记录';

create table if not exists t_role
(
	id bigint auto_increment comment '角色ID'
		primary key,
	name varchar(30) not null comment '角色名称',
	sort int(4) not null comment '显示顺序',
	scope char default '1' null comment '数据范围（1：全部数据权限 2：自定数据权限）',
	status enum('正常', '禁用') default '正常' not null comment '角色状态（0正常 1停用）',
	remark varchar(500) null comment '备注',
	create_by varchar(64) default '' null comment '创建者',
	create_time datetime null comment '创建时间',
	update_by varchar(64) default '' null comment '更新者',
	update_time datetime null comment '更新时间'
)
comment '角色信息表';

create table if not exists t_role_dept
(
	role_id bigint not null comment '角色ID',
	dept_id bigint not null comment '部门ID',
	primary key (role_id, dept_id)
)
comment '角色和部门关联表';

create table if not exists t_role_menu
(
	rid bigint not null comment '角色ID',
	mid bigint not null comment '菜单ID',
	primary key (rid, mid)
)
comment '角色和菜单关联表';

