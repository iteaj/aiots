-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: izone
-- ------------------------------------------------------
-- Server version	5.7.17-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `t_access_log`
--

DROP TABLE IF EXISTS `t_access_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_access_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `ip` varchar(50) DEFAULT '' COMMENT '主机地址',
  `msn` varchar(32) NOT NULL DEFAULT '' COMMENT '所属模块',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '日志标题',
  `type` varchar(16) NOT NULL DEFAULT '' COMMENT '功能类型',
  `millis` int(11) DEFAULT NULL COMMENT '操作毫秒数',
  `user_id` bigint(20) DEFAULT '0' COMMENT '操作人员',
  `user_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '操作状态',
  `err_msg` varchar(512) NOT NULL DEFAULT '' COMMENT '异常消息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5278 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_access_log`
--

LOCK TABLES `t_access_log` WRITE;
/*!40000 ALTER TABLE `t_access_log` DISABLE KEYS */;
INSERT INTO `t_access_log` VALUES (5271,'/core/admin/view','127.0.0.1','用户管理','查询','',121,1,'超级管理员',1,'执行成功','2021-10-07 14:54:44',NULL),(5272,'/core/role/view','127.0.0.1','角色管理','查询','',84,1,'超级管理员',1,'执行成功','2021-10-07 14:54:45',NULL),(5273,'/core/menu/view','127.0.0.1','菜单管理','查询','',58,1,'超级管理员',1,'执行成功','2021-10-07 14:54:47',NULL),(5274,'/core/config/view','127.0.0.1','系统配置','查询','',57,1,'超级管理员',1,'执行成功','2021-10-07 14:54:48',NULL),(5275,'/core/dictType/view','127.0.0.1','字典管理','查询','',61,1,'超级管理员',1,'执行成功','2021-10-07 14:54:49',NULL),(5276,'/core/org/view','127.0.0.1','部门管理','查询','',27,1,'超级管理员',1,'执行成功','2021-10-07 14:54:51',NULL),(5277,'/core/onlineUser/view','127.0.0.1','在线用户','查询','',51,1,'超级管理员',1,'执行成功','2021-10-07 14:54:54',NULL);
/*!40000 ALTER TABLE `t_access_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_admin`
--

DROP TABLE IF EXISTS `t_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_admin` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `org_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `account` char(24) NOT NULL COMMENT '登录账号',
  `name` varchar(30) NOT NULL COMMENT '用户名称',
  `email` varchar(50) DEFAULT '' COMMENT '用户邮箱',
  `phone` varchar(11) DEFAULT '' COMMENT '手机号码',
  `sex` enum('man','woman','non') NOT NULL DEFAULT 'non' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像路径',
  `password` varchar(50) DEFAULT '' COMMENT '密码',
  `status` enum('enabled','disabled') NOT NULL DEFAULT 'enabled' COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(16) DEFAULT '' COMMENT '最后登陆IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_admin_account_uindex` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='管理员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_admin`
--

LOCK TABLES `t_admin` WRITE;
/*!40000 ALTER TABLE `t_admin` DISABLE KEYS */;
INSERT INTO `t_admin` VALUES (1,3,'admin','超级管理员','iteaj@outlook.com','18059222381','man','/img/logo.png','a66abb5684c45962d887564f08346e8d','enabled','127.0.0.1','2018-03-16 11:33:00','做回自己, 活出未来(iteaj)','admin','2018-03-15 11:33:00','admin','2018-03-15 03:33:00'),(7,1,'demo','demo','iteaj@outlook.com',NULL,'man',NULL,'fe01ce2a7fbac8fafaed7c982a04e229','enabled',NULL,NULL,'用于演示','',NULL,'',NULL);
/*!40000 ALTER TABLE `t_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_admin_role`
--

DROP TABLE IF EXISTS `t_admin_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_admin_role` (
  `aid` bigint(20) NOT NULL COMMENT '管理员id',
  `rid` bigint(20) DEFAULT NULL COMMENT '角色id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='管理员角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_admin_role`
--

LOCK TABLES `t_admin_role` WRITE;
/*!40000 ALTER TABLE `t_admin_role` DISABLE KEYS */;
INSERT INTO `t_admin_role` VALUES (1,3),(7,4);
/*!40000 ALTER TABLE `t_admin_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_config`
--

DROP TABLE IF EXISTS `t_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_config` (
  `id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '参数名称',
  `label` varchar(32) NOT NULL COMMENT '系统配置key',
  `value` varchar(128) DEFAULT '' COMMENT '参数键值',
  `type` enum('def','sys') NOT NULL DEFAULT 'def' COMMENT '配置类型',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_config_label_uindex` (`label`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_config`
--

LOCK TABLES `t_config` WRITE;
/*!40000 ALTER TABLE `t_config` DISABLE KEYS */;
INSERT INTO `t_config` VALUES (1,'系统名称','sys_name','厦门由创源科技','sys','系统里面显示的公司名','2019-12-04 07:25:31',NULL);
/*!40000 ALTER TABLE `t_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dict_data`
--

DROP TABLE IF EXISTS `t_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dict_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典id',
  `sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `label` varchar(64) DEFAULT '' COMMENT '字典标签',
  `value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `type` char(32) NOT NULL DEFAULT '' COMMENT '字典类型',
  `iz_default` tinyint(1) DEFAULT '0' COMMENT '是否默认（Y是 N否）',
  `status` enum('enabled','disabled') NOT NULL DEFAULT 'enabled' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8 COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dict_data`
--

LOCK TABLES `t_dict_data` WRITE;
/*!40000 ALTER TABLE `t_dict_data` DISABLE KEYS */;
INSERT INTO `t_dict_data` VALUES (1,1,'主栏','T','opera_position',0,'enabled',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(2,2,'主栏(更多)','MT','opera_position',0,'enabled',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(6,0,'主栏(更多)','MM','function-position',0,'enabled','主栏(更多)','',NULL,'',NULL),(11,0,'正常','status','common_status',0,'enabled','正常状态0','',NULL,'',NULL),(12,0,'禁用','disabled','common_status',0,'enabled','禁用状态','',NULL,'',NULL),(13,0,'主栏','M','function-position',0,'enabled','主栏','',NULL,'',NULL),(14,0,'隐藏','hidden','common_status',0,'enabled','隐藏状态','',NULL,'',NULL),(16,0,'男','male','sex',0,'enabled',NULL,'',NULL,'',NULL),(17,0,'女','female','sex',0,'enabled',NULL,'',NULL,'',NULL),(18,0,'管理模块','core','sys.module',0,'enabled','系统管理核心模块(admin)','',NULL,'',NULL),(19,0,'开发模块','dev','sys.module',0,'enabled','开发人员管理、开发、维护模块()','',NULL,'',NULL),(20,10,'启用','enabled','sys.status',0,'enabled',NULL,'',NULL,'',NULL),(21,12,'禁用','disabled','sys.status',0,'enabled',NULL,'',NULL,'',NULL);
/*!40000 ALTER TABLE `t_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_dict_type`
--

DROP TABLE IF EXISTS `t_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_dict_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `type` char(32) NOT NULL DEFAULT '' COMMENT '字典类型',
  `name` varchar(100) DEFAULT '' COMMENT '字典名称',
  `status` enum('enabled','disabled') NOT NULL DEFAULT 'enabled' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type t_dict_type_type_uindex` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dict_type`
--

LOCK TABLES `t_dict_type` WRITE;
/*!40000 ALTER TABLE `t_dict_type` DISABLE KEYS */;
INSERT INTO `t_dict_type` VALUES (3,'sys.module','系统模块','enabled','已经取消','','2020-04-27 09:44:57','',NULL),(4,'sys.status','状态','enabled','系统模块的状态','','2021-10-05 13:24:33','',NULL);
/*!40000 ALTER TABLE `t_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_menu`
--

DROP TABLE IF EXISTS `t_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `msn` char(32) NOT NULL DEFAULT '' COMMENT '模块编码, 用来标识此权限属于哪个模块',
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `pid` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `sort` int(4) DEFAULT '0' COMMENT '显示顺序',
  `url` varchar(200) DEFAULT '#' COMMENT '请求地址',
  `type` enum('A','M','V') NOT NULL COMMENT '菜单类型: A.权限、M.菜单、V.视图',
  `log` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否记录日志',
  `log_desc` varchar(64) NOT NULL DEFAULT '' COMMENT '日志说明',
  `status` enum('view','hide','disabled') DEFAULT 'view' COMMENT '菜单状态（view-显示 hide-隐藏 disabled-禁用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `remark` varchar(512) DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1223 DEFAULT CHARSET=utf8 COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_menu`
--

LOCK TABLES `t_menu` WRITE;
/*!40000 ALTER TABLE `t_menu` DISABLE KEYS */;
INSERT INTO `t_menu` VALUES (1,'core','系统设置',0,0,'#1','M',1,'00','view',NULL,'iz-icon-xitong','显示位置','2021-09-25 11:18:39',NULL),(1058,'core','系统管理',1,50,'#1','M',1,'','view',NULL,'iz-icon-sys-setting','','2021-09-25 11:18:39',NULL),(1060,'core','用户管理',1058,0,'/core/admin','V',0,'','view','core:admin:view','iz-icon-sys-user','','2021-09-25 11:18:39',NULL),(1065,'core','角色管理',1058,1,'/core/role','V',0,'','view','core:role:view','iz-icon-sys-role','','2021-09-25 11:18:39',NULL),(1066,'core','菜单管理',1058,2,'/core/menu','V',0,'浏览菜单信息','view','core:menu:view','iz-icon-menu','','2021-09-25 11:18:39',NULL),(1069,'core','新增',1066,1,'/core/menu/add','A',1,'新增菜单','view','core:menu:add','#','','2021-09-25 11:18:39',NULL),(1070,'core','编辑',1066,2,'/core/menu/edit','A',1,'修改菜单信息','view','core:menu:edit','#','','2021-09-25 11:18:39',NULL),(1071,'core','删除',1066,3,'/core/menu/del','A',1,'删除菜单','view','core:menu:del','#','','2021-09-25 11:18:39',NULL),(1072,'core','查询',1066,0,'/core/menu/view','A',1,'浏览菜单信息','view','core:menu:view','#','','2021-09-25 11:18:39',NULL),(1090,'core','字典管理',1058,8,'/core/dict','V',0,'','view','core:dictType:view','iz-icon-zidianguanli','','2021-09-25 11:18:39',NULL),(1092,'core','新增',1090,0,'/core/dictType/add','A',1,'新增字典类型','view','core:dictType:add','#','','2021-09-25 11:18:39',NULL),(1093,'core','修改',1090,0,'/core/dictType/edit','A',1,'修改字典类型','view','core:dictType:edit','#','','2021-09-25 11:18:39',NULL),(1094,'core','查询',1090,0,'/core/dictType/view','A',1,'浏览字典信息','view','core:dictType:view','#','','2021-09-25 11:18:39',NULL),(1095,'core','字典数据',1090,0,'/core/dictData/listByType','A',1,'','view','core:dictData:view','iz-icon-dictData','','2021-09-25 11:18:39',NULL),(1101,'dev','代码生成',1107,10,'#','M',1,'','view',NULL,'#','','2021-09-25 11:18:39',NULL),(1102,'dev','数据表浏览',1101,0,'/dev/table/db','V',0,'','view',NULL,'iz-icon-mgen','','2021-09-25 11:18:39',NULL),(1103,'dev','查询',1102,0,'/dev/table/db/data','A',0,'','hide',NULL,'#','','2021-09-25 11:18:39',NULL),(1107,'dev','开发设置',0,0,'#','M',1,'','view',NULL,'iz-icon-kaifa','','2021-09-25 11:18:39',NULL),(1108,'core','部门管理',1058,50,'/core/org','V',0,'','view','core:org:view','iz-icon-jigouguanli','','2021-09-25 11:18:39',NULL),(1113,'core','新增',1108,0,'/core/org/add','A',1,'新增组织机构','view','core:org:add','#','','2021-09-25 11:18:39',NULL),(1114,'core','删除',1108,0,'/core/org/del','A',1,'删除组织机构','view','core:org:del','#','','2021-09-25 11:18:39',NULL),(1115,'core','修改',1108,0,'/core/org/edit','A',1,'修改组织机构','view','core:org:edit','#','','2021-09-25 11:18:39',NULL),(1116,'core','查询',1108,0,'/core/org/view','A',1,'浏览组织机构信息','view','core:org:view','#','','2021-09-25 11:18:39',NULL),(1117,'core','新增',1060,0,'/core/admin/add','A',1,'','view','core:admin:add','#','','2021-09-25 11:18:39',NULL),(1118,'core','删除',1060,0,'/core/admin/del','A',1,'删除用户信息','view','core:admin:del','#','','2021-09-25 11:18:39',NULL),(1119,'core','修改',1060,0,'/core/admin/edit','A',1,'修改用户信息','view','core:admin:edit','#','','2021-09-25 11:18:39',NULL),(1120,'core','查询',1060,0,'/core/admin/view','A',1,'浏览用户信息','view','core:admin:view','#','','2021-09-25 11:18:39',NULL),(1121,'core','新增',1065,0,'/core/role/add','A',1,'新增用户角色','view','core:role:add','#','','2021-09-25 11:18:39',NULL),(1122,'core','删除',1065,0,'/core/role/del','A',1,'删除用户角色','view','core:role:del','#','','2021-09-25 11:18:39',NULL),(1123,'core','修改',1065,0,'/core/role/edit','A',1,'修改用户角色','view','core:role:edit','#','','2021-09-25 11:18:39',NULL),(1124,'core','查询',1065,0,'/core/role/view','A',1,'浏览用户角色','view','core:role:view','#','','2021-09-25 11:18:39',NULL),(1125,'core','设置密码',1060,0,'/core/admin/pwd','A',1,'修改用户密码','view','core:admin:pwd','#','修改用户密码','2021-09-25 11:18:39',NULL),(1126,'dev','开发文档',1107,50,'#','M',1,'','view','##','iz-icon-devdoc','','2021-09-25 11:18:39',NULL),(1127,'core','系统配置',1058,5,'/core/config','V',0,'','view','core:config:view','iz-icon-sysconfig','','2021-09-25 11:18:39',NULL),(1128,'core','新增',1127,0,'/core/config/add','A',1,'新增系统配置','view','core:config:add','#','','2021-09-25 11:18:39',NULL),(1129,'core','删除',1127,0,'/core/config/del','A',1,'删除系统配置','view','core:config:del','#','','2021-09-25 11:18:39',NULL),(1130,'core','修改',1127,0,'/core/config/edit','A',1,'修改系统配置','view','core:config:edit','#','','2021-09-25 11:18:39',NULL),(1131,'core','查询',1127,0,'/core/config/view','A',1,'浏览系统配置','view','core:config:view','#','','2021-09-25 11:18:39',NULL),(1132,'core','操作日志',1156,18,'/core/log','V',0,'','view','core:log:view','iz-icon-log','','2021-09-25 11:18:39',NULL),(1134,'core','删除',1132,0,'/core/log/del','A',1,'删除访问日志','view','core:log:del','#','','2021-09-25 11:18:39',NULL),(1136,'core','查询',1132,0,'/core/log/view','A',0,'','view','core:log:view','#','','2021-09-25 11:18:39',NULL),(1137,'core','详情',1066,0,'','A',0,'','view','core:menu:detail','#','','2021-09-25 11:18:39',NULL),(1138,'dev','导入表',1102,0,'/dev/table/import','A',0,'','view','dev:table:import','#','','2021-09-25 11:18:39',NULL),(1139,'dev','代码生成器',1101,0,'/dev/table','V',0,'','view','dev:table','iz-icon-table','','2021-09-25 11:18:39',NULL),(1140,'dev','新增',1139,0,'/dev/table/add','A',1,'','disabled','dev:table:add','#','','2021-09-25 11:18:39',NULL),(1141,'dev','删除',1139,0,'/dev/table/del','A',1,'','view','dev:table:del','#','','2021-09-25 11:18:39',NULL),(1142,'dev','修改',1139,0,'/dev/table/edit','A',1,'','view','dev:table:edit','#','','2021-09-25 11:18:39',NULL),(1143,'dev','查询',1139,0,'/dev/table/view','A',0,'','view','dev:table:view','#','','2021-09-25 11:18:39',NULL),(1144,'dev','生成',1139,0,'/dev/table/gen','A',0,'','view','dev:table:gen','#','','2021-09-25 11:18:39',NULL),(1145,'dev','表字段配置',1101,0,'/dev/field','V',0,'','hide','dev:field:view','iz-icon-field','','2021-09-25 11:18:39',NULL),(1146,'dev','新增',1145,0,'/dev/field/add','A',1,'','view','dev:fiel:add','#','','2021-09-25 11:18:39',NULL),(1147,'dev','删除',1145,0,'/dev/field/del','A',1,'','view','dev:fiel:del','#','','2021-09-25 11:18:39',NULL),(1148,'dev','修改',1145,0,'/dev/field/edit','A',1,'','view','dev:fiel:edit','#','','2021-09-25 11:18:39',NULL),(1149,'dev','查询',1145,0,'/dev/field/view','A',1,'','view','dev:fiel:view','#','','2021-09-25 11:18:39',NULL),(1150,'dev','重新导入',1139,0,'/dev/table/reImport','A',0,'','view','dev:table:import','#','','2021-09-25 11:18:39',NULL),(1152,'core','新增',1151,0,'/core/dictData/add','A',1,'','view','core:dictData:add','#','','2021-09-25 11:18:39',NULL),(1153,'core','删除',1151,0,'/core/dictData/del','A',1,'00','view','core:dictData:del','#','','2021-09-25 11:18:39',NULL),(1154,'core','修改',1151,0,'/core/dictData/edit','A',1,'','view','core:dictData:edit','#','','2021-09-25 11:18:39',NULL),(1155,'core','查询',1151,0,'/core/dictData/view','A',0,'','view','core:dictData:view','#','','2021-09-25 11:18:39',NULL),(1156,'core','系统监控',1,300,'#','M',0,'','view',NULL,'iz-icon-sys-monitor','','2021-09-25 11:18:39',NULL),(1157,'core','在线用户',1156,0,'/core/online','V',1,'在线用户操作','view','core:online:user','iz-icon-online-user','','2021-09-25 11:18:39',NULL),(1162,'quartz','定时任务',1156,50,'/quartz/triggers','V',1,'作业监控','view','quartz:triggers:view','iz-icon-quartz','','2021-09-25 11:18:39',NULL),(1163,'quartz','新增',1162,0,'/quartz/triggers/add','A',1,'','disabled','quartz:trigger:add','#','','2021-09-25 11:18:39',NULL),(1164,'quartz','删除',1162,0,'/quartz/triggers/del','A',1,'','view','quartz:trigger:del','#','','2021-09-25 11:18:39',NULL),(1165,'quartz','修改',1162,0,'/quartz/triggers/edit','A',1,'','view','quartz:trigger:edit','#','','2021-09-25 11:18:39',NULL),(1166,'quartz','查询',1162,0,'/quartz/triggers/view','A',1,'','view','quartz:trigger:view','#','','2021-09-25 11:18:39',NULL),(1167,'quartz','暂停',1162,5,'/quartz/triggers/paused','A',1,'暂停作业','hide','quartz:triggers:paused','#','','2021-09-25 11:18:39',NULL),(1168,'quartz','运行一次',1162,0,'/quartz/triggers/run','A',1,'运行一次作业','view','quartz:triggers:run','#','','2021-09-25 11:18:39',NULL),(1169,'dev','在线',1101,0,'/develop/genOnline','V',0,'','view','core:genOnline:view','iz-icon-online-design','','2021-09-25 11:18:39',NULL),(1170,'dev','新增',1169,0,'/develop/genOnline/add','A',1,'','view','develop:genOnlin:add','#','','2021-09-25 11:18:39',NULL),(1171,'dev','删除',1169,0,'/develop/genOnline/del','A',1,'','view','develop:genOnlin:del','#','','2021-09-25 11:18:39',NULL),(1172,'dev','修改',1169,0,'/develop/genOnline/edit','A',1,'','view','develop:genOnlin:edit','#','','2021-09-25 11:18:39',NULL),(1173,'dev','查询',1169,0,'/develop/genOnline/view','A',1,'','view','develop:genOnlin:view','#','','2021-09-25 11:18:39',NULL),(1174,'dev','生成',1169,0,'/develop/genOnline/gen','A',1,'','view','develop:genOnline:gen','#','','2021-09-25 11:18:04',NULL),(1217,'core','分配权限',1065,68,'/core/role/perm','A',1,'','view','core:role:perm','#','',NULL,NULL),(1218,'core','删除',1157,68,'/core/onlineUser/del','A',1,'','view','core:onlineUser:del','#','',NULL,NULL),(1219,'core','下线',1157,68,'/core/onlineUser/offline','A',1,'','view','core:onlineUser:offline','#','',NULL,NULL),(1220,'core','查询',1157,68,'/core/onlineUser/view','A',1,'','view','core:onlineUser:view','#','',NULL,NULL);
/*!40000 ALTER TABLE `t_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_online_user`
--

DROP TABLE IF EXISTS `t_online_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_online_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `session_id` varchar(64) NOT NULL COMMENT '会话编号',
  `account` varchar(32) DEFAULT NULL COMMENT '当前在线的用户',
  `user_nick` varchar(64) NOT NULL DEFAULT '' COMMENT '用户昵称',
  `access_ip` char(16) NOT NULL DEFAULT '' COMMENT '登入ip',
  `browse` varchar(32) NOT NULL DEFAULT '' COMMENT '登录时使用的浏览器',
  `os` varchar(32) NOT NULL DEFAULT '' COMMENT '使用的操作系统',
  `status` enum('Online','Offline') NOT NULL DEFAULT 'Online' COMMENT '用户状态(Def. 默认 ,on. 在线, off. 离线)',
  `type` enum('COMPUTER','MOBILE','TABLET','UNKNOWN') NOT NULL DEFAULT 'UNKNOWN' COMMENT '应用类型',
  `location` varchar(64) NOT NULL DEFAULT '' COMMENT '访问的本地位置',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `expire_time` int(11) DEFAULT '0' COMMENT '超时时长, 从访问到离开的时间， 单位分钟',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '系统访问时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '系统最后访问时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_online_user_session_id_uindex` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=571 DEFAULT CHARSET=utf8 COMMENT='在线用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_online_user`
--

LOCK TABLES `t_online_user` WRITE;
/*!40000 ALTER TABLE `t_online_user` DISABLE KEYS */;
INSERT INTO `t_online_user` VALUES (570,'53956127-43df-4303-8af6-a9698c7063b9','admin','超级管理员','127.0.0.1','Chrome 9','Windows 10','Online','COMPUTER','','2021-10-07 14:54:39',1800,'2021-10-07 06:54:37','2021-10-07 14:54:37');
/*!40000 ALTER TABLE `t_online_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_organize`
--

DROP TABLE IF EXISTS `t_organize`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_organize` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `pid` bigint(20) DEFAULT '0' COMMENT '父级id',
  `level` int(3) DEFAULT '1' COMMENT '属于第几级',
  `name` varchar(64) NOT NULL COMMENT '机构名称',
  `sort` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `path` varchar(128) DEFAULT NULL COMMENT '机构路径',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='组织机构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_organize`
--

LOCK TABLES `t_organize` WRITE;
/*!40000 ALTER TABLE `t_organize` DISABLE KEYS */;
INSERT INTO `t_organize` VALUES (1,0,1,'厦门由源创科技',0,'iteaj',NULL,'1','','2019-11-26 09:20:59','',NULL),(2,1,1,'运维部',0,'iteaj',NULL,'1,2','','2019-11-26 01:21:01','',NULL),(3,2,2,'技术部',0,'iteaj',NULL,'1,2,3','','2019-11-26 18:19:23','',NULL),(4,1,1,'产品部',0,'iteaj','13123456028','1,4','','2021-06-06 15:49:13','',NULL),(5,2,2,'测试部',0,'test','13123456029','1,2,5','','2021-06-06 15:51:12','',NULL),(7,2,1,'运维一部',0,'one','13123456030','1,2,7','','2021-06-06 15:59:13','',NULL);
/*!40000 ALTER TABLE `t_organize` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_role`
--

DROP TABLE IF EXISTS `t_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_role` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(30) NOT NULL COMMENT '角色名称',
  `sort` int(4) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `scope` char(1) DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限）',
  `status` enum('enabled','disabled') DEFAULT 'enabled' COMMENT '角色状态（enabled 启用 disabled 禁用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8 COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role`
--

LOCK TABLES `t_role` WRITE;
/*!40000 ALTER TABLE `t_role` DISABLE KEYS */;
INSERT INTO `t_role` VALUES (2,'管理员',2,'1','enabled',NULL,'','2019-11-27 16:48:35','',NULL),(3,'技术总监',0,'1','enabled','CTO','','2019-11-27 18:37:24','',NULL),(4,'演示角色',8,'1','enabled','用于演示','','2020-04-24 12:03:38','',NULL);
/*!40000 ALTER TABLE `t_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_role_menu`
--

DROP TABLE IF EXISTS `t_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_role_menu` (
  `rid` bigint(20) NOT NULL COMMENT '角色ID',
  `mid` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`rid`,`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role_menu`
--

LOCK TABLES `t_role_menu` WRITE;
/*!40000 ALTER TABLE `t_role_menu` DISABLE KEYS */;
INSERT INTO `t_role_menu` VALUES (2,1),(2,1058),(2,1060),(2,1065),(2,1066),(2,1069),(2,1070),(2,1071),(2,1072),(2,1090),(2,1092),(2,1093),(2,1094),(2,1095),(2,1108),(2,1113),(2,1114),(2,1115),(2,1116),(2,1117),(2,1118),(2,1119),(2,1120),(2,1121),(2,1122),(2,1123),(2,1124),(2,1125),(2,1127),(2,1128),(2,1129),(2,1130),(2,1131),(2,1132),(2,1133),(2,1134),(2,1135),(2,1136),(2,1137),(2,1156),(2,1157),(2,1159),(2,1161),(2,1162),(2,1163),(2,1164),(2,1165),(2,1166),(2,1167),(2,1168),(3,1060),(3,1117),(3,1118),(3,1119),(3,1120),(3,1125),(4,1117),(4,1118),(4,1119),(4,1120);
/*!40000 ALTER TABLE `t_role_menu` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-10-07 14:58:09
