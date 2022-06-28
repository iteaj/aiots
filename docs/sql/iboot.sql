-- MySQL dump 10.13  Distrib 8.0.25, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: iboot
-- ------------------------------------------------------
-- Server version	8.0.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `iot_device`
--

DROP TABLE IF EXISTS `iot_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iot_device` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `ip` varchar(32) DEFAULT NULL COMMENT '设备ip',
  `uid` bigint NOT NULL COMMENT '设备唯一的uid',
  `name` varchar(64) NOT NULL COMMENT '设备名称',
  `port` int DEFAULT NULL COMMENT '设备端口',
  `model` varchar(64) DEFAULT NULL COMMENT '设备型号',
  `status` enum('online','offline') NOT NULL DEFAULT 'offline' COMMENT '设备状态',
  `user_id` bigint DEFAULT NULL COMMENT '所属会员',
  `device_sn` varchar(64) NOT NULL COMMENT '设备编号',
  `switch_time` datetime DEFAULT NULL COMMENT '状态切换时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `device_type_id` bigint NOT NULL COMMENT '设备所属类型',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iot_device_uid_uindex` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_device`
--

LOCK TABLES `iot_device` WRITE;
/*!40000 ALTER TABLE `iot_device` DISABLE KEYS */;
INSERT INTO `iot_device` VALUES (3,NULL,1537990013924249602,'电箱1号',NULL,NULL,'offline',NULL,'dx0000001','2022-06-28 16:11:44','2022-06-18 10:46:08',4,NULL);
/*!40000 ALTER TABLE `iot_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_device_child`
--

DROP TABLE IF EXISTS `iot_device_child`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iot_device_child` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '子设备名称',
  `uid` bigint NOT NULL COMMENT '设备uid',
  `child_sn` varchar(64) NOT NULL COMMENT '子设备编号',
  `protocol_type` smallint NOT NULL COMMENT '协议类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `iot_device_child_child_sn_index` (`child_sn`),
  KEY `iot_device_child_uid_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3 COMMENT='子设备';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_device_child`
--

LOCK TABLES `iot_device_child` WRITE;
/*!40000 ALTER TABLE `iot_device_child` DISABLE KEYS */;
INSERT INTO `iot_device_child` VALUES (1,'继电器',1537990013924249600,'1',1,'2022-06-18 10:48:32'),(2,'电流采集',1537990013924249600,'2',2,'2022-06-21 15:36:32');
/*!40000 ALTER TABLE `iot_device_child` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_device_type`
--

DROP TABLE IF EXISTS `iot_device_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iot_device_type` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pid` bigint NOT NULL DEFAULT '0' COMMENT '父id',
  `path` varchar(128) DEFAULT NULL COMMENT '路径',
  `name` varchar(64) NOT NULL COMMENT '类型名称',
  `alias` varchar(36) DEFAULT NULL COMMENT '类型别名',
  `image` varchar(128) DEFAULT NULL COMMENT '类型图地址',
  `remark` varchar(256) DEFAULT NULL COMMENT '类型说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `iot_device_type_alias_index` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='设备类型';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_device_type`
--

LOCK TABLES `iot_device_type` WRITE;
/*!40000 ALTER TABLE `iot_device_type` DISABLE KEYS */;
INSERT INTO `iot_device_type` VALUES (1,5,NULL,'plc','PLC',NULL,'plc设备','2022-05-15 22:04:02'),(2,1,NULL,'西门子','PLC',NULL,'西门子','2022-05-15 22:08:08'),(3,1,NULL,'欧姆龙','PLC',NULL,'欧姆龙','2022-05-15 22:09:24'),(4,5,NULL,'dtu','DTU',NULL,'数据传输单元(用户将串口转互联网传输)','2022-06-04 14:07:42'),(5,0,NULL,'工业物联网',NULL,NULL,NULL,'2022-06-12 13:10:58');
/*!40000 ALTER TABLE `iot_device_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_access_log`
--

DROP TABLE IF EXISTS `sys_access_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_access_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求URL',
  `ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '主机地址',
  `msn` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '所属模块',
  `title` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '日志标题',
  `type` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '功能类型',
  `millis` int DEFAULT NULL COMMENT '操作毫秒数',
  `user_id` bigint DEFAULT '0' COMMENT '操作人员',
  `user_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `status` tinyint(1) NOT NULL DEFAULT '1' COMMENT '操作状态',
  `err_msg` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '异常消息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2312 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_access_log`
--

LOCK TABLES `sys_access_log` WRITE;
/*!40000 ALTER TABLE `sys_access_log` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_access_log` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_admin`
--

DROP TABLE IF EXISTS `sys_admin`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_admin` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `org_id` bigint DEFAULT NULL COMMENT '部门ID',
  `account` char(24) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录账号',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户名称',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户邮箱',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '手机号码',
  `sex` enum('man','woman','non') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'non' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像路径',
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '密码',
  `status` enum('enabled','disabled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'enabled' COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登陆IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_admin_account_uindex` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_admin`
--

LOCK TABLES `sys_admin` WRITE;
/*!40000 ALTER TABLE `sys_admin` DISABLE KEYS */;
INSERT INTO `sys_admin` VALUES (1,3,'admin','超级管理员','iteaj@outlook.com','18059222381','man','/img/logo.png','a66abb5684c45962d887564f08346e8d','enabled','127.0.0.1','2018-03-16 11:33:00','做回自己, 活出未来(iteaj)','admin','2018-03-15 11:33:00','admin','2018-03-14 19:33:00'),(7,1,'demo','demo','iteaj@outlook.com',NULL,'man',NULL,'fe01ce2a7fbac8fafaed7c982a04e229','enabled',NULL,NULL,'用于演示','',NULL,'',NULL),(8,1,'iot','物联网','iteaj@outlook.com',NULL,'non',NULL,'97c209ca505f2958db30a42135afab5c','enabled',NULL,NULL,NULL,'',NULL,'',NULL);
/*!40000 ALTER TABLE `sys_admin` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_admin_role`
--

DROP TABLE IF EXISTS `sys_admin_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_admin_role` (
  `aid` bigint NOT NULL COMMENT '管理员id',
  `rid` bigint DEFAULT NULL COMMENT '角色id'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='管理员角色表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_admin_role`
--

LOCK TABLES `sys_admin_role` WRITE;
/*!40000 ALTER TABLE `sys_admin_role` DISABLE KEYS */;
INSERT INTO `sys_admin_role` VALUES (7,4),(1,3),(1,4),(8,5);
/*!40000 ALTER TABLE `sys_admin_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_config`
--

DROP TABLE IF EXISTS `sys_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_config` (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '参数名称',
  `label` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '系统配置key',
  `value` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键值',
  `type` enum('def','sys') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'def' COMMENT '配置类型',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_config_label_uindex` (`label`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_config`
--

LOCK TABLES `sys_config` WRITE;
/*!40000 ALTER TABLE `sys_config` DISABLE KEYS */;
INSERT INTO `sys_config` VALUES (1,'系统名称','sys_name','厦门由创源科技','sys','菜单栏显示的名称','2019-12-04 07:25:31',NULL);
/*!40000 ALTER TABLE `sys_config` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_data`
--

DROP TABLE IF EXISTS `sys_dict_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_data` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典id',
  `sort` int DEFAULT '0' COMMENT '字典排序',
  `label` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
  `value` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典键值',
  `type` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典类型',
  `iz_default` tinyint(1) DEFAULT '0' COMMENT '是否默认（Y是 N否）',
  `status` enum('enabled','disabled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'enabled' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_data`
--

LOCK TABLES `sys_dict_data` WRITE;
/*!40000 ALTER TABLE `sys_dict_data` DISABLE KEYS */;
INSERT INTO `sys_dict_data` VALUES (1,1,'主栏','T','opera_position',0,'enabled',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(2,2,'主栏(更多)','MT','opera_position',0,'enabled',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(6,0,'主栏(更多)','MM','function-position',0,'enabled','主栏(更多)','',NULL,'',NULL),(11,0,'正常','status','common_status',0,'enabled','正常状态0','',NULL,'',NULL),(12,0,'禁用','disabled','common_status',0,'enabled','禁用状态','',NULL,'',NULL),(13,0,'主栏','M','function-position',0,'enabled','主栏','',NULL,'',NULL),(14,0,'隐藏','hidden','common_status',0,'enabled','隐藏状态','',NULL,'',NULL),(16,0,'男','male','sex',0,'enabled',NULL,'',NULL,'',NULL),(17,0,'女','female','sex',0,'enabled',NULL,'',NULL,'',NULL),(22,10,'启用','enabled','sys_func_status',0,'enabled',NULL,'',NULL,'',NULL),(23,20,'禁用','disabled','sys_func_status',0,'enabled',NULL,'',NULL,'',NULL),(24,10,'modbus_tcp','1','iot_dtu_protocol_type',0,'enabled',NULL,'',NULL,'',NULL),(25,20,'modbus_rtu','2','iot_dtu_protocol_type',0,'enabled',NULL,'',NULL,'',NULL),(26,30,'自定义','3','iot_dtu_protocol_type',0,'enabled',NULL,'',NULL,'',NULL),(27,10,'在线','online','iot_device_status',0,'enabled',NULL,'',NULL,'',NULL),(28,20,'离线','offline','iot_device_status',0,'enabled',NULL,'',NULL,'',NULL);
/*!40000 ALTER TABLE `sys_dict_data` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_dict_type`
--

DROP TABLE IF EXISTS `sys_dict_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_dict_type` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `type` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '字典类型',
  `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
  `status` enum('enabled','disabled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'enabled' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type t_dict_type_type_uindex` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_dict_type`
--

LOCK TABLES `sys_dict_type` WRITE;
/*!40000 ALTER TABLE `sys_dict_type` DISABLE KEYS */;
INSERT INTO `sys_dict_type` VALUES (5,'sys_func_status','功能状态','enabled','启用(enabled) 禁用(disabled)','','2022-04-17 04:53:36','',NULL),(6,'iot_dtu_protocol_type','dtu协议类型','enabled','dtu协议类型','','2022-06-21 06:49:10','',NULL),(7,'iot_device_status','设备状态','enabled',NULL,'','2022-06-21 07:39:40','',NULL);
/*!40000 ALTER TABLE `sys_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_menu`
--

DROP TABLE IF EXISTS `sys_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_menu` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `pid` bigint DEFAULT '0' COMMENT '父菜单ID',
  `sort` int DEFAULT '0' COMMENT '显示顺序',
  `url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '请求地址',
  `type` enum('A','M','V') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单类型: A.权限、M.菜单、V.视图',
  `log` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否记录日志',
  `log_desc` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '日志说明',
  `status` enum('enabled','disabled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'enabled' COMMENT '菜单状态',
  `perms` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
  `remark` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1233 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_menu`
--

LOCK TABLES `sys_menu` WRITE;
/*!40000 ALTER TABLE `sys_menu` DISABLE KEYS */;
INSERT INTO `sys_menu` VALUES (1058,'系统管理',0,50,'#1','M',1,'','enabled',NULL,'iz-icon-sys-setting','','2021-09-25 11:18:39',NULL),(1060,'用户管理',1058,0,'/core/admin','V',0,'','enabled','core:admin:view','iz-icon-sys-user','','2021-09-25 11:18:39',NULL),(1065,'角色管理',1058,1,'/core/role','V',0,'','enabled','core:role:view','iz-icon-sys-role','','2021-09-25 11:18:39',NULL),(1066,'菜单管理',1058,2,'/core/menu','V',0,'浏览菜单信息','enabled','core:menu:view','iz-icon-menu','','2021-09-25 11:18:39',NULL),(1069,'新增',1066,1,'/core/menu/add','A',1,'新增菜单','enabled','core:menu:add','#','','2021-09-25 11:18:39',NULL),(1070,'编辑',1066,2,'/core/menu/edit','A',1,'修改菜单信息','enabled','core:menu:edit','#','','2021-09-25 11:18:39',NULL),(1071,'删除',1066,3,'/core/menu/del','A',1,'删除菜单','enabled','core:menu:del','#','','2021-09-25 11:18:39',NULL),(1072,'查询',1066,0,'/core/menu/view','A',1,'浏览菜单信息','enabled','core:menu:view','#','','2021-09-25 11:18:39',NULL),(1090,'字典管理',1058,8,'/core/dict','V',0,'','enabled','core:dictType:view','iz-icon-zidianguanli','','2021-09-25 11:18:39',NULL),(1092,'新增',1090,0,'/core/dictType/add','A',1,'新增字典类型','enabled','core:dictType:add','#','','2021-09-25 11:18:39',NULL),(1093,'修改',1090,0,'/core/dictType/edit','A',1,'修改字典类型','enabled','core:dictType:edit','#','','2021-09-25 11:18:39',NULL),(1094,'查询',1090,0,'/core/dictType/view','A',1,'浏览字典信息','enabled','core:dictType:view','#','','2021-09-25 11:18:39',NULL),(1108,'部门管理',1058,50,'/core/org','V',0,'','enabled','core:org:view','iz-icon-jigouguanli','','2021-09-25 11:18:39',NULL),(1113,'新增',1108,0,'/core/org/add','A',1,'新增组织机构','enabled','core:org:add','#','','2021-09-25 11:18:39',NULL),(1114,'删除',1108,0,'/core/org/del','A',1,'删除组织机构','enabled','core:org:del','#','','2021-09-25 11:18:39',NULL),(1115,'修改',1108,0,'/core/org/edit','A',1,'修改组织机构','enabled','core:org:edit','#','','2021-09-25 11:18:39',NULL),(1116,'查询',1108,0,'/core/org/view','A',1,'浏览组织机构信息','enabled','core:org:view','#','','2021-09-25 11:18:39',NULL),(1117,'新增',1060,0,'/core/admin/add','A',1,'','enabled','core:admin:add','#','','2021-09-25 11:18:39',NULL),(1118,'删除',1060,0,'/core/admin/del','A',1,'删除用户信息','enabled','core:admin:del','#','','2021-09-25 11:18:39',NULL),(1119,'修改',1060,0,'/core/admin/edit','A',1,'修改用户信息','enabled','core:admin:edit','#','','2021-09-25 11:18:39',NULL),(1120,'查询',1060,0,'/core/admin/view','A',1,'浏览用户信息','enabled','core:admin:view','#','','2021-09-25 11:18:39',NULL),(1121,'新增',1065,0,'/core/role/add','A',1,'新增用户角色','enabled','core:role:add','#','','2021-09-25 11:18:39',NULL),(1122,'删除',1065,0,'/core/role/del','A',1,'删除用户角色','enabled','core:role:del','#','','2021-09-25 11:18:39',NULL),(1123,'修改',1065,0,'/core/role/edit','A',1,'修改用户角色','enabled','core:role:edit','#','','2021-09-25 11:18:39',NULL),(1124,'查询',1065,0,'/core/role/view','A',1,'浏览用户角色','enabled','core:role:view','#','','2021-09-25 11:18:39',NULL),(1125,'设置密码',1060,0,'/core/admin/pwd','A',1,'修改用户密码','enabled','core:admin:pwd','#','修改用户密码','2021-09-25 11:18:39',NULL),(1127,'系统配置',1058,5,'/core/config','V',0,'','enabled','core:config:view','iz-icon-sysconfig','','2021-09-25 11:18:39',NULL),(1128,'新增',1127,0,'/core/config/add','A',1,'新增系统配置','enabled','core:config:add','#','','2021-09-25 11:18:39',NULL),(1129,'删除',1127,0,'/core/config/del','A',1,'删除系统配置','enabled','core:config:del','#','','2021-09-25 11:18:39',NULL),(1130,'修改',1127,0,'/core/config/edit','A',1,'修改系统配置','enabled','core:config:edit','#','','2021-09-25 11:18:39',NULL),(1131,'查询',1127,0,'/core/config/view','A',1,'浏览系统配置','enabled','core:config:view','#','','2021-09-25 11:18:39',NULL),(1132,'操作日志',1156,18,'/core/log','V',0,'','enabled','core:log:view','iz-icon-log','','2021-09-25 11:18:39',NULL),(1134,'删除',1132,0,'/core/log/del','A',1,'删除访问日志','enabled','core:log:del','#','','2021-09-25 11:18:39',NULL),(1136,'查询',1132,0,'/core/log/view','A',0,'','enabled','core:log:view','#','','2021-09-25 11:18:39',NULL),(1137,'详情',1066,0,'','A',0,'','enabled','core:menu:detail','#','','2021-09-25 11:18:39',NULL),(1156,'系统监控',0,100,'#','M',0,'','enabled',NULL,'iz-icon-sys-monitor','','2021-09-25 00:00:00',NULL),(1157,'在线用户',1156,0,'/core/online','V',1,'在线用户操作','enabled','core:online:user','iz-icon-online-user','','2021-09-25 11:18:39',NULL),(1162,'定时任务',1156,50,'/quartz/triggers','V',1,'作业监控','enabled','quartz:triggers:view','iz-icon-quartz','','2021-09-25 11:18:39',NULL),(1163,'新增',1162,0,'/quartz/triggers/add','A',1,'','enabled','quartz:trigger:add','#','','2021-09-25 11:18:39',NULL),(1164,'删除',1162,0,'/quartz/triggers/del','A',1,'','enabled','quartz:trigger:del','#','','2021-09-25 11:18:39',NULL),(1165,'修改',1162,0,'/quartz/triggers/edit','A',1,'','enabled','quartz:trigger:edit','#','','2021-09-25 11:18:39',NULL),(1166,'查询',1162,0,'/quartz/triggers/view','A',1,'','enabled','quartz:trigger:view','#','','2021-09-25 11:18:39',NULL),(1167,'暂停',1162,5,'/quartz/triggers/paused','A',1,'暂停作业','enabled','quartz:triggers:paused','#','','2021-09-25 11:18:39',NULL),(1168,'运行一次',1162,0,'/quartz/triggers/run','A',1,'运行一次作业','enabled','quartz:triggers:run','#','','2021-09-25 11:18:39',NULL),(1217,'分配权限',1065,68,'/core/role/perm','A',1,'','enabled','core:role:perm','#','',NULL,NULL),(1218,'删除',1157,68,'/core/online/del','A',1,'','enabled','core:online:del','#','',NULL,NULL),(1219,'下线',1157,68,'/core/online/offline','A',1,'','enabled','core:online:offline','#','',NULL,NULL),(1220,'查询',1157,68,'/core/online/view','A',1,'','enabled','core:online:view','#','',NULL,NULL),(1221,'删除',1090,68,'/core/dictType/del','A',1,'','enabled','core:dictType:del','#','',NULL,NULL),(1222,'开发环境',0,150,'#','M',0,'','enabled',NULL,'iz-icon-kaifa','',NULL,NULL),(1223,'代码生成',1222,10,'/gen/code','V',0,'','enabled','gen:code:view','iz-icon-code','',NULL,NULL),(1224,'物联网',0,36,'#','M',0,'','enabled',NULL,'#','',NULL,NULL),(1225,'DTU管理',1224,10,'/iot/dtu','V',0,'','enabled','iot:dtu:view','#','',NULL,NULL),(1226,'调试',1225,100,'/iot/dtu/debug','A',0,'','enabled','iot:dtu:debug','#','',NULL,NULL),(1227,'修改',1225,50,'/iot/dtu/edit','A',1,'','enabled','iot:dtu:edit','#','',NULL,NULL),(1228,'PLC管理',1224,80,'/iot/plc','V',0,'','enabled','iot:plc:view','#','',NULL,NULL),(1229,'新增',1228,10,'/iot/plc/add','A',1,'','enabled','iot:plc:add','#','',NULL,NULL),(1230,'修改',1228,20,'/iot/plc/edit','A',1,'','enabled','iot:plc:edit','#','',NULL,NULL),(1231,'查询',1225,10,'/iot/dtu/view','A',0,'','enabled','iot:dtu:view','#','',NULL,NULL),(1232,'新增',1225,20,'/iot/dtu/add','A',1,'','enabled','io:dtu:add','#','',NULL,NULL);
/*!40000 ALTER TABLE `sys_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_online_user`
--

DROP TABLE IF EXISTS `sys_online_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_online_user` (
  `id` int NOT NULL AUTO_INCREMENT,
  `session_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话编号',
  `account` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '当前在线的用户',
  `user_nick` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '用户昵称',
  `access_ip` char(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登入ip',
  `browse` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '登录时使用的浏览器',
  `os` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '使用的操作系统',
  `status` enum('Online','Offline') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'Online' COMMENT '用户状态(Def. 默认 ,on. 在线, off. 离线)',
  `type` enum('COMPUTER','MOBILE','TABLET','UNKNOWN') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'UNKNOWN' COMMENT '应用类型',
  `location` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '访问的本地位置',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `expire_time` int DEFAULT '0' COMMENT '超时时长, 从访问到离开的时间， 单位分钟',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '系统访问时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '系统最后访问时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_online_user_session_id_uindex` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='在线用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_online_user`
--

LOCK TABLES `sys_online_user` WRITE;
/*!40000 ALTER TABLE `sys_online_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `sys_online_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_org`
--

DROP TABLE IF EXISTS `sys_org`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_org` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `pid` bigint DEFAULT '0' COMMENT '父级id',
  `level` int DEFAULT '1' COMMENT '属于第几级',
  `name` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '机构名称',
  `sort` int DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `path` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '机构路径',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='组织机构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_org`
--

LOCK TABLES `sys_org` WRITE;
/*!40000 ALTER TABLE `sys_org` DISABLE KEYS */;
INSERT INTO `sys_org` VALUES (1,0,1,'厦门由源创科技',0,'iteaj',NULL,'1','','2019-11-26 09:20:59','',NULL),(2,1,1,'运维部',0,'iteaj',NULL,'1,2','','2019-11-26 01:21:01','',NULL),(3,2,2,'技术部',0,'iteaj','rewre','1,2,3','','2019-11-26 18:19:23','',NULL),(4,1,1,'产品部',0,'iteaj','13123456028','1,4','','2021-06-06 15:49:13','',NULL),(5,2,2,'测试部',0,'test','13123456029','1,2,5','','2021-06-06 15:51:12','',NULL);
/*!40000 ALTER TABLE `sys_org` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role`
--

DROP TABLE IF EXISTS `sys_role`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `name` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `sort` int NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `scope` char(1) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限）',
  `status` enum('enabled','disabled') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT 'enabled' COMMENT '角色状态（enabled 启用 disabled 禁用）',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role`
--

LOCK TABLES `sys_role` WRITE;
/*!40000 ALTER TABLE `sys_role` DISABLE KEYS */;
INSERT INTO `sys_role` VALUES (2,'管理员',2,'1','enabled',NULL,'','2019-11-27 16:48:35','',NULL),(3,'技术总监',0,'1','enabled','CTO','','2019-11-27 18:37:24','',NULL),(4,'演示角色',8,'1','enabled','用于演示','','2020-04-24 12:03:38','',NULL),(5,'物联网管理员',50,'1','enabled',NULL,'','2022-06-22 09:16:01','',NULL);
/*!40000 ALTER TABLE `sys_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_role_menu`
--

DROP TABLE IF EXISTS `sys_role_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sys_role_menu` (
  `rid` bigint NOT NULL COMMENT '角色ID',
  `mid` bigint NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`rid`,`mid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_role_menu`
--

LOCK TABLES `sys_role_menu` WRITE;
/*!40000 ALTER TABLE `sys_role_menu` DISABLE KEYS */;
INSERT INTO `sys_role_menu` VALUES (2,1),(2,1058),(2,1060),(2,1065),(2,1066),(2,1069),(2,1070),(2,1071),(2,1072),(2,1090),(2,1092),(2,1093),(2,1094),(2,1095),(2,1108),(2,1113),(2,1114),(2,1115),(2,1116),(2,1117),(2,1118),(2,1119),(2,1120),(2,1121),(2,1122),(2,1123),(2,1124),(2,1125),(2,1127),(2,1128),(2,1129),(2,1130),(2,1131),(2,1132),(2,1133),(2,1134),(2,1135),(2,1136),(2,1137),(2,1156),(2,1157),(2,1159),(2,1161),(2,1162),(2,1163),(2,1164),(2,1165),(2,1166),(2,1167),(2,1168),(3,1),(3,1058),(3,1060),(3,1065),(3,1066),(3,1069),(3,1070),(3,1071),(3,1072),(3,1090),(3,1092),(3,1093),(3,1094),(3,1108),(3,1113),(3,1114),(3,1115),(3,1116),(3,1117),(3,1118),(3,1119),(3,1120),(3,1121),(3,1122),(3,1123),(3,1124),(3,1125),(3,1126),(3,1127),(3,1128),(3,1129),(3,1130),(3,1131),(3,1132),(3,1134),(3,1136),(3,1137),(3,1156),(3,1157),(3,1162),(3,1163),(3,1164),(3,1165),(3,1166),(3,1167),(3,1168),(3,1217),(3,1218),(3,1219),(3,1220),(3,1221),(4,1101),(4,1102),(4,1103),(4,1107),(4,1126),(4,1138),(4,1139),(4,1140),(4,1141),(4,1142),(4,1143),(4,1144),(4,1145),(4,1146),(4,1147),(4,1148),(4,1149),(4,1150),(4,1169),(4,1170),(4,1171),(4,1172),(4,1173),(4,1174),(5,1072),(5,1094),(5,1116),(5,1120),(5,1124),(5,1131),(5,1136),(5,1220),(5,1222),(5,1223),(5,1224),(5,1225),(5,1226),(5,1227),(5,1228),(5,1229),(5,1230);
/*!40000 ALTER TABLE `sys_role_menu` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-06-28 20:26:09
