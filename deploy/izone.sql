-- MySQL dump 10.13  Distrib 5.7.21, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: izone
-- ------------------------------------------------------
-- Server version	5.7.21-1

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
-- Table structure for table `menu_view`
--

DROP TABLE IF EXISTS `menu_view`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `menu_view` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` char(32) NOT NULL COMMENT '视图名称',
  `type` enum('默认','菜单组') NOT NULL DEFAULT '默认',
  `icon` char(32) DEFAULT NULL COMMENT '图标',
  `sort` tinyint(4) NOT NULL DEFAULT '0' COMMENT '显示顺序',
  `status` enum('正常','禁用') NOT NULL DEFAULT '正常',
  `menus` varchar(128) DEFAULT NULL COMMENT '菜单列表',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_view_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='菜单视图';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `menu_view`
--

LOCK TABLES `menu_view` WRITE;
/*!40000 ALTER TABLE `menu_view` DISABLE KEYS */;
INSERT INTO `menu_view` VALUES (1,'系统管理','默认','iz-icon-xitong',0,'正常','1058,1059,1064','2019-08-16 23:38:45','2019-08-16 15:38:45'),(4,'文章资讯','默认','iz-icon-xinwen',0,'正常',NULL,'2019-08-16 23:38:45','2019-08-16 15:38:45');
/*!40000 ALTER TABLE `menu_view` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_blob_triggers`
--

DROP TABLE IF EXISTS `qrtz_blob_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_blob_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `BLOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_blob_triggers`
--

LOCK TABLES `qrtz_blob_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_blob_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_blob_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_calendars`
--

DROP TABLE IF EXISTS `qrtz_calendars`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_calendars` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `CALENDAR_NAME` varchar(200) NOT NULL,
  `CALENDAR` blob NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`CALENDAR_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_calendars`
--

LOCK TABLES `qrtz_calendars` WRITE;
/*!40000 ALTER TABLE `qrtz_calendars` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_calendars` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_cron_triggers`
--

DROP TABLE IF EXISTS `qrtz_cron_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_cron_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `CRON_EXPRESSION` varchar(200) NOT NULL,
  `TIME_ZONE_ID` varchar(80) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_cron_triggers`
--

LOCK TABLES `qrtz_cron_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_cron_triggers` DISABLE KEYS */;
INSERT INTO `qrtz_cron_triggers` VALUES ('ClusterQuartz','demo','系统','18 17 18 * * ?','Asia/Shanghai');
/*!40000 ALTER TABLE `qrtz_cron_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_fired_triggers`
--

DROP TABLE IF EXISTS `qrtz_fired_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_fired_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `ENTRY_ID` varchar(95) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `FIRED_TIME` bigint(13) NOT NULL,
  `SCHED_TIME` bigint(13) NOT NULL,
  `PRIORITY` int(11) NOT NULL,
  `STATE` varchar(16) NOT NULL,
  `JOB_NAME` varchar(200) DEFAULT NULL,
  `JOB_GROUP` varchar(200) DEFAULT NULL,
  `IS_NONCONCURRENT` varchar(1) DEFAULT NULL,
  `REQUESTS_RECOVERY` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`ENTRY_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_fired_triggers`
--

LOCK TABLES `qrtz_fired_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_fired_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_fired_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_job_details`
--

DROP TABLE IF EXISTS `qrtz_job_details`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_job_details` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `JOB_CLASS_NAME` varchar(250) NOT NULL,
  `IS_DURABLE` varchar(1) NOT NULL,
  `IS_NONCONCURRENT` varchar(1) NOT NULL,
  `IS_UPDATE_DATA` varchar(1) NOT NULL,
  `REQUESTS_RECOVERY` varchar(1) NOT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_job_details`
--

LOCK TABLES `qrtz_job_details` WRITE;
/*!40000 ALTER TABLE `qrtz_job_details` DISABLE KEYS */;
INSERT INTO `qrtz_job_details` VALUES ('ClusterQuartz','demo','系统','系统作业调度测试','com.iteaj.quartz.demo.DemoJob','0','0','0','0','��\0sr\0org.quartz.JobDataMap���迩��\0\0xr\0&org.quartz.utils.StringKeyDirtyFlagMap�����](\0Z\0allowsTransientDataxr\0org.quartz.utils.DirtyFlagMap�.�(v\n�\0Z\0dirtyL\0mapt\0Ljava/util/Map;xpsr\0java.util.HashMap���`�\0F\0\nloadFactorI\0	thresholdxp?@\0\0\0\0\0w\0\0\0\0\0\0t\0authort\0iteajt\0demosr\0java.lang.Boolean� r�՜��\0Z\0valuexpx\0');
/*!40000 ALTER TABLE `qrtz_job_details` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_locks`
--

DROP TABLE IF EXISTS `qrtz_locks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_locks` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `LOCK_NAME` varchar(40) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`LOCK_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_locks`
--

LOCK TABLES `qrtz_locks` WRITE;
/*!40000 ALTER TABLE `qrtz_locks` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_locks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_paused_trigger_grps`
--

DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_paused_trigger_grps`
--

LOCK TABLES `qrtz_paused_trigger_grps` WRITE;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_paused_trigger_grps` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_scheduler_state`
--

DROP TABLE IF EXISTS `qrtz_scheduler_state`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_scheduler_state` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `INSTANCE_NAME` varchar(200) NOT NULL,
  `LAST_CHECKIN_TIME` bigint(13) NOT NULL,
  `CHECKIN_INTERVAL` bigint(13) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`INSTANCE_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_scheduler_state`
--

LOCK TABLES `qrtz_scheduler_state` WRITE;
/*!40000 ALTER TABLE `qrtz_scheduler_state` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_scheduler_state` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simple_triggers`
--

DROP TABLE IF EXISTS `qrtz_simple_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_simple_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `REPEAT_COUNT` bigint(7) NOT NULL,
  `REPEAT_INTERVAL` bigint(12) NOT NULL,
  `TIMES_TRIGGERED` bigint(10) NOT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simple_triggers`
--

LOCK TABLES `qrtz_simple_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simple_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simple_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_simprop_triggers`
--

DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_simprop_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `STR_PROP_1` varchar(512) DEFAULT NULL,
  `STR_PROP_2` varchar(512) DEFAULT NULL,
  `STR_PROP_3` varchar(512) DEFAULT NULL,
  `INT_PROP_1` int(11) DEFAULT NULL,
  `INT_PROP_2` int(11) DEFAULT NULL,
  `LONG_PROP_1` bigint(20) DEFAULT NULL,
  `LONG_PROP_2` bigint(20) DEFAULT NULL,
  `DEC_PROP_1` decimal(13,4) DEFAULT NULL,
  `DEC_PROP_2` decimal(13,4) DEFAULT NULL,
  `BOOL_PROP_1` varchar(1) DEFAULT NULL,
  `BOOL_PROP_2` varchar(1) DEFAULT NULL,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`) REFERENCES `qrtz_triggers` (`SCHED_NAME`, `TRIGGER_NAME`, `TRIGGER_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_simprop_triggers`
--

LOCK TABLES `qrtz_simprop_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` DISABLE KEYS */;
/*!40000 ALTER TABLE `qrtz_simprop_triggers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `qrtz_triggers`
--

DROP TABLE IF EXISTS `qrtz_triggers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `qrtz_triggers` (
  `SCHED_NAME` varchar(120) NOT NULL,
  `TRIGGER_NAME` varchar(200) NOT NULL,
  `TRIGGER_GROUP` varchar(200) NOT NULL,
  `JOB_NAME` varchar(200) NOT NULL,
  `JOB_GROUP` varchar(200) NOT NULL,
  `DESCRIPTION` varchar(250) DEFAULT NULL,
  `NEXT_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PREV_FIRE_TIME` bigint(13) DEFAULT NULL,
  `PRIORITY` int(11) DEFAULT NULL,
  `TRIGGER_STATE` varchar(16) NOT NULL,
  `TRIGGER_TYPE` varchar(8) NOT NULL,
  `START_TIME` bigint(13) NOT NULL,
  `END_TIME` bigint(13) DEFAULT NULL,
  `CALENDAR_NAME` varchar(200) DEFAULT NULL,
  `MISFIRE_INSTR` smallint(2) DEFAULT NULL,
  `JOB_DATA` blob,
  PRIMARY KEY (`SCHED_NAME`,`TRIGGER_NAME`,`TRIGGER_GROUP`),
  KEY `SCHED_NAME` (`SCHED_NAME`,`JOB_NAME`,`JOB_GROUP`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`) REFERENCES `qrtz_job_details` (`SCHED_NAME`, `JOB_NAME`, `JOB_GROUP`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `qrtz_triggers`
--

LOCK TABLES `qrtz_triggers` WRITE;
/*!40000 ALTER TABLE `qrtz_triggers` DISABLE KEYS */;
INSERT INTO `qrtz_triggers` VALUES ('ClusterQuartz','demo','系统','demo','系统',NULL,1596017838000,1595946753468,5,'WAITING','CRON',1595860628000,0,NULL,0,'');
/*!40000 ALTER TABLE `qrtz_triggers` ENABLE KEYS */;
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
  `sex` enum('男','女','未知') DEFAULT '未知' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) DEFAULT '' COMMENT '头像路径',
  `password` varchar(50) DEFAULT '' COMMENT '密码',
  `status` enum('正常','禁用') NOT NULL DEFAULT '正常' COMMENT '帐号状态（0正常 1停用）',
  `login_ip` varchar(16) DEFAULT '' COMMENT '最后登陆IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登陆时间',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_admin_account_uindex` (`account`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='管理员信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_admin`
--

LOCK TABLES `t_admin` WRITE;
/*!40000 ALTER TABLE `t_admin` DISABLE KEYS */;
INSERT INTO `t_admin` VALUES (1,3,'admin','超级管理员','iteaj@outlook.com','18059222381','男','/img/upload/avatar/avatar_1.jpg?t=1594538208542','a66abb5684c45962d887564f08346e8d','正常','127.0.0.1','2018-03-16 11:33:00','做回自己, 活出未来','admin','2018-03-15 11:33:00','admin','2018-03-15 03:33:00'),(5,3,'test','演示用户','test@outlook.com','15888888888','未知',NULL,'098f6bcd4621d373cade4e832627b4f6','正常',NULL,NULL,NULL,'',NULL,'',NULL);
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
INSERT INTO `t_admin_role` VALUES (5,4);
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
INSERT INTO `t_config` VALUES (1,'系统名称','sys_name','厦门由创源科技','sys','系统里面显示的公司名','2019-12-04 07:25:31',NULL),(3,'菜单展开模式','expand_mode','single','sys','可选值：single、multi','2019-12-09 13:51:05',NULL),(4,'主页地址','main_url',' /core/main','sys','没设置地址则不显示工作台且不能关闭','2020-04-23 15:18:20',NULL),(5,'主页名称','main_name','主页','sys','任务栏上面显示的文本','2020-04-23 15:23:10',NULL);
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
  `label` varchar(100) DEFAULT '' COMMENT '字典标签',
  `value` varchar(100) DEFAULT '' COMMENT '字典键值',
  `type` char(32) NOT NULL DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) DEFAULT NULL COMMENT '表格回显样式',
  `iz_default` tinyint(1) DEFAULT '0' COMMENT '是否默认（Y是 N否）',
  `status` enum('正常','禁用') NOT NULL DEFAULT '正常' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8 COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dict_data`
--

LOCK TABLES `t_dict_data` WRITE;
/*!40000 ALTER TABLE `t_dict_data` DISABLE KEYS */;
INSERT INTO `t_dict_data` VALUES (1,1,'主栏','T','opera_position',NULL,NULL,0,'正常',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(2,2,'主栏(更多)','MT','opera_position',NULL,NULL,0,'正常',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(6,0,'主栏(更多)','MM','function-position',NULL,NULL,0,'正常','主栏(更多)','',NULL,'',NULL),(11,0,'正常','status','common_status',NULL,NULL,0,'正常','正常状态0','',NULL,'',NULL),(12,0,'禁用','disabled','common_status',NULL,NULL,0,'正常','禁用状态','',NULL,'',NULL),(13,0,'主栏','M','function-position',NULL,NULL,0,'正常','主栏','',NULL,'',NULL),(14,0,'隐藏','hidden','common_status',NULL,NULL,0,'正常','隐藏状态','',NULL,'',NULL),(15,0,'xx','xx','test',NULL,NULL,0,'正常','xx','',NULL,'',NULL),(16,0,'男','male','sex',NULL,NULL,0,'正常',NULL,'',NULL,'',NULL),(17,0,'女','female','sex',NULL,NULL,0,'正常',NULL,'',NULL,'',NULL),(18,0,'管理模块','core','sys.module',NULL,NULL,0,'正常','系统管理核心模块(admin)','',NULL,'',NULL),(19,0,'开发模块','dev','sys.module',NULL,NULL,0,'正常','开发人员管理、开发、维护模块()','',NULL,'',NULL);
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
  `status` enum('正常','禁用') NOT NULL DEFAULT '正常' COMMENT '状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `type t_dict_type_type_uindex` (`type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='字典类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dict_type`
--

LOCK TABLES `t_dict_type` WRITE;
/*!40000 ALTER TABLE `t_dict_type` DISABLE KEYS */;
INSERT INTO `t_dict_type` VALUES (1,'common_status','状态','正常','通用状态','',NULL,'',NULL),(3,'sys.module','系统模块','正常','已经取消','','2020-04-27 09:44:57','',NULL);
/*!40000 ALTER TABLE `t_dict_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_gen_field`
--

DROP TABLE IF EXISTS `t_gen_field`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_gen_field` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) DEFAULT NULL COMMENT '字段名',
  `table_name` varchar(32) DEFAULT NULL COMMENT '表明',
  `property_name` varchar(32) DEFAULT NULL COMMENT '实体类属性名',
  `type` varchar(128) NOT NULL DEFAULT '' COMMENT '字段类型',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '字段标题',
  `ctype` varchar(16) DEFAULT 'text' COMMENT '组件类型',
  `dict_type` varchar(32) DEFAULT '' COMMENT '字典类型',
  `column_type` varchar(64) NOT NULL DEFAULT '' COMMENT '属性类型',
  `comment` varchar(128) DEFAULT NULL COMMENT '字段描述',
  `key_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否是主键',
  `validate` varchar(128) NOT NULL DEFAULT '' COMMENT '验证类型数组',
  `remark` varchar(128) DEFAULT NULL,
  `edit_flag` tinyint(1) NOT NULL DEFAULT '1',
  `search_flag` tinyint(1) DEFAULT '0' COMMENT '是否是搜索字段',
  `identity_flag` tinyint(1) NOT NULL DEFAULT '0' COMMENT '主键是否自增',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_gen_field_pk` (`table_name`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4143 DEFAULT CHARSET=utf8 COMMENT='代码生成器-字段信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_gen_field`
--

LOCK TABLES `t_gen_field` WRITE;
/*!40000 ALTER TABLE `t_gen_field` DISABLE KEYS */;
INSERT INTO `t_gen_field` VALUES (3758,'org_id','t_admin','orgId','bigint(20)','部门ID','number',NULL,'Long','部门ID',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3759,'account','t_admin','account','char(24)','登录账号','text',NULL,'String','登录账号',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3760,'name','t_admin','name','varchar(30)','用户名称','text',NULL,'String','用户名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3761,'email','t_admin','email','varchar(50)','用户邮箱','text',NULL,'String','用户邮箱',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3762,'phone','t_admin','phone','varchar(11)','手机号码','text',NULL,'String','手机号码',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3763,'sex','t_admin','sex','enum(\'男\',\'女\',\'未知\')','用户性别','text',NULL,'String','用户性别（0男 1女 2未知）',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3764,'avatar','t_admin','avatar','varchar(100)','头像路径','text',NULL,'String','头像路径',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3765,'password','t_admin','password','varchar(50)','密码','text',NULL,'String','密码',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3766,'status','t_admin','status','enum(\'正常\',\'禁用\')','帐号状态','text',NULL,'String','帐号状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3767,'login_ip','t_admin','loginIp','varchar(16)','最后登陆IP','text',NULL,'String','最后登陆IP',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3768,'login_date','t_admin','loginDate','datetime','最后登陆时间','date',NULL,'Date','最后登陆时间',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3769,'remark','t_admin','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3770,'create_by','t_admin','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3771,'update_by','t_admin','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3772,'id','t_admin','id','bigint(20)','用户ID','number',NULL,'Long','用户ID',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3773,'create_time','t_admin','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3774,'update_time','t_admin','updateTime','timestamp','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3775,'aid','t_admin_role','aid','bigint(20)','管理员id','number',NULL,'Long','管理员id',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3776,'rid','t_admin_role','rid','bigint(20)','角色id','number',NULL,'Long','角色id',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3777,'name','t_config','name','varchar(64)','参数名称','text',NULL,'String','参数名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3778,'label','t_config','label','varchar(32)','系统配置key','text',NULL,'String','系统配置key',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3779,'value','t_config','value','varchar(128)','参数键值','text',NULL,'String','参数键值',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3780,'type','t_config','type','enum(\'def\',\'sys\')','配置类型','text',NULL,'String','配置类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3781,'remark','t_config','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3782,'id','t_config','id','int(5)','参数主键','number',NULL,'Integer','参数主键',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3783,'create_time','t_config','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3784,'update_time','t_config','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3785,'sort','t_dict_data','sort','int(4)','字典排序','number',NULL,'Integer','字典排序',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3786,'label','t_dict_data','label','varchar(100)','字典标签','text',NULL,'String','字典标签',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3787,'value','t_dict_data','value','varchar(100)','字典键值','text',NULL,'String','字典键值',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3788,'type','t_dict_data','type','char(32)','字典类型','text',NULL,'String','字典类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3789,'css_class','t_dict_data','cssClass','varchar(100)','样式属性','text',NULL,'String','样式属性（其他样式扩展）',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3790,'list_class','t_dict_data','listClass','varchar(100)','表格回显样式','text',NULL,'String','表格回显样式',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3791,'iz_default','t_dict_data','izDefault','tinyint(1)','是否默认','radio',NULL,'Boolean','是否默认（Y是 N否）',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3792,'status','t_dict_data','status','enum(\'正常\',\'禁用\')','状态','text',NULL,'String','状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3793,'remark','t_dict_data','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3794,'create_by','t_dict_data','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3795,'update_by','t_dict_data','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3796,'id','t_dict_data','id','bigint(20)','字典id','number',NULL,'Long','字典id',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3797,'create_time','t_dict_data','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3798,'update_time','t_dict_data','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3799,'type','t_dict_type','type','char(32)','字典类型','text',NULL,'String','字典类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3800,'name','t_dict_type','name','varchar(100)','字典名称','text',NULL,'String','字典名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3801,'status','t_dict_type','status','enum(\'正常\',\'禁用\')','状态','text',NULL,'String','状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3802,'remark','t_dict_type','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3803,'create_by','t_dict_type','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3804,'update_by','t_dict_type','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3805,'id','t_dict_type','id','bigint(20)','字典主键','number',NULL,'Long','字典主键',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3806,'create_time','t_dict_type','createTime','timestamp','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3807,'update_time','t_dict_type','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3808,'name','t_gen_field','name','varchar(32)','字段名','text',NULL,'String','字段名',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3809,'table_name','t_gen_field','tableName','varchar(32)','表明','text',NULL,'String','表明',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3810,'property_name','t_gen_field','propertyName','varchar(32)','实体类属性名','text',NULL,'String','实体类属性名',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3811,'type','t_gen_field','type','varchar(64)','字段类型','text',NULL,'String','字段类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3812,'title','t_gen_field','title','varchar(64)','字段标题','text',NULL,'String','字段标题',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3813,'ctype','t_gen_field','ctype','varchar(16)','组件类型','text',NULL,'String','组件类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3814,'dict_type','t_gen_field','dictType','varchar(32)','字典类型','text',NULL,'String','字典类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3815,'column_type','t_gen_field','columnType','varchar(64)','属性类型','text',NULL,'String','属性类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3816,'comment','t_gen_field','comment','varchar(128)','字段描述','text',NULL,'String','字段描述',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3817,'key_flag','t_gen_field','keyFlag','tinyint(1)','是否是主键','radio',NULL,'Boolean','是否是主键',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3818,'validate','t_gen_field','validate','varchar(128)','验证类型数组','text',NULL,'String','验证类型数组',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3819,'remark','t_gen_field','remark','varchar(128)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3820,'edit_flag','t_gen_field','editFlag','tinyint(1)','','radio',NULL,'Boolean','',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3821,'search_flag','t_gen_field','searchFlag','tinyint(1)','是否是搜索字段','radio',NULL,'Boolean','是否是搜索字段',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3822,'identity_flag','t_gen_field','identityFlag','tinyint(1)','主键是否自增','radio',NULL,'Boolean','主键是否自增',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3823,'id','t_gen_field','id','int(11)','','number',NULL,'Integer','',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3824,'create_time','t_gen_field','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3825,'update_time','t_gen_field','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3826,'name','t_gen_module','name','varchar(64)','模块名称','text',NULL,'String','模块名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3827,'msn','t_gen_module','msn','varchar(10)','模块标识','text',NULL,'String','模块标识, 此标识将作为url的一部分',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3828,'remark','t_gen_module','remark','varchar(128)','模块说明','text',NULL,'String','模块说明',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3829,'project','t_gen_module','project','varchar(12)','项目名称','text',NULL,'String','项目名称, dao层project-dao, service层project-service\nview层project-view',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3830,'table_prefix','t_gen_module','tablePrefix','char(8)','表前缀','text',NULL,'String','表前缀',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3831,'id','t_gen_module','id','int(11)','','number',NULL,'Integer','',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3832,'create_time','t_gen_module','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3833,'update_time','t_gen_module','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3834,'name','t_gen_table','name','varchar(32)','表明','text',NULL,'String','表明',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3835,'status','t_gen_table','status','enum(\'已生成\',\'未生成\')','禁用的表将不会生成代码','text',NULL,'String','禁用的表将不会生成代码',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3836,'disabled','t_gen_table','disabled','tinyint(1)','是否禁用','radio',NULL,'Boolean','是否禁用',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3837,'cover','t_gen_table','cover','tinyint(1)','是否覆盖已存在的文件','radio',NULL,'Boolean','是否覆盖已存在的文件',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3838,'view_type','t_gen_table','viewType','varchar(32)','视图类型','text',NULL,'String','视图类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3839,'entity_name','t_gen_table','entityName','varchar(32)','表对应的实体名','text',NULL,'String','表对应的实体名',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3840,'prefix','t_gen_table','prefix','char(6)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3841,'comment','t_gen_table','comment','varchar(128)','表说明','text',NULL,'String','表说明',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3842,'service_name','t_gen_table','serviceName','varchar(32)','业务接口名称','text',NULL,'String','业务接口名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3843,'mapper_name','t_gen_table','mapperName','varchar(32)','到接口名称','text',NULL,'String','到接口名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3844,'service_impl_name','t_gen_table','serviceImplName','varchar(32)','业务接口实现名称','text',NULL,'String','业务接口实现名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3845,'controller_name','t_gen_table','controllerName','varchar(32)','视图层控制器名称','text',NULL,'String','视图层控制器名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3846,'xml_name','t_gen_table','xmlName','varchar(32)','mapper配置文件名称','text',NULL,'String','mapper配置文件名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3847,'remark','t_gen_table','remark','varchar(128)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3848,'id','t_gen_table','id','int(11)','','number',NULL,'Integer','',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3849,'create_time','t_gen_table','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3850,'update_time','t_gen_table','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3851,'msn','t_menu','msn','char(32)','模块编码','text',NULL,'String','模块编码, 用来标识此权限属于哪个模块',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3852,'name','t_menu','name','varchar(50)','菜单名称','text',NULL,'String','菜单名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3853,'pid','t_menu','pid','bigint(20)','父菜单ID','number',NULL,'Long','父菜单ID',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3854,'sort','t_menu','sort','int(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3855,'url','t_menu','url','varchar(200)','请求地址','text',NULL,'String','请求地址',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3856,'type','t_menu','type','enum(\'A\',\'M\',\'G\',\'V\')','菜单类型:','text',NULL,'String','菜单类型: A.权限、M.菜单、V.视图、G.菜单组',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3857,'perm_type','t_menu','permType','char(16)','权限类型','text',NULL,'String','权限类型(View, Query, Add, Edit, Del, ...)',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3858,'log','t_menu','log','tinyint(1)','是否记录日志','radio',NULL,'Boolean','是否记录日志',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3859,'log_desc','t_menu','logDesc','varchar(64)','日志说明','text',NULL,'String','日志说明',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3860,'status','t_menu','status','enum(\'view\',\'hide\',\'disabled\')','菜单状态','text',NULL,'String','菜单状态（view-显示 hide-隐藏 disabled-禁用）',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3861,'perms','t_menu','perms','varchar(100)','权限标识','text',NULL,'String','权限标识',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3862,'icon','t_menu','icon','varchar(100)','菜单图标','text',NULL,'String','菜单图标',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3863,'position','t_menu','position','enum(\'M\',\'MM\',\'T\',\'TM\',\'AM\')','功能点位置','text',NULL,'String','功能点位置 M.主操作栏 MM.主操作栏更多 T.表操作 TM.表操作更多 AM. 主栏和表主栏',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3864,'remark','t_menu','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3865,'create_by','t_menu','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3866,'update_by','t_menu','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3867,'id','t_menu','id','bigint(20)','菜单ID','number',NULL,'Long','菜单ID',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3868,'create_time','t_menu','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3869,'update_time','t_menu','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3879,'msg_id','t_msg_accept','msgId','bigint(20)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3880,'user_id','t_msg_accept','userId','int(11)','消息接收者用户','number',NULL,'Integer','消息接收者用户',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3881,'have_read','t_msg_accept','haveRead','tinyint(1)','消息是否已读','radio',NULL,'Boolean','消息是否已读',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3882,'read_time','t_msg_accept','readTime','datetime','已读时间','date',NULL,'Date','已读时间',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3883,'id','t_msg_accept','id','bigint(20)','','number',NULL,'Long','',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3884,'create_time','t_msg_accept','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3885,'update_time','t_msg_accept','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3886,'type','t_msg_send','type','enum(\'公告\',\'消息\',\'其他\')','消息类型','text',NULL,'String','消息类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3887,'title','t_msg_send','title','varchar(128)','标题','text',NULL,'String','标题',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3888,'user_id','t_msg_send','userId','int(11)','发送者','number',NULL,'Integer','发送者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3889,'user_name','t_msg_send','userName','varchar(64)','发送人昵称','text',NULL,'String','发送人昵称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3890,'priority','t_msg_send','priority','enum(\'normal\',\'high\',\'special\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3891,'content','t_msg_send','content','text','消息内容','text',NULL,'String','消息内容',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3892,'id','t_msg_send','id','bigint(20)','','number',NULL,'Long','',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3893,'create_time','t_msg_send','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3894,'update_time','t_msg_send','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3895,'session_id','t_online_user','sessionId','varchar(64)','会话编号','text',NULL,'String','会话编号',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3896,'account','t_online_user','account','varchar(32)','当前在线的用户','text',NULL,'String','当前在线的用户',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3897,'user_nick','t_online_user','userNick','varchar(64)','用户昵称','text',NULL,'String','用户昵称',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3898,'access_ip','t_online_user','accessIp','char(16)','登入ip','text',NULL,'String','登入ip',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3899,'browse','t_online_user','browse','varchar(32)','登录时使用的浏览器','text',NULL,'String','登录时使用的浏览器',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3900,'os','t_online_user','os','varchar(32)','使用的操作系统','text',NULL,'String','使用的操作系统',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3901,'status','t_online_user','status','enum(\'Guest\',\'On\',\'Off\')','用户状态','text',NULL,'String','用户状态(Def. 默认 ,on. 在线, off. 离线)',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3902,'app_type','t_online_user','appType','enum(\'App\',\'WebApp\',\'Web\',\'Unknown\')','应用类型','text',NULL,'String','应用类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3903,'location','t_online_user','location','varchar(64)','访问的本地位置','text',NULL,'String','访问的本地位置',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3904,'login_time','t_online_user','loginTime','datetime','登录时间','date',NULL,'Date','登录时间',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3905,'expire_time','t_online_user','expireTime','int(11)','超时时长','number',NULL,'Integer','超时时长, 从访问到离开的时间， 单位分钟',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3906,'id','t_online_user','id','int(11)','','number',NULL,'Integer','',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3907,'create_time','t_online_user','createTime','datetime','系统访问时间','date',NULL,'Date','系统访问时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3908,'update_time','t_online_user','updateTime','datetime','系统最后访问时间','date',NULL,'Date','系统最后访问时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3909,'url','t_opera_log','url','varchar(255)','请求URL','text',NULL,'String','请求URL',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3910,'ip','t_opera_log','ip','varchar(50)','主机地址','text',NULL,'String','主机地址',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3911,'msn','t_opera_log','msn','varchar(16)','模块编号','text',NULL,'String','模块编号',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3912,'title','t_opera_log','title','varchar(64)','日志标题','text',NULL,'String','日志标题',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3913,'type','t_opera_log','type','varchar(16)','功能类型','text',NULL,'String','功能类型',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3914,'millis','t_opera_log','millis','int(11)','操作毫秒数','number',NULL,'Integer','操作毫秒数',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3915,'user_id','t_opera_log','userId','bigint(20)','操作人员','number',NULL,'Long','操作人员',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3916,'user_name','t_opera_log','userName','varchar(50)','操作人员','text',NULL,'String','操作人员',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3917,'short_name','t_opera_log','shortName','varchar(64)','简写的class','text',NULL,'String','简写的class.method()',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3918,'status','t_opera_log','status','char(12)','操作状态','text',NULL,'String','操作状态',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3919,'err_msg','t_opera_log','errMsg','varchar(2000)','异常消息','text',NULL,'String','异常消息',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3920,'id','t_opera_log','id','bigint(20)','日志主键','number',NULL,'Long','日志主键',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3921,'create_time','t_opera_log','createTime','datetime','操作时间','date',NULL,'Date','操作时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3922,'update_time','t_opera_log','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3923,'pid','t_organize','pid','bigint(20)','父级id','number',NULL,'Long','父级id',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3924,'level','t_organize','level','int(3)','属于第几级','number',NULL,'Integer','属于第几级',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3925,'name','t_organize','name','varchar(64)','机构名称','text',NULL,'String','机构名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3926,'sort','t_organize','sort','int(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3927,'type','t_organize','type','enum(\'Org\',\'Company\',\'Dept\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3928,'leader','t_organize','leader','varchar(20)','负责人','text',NULL,'String','负责人',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3929,'phone','t_organize','phone','varchar(11)','联系电话','text',NULL,'String','联系电话',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3930,'email','t_organize','email','varchar(50)','邮箱','text',NULL,'String','邮箱',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3931,'status','t_organize','status','enum(\'正常\',\'禁用\')','部门状态','text',NULL,'String','部门状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3932,'create_by','t_organize','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3933,'update_by','t_organize','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3934,'id','t_organize','id','bigint(20)','部门id','number',NULL,'Long','部门id',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3935,'create_time','t_organize','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3936,'update_time','t_organize','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3937,'name','t_role','name','varchar(30)','角色名称','text',NULL,'String','角色名称',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3938,'sort','t_role','sort','int(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3939,'scope','t_role','scope','char(1)','数据范围','text',NULL,'String','数据范围（1：全部数据权限 2：自定数据权限）',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3940,'status','t_role','status','enum(\'正常\',\'禁用\')','角色状态','text',NULL,'String','角色状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3941,'remark','t_role','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3942,'create_by','t_role','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3943,'update_by','t_role','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3944,'id','t_role','id','bigint(20)','角色ID','number',NULL,'Long','角色ID',1,'',NULL,0,0,1,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3945,'create_time','t_role','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3946,'update_time','t_role','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3947,'role_id','t_role_dept','roleId','bigint(20)','角色ID','number',NULL,'Long','角色ID',1,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3948,'dept_id','t_role_dept','deptId','bigint(20)','部门ID','number',NULL,'Long','部门ID',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3949,'rid','t_role_menu','rid','bigint(20)','角色ID','number',NULL,'Long','角色ID',1,'',NULL,0,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3950,'mid','t_role_menu','mid','bigint(20)','菜单ID','number',NULL,'Long','菜单ID',0,'',NULL,1,0,0,'2020-07-07 20:56:54','2020-07-07 12:56:54'),(3951,'SCHED_NAME','qrtz_blob_triggers','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3952,'TRIGGER_NAME','qrtz_blob_triggers','triggerName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3953,'TRIGGER_GROUP','qrtz_blob_triggers','triggerGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3954,'BLOB_DATA','qrtz_blob_triggers','blobData','blob','','text',NULL,'Blob','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3955,'SCHED_NAME','qrtz_calendars','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3956,'CALENDAR_NAME','qrtz_calendars','calendarName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3957,'CALENDAR','qrtz_calendars','calendar','blob','','text',NULL,'Blob','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3958,'SCHED_NAME','qrtz_cron_triggers','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3959,'TRIGGER_NAME','qrtz_cron_triggers','triggerName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3960,'TRIGGER_GROUP','qrtz_cron_triggers','triggerGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3961,'CRON_EXPRESSION','qrtz_cron_triggers','cronExpression','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3962,'TIME_ZONE_ID','qrtz_cron_triggers','timeZoneId','varchar(80)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3963,'SCHED_NAME','qrtz_fired_triggers','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3964,'ENTRY_ID','qrtz_fired_triggers','entryId','varchar(95)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3965,'TRIGGER_NAME','qrtz_fired_triggers','triggerName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3966,'TRIGGER_GROUP','qrtz_fired_triggers','triggerGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3967,'INSTANCE_NAME','qrtz_fired_triggers','instanceName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3968,'FIRED_TIME','qrtz_fired_triggers','firedTime','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3969,'SCHED_TIME','qrtz_fired_triggers','schedTime','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3970,'PRIORITY','qrtz_fired_triggers','priority','int(11)','','number',NULL,'Integer','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3971,'STATE','qrtz_fired_triggers','state','varchar(16)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3972,'JOB_NAME','qrtz_fired_triggers','jobName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3973,'JOB_GROUP','qrtz_fired_triggers','jobGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3974,'IS_NONCONCURRENT','qrtz_fired_triggers','isNonconcurrent','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3975,'REQUESTS_RECOVERY','qrtz_fired_triggers','requestsRecovery','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3976,'SCHED_NAME','qrtz_job_details','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3977,'JOB_NAME','qrtz_job_details','jobName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3978,'JOB_GROUP','qrtz_job_details','jobGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3979,'DESCRIPTION','qrtz_job_details','description','varchar(250)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3980,'JOB_CLASS_NAME','qrtz_job_details','jobClassName','varchar(250)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3981,'IS_DURABLE','qrtz_job_details','isDurable','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3982,'IS_NONCONCURRENT','qrtz_job_details','isNonconcurrent','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3983,'IS_UPDATE_DATA','qrtz_job_details','isUpdateData','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3984,'REQUESTS_RECOVERY','qrtz_job_details','requestsRecovery','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3985,'JOB_DATA','qrtz_job_details','jobData','blob','','text',NULL,'Blob','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3986,'SCHED_NAME','qrtz_scheduler_state','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3987,'INSTANCE_NAME','qrtz_scheduler_state','instanceName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3988,'LAST_CHECKIN_TIME','qrtz_scheduler_state','lastCheckinTime','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3989,'CHECKIN_INTERVAL','qrtz_scheduler_state','checkinInterval','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3990,'SCHED_NAME','qrtz_simple_triggers','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3991,'TRIGGER_NAME','qrtz_simple_triggers','triggerName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3992,'TRIGGER_GROUP','qrtz_simple_triggers','triggerGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3993,'REPEAT_COUNT','qrtz_simple_triggers','repeatCount','bigint(7)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3994,'REPEAT_INTERVAL','qrtz_simple_triggers','repeatInterval','bigint(12)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3995,'TIMES_TRIGGERED','qrtz_simple_triggers','timesTriggered','bigint(10)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3996,'SCHED_NAME','qrtz_simprop_triggers','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3997,'TRIGGER_NAME','qrtz_simprop_triggers','triggerName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3998,'TRIGGER_GROUP','qrtz_simprop_triggers','triggerGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(3999,'STR_PROP_1','qrtz_simprop_triggers','strProp1','varchar(512)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4000,'STR_PROP_2','qrtz_simprop_triggers','strProp2','varchar(512)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4001,'STR_PROP_3','qrtz_simprop_triggers','strProp3','varchar(512)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4002,'INT_PROP_1','qrtz_simprop_triggers','intProp1','int(11)','','number',NULL,'Integer','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4003,'INT_PROP_2','qrtz_simprop_triggers','intProp2','int(11)','','number',NULL,'Integer','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4004,'LONG_PROP_1','qrtz_simprop_triggers','longProp1','bigint(20)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4005,'LONG_PROP_2','qrtz_simprop_triggers','longProp2','bigint(20)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4006,'DEC_PROP_1','qrtz_simprop_triggers','decProp1','decimal(13,4)','','number',NULL,'BigDecimal','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4007,'DEC_PROP_2','qrtz_simprop_triggers','decProp2','decimal(13,4)','','number',NULL,'BigDecimal','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4008,'BOOL_PROP_1','qrtz_simprop_triggers','boolProp1','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4009,'BOOL_PROP_2','qrtz_simprop_triggers','boolProp2','varchar(1)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:06:38','2020-07-26 03:06:38'),(4026,'SCHED_NAME','qrtz_locks','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-07-26 11:18:12','2020-07-26 03:18:12'),(4027,'LOCK_NAME','qrtz_locks','lockName','varchar(40)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-07-26 11:18:12','2020-07-26 03:18:12'),(4046,'SCHED_NAME','qrtz_triggers','schedName','varchar(120)','','text',NULL,'String','',1,'',NULL,0,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4047,'TRIGGER_NAME','qrtz_triggers','triggerName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4048,'TRIGGER_GROUP','qrtz_triggers','triggerGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4049,'JOB_NAME','qrtz_triggers','jobName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4050,'JOB_GROUP','qrtz_triggers','jobGroup','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4051,'DESCRIPTION','qrtz_triggers','description','varchar(250)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4052,'NEXT_FIRE_TIME','qrtz_triggers','nextFireTime','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4053,'PREV_FIRE_TIME','qrtz_triggers','prevFireTime','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4054,'PRIORITY','qrtz_triggers','priority','int(11)','','number',NULL,'Integer','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4055,'TRIGGER_STATE','qrtz_triggers','triggerState','varchar(16)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4056,'TRIGGER_TYPE','qrtz_triggers','triggerType','varchar(8)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4057,'START_TIME','qrtz_triggers','startTime','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4058,'END_TIME','qrtz_triggers','endTime','bigint(13)','','number',NULL,'Long','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4059,'CALENDAR_NAME','qrtz_triggers','calendarName','varchar(200)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4060,'MISFIRE_INSTR','qrtz_triggers','misfireInstr','smallint(2)','','number',NULL,'Integer','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4061,'JOB_DATA','qrtz_triggers','jobData','blob','','text',NULL,'Blob','',0,'',NULL,1,0,0,'2020-09-06 11:12:23','2020-09-06 03:12:23'),(4107,'name','t_menu_view','name','char(32)','视图名称','text',NULL,'String','视图名称',0,'',NULL,1,1,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4108,'type','t_menu_view','type','enum(\'默认\',\'菜单组\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4109,'icon','t_menu_view','icon','char(32)','图标','text',NULL,'String','图标',0,'',NULL,1,0,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4110,'sort','t_menu_view','sort','tinyint(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4111,'status','t_menu_view','status','enum(\'正常\',\'禁用\')','','text',NULL,'String','',0,'',NULL,1,1,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4112,'menus','t_menu_view','menus','varchar(128)','菜单列表','text',NULL,'String','菜单列表',0,'',NULL,1,0,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4113,'id','t_menu_view','id','bigint(20)','','number',NULL,'Long','',1,'',NULL,0,0,1,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4114,'create_time','t_menu_view','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4115,'update_time','t_menu_view','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-09-06 11:41:11','2020-09-06 03:41:11'),(4125,'name','menu_view','name','char(32)','视图名称','text',NULL,'String','视图名称',0,'',NULL,1,1,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4126,'type','menu_view','type','enum(\'默认\',\'菜单组\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4127,'icon','menu_view','icon','char(32)','图标','text',NULL,'String','图标',0,'',NULL,1,0,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4128,'sort','menu_view','sort','tinyint(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4129,'status','menu_view','status','enum(\'正常\',\'禁用\')','','text',NULL,'String','',0,'',NULL,1,1,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4130,'menus','menu_view','menus','varchar(128)','菜单列表','text',NULL,'String','菜单列表',0,'',NULL,1,0,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4131,'id','menu_view','id','bigint(20)','','number',NULL,'Long','',1,'',NULL,0,0,1,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4132,'create_time','menu_view','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4133,'update_time','menu_view','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-09-06 12:01:27','2020-09-06 04:01:27'),(4134,'name','t_gen_online','name','varchar(64)','功能名称','text',NULL,'String','功能名称',0,'',NULL,1,1,0,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4135,'desc','t_gen_online','desc','varchar(256)','功能说明','text',NULL,'String','功能说明',0,'',NULL,1,0,0,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4136,'html','t_gen_online','html','text','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4137,'metas','t_gen_online','metas','json','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4138,'permMetas','t_gen_online','permMetas','json','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4139,'view_config','t_gen_online','viewConfig','json','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4140,'id','t_gen_online','id','int(11)','','number',NULL,'Integer','',1,'',NULL,0,0,1,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4141,'create_time','t_gen_online','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-09-13 10:24:27','2020-09-13 02:24:27'),(4142,'update_time','t_gen_online','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-09-13 10:24:27','2020-09-13 02:24:27');
/*!40000 ALTER TABLE `t_gen_field` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_gen_module`
--

DROP TABLE IF EXISTS `t_gen_module`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_gen_module` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) DEFAULT NULL COMMENT '模块名称',
  `msn` varchar(10) NOT NULL COMMENT '模块标识, 此标识将作为url的一部分',
  `remark` varchar(128) NOT NULL DEFAULT '' COMMENT '模块说明',
  `project` varchar(12) DEFAULT NULL COMMENT '项目名称, dao层project-dao, service层project-service\nview层project-view',
  `table_prefix` char(8) DEFAULT NULL COMMENT '表前缀',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_gen_module_sn_uindex` (`msn`),
  UNIQUE KEY `t_gen_module_project_uindex` (`project`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='代码生成模块';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_gen_module`
--

LOCK TABLES `t_gen_module` WRITE;
/*!40000 ALTER TABLE `t_gen_module` DISABLE KEYS */;
INSERT INTO `t_gen_module` VALUES (3,'核心模块','core','','admin','t_','2020-04-30 19:15:03','2020-04-30 11:15:03'),(4,'开发模块','dev','','develop','t_','2020-04-30 19:15:58','2020-04-30 11:15:58'),(7,'人员模块','renyuan','','人员模块','t_','2020-05-15 22:06:49','2020-05-15 14:06:49');
/*!40000 ALTER TABLE `t_gen_module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_gen_online`
--

DROP TABLE IF EXISTS `t_gen_online`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_gen_online` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '功能名称',
  `remark` varchar(256) NOT NULL DEFAULT '' COMMENT '功能说明',
  `html` text,
  `metas` json DEFAULT NULL,
  `perm_metas` json DEFAULT NULL,
  `view_config` json DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_gen_online_name_uindex` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='在线生成器';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_gen_online`
--

LOCK TABLES `t_gen_online` WRITE;
/*!40000 ALTER TABLE `t_gen_online` DISABLE KEYS */;
INSERT INTO `t_gen_online` VALUES (1,'user','用户管理','<html lang=\"zh-CN\">\n    <head>\n        <title></title>\n    </head>\n    <body>\n        <div id=\"app\">\n            <ivz-basic-view :metas=\"metas\" :search-metas=\"searchMetas\" :config=\"config\"></ivz-basic-view>\n            <ivz-modal-form ref=\"ivo1599966483322Ref\" :width=\"ivo1599966483322.width\" :title=\"ivo1599966483322.title\" :save-metas=\"{}\" :metas=\"ivo1599966483322.metas\" :config=\"ivo1599966483322.config\"></ivz-modal-form>\r\n			\n        </div>\n    </body>\n    <script type=\"text/javascript\">\n        let vue = new Vue({\n            el: \"#app\",\n            data: function() {\n                return {\n                    metas: [\r						{field: \"ivo1599966485216\", title: \"单行文本\", type: \"text\"},\r					],\n                    searchMetas: [\r					],\n                    config: {\n						form: {\n							layout: \"horizontal\",\n							column: 1,\n							addTitle: null,\n							editTitle: null,\n							labelCol: {\n								span: 6,\n							},\n							wrapperCol: {\n								span: 14,\n							},\n						},\n						table: {\n							rowKey: \"id\",\n							bordered: true,\n							fixedHeight: true,\n							pagination: {\n							},\n						},\n						detail: {\n						},\n						search: {\n						},\n					},\n                    ivo1599966483322:{\n                        width: 520,\n                        title: \'自定义功能\',\n                        metas: [\n                            \n                        ],\n                        config:  {\n							form: {\n								column: 1,\n								labelCol: {\n									span: 6,\n								},\n								wrapperCol: {\n									span: 15,\n								},\n							},\n							keyboard: undefined,\n							centered: false,\n							closable: true,\n							maskClosable: true,\n							bodyStyle: {\n								height: \"260px\",\n							},\n						}\n                    },\n					\n                }\n            },\n            created: function() {\n                this.$page.setActionMeta(\'ivo1599966483322\', {\n                    callBack: function (row) {\n                        return new Promise(function (resolve, reject) {\n                            vue.$refs[\'ivo1599966483322Ref\'].open(row);\n                        })\n                    }\n                });\r\n				\n            },\n            methods: {\n\n            }\n        });\n    </script>\n</html>\n        ','[{\"id\": \"ivo1599966485216\", \"icon\": \"iz-icon-input\", \"name\": \"单行文本\", \"type\": \"text\", \"model\": {\"id\": \"ivo1599966485216\", \"name\": \"user_name\", \"span\": 0, \"type\": \"text\", \"clear\": false, \"extra\": \"\", \"index\": false, \"label\": \"用户名\", \"rules\": [], \"width\": null, \"isForm\": true, \"unique\": true, \"comment\": \"用户名\", \"isTable\": true, \"disabled\": false, \"isDetail\": true, \"isSearch\": false, \"labelCol\": null, \"readonly\": false, \"required\": false, \"validate\": null, \"activeKey\": [\"tableConfig\", \"formConfig\"], \"fieldType\": \"VARCHAR\", \"fieldView\": [\"form\", \"table\", \"detail\"], \"inputType\": \"TEXT\", \"otherForm\": [\"clear\"], \"otherType\": [\"unique\"], \"typeLength\": \"\", \"wrapperCol\": null, \"placeholder\": \"\", \"validateValue\": \"\", \"ivo1599966485216\": null}, \"title\": \"\"}, {\"id\": \"ivo1599974842650\", \"icon\": \"iz-icon-number\", \"name\": \"数值\", \"type\": \"number\", \"model\": {\"id\": \"ivo1599974842650\", \"max\": 0, \"min\": 0, \"name\": \"pwd\", \"span\": 0, \"step\": null, \"type\": \"number\", \"clear\": false, \"extra\": \"\", \"index\": false, \"label\": \"密码\", \"rules\": [], \"width\": null, \"isForm\": true, \"unique\": false, \"comment\": \"密码\", \"isTable\": true, \"disabled\": false, \"isDetail\": true, \"isSearch\": false, \"labelCol\": null, \"readonly\": false, \"required\": false, \"validate\": null, \"activeKey\": [\"tableConfig\", \"formConfig\"], \"fieldType\": \"INT\", \"fieldView\": [\"form\", \"table\", \"detail\"], \"otherForm\": [\"clear\"], \"otherType\": [], \"typeLength\": \"\", \"wrapperCol\": null, \"placeholder\": \"\", \"validateValue\": \"\", \"ivo1599974842650\": null}, \"title\": \"\"}, {\"id\": \"ivo1599974846232\", \"icon\": \"iz-icon-select\", \"name\": \"下拉框\", \"type\": \"select\", \"model\": {\"id\": \"ivo1599974846232\", \"mode\": \"default\", \"name\": \"select\", \"span\": 0, \"type\": \"select\", \"clear\": false, \"extra\": \"\", \"index\": false, \"label\": \"下拉框\", \"rules\": [], \"width\": null, \"isForm\": true, \"unique\": false, \"comment\": \"\", \"isTable\": true, \"options\": [{\"label\": \"demo\", \"value\": \"demo\"}], \"dataType\": \"cus\", \"disabled\": false, \"isDetail\": true, \"isSearch\": false, \"labelCol\": null, \"readonly\": false, \"required\": false, \"urlValue\": null, \"validate\": null, \"activeKey\": [\"tableConfig\", \"formConfig\"], \"dictValue\": [], \"fieldType\": \"VARCHAR\", \"fieldView\": [\"form\", \"table\", \"detail\"], \"otherForm\": [\"clear\"], \"otherType\": [], \"labelField\": \"label\", \"typeLength\": \"\", \"valueField\": \"value\", \"wrapperCol\": null, \"placeholder\": \"\", \"validateValue\": \"\", \"ivo1599974846232\": null}, \"title\": \"\"}, {\"id\": \"ivo1599975027392\", \"icon\": \"iz-icon-group\", \"name\": \"分组\", \"type\": \"group\", \"model\": {\"id\": \"ivo1599975027392\", \"desc\": \"\", \"name\": \"\", \"type\": \"group\", \"field\": \"\", \"activeKey\": [\"basicConfig\"], \"tableSpan\": false}, \"title\": \"\", \"children\": [{\"id\": \"ivo1599975029863\", \"icon\": \"iz-icon-input\", \"name\": \"单行文本\", \"type\": \"text\", \"model\": {\"id\": \"ivo1599975029863\", \"name\": \"\", \"span\": 0, \"type\": \"text\", \"clear\": false, \"extra\": \"\", \"index\": false, \"label\": \"单行文本\", \"rules\": [], \"width\": null, \"isForm\": true, \"unique\": false, \"comment\": \"\", \"isTable\": true, \"disabled\": false, \"isDetail\": true, \"isSearch\": false, \"labelCol\": null, \"readonly\": false, \"required\": false, \"validate\": null, \"activeKey\": [\"tableConfig\", \"formConfig\"], \"fieldType\": \"VARCHAR\", \"fieldView\": [\"form\", \"table\", \"detail\"], \"inputType\": \"TEXT\", \"otherForm\": [\"clear\"], \"otherType\": [], \"typeLength\": \"\", \"wrapperCol\": null, \"placeholder\": \"\", \"validateValue\": \"\", \"ivo1599975029863\": null}, \"title\": \"\"}]}]','[{\"id\": \"ivo1599968220309\", \"icon\": \"iz-icon-modal\", \"name\": \"模态框\", \"type\": \"modal\", \"metas\": [{\"id\": \"ivo1599968222337\", \"icon\": \"iz-icon-input\", \"name\": \"单行文本\", \"type\": \"text\", \"model\": {\"id\": \"ivo1599968222337\", \"name\": \"pwd\", \"span\": 24, \"type\": \"text\", \"clear\": true, \"extra\": \"\", \"index\": false, \"label\": \"密码\", \"rules\": [{\"message\": \"必填\", \"required\": true}], \"width\": null, \"isForm\": true, \"unique\": false, \"comment\": \"\", \"isTable\": true, \"disabled\": false, \"isDetail\": true, \"isSearch\": false, \"labelCol\": null, \"readonly\": false, \"required\": true, \"validate\": \"phone\", \"activeKey\": [\"tableConfig\", \"formConfig\"], \"fieldType\": \"VARCHAR\", \"fieldView\": [\"form\", \"table\", \"detail\"], \"inputType\": \"TEXT\", \"otherForm\": [\"required\", \"clear\"], \"otherType\": [], \"typeLength\": \"\", \"wrapperCol\": null, \"placeholder\": \"\", \"validateValue\": \"\", \"ivo1599968222337\": null}, \"title\": \"\"}, {\"id\": \"ivo1599974808121\", \"icon\": \"iz-icon-date\", \"name\": \"日期选择器\", \"type\": \"date\", \"model\": {\"id\": \"ivo1599974808121\", \"name\": \"\", \"show\": [], \"span\": 24, \"type\": \"date\", \"clear\": false, \"extra\": \"\", \"index\": false, \"label\": \"日期选择器\", \"rules\": [], \"width\": null, \"format\": \"YYYY-MM-DD HH:mm:ss\", \"isForm\": true, \"unique\": false, \"comment\": \"\", \"isTable\": true, \"disabled\": false, \"isDetail\": true, \"isSearch\": false, \"labelCol\": null, \"readonly\": false, \"required\": false, \"showTime\": false, \"validate\": null, \"activeKey\": [\"tableConfig\", \"formConfig\"], \"fieldType\": \"DATE\", \"fieldView\": [\"form\", \"table\", \"detail\"], \"otherForm\": [\"clear\"], \"otherType\": [], \"showToday\": false, \"typeLength\": \"\", \"wrapperCol\": null, \"placeholder\": \"\", \"validateValue\": \"\", \"ivo1599974808121\": null}, \"title\": \"\"}], \"model\": {\"id\": \"ivo1599968220309\", \"url\": \"\", \"mask\": false, \"type\": \"modal\", \"label\": \"模态框\", \"title\": \"修改密码\", \"length\": [260, 520], \"permId\": \"pwd\", \"formCol\": [6, 16], \"centered\": false, \"closable\": true, \"labelCol\": {\"span\": 6}, \"position\": \"T\", \"permTitle\": \"修改密码\", \"wrapperCol\": {\"span\": 15}, \"maskClosable\": true}, \"title\": \"\"}]','{\"id\": 1599973764416, \"type\": \"IvzBasicView\", \"model\": {\"mask\": true, \"span\": 8, \"align\": \"right\", \"perms\": [], \"width\": 920, \"column\": 3, \"isPage\": true, \"layout\": \"horizontal\", \"menuId\": 1, \"comment\": \"用户管理\", \"formCol\": [6, 14], \"keyType\": \"bigint\", \"addTitle\": \"新增用户管理\", \"isBorder\": true, \"keyField\": \"id\", \"labelCol\": {\"span\": 6}, \"activeKey\": [\"tableConfig\", \"formConfig\"], \"editTitle\": \"修改用户管理\", \"placement\": \"right\", \"selection\": \"none\", \"tableName\": \"user\", \"tableView\": [\"bordered\", \"pagination\", \"fixedHeight\"], \"drawerRate\": 420, \"wrapperCol\": {\"span\": 14}, \"fixedHeight\": true, \"maskClosable\": true}}','2020-09-13 11:08:10','2020-09-13 06:06:41');
/*!40000 ALTER TABLE `t_gen_online` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_gen_table`
--

DROP TABLE IF EXISTS `t_gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_gen_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(32) NOT NULL COMMENT '表明',
  `status` enum('已生成','未生成') NOT NULL DEFAULT '未生成' COMMENT '禁用的表将不会生成代码',
  `disabled` tinyint(1) DEFAULT '0' COMMENT '是否禁用',
  `view_type` varchar(32) DEFAULT NULL COMMENT '视图类型',
  `entity_name` varchar(32) DEFAULT NULL COMMENT '表对应的实体名',
  `prefix` char(6) NOT NULL,
  `comment` varchar(128) NOT NULL DEFAULT '' COMMENT '表说明',
  `service_name` varchar(32) DEFAULT NULL COMMENT '业务接口名称',
  `mapper_name` varchar(32) DEFAULT NULL COMMENT '到接口名称',
  `service_impl_name` varchar(32) DEFAULT NULL COMMENT '业务接口实现名称',
  `controller_name` varchar(32) DEFAULT NULL COMMENT '视图层控制器名称',
  `xml_name` varchar(32) DEFAULT NULL COMMENT 'mapper配置文件名称',
  `remark` varchar(128) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=495 DEFAULT CHARSET=utf8 COMMENT='代码生成器表配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_gen_table`
--

LOCK TABLES `t_gen_table` WRITE;
/*!40000 ALTER TABLE `t_gen_table` DISABLE KEYS */;
INSERT INTO `t_gen_table` VALUES (455,'t_admin','已生成',0,'ivz-basic-view','Admin','an','管理员信息表','IAdminService','IAdminMapper','AdminServiceImpl','AdminController','AdminMapper',NULL,NULL,'2020-07-07 12:56:54'),(456,'t_admin_role','未生成',0,'ivz-drawer-view','AdminRole','are','管理员角色表','IAdminRoleService','IAdminRoleMapper','AdminRoleServiceImpl','AdminRoleController','AdminRoleMapper',NULL,NULL,'2020-07-07 12:56:54'),(457,'t_config','未生成',0,'ivz-basic-view','Config','cg','参数配置表','IConfigService','IConfigMapper','ConfigServiceImpl','ConfigController','ConfigMapper',NULL,NULL,'2020-07-07 12:56:54'),(458,'t_dict_data','未生成',0,'ivz-basic-view','DictData','dda','字典数据表','IDictDataService','IDictDataMapper','DictDataServiceImpl','DictDataController','DictDataMapper',NULL,NULL,'2020-07-07 12:56:54'),(459,'t_dict_type','未生成',0,'ivz-basic-view','DictType','dte','字典类型表','IDictTypeService','IDictTypeMapper','DictTypeServiceImpl','DictTypeController','DictTypeMapper',NULL,NULL,'2020-07-07 12:56:54'),(460,'t_gen_field','未生成',0,'ivz-basic-view','GenField','gfd','代码生成器-字段信息','IGenFieldService','IGenFieldMapper','GenFieldServiceImpl','GenFieldController','GenFieldMapper',NULL,NULL,'2020-07-07 12:56:54'),(461,'t_gen_module','未生成',0,'ivz-basic-view','GenModule','gme','代码生成模块','IGenModuleService','IGenModuleMapper','GenModuleServiceImpl','GenModuleController','GenModuleMapper',NULL,NULL,'2020-07-07 12:56:54'),(462,'t_gen_table','未生成',0,'ivz-basic-view','GenTable','gte','代码生成器表配置','IGenTableService','IGenTableMapper','GenTableServiceImpl','GenTableController','GenTableMapper',NULL,NULL,'2020-07-07 12:56:54'),(463,'t_menu','已生成',0,'ivz-basic-view','Menu','mu','菜单权限表','IMenuService','IMenuMapper','MenuServiceImpl','MenuController','MenuMapper',NULL,NULL,'2020-07-07 12:56:54'),(465,'t_msg_accept','已生成',0,'ivz-drawer-view','MsgAccept','mat','消息用户表','IMsgAcceptService','IMsgAcceptMapper','MsgAcceptServiceImpl','MsgAcceptController','MsgAcceptMapper',NULL,NULL,'2020-07-07 12:56:54'),(466,'t_msg_send','已生成',0,'ivz-basic-view','MsgSend','msd','消息','IMsgSendService','IMsgSendMapper','MsgSendServiceImpl','MsgSendController','MsgSendMapper',NULL,NULL,'2020-07-07 12:56:54'),(467,'t_online_user','未生成',0,'ivz-basic-view','OnlineUser','our','在线用户','IOnlineUserService','IOnlineUserMapper','OnlineUserServiceImpl','OnlineUserController','OnlineUserMapper',NULL,NULL,'2020-07-07 12:56:54'),(468,'t_opera_log','未生成',0,'ivz-basic-view','OperaLog','olg','操作日志记录','IOperaLogService','IOperaLogMapper','OperaLogServiceImpl','OperaLogController','OperaLogMapper',NULL,NULL,'2020-07-07 12:56:54'),(469,'t_organize','未生成',0,'ivz-basic-view','Organize','oe','组织机构表','IOrganizeService','IOrganizeMapper','OrganizeServiceImpl','OrganizeController','OrganizeMapper',NULL,NULL,'2020-07-07 12:56:54'),(470,'t_role','未生成',0,'ivz-basic-view','Role','re','角色信息表','IRoleService','IRoleMapper','RoleServiceImpl','RoleController','RoleMapper',NULL,NULL,'2020-07-07 12:56:54'),(471,'t_role_dept','未生成',0,'ivz-drawer-view','RoleDept','rdt','角色和部门关联表','IRoleDeptService','IRoleDeptMapper','RoleDeptServiceImpl','RoleDeptController','RoleDeptMapper',NULL,NULL,'2020-07-07 12:56:54'),(472,'t_role_menu','未生成',0,'ivz-drawer-view','RoleMenu','rmu','角色和菜单关联表','IRoleMenuService','IRoleMenuMapper','RoleMenuServiceImpl','RoleMenuController','RoleMenuMapper',NULL,NULL,'2020-07-07 12:56:54'),(473,'qrtz_blob_triggers','已生成',0,'ivz-drawer-view','QrtzBlobTriggers','qbt','','IQrtzBlobTriggersService','IQrtzBlobTriggersMapper','QrtzBlobTriggersServiceImpl','QrtzBlobTriggersController','QrtzBlobTriggersMapper',NULL,NULL,'2020-07-26 03:06:38'),(474,'qrtz_calendars','已生成',0,'ivz-drawer-view','QrtzCalendars','qcs','','IQrtzCalendarsService','IQrtzCalendarsMapper','QrtzCalendarsServiceImpl','QrtzCalendarsController','QrtzCalendarsMapper',NULL,NULL,'2020-07-26 03:06:38'),(475,'qrtz_cron_triggers','已生成',0,'ivz-drawer-view','QrtzCronTriggers','qct','','IQrtzCronTriggersService','IQrtzCronTriggersMapper','QrtzCronTriggersServiceImpl','QrtzCronTriggersController','QrtzCronTriggersMapper',NULL,NULL,'2020-07-26 03:06:38'),(476,'qrtz_fired_triggers','已生成',0,'ivz-basic-view','QrtzFiredTriggers','qft','','IQrtzFiredTriggersService','IQrtzFiredTriggersMapper','QrtzFiredTriggersServiceImpl','QrtzFiredTriggersController','QrtzFiredTriggersMapper',NULL,NULL,'2020-07-26 03:06:38'),(477,'qrtz_job_details','已生成',0,'ivz-basic-view','QrtzJobDetails','qjd','','IQrtzJobDetailsService','IQrtzJobDetailsMapper','QrtzJobDetailsServiceImpl','QrtzJobDetailsController','QrtzJobDetailsMapper',NULL,NULL,'2020-07-26 03:06:38'),(478,'qrtz_scheduler_state','已生成',0,'ivz-drawer-view','QrtzSchedulerState','qss','','IQrtzSchedulerStateService','IQrtzSchedulerStateMapper','QrtzSchedulerStateServiceImpl','QrtzSchedulerStateController','QrtzSchedulerStateMapper',NULL,NULL,'2020-07-26 03:06:38'),(479,'qrtz_simple_triggers','已生成',0,'ivz-drawer-view','QrtzSimpleTriggers','qst','','IQrtzSimpleTriggersService','IQrtzSimpleTriggersMapper','QrtzSimpleTriggersServiceImpl','QrtzSimpleTriggersController','QrtzSimpleTriggersMapper',NULL,NULL,'2020-07-26 03:06:38'),(482,'qrtz_locks','未生成',0,'ivz-drawer-view','QrtzLocks','qls','','IQrtzLocksService','IQrtzLocksMapper','QrtzLocksServiceImpl','QrtzLocksController','QrtzLocksMapper',NULL,NULL,'2020-07-26 03:18:12'),(485,'qrtz_triggers','已生成',0,'ivz-basic-view','QrtzTriggers','qts','','IQrtzTriggersService','IQrtzTriggersMapper','QrtzTriggersServiceImpl','QrtzTriggersController','QrtzTriggersMapper',NULL,NULL,'2020-09-06 03:12:23'),(493,'menu_view','已生成',0,'ivz-basic-view','MenuView','mvw','菜单视图','IMenuViewService','IMenuViewMapper','MenuViewServiceImpl','MenuViewController','MenuViewMapper',NULL,NULL,'2020-09-06 04:01:27'),(494,'t_gen_online','已生成',0,'ivz-basic-view','GenOnline','goe','在线生成器','IGenOnlineService','IGenOnlineMapper','GenOnlineServiceImpl','GenOnlineController','GenOnlineMapper',NULL,NULL,'2020-09-13 02:24:27');
/*!40000 ALTER TABLE `t_gen_table` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_menu`
--

DROP TABLE IF EXISTS `t_menu`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_menu` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `msn` char(32) NOT NULL COMMENT '模块编码, 用来标识此权限属于哪个模块',
  `name` varchar(50) NOT NULL COMMENT '菜单名称',
  `pid` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `sort` int(4) DEFAULT '0' COMMENT '显示顺序',
  `url` varchar(200) DEFAULT '#' COMMENT '请求地址',
  `type` enum('A','M','G','V') NOT NULL COMMENT '菜单类型: A.权限、M.菜单、V.视图、G.菜单组',
  `perm_type` char(16) DEFAULT NULL COMMENT '权限类型(View, Query, Add, Edit, Del, ...)',
  `log` tinyint(1) NOT NULL DEFAULT '1' COMMENT '是否记录日志',
  `log_desc` varchar(64) NOT NULL DEFAULT '' COMMENT '日志说明',
  `status` enum('view','hide','disabled') DEFAULT 'view' COMMENT '菜单状态（view-显示 hide-隐藏 disabled-禁用）',
  `perms` varchar(100) DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',
  `position` enum('M','MM','T','TM','AM') DEFAULT NULL COMMENT '功能点位置 M.主操作栏 MM.主操作栏更多 T.表操作 TM.表操作更多 AM. 主栏和表主栏',
  `remark` varchar(500) DEFAULT '' COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1174 DEFAULT CHARSET=utf8 COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_menu`
--

LOCK TABLES `t_menu` WRITE;
/*!40000 ALTER TABLE `t_menu` DISABLE KEYS */;
INSERT INTO `t_menu` VALUES (1,'core','系统设置',0,0,'#1','M',NULL,1,'00','view',NULL,'iz-icon-xitong',NULL,'显示位置','',NULL,'',NULL),(1058,'core','系统管理',1,50,'#1','M',NULL,1,'','view',NULL,'iz-icon-sys-setting',NULL,'','',NULL,'',NULL),(1059,'core','组织管理',1,100,'#2','M',NULL,1,'','view',NULL,'iz-icon-ziyuan',NULL,'','',NULL,'',NULL),(1060,'core','用户管理',1059,0,'/core/admin','V','View',0,'','view','core:admin:view','iz-icon-sys-user',NULL,'','',NULL,'',NULL),(1064,'core','权限管理',1,150,'#3','M',NULL,1,'','view',NULL,'iz-icon-quanxian',NULL,'','',NULL,'',NULL),(1065,'core','角色管理',1064,1,'/core/role','V','View',0,'','view','core:role:view','iz-icon-sys-role',NULL,'','',NULL,'',NULL),(1066,'core','菜单管理',1058,2,'/core/menu','V','View',0,'浏览菜单信息','view','core:menu:view','iz-icon-menu',NULL,'','',NULL,'',NULL),(1069,'core','新增',1066,1,'/core/menu/add','A','Add',1,'新增菜单','view','core:menu:add','#','M','','',NULL,'',NULL),(1070,'core','编辑',1066,2,'/core/menu/edit','A','Edit',1,'修改菜单信息','view','core:menu:edit','#','T','','',NULL,'',NULL),(1071,'core','删除',1066,3,'/core/menu/del','A','Del',1,'删除菜单','view','core:menu:del','#','T','','',NULL,'',NULL),(1072,'core','查询',1066,0,'/core/menu/view','A','View',1,'浏览菜单信息','view','core:menu:view','#','M','','',NULL,'',NULL),(1090,'core','字典管理',1058,8,'/core/dictType','V','View',0,'','view','core:dictType:view','iz-icon-zidianguanli',NULL,'','',NULL,'',NULL),(1092,'core','新增',1090,0,'/core/dictType/add','A','Add',1,'新增字典类型','view','core:dictType:add','#','M','','',NULL,'',NULL),(1093,'core','修改',1090,0,'/core/dictType/edit','A','Edit',1,'修改字典类型','view','core:dictType:edit','#','T','','',NULL,'',NULL),(1094,'core','查询',1090,0,'/core/dictType/view','A','View',1,'浏览字典信息','view','core:dictType:view','#','M','','',NULL,'',NULL),(1095,'core','字典数据',1090,0,'/core/dictData/listByType','A','DictData',1,'','view','core:dictData:view','iz-icon-dictData','T','','',NULL,'',NULL),(1101,'dev','代码生成',1107,0,'#','M',NULL,1,'','view',NULL,'iz-icon-kaifa',NULL,'','',NULL,'',NULL),(1102,'dev','数据表浏览',1101,0,'/dev/table/db','V','View',0,'','view',NULL,'iz-icon-mgen',NULL,'','',NULL,'',NULL),(1103,'dev','查询',1102,0,'/dev/table/db/data','A','View',0,'','hide',NULL,'#','M','','',NULL,'',NULL),(1107,'dev','开发设置',0,0,'#','M',NULL,1,'','view',NULL,'iz-icon-kaifa',NULL,'','',NULL,'',NULL),(1108,'core','组织机构',1059,0,'/core/org','V','View',0,'','view','core:org:view','iz-icon-jigouguanli',NULL,'','',NULL,'',NULL),(1113,'core','新增',1108,0,'/core/org/add','A','Add',1,'新增组织机构','view','core:org:add','#','M','','',NULL,'',NULL),(1114,'core','删除',1108,0,'/core/org/del','A','Del',1,'删除组织机构','view','core:org:del','#','T','','',NULL,'',NULL),(1115,'core','修改',1108,0,'/core/org/edit','A','Edit',1,'修改组织机构','view','core:org:edit','#','T','','',NULL,'',NULL),(1116,'core','查询',1108,0,'/core/org/view','A','View',1,'浏览组织机构信息','view','core:org:view','#','M','','',NULL,'',NULL),(1117,'core','新增',1060,0,'/core/admin/add','A','Add',1,'','view','core:admin:add','#','M','','',NULL,'',NULL),(1118,'core','删除',1060,0,'/core/admin/del','A','Del',1,'删除用户信息','view','core:admin:del','#','T','','',NULL,'',NULL),(1119,'core','修改',1060,0,'/core/admin/edit','A','Edit',1,'修改用户信息','view','core:admin:edit','#','T','','',NULL,'',NULL),(1120,'core','查询',1060,0,'/core/admin/view','A','View',1,'浏览用户信息','view','core:admin:view','#','M','','',NULL,'',NULL),(1121,'core','新增',1065,0,'/core/role/add','A','Add',1,'新增用户角色','view','core:role:add','#','M','','',NULL,'',NULL),(1122,'core','删除',1065,0,'/core/role/del','A','Del',1,'删除用户角色','view','core:role:del','#','T','','',NULL,'',NULL),(1123,'core','修改',1065,0,'/core/role/edit','A','Edit',1,'修改用户角色','view','core:role:edit','#','T','','',NULL,'',NULL),(1124,'core','查询',1065,0,'/core/role/view','A','View',1,'浏览用户角色','view','core:role:view','#','M','','',NULL,'',NULL),(1125,'core','修改密码',1060,0,'/core/admin/pwd','A','Pwd',1,'修改用户密码','view','core:admin:pwd','#','T','修改用户密码','',NULL,'',NULL),(1126,'dev','开发文档',1107,0,'#','M',NULL,1,'','view',NULL,'iz-icon-devdoc',NULL,'','',NULL,'',NULL),(1127,'core','系统配置',1058,5,'/core/config','V','View',0,'','view','core:config:view','iz-icon-sysconfig',NULL,'','',NULL,'',NULL),(1128,'core','新增',1127,0,'/core/config/add','A','Add',1,'新增系统配置','view','core:config:add','#','M','','',NULL,'',NULL),(1129,'core','删除',1127,0,'/core/config/del','A','Del',1,'删除系统配置','view','core:config:del','#','T','','',NULL,'',NULL),(1130,'core','修改',1127,0,'/core/config/edit','A','Edit',1,'修改系统配置','view','core:config:edit','#','T','','',NULL,'',NULL),(1131,'core','查询',1127,0,'/core/config/view','A','View',1,'浏览系统配置','view','core:config:view','#','M','','',NULL,'',NULL),(1132,'core','访问日志',1058,18,'/core/log','V','View',0,'','view','core:log:view','iz-icon-log',NULL,'','',NULL,'',NULL),(1133,'core','新增',1132,0,'/core/log/add','A','Add',1,'','disabled','core:log:add','#','M','访问日志无需删除','',NULL,'',NULL),(1134,'core','删除',1132,0,'/core/log/del','A','Del',1,'删除访问日志','view','core:log:del','#','M','','',NULL,'',NULL),(1135,'core','修改',1132,0,'/core/log/edit','A','Edit',1,'','disabled','core:log:edit','#','T','操作日志无需编辑','',NULL,'',NULL),(1136,'core','查询',1132,0,'/core/log/view','A','View',0,'','view','core:log:view','#','M','','',NULL,'',NULL),(1137,'core','详情',1066,0,'','A','Detail',0,'','view','core:menu:detail','#','T','','',NULL,'',NULL),(1138,'dev','导入表',1102,0,'/dev/table/import','A','gen',0,'','view','dev:table:import','#','AM','','',NULL,'',NULL),(1139,'dev','代码生成器',1101,0,'/dev/table','V','View',0,'','view','dev:table','iz-icon-table','M','','',NULL,'',NULL),(1140,'dev','新增',1139,0,'/dev/table/add','A','Add',1,'','disabled','dev:table:add','#','M','','',NULL,'',NULL),(1141,'dev','删除',1139,0,'/dev/table/del','A','Del',1,'','view','dev:table:del','#','M','','',NULL,'',NULL),(1142,'dev','修改',1139,0,'/dev/table/edit','A','Edit',1,'','view','dev:table:edit','#','T','','',NULL,'',NULL),(1143,'dev','查询',1139,0,'/dev/table/view','A','View',0,'','view','dev:table:view','#','M','','',NULL,'',NULL),(1144,'dev','生成',1139,0,'/dev/table/gen','A','gen',0,'','view','dev:table:gen','#','AM','','',NULL,'',NULL),(1145,'dev','表字段配置',1101,0,'/dev/field','V','View',0,'','hide','dev:field:view','iz-icon-field','M','','',NULL,'',NULL),(1146,'dev','新增',1145,0,'/dev/field/add','A','Add',1,'','view','dev:fiel:add','#','M','','',NULL,'',NULL),(1147,'dev','删除',1145,0,'/dev/field/del','A','Del',1,'','view','dev:fiel:del','#','T','','',NULL,'',NULL),(1148,'dev','修改',1145,0,'/dev/field/edit','A','Edit',1,'','view','dev:fiel:edit','#','T','','',NULL,'',NULL),(1149,'dev','查询',1145,0,'/dev/field/view','A','View',1,'','view','dev:fiel:view','#','M','','',NULL,'',NULL),(1150,'dev','重新导入',1139,0,'/dev/table/reImport','A','import',0,'','view','dev:table:import','#','T','','',NULL,'',NULL),(1151,'core','字典数据',1058,0,'/core/dictData','V','View',0,'','hide','core:dictData:view','#','M','','',NULL,'',NULL),(1152,'core','新增',1151,0,'/core/dictData/add','A','Add',1,'','view','core:dictData:add','#','M','','',NULL,'',NULL),(1153,'core','删除',1151,0,'/core/dictData/del','A','Del',1,'00','view','core:dictData:del','#','T','','',NULL,'',NULL),(1154,'core','修改',1151,0,'/core/dictData/edit','A','Edit',1,'','view','core:dictData:edit','#','T','','',NULL,'',NULL),(1155,'core','查询',1151,0,'/core/dictData/view','A','View',0,'','view','core:dictData:view','#','M','','',NULL,'',NULL),(1156,'core','系统监控',1,300,'#','M',NULL,0,'','view',NULL,'iz-icon-sys-monitor',NULL,'','',NULL,'',NULL),(1157,'core','在线用户',1156,0,'/core/online','V','View',1,'在线用户操作','view','core:online:user','iz-icon-online-user','M','','',NULL,'',NULL),(1159,'core','强退',1157,0,'/core/online/del','A','Del',1,'','view','core:online:del','#','T','','',NULL,'',NULL),(1161,'core','查询',1157,0,'/core/online/view','A','View',1,'','view','core:online:view','#','M','','',NULL,'',NULL),(1162,'core','系统作业',1156,50,'/quartz/triggers','V','View',1,'作业监控','view','quartz:triggers:view','iz-icon-quartz','M','','',NULL,'',NULL),(1163,'core','新增',1162,0,'/quartz/triggers/add','A','Add',1,'','disabled','quartz:trigger:add','#','M','','',NULL,'',NULL),(1164,'core','删除',1162,0,'/quartz/triggers/del','A','Del',1,'','view','quartz:trigger:del','#','T','','',NULL,'',NULL),(1165,'core','修改',1162,0,'/quartz/triggers/edit','A','Edit',1,'','view','quartz:trigger:edit','#','T','','',NULL,'',NULL),(1166,'core','查询',1162,0,'/quartz/triggers/view','A','View',1,'','view','quartz:trigger:view','#','M','','',NULL,'',NULL),(1167,'core','暂停',1162,5,'/quartz/triggers/paused','A','Paused',1,'暂停作业','hide','quartz:triggers:paused','#','T','','',NULL,'',NULL),(1168,'core','运行一次',1162,0,'/quartz/triggers/run','A','Run',1,'运行一次作业','view','quartz:triggers:run','#','T','','',NULL,'',NULL),(1169,'dev','在线设计器',1101,0,'/develop/genOnline','V','View',0,'','view','core:genOnline:view','iz-icon-online-design','M','','',NULL,'',NULL),(1170,'dev','新增',1169,0,'/develop/genOnline/add','A','Add',1,'','view','develop:genOnlin:add','#','M','','',NULL,'',NULL),(1171,'dev','删除',1169,0,'/develop/genOnline/del','A','Del',1,'','view','develop:genOnlin:del','#','T','','',NULL,'',NULL),(1172,'dev','修改',1169,0,'/develop/genOnline/edit','A','Edit',1,'','view','develop:genOnlin:edit','#','T','','',NULL,'',NULL),(1173,'dev','查询',1169,0,'/develop/genOnline/view','A','View',1,'','view','develop:genOnlin:view','#','M','','',NULL,'',NULL);
/*!40000 ALTER TABLE `t_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_msg_accept`
--

DROP TABLE IF EXISTS `t_msg_accept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_msg_accept` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `msg_id` bigint(20) NOT NULL,
  `user_id` int(11) NOT NULL COMMENT '消息接收者用户',
  `have_read` tinyint(1) NOT NULL DEFAULT '0' COMMENT '消息是否已读',
  `read_time` datetime DEFAULT NULL COMMENT '已读时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息用户表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_msg_accept`
--

LOCK TABLES `t_msg_accept` WRITE;
/*!40000 ALTER TABLE `t_msg_accept` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_msg_accept` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_msg_send`
--

DROP TABLE IF EXISTS `t_msg_send`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_msg_send` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `type` enum('公告','消息','其他') NOT NULL DEFAULT '其他' COMMENT '消息类型',
  `title` varchar(128) NOT NULL DEFAULT '' COMMENT '标题',
  `user_id` int(11) NOT NULL COMMENT '发送者',
  `user_name` varchar(64) NOT NULL DEFAULT '' COMMENT '发送人昵称',
  `priority` enum('normal','high','special') NOT NULL DEFAULT 'normal' COMMENT '优先级',
  `content` text COMMENT '消息内容',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='消息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_msg_send`
--

LOCK TABLES `t_msg_send` WRITE;
/*!40000 ALTER TABLE `t_msg_send` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_msg_send` ENABLE KEYS */;
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
  `status` enum('Guest','On','Off') NOT NULL DEFAULT 'Guest' COMMENT '用户状态(Def. 默认 ,on. 在线, off. 离线)',
  `app_type` enum('App','WebApp','Web','Unknown') DEFAULT 'Web' COMMENT '应用类型',
  `location` varchar(64) NOT NULL DEFAULT '' COMMENT '访问的本地位置',
  `login_time` datetime DEFAULT NULL COMMENT '登录时间',
  `expire_time` int(11) DEFAULT '0' COMMENT '超时时长, 从访问到离开的时间， 单位分钟',
  `create_time` datetime NOT NULL COMMENT '系统访问时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '系统最后访问时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_online_user_session_id_uindex` (`session_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8 COMMENT='在线用户';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_online_user`
--

LOCK TABLES `t_online_user` WRITE;
/*!40000 ALTER TABLE `t_online_user` DISABLE KEYS */;
INSERT INTO `t_online_user` VALUES (1,'21909bdd-0a97-4be6-a201-7db2c966b056','admin','超级管理员','0:0:0:0:0:0:0:1','Chrome 8','Linux','Off','Web','','2020-09-06 12:01:08',1800000,'2020-09-06 11:09:50','2020-09-13 10:08:08'),(2,'ab2a1f7a-bae8-4a75-97f3-67dfb4885b13','admin','超级管理员','0:0:0:0:0:0:0:1','Chrome 8','Linux','On','Web','','2020-09-13 13:54:32',1800000,'2020-09-13 10:22:41','2020-09-13 13:54:32');
/*!40000 ALTER TABLE `t_online_user` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_opera_log`
--

DROP TABLE IF EXISTS `t_opera_log`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_opera_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `url` varchar(255) DEFAULT '' COMMENT '请求URL',
  `ip` varchar(50) DEFAULT '' COMMENT '主机地址',
  `msn` varchar(16) NOT NULL DEFAULT '' COMMENT '模块编号',
  `title` varchar(64) NOT NULL DEFAULT '' COMMENT '日志标题',
  `type` varchar(16) NOT NULL DEFAULT '' COMMENT '功能类型',
  `millis` int(11) DEFAULT NULL COMMENT '操作毫秒数',
  `user_id` bigint(20) DEFAULT '0' COMMENT '操作人员',
  `user_name` varchar(50) DEFAULT '' COMMENT '操作人员',
  `short_name` varchar(64) NOT NULL DEFAULT '' COMMENT '简写的class.method()',
  `status` char(12) DEFAULT '' COMMENT '操作状态',
  `err_msg` varchar(2000) DEFAULT '' COMMENT '异常消息',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  `update_time` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=668 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_opera_log`
--

LOCK TABLES `t_opera_log` WRITE;
/*!40000 ALTER TABLE `t_opera_log` DISABLE KEYS */;
INSERT INTO `t_opera_log` VALUES (664,'/login','','','系统登录','Login',341,1,'超级管理员','','Login','用户登录','2020-09-06 11:04:32',NULL),(665,'/core/menu/view','','','浏览菜单信息','View',33,1,'超级管理员','MenuController.list(Menu)','Success','操作成功','2020-09-06 11:04:38',NULL),(666,'/core/menu/view','','','浏览菜单信息','View',20,1,'超级管理员','MenuController.list(Menu)','Success','操作成功','2020-09-06 11:05:12',NULL),(667,'/login','','','系统登录','Login',27,1,'超级管理员','','Login','用户登录','2020-09-06 11:09:57',NULL);
/*!40000 ALTER TABLE `t_opera_log` ENABLE KEYS */;
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
  `type` enum('Org','Company','Dept') NOT NULL DEFAULT 'Company',
  `leader` varchar(20) DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) DEFAULT NULL COMMENT '邮箱',
  `status` enum('正常','禁用') NOT NULL DEFAULT '正常' COMMENT '部门状态（0正常 1停用）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8 COMMENT='组织机构表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_organize`
--

LOCK TABLES `t_organize` WRITE;
/*!40000 ALTER TABLE `t_organize` DISABLE KEYS */;
INSERT INTO `t_organize` VALUES (1,0,1,'厦门由源创科技',0,'Company','iteaj',NULL,'iteaj@outlook.com','禁用','','2019-11-26 09:20:59','',NULL),(2,1,1,'运维部',0,'Dept','iteaj',NULL,'iteaj@live.cn','正常','','2019-11-26 01:21:01','',NULL),(3,1,1,'技术部',0,'Dept','iteaj',NULL,'iteaj@outlook.com','正常','','2019-11-26 18:19:23','',NULL);
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
  `status` enum('正常','禁用') NOT NULL DEFAULT '正常' COMMENT '角色状态（0正常 1停用）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COMMENT='角色信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role`
--

LOCK TABLES `t_role` WRITE;
/*!40000 ALTER TABLE `t_role` DISABLE KEYS */;
INSERT INTO `t_role` VALUES (2,'管理员',2,'1','正常',NULL,'','2019-11-27 16:48:35','',NULL),(3,'技术总监',0,'1','正常','CTO','','2019-11-27 18:37:24','',NULL),(4,'演示角色',0,'1','正常','用于演示','','2020-04-24 12:03:38','',NULL);
/*!40000 ALTER TABLE `t_role` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_role_dept`
--

DROP TABLE IF EXISTS `t_role_dept`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='角色和部门关联表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_role_dept`
--

LOCK TABLES `t_role_dept` WRITE;
/*!40000 ALTER TABLE `t_role_dept` DISABLE KEYS */;
/*!40000 ALTER TABLE `t_role_dept` ENABLE KEYS */;
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
INSERT INTO `t_role_menu` VALUES (2,1069),(2,1071),(2,1072),(3,1),(3,1058),(3,1059),(3,1060),(3,1064),(3,1065),(3,1066),(3,1069),(3,1070),(3,1071),(3,1072),(3,1090),(3,1092),(3,1093),(3,1094),(3,1095),(3,1104),(3,1108),(3,1113),(3,1114),(3,1115),(3,1116),(3,1117),(3,1118),(3,1119),(3,1120),(3,1121),(3,1122),(3,1123),(3,1124),(3,1125),(3,1126),(3,1127),(3,1128),(3,1129),(3,1130),(3,1131),(4,1072),(4,1094),(4,1095),(4,1102),(4,1103),(4,1116),(4,1120),(4,1124),(4,1126),(4,1131),(4,1132),(4,1133),(4,1134),(4,1135),(4,1136),(4,1137),(4,1138);
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

-- Dump completed on 2020-09-13 14:11:42
