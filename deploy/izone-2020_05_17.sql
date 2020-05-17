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
  `login_ip` varchar(50) DEFAULT '' COMMENT '最后登陆IP',
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
INSERT INTO `t_admin` VALUES (1,3,'admin','超级管理员','iteaj@outlook.com','15888888888','男','http://www','a66abb5684c45962d887564f08346e8d','正常','127.0.0.1','2018-03-16 11:33:00','超级管理员','admin','2018-03-15 11:33:00','admin','2018-03-15 03:33:00'),(4,1,'iteaj','iteaj','iteaj@outlook.com','15888888888','男',NULL,'a88b27088abf12811cf7df0f1421d7e1','正常',NULL,NULL,'技术总监','',NULL,'',NULL),(5,3,'test','演示用户','test@outlook.com','15888888888','未知',NULL,'098f6bcd4621d373cade4e832627b4f6','正常',NULL,NULL,NULL,'',NULL,'',NULL);
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
INSERT INTO `t_admin_role` VALUES (5,4),(4,3);
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
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8 COMMENT='字典数据表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_dict_data`
--

LOCK TABLES `t_dict_data` WRITE;
/*!40000 ALTER TABLE `t_dict_data` DISABLE KEYS */;
INSERT INTO `t_dict_data` VALUES (1,1,'主栏','T','opera_position',NULL,NULL,0,'正常',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(2,2,'主栏(更多)','MT','opera_position',NULL,NULL,0,'正常',NULL,'','2019-06-27 11:13:02','','2019-06-27 11:13:07'),(6,0,'主栏(更多)','MM','function-position',NULL,NULL,0,'正常','主栏(更多)','',NULL,'',NULL),(11,0,'正常','status','common_status',NULL,NULL,0,'正常','正常状态0','',NULL,'',NULL),(12,0,'禁用','disabled','common_status',NULL,NULL,0,'正常','禁用状态','',NULL,'',NULL),(13,0,'主栏','M','function-position',NULL,NULL,0,'正常','主栏','',NULL,'',NULL),(14,0,'隐藏','hidden','common_status',NULL,NULL,0,'正常','隐藏状态','',NULL,'',NULL),(15,0,'xx','xx','test',NULL,NULL,0,'正常','xx','',NULL,'',NULL),(16,0,'男','male','sex',NULL,NULL,0,'正常',NULL,'',NULL,'',NULL),(17,0,'女','female','sex',NULL,NULL,0,'正常',NULL,'',NULL,'',NULL),(18,0,'核心模块','core','sys.module',NULL,NULL,0,'正常','系统管理核心模块(admin)','',NULL,'',NULL),(19,0,'开发模块','dev','sys.module',NULL,NULL,0,'正常','开发人员管理、开发、维护模块','',NULL,'',NULL),(20,0,'人员模块','renyuan','sys.module',NULL,NULL,0,'正常','人员模块','',NULL,'',NULL);
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
INSERT INTO `t_dict_type` VALUES (1,'common_status','状态','正常','通用状态','',NULL,'',NULL),(3,'sys.module','系统模块','正常','系统包含的模块','','2020-04-27 09:44:57','',NULL);
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
  `db_name` varchar(16) NOT NULL COMMENT '所属模块',
  `name` varchar(32) DEFAULT NULL COMMENT '表字段名',
  `table_name` varchar(32) DEFAULT NULL COMMENT '表明',
  `property_name` varchar(32) DEFAULT NULL COMMENT '实体类属性名',
  `type` varchar(32) NOT NULL DEFAULT '' COMMENT '字段类型',
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
  UNIQUE KEY `t_gen_field_pk` (`db_name`,`table_name`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3396 DEFAULT CHARSET=utf8 COMMENT='代码生成器-字段信息';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_gen_field`
--

LOCK TABLES `t_gen_field` WRITE;
/*!40000 ALTER TABLE `t_gen_field` DISABLE KEYS */;
INSERT INTO `t_gen_field` VALUES (3209,'izone','id','t_gen_module','id','int(11)','','number',NULL,'Integer','',1,'',NULL,1,0,1,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3210,'izone','name','t_gen_module','name','varchar(64)','模块名称','text',NULL,'String','模块名称',0,'',NULL,1,1,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3211,'izone','msn','t_gen_module','msn','varchar(10)','模块标识','text',NULL,'String','模块标识, 此标识将作为url的一部分',0,'',NULL,1,1,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3212,'izone','remark','t_gen_module','remark','varchar(128)','模块说明','text',NULL,'String','模块说明',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3213,'izone','project','t_gen_module','project','varchar(12)','项目名称','text',NULL,'String','项目名称, dao层project-dao, service层project-service\nview层project-view',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3214,'izone','account','t_gen_module','account','varchar(32)','数据登录账号','text',NULL,'String','数据登录账号',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3215,'izone','password','t_gen_module','password','varchar(32)','数据库登录密码','text',NULL,'String','数据库登录密码',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3216,'izone','db_name','t_gen_module','dbName','varchar(32)','数据库名称','text',NULL,'String','数据库名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3217,'izone','table_prefix','t_gen_module','tablePrefix','char(8)','表前缀','text',NULL,'String','表前缀',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3218,'izone','create_time','t_gen_module','createTime','datetime','','date',NULL,'Date','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3219,'izone','update_time','t_gen_module','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3220,'izone','id','t_gen_table','id','int(11)','','number',NULL,'Integer','',1,'',NULL,1,0,1,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3221,'izone','msn','t_gen_table','msn','varchar(16)','所属模块','text',NULL,'String','所属模块',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3222,'izone','name','t_gen_table','name','varchar(32)','表明','text',NULL,'String','表明',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3223,'izone','prefix','t_gen_table','prefix','char(3)','关联表的列前缀','text',NULL,'String','关联表的列前缀',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3224,'izone','db_name','t_gen_table','dbName','varchar(32)','数据库','text',NULL,'String','数据库',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3225,'izone','status','t_gen_table','status','enum(\'已生成\',\'未生成\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3226,'izone','cover','t_gen_table','cover','tinyint(1)','是否覆盖已存在的文件','radio',NULL,'Boolean','是否覆盖已存在的文件',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3227,'izone','view_type','t_gen_table','viewType','varchar(32)','视图类型','text',NULL,'String','视图类型',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3228,'izone','entity_name','t_gen_table','entityName','varchar(32)','表对应的实体名','text',NULL,'String','表对应的实体名',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3229,'izone','comment','t_gen_table','comment','varchar(128)','表说明','text',NULL,'String','表说明',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3230,'izone','service_name','t_gen_table','serviceName','varchar(32)','业务接口名称','text',NULL,'String','业务接口名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3231,'izone','mapper_name','t_gen_table','mapperName','varchar(32)','到接口名称','text',NULL,'String','到接口名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3232,'izone','service_impl_name','t_gen_table','serviceImplName','varchar(32)','业务接口实现名称','text',NULL,'String','业务接口实现名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3233,'izone','controller_name','t_gen_table','controllerName','varchar(32)','视图层控制器名称','text',NULL,'String','视图层控制器名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3234,'izone','xml_name','t_gen_table','xmlName','varchar(32)','mapper配置文件名称','text',NULL,'String','mapper配置文件名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3235,'izone','remark','t_gen_table','remark','varchar(128)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3236,'izone','create_time','t_gen_table','createTime','datetime','','date',NULL,'Date','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3237,'izone','update_time','t_gen_table','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3238,'izone','id','t_menu','id','bigint(20)','菜单ID','number',NULL,'Long','菜单ID',1,'',NULL,1,0,1,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3239,'izone','msn','t_menu','msn','char(32)','模块编码','text',NULL,'String','模块编码, 用来标识此权限属于哪个模块',0,'',NULL,1,1,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3240,'izone','name','t_menu','name','varchar(50)','菜单名称','text',NULL,'String','菜单名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3241,'izone','pid','t_menu','pid','bigint(20)','父菜单ID','number',NULL,'Long','父菜单ID',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3242,'izone','sort','t_menu','sort','int(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3243,'izone','url','t_menu','url','varchar(200)','请求地址','text',NULL,'String','请求地址',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3244,'izone','type','t_menu','type','enum(\'A\',\'M\',\'V\')','菜单类型:','text',NULL,'String','菜单类型: A.权限、M.菜单、V.视图',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3245,'izone','perm_type','t_menu','permType','char(16)','权限类型','text',NULL,'String','权限类型(View, Query, Add, Edit, Del, ...)',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3246,'izone','log','t_menu','log','tinyint(1)','是否记录日志','radio',NULL,'Boolean','是否记录日志',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3247,'izone','log_desc','t_menu','logDesc','varchar(64)','日志说明','text',NULL,'String','日志说明',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3248,'izone','status','t_menu','status','enum(\'view\',\'hide\',\'disabled\')','菜单状态','text',NULL,'String','菜单状态（view-显示 hide-隐藏 disabled-禁用）',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3249,'izone','perms','t_menu','perms','varchar(100)','权限标识','text',NULL,'String','权限标识',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3250,'izone','icon','t_menu','icon','varchar(100)','菜单图标','text',NULL,'String','菜单图标',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3251,'izone','position','t_menu','position','enum(\'M\',\'MM\',\'T\',\'TM\',\'AM\')','功能点位置','text',NULL,'String','功能点位置 M.主操作栏 MM.主操作栏更多 T.表操作 TM.表操作更多 AM. 主栏和表主栏',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3252,'izone','remark','t_menu','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3253,'izone','create_by','t_menu','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3254,'izone','create_time','t_menu','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3255,'izone','update_by','t_menu','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3256,'izone','update_time','t_menu','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3257,'izone','id','t_menu_view','id','bigint(20)','','number',NULL,'Long','',1,'',NULL,1,0,1,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3258,'izone','name','t_menu_view','name','char(32)','视图名称','text',NULL,'String','视图名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3259,'izone','type','t_menu_view','type','enum(\'默认\',\'菜单组\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3260,'izone','icon','t_menu_view','icon','char(32)','图标','text',NULL,'String','图标',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3261,'izone','sort','t_menu_view','sort','tinyint(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3262,'izone','status','t_menu_view','status','enum(\'正常\',\'禁用\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3263,'izone','menus','t_menu_view','menus','varchar(128)','菜单列表','text',NULL,'String','菜单列表',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3264,'izone','create_time','t_menu_view','createTime','datetime','','date',NULL,'Date','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3265,'izone','update_time','t_menu_view','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3266,'izone','id','t_opera_log','id','bigint(20)','日志主键','number',NULL,'Long','日志主键',1,'',NULL,1,0,1,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3267,'izone','url','t_opera_log','url','varchar(255)','请求URL','text',NULL,'String','请求URL',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3268,'izone','ip','t_opera_log','ip','varchar(50)','主机地址','text',NULL,'String','主机地址',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3269,'izone','msn','t_opera_log','msn','varchar(16)','模块编号','text',NULL,'String','模块编号',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3270,'izone','title','t_opera_log','title','varchar(64)','日志标题','text',NULL,'String','日志标题',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3271,'izone','type','t_opera_log','type','varchar(16)','功能类型','text',NULL,'String','功能类型',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3272,'izone','millis','t_opera_log','millis','int(11)','操作毫秒数','number',NULL,'Integer','操作毫秒数',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3273,'izone','user_id','t_opera_log','userId','bigint(20)','操作人员','number',NULL,'Long','操作人员',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3274,'izone','user_name','t_opera_log','userName','varchar(50)','操作人员','text',NULL,'String','操作人员',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3275,'izone','short_name','t_opera_log','shortName','varchar(64)','简写的class','text',NULL,'String','简写的class.method()',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3276,'izone','status','t_opera_log','status','char(12)','操作状态','text',NULL,'String','操作状态',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3277,'izone','err_msg','t_opera_log','errMsg','varchar(2000)','异常消息','text',NULL,'String','异常消息',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3278,'izone','create_time','t_opera_log','createTime','datetime','操作时间','date',NULL,'Date','操作时间',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3279,'izone','update_time','t_opera_log','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3280,'izone','id','t_organize','id','bigint(20)','部门id','number',NULL,'Long','部门id',1,'',NULL,1,0,1,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3281,'izone','pid','t_organize','pid','bigint(20)','父级id','number',NULL,'Long','父级id',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3282,'izone','level','t_organize','level','int(3)','属于第几级','number',NULL,'Integer','属于第几级',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3283,'izone','name','t_organize','name','varchar(64)','机构名称','text',NULL,'String','机构名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3284,'izone','sort','t_organize','sort','int(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3285,'izone','type','t_organize','type','enum(\'Org\',\'Company\',\'Dept\')','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3286,'izone','leader','t_organize','leader','varchar(20)','负责人','text',NULL,'String','负责人',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3287,'izone','phone','t_organize','phone','varchar(11)','联系电话','text',NULL,'String','联系电话',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3288,'izone','email','t_organize','email','varchar(50)','邮箱','text',NULL,'String','邮箱',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3289,'izone','status','t_organize','status','enum(\'正常\',\'禁用\')','部门状态','text',NULL,'String','部门状态（0正常 1停用）',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3290,'izone','create_by','t_organize','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3291,'izone','create_time','t_organize','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3292,'izone','update_by','t_organize','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3293,'izone','update_time','t_organize','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3294,'izone','id','t_role','id','bigint(20)','角色ID','number',NULL,'Long','角色ID',1,'',NULL,1,0,1,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3295,'izone','name','t_role','name','varchar(30)','角色名称','text',NULL,'String','角色名称',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3296,'izone','sort','t_role','sort','int(4)','显示顺序','number',NULL,'Integer','显示顺序',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3297,'izone','scope','t_role','scope','char(1)','数据范围','text',NULL,'String','数据范围（1：全部数据权限 2：自定数据权限）',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3298,'izone','status','t_role','status','enum(\'正常\',\'禁用\')','角色状态','text',NULL,'String','角色状态（0正常 1停用）',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3299,'izone','remark','t_role','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3300,'izone','create_by','t_role','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3301,'izone','create_time','t_role','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3302,'izone','update_by','t_role','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3303,'izone','update_time','t_role','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3304,'izone','role_id','t_role_dept','roleId','bigint(20)','角色ID','number',NULL,'Long','角色ID',1,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3305,'izone','dept_id','t_role_dept','deptId','bigint(20)','部门ID','number',NULL,'Long','部门ID',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3306,'izone','rid','t_role_menu','rid','bigint(20)','角色ID','number',NULL,'Long','角色ID',1,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3307,'izone','mid','t_role_menu','mid','bigint(20)','菜单ID','number',NULL,'Long','菜单ID',0,'',NULL,1,0,0,'2020-05-02 23:24:41','2020-05-02 15:24:41'),(3310,'izone','id','t_config','id','int(5)','参数主键','number',NULL,'Integer','参数主键',1,'',NULL,1,0,1,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3311,'izone','name','t_config','name','varchar(64)','参数名称','text',NULL,'String','参数名称',0,'',NULL,1,0,0,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3312,'izone','label','t_config','label','varchar(32)','系统配置key','text',NULL,'String','系统配置key',0,'',NULL,1,0,0,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3313,'izone','value','t_config','value','varchar(128)','参数键值','text',NULL,'String','参数键值',0,'',NULL,1,0,0,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3314,'izone','type','t_config','type','enum(\'def\',\'sys\')','配置类型','text',NULL,'String','配置类型',0,'',NULL,1,0,0,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3315,'izone','remark','t_config','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3316,'izone','create_time','t_config','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,1,0,0,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3317,'izone','update_time','t_config','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,1,0,0,'2020-05-03 00:26:39','2020-05-02 16:26:39'),(3335,'izone','id','t_admin','id','bigint(20)','用户ID','number',NULL,'Long','用户ID',1,'',NULL,0,0,1,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3336,'izone','org_id','t_admin','orgId','bigint(20)','部门ID','number',NULL,'Long','部门ID',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3337,'izone','account','t_admin','account','char(24)','登录账号','text',NULL,'String','登录账号',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3338,'izone','name','t_admin','name','varchar(30)','用户名称','text',NULL,'String','用户名称',0,'',NULL,1,1,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3339,'izone','email','t_admin','email','varchar(50)','用户邮箱','text',NULL,'String','用户邮箱',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3340,'izone','phone','t_admin','phone','varchar(11)','手机号码','text',NULL,'String','手机号码',0,'',NULL,1,1,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3341,'izone','sex','t_admin','sex','enum(\'男\',\'女\',\'未知\')','用户性别','text',NULL,'String','用户性别（0男 1女 2未知）',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3342,'izone','avatar','t_admin','avatar','varchar(100)','头像路径','text',NULL,'String','头像路径',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3343,'izone','password','t_admin','password','varchar(50)','密码','text',NULL,'String','密码',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3344,'izone','status','t_admin','status','enum(\'正常\',\'禁用\')','帐号状态','text',NULL,'String','帐号状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3345,'izone','login_ip','t_admin','loginIp','varchar(50)','最后登陆IP','text',NULL,'String','最后登陆IP',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3346,'izone','login_date','t_admin','loginDate','datetime','最后登陆时间','date',NULL,'Date','最后登陆时间',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3347,'izone','remark','t_admin','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3348,'izone','create_by','t_admin','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3349,'izone','create_time','t_admin','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3350,'izone','update_by','t_admin','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3351,'izone','update_time','t_admin','updateTime','timestamp','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-05-14 11:53:20','2020-05-14 03:53:20'),(3352,'izone','aid','t_admin_role','aid','bigint(20)','管理员id','number',NULL,'Long','管理员id',0,'',NULL,1,0,0,'2020-05-15 18:14:44','2020-05-15 10:14:44'),(3353,'izone','rid','t_admin_role','rid','bigint(20)','角色id','number',NULL,'Long','角色id',0,'',NULL,1,0,0,'2020-05-15 18:14:44','2020-05-15 10:14:44'),(3354,'izone','id','t_dict_data','id','bigint(20)','字典id','number',NULL,'Long','字典id',1,'',NULL,0,0,1,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3355,'izone','sort','t_dict_data','sort','int(4)','字典排序','number',NULL,'Integer','字典排序',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3356,'izone','label','t_dict_data','label','varchar(100)','字典标签','text',NULL,'String','字典标签',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3357,'izone','value','t_dict_data','value','varchar(100)','字典键值','text',NULL,'String','字典键值',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3358,'izone','type','t_dict_data','type','char(32)','字典类型','text',NULL,'String','字典类型',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3359,'izone','css_class','t_dict_data','cssClass','varchar(100)','样式属性','text',NULL,'String','样式属性（其他样式扩展）',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3360,'izone','list_class','t_dict_data','listClass','varchar(100)','表格回显样式','text',NULL,'String','表格回显样式',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3361,'izone','iz_default','t_dict_data','izDefault','tinyint(1)','是否默认','radio',NULL,'Boolean','是否默认（Y是 N否）',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3362,'izone','status','t_dict_data','status','enum(\'正常\',\'禁用\')','状态','text',NULL,'String','状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3363,'izone','remark','t_dict_data','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3364,'izone','create_by','t_dict_data','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3365,'izone','create_time','t_dict_data','createTime','datetime','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3366,'izone','update_by','t_dict_data','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3367,'izone','update_time','t_dict_data','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-05-15 18:15:48','2020-05-15 10:15:48'),(3368,'izone','id','t_dict_type','id','bigint(20)','字典主键','number',NULL,'Long','字典主键',1,'',NULL,0,0,1,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3369,'izone','type','t_dict_type','type','char(32)','字典类型','text',NULL,'String','字典类型',0,'',NULL,1,0,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3370,'izone','name','t_dict_type','name','varchar(100)','字典名称','text',NULL,'String','字典名称',0,'',NULL,1,1,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3371,'izone','status','t_dict_type','status','enum(\'正常\',\'禁用\')','状态','text',NULL,'String','状态（0正常 1停用）',0,'',NULL,1,1,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3372,'izone','remark','t_dict_type','remark','varchar(500)','备注','text',NULL,'String','备注',0,'',NULL,1,0,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3373,'izone','create_by','t_dict_type','createBy','varchar(64)','创建者','text',NULL,'String','创建者',0,'',NULL,1,0,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3374,'izone','create_time','t_dict_type','createTime','timestamp','创建时间','date',NULL,'Date','创建时间',0,'',NULL,0,0,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3375,'izone','update_by','t_dict_type','updateBy','varchar(64)','更新者','text',NULL,'String','更新者',0,'',NULL,1,0,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3376,'izone','update_time','t_dict_type','updateTime','datetime','更新时间','date',NULL,'Date','更新时间',0,'',NULL,0,0,0,'2020-05-15 18:16:07','2020-05-15 10:16:07'),(3377,'izone','id','t_gen_field','id','int(11)','','number',NULL,'Integer','',1,'',NULL,0,0,1,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3378,'izone','db_name','t_gen_field','dbName','varchar(16)','所属模块','text',NULL,'String','所属模块',0,'',NULL,1,1,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3379,'izone','name','t_gen_field','name','varchar(32)','表字段名','text',NULL,'String','表字段名',0,'',NULL,1,1,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3380,'izone','table_name','t_gen_field','tableName','varchar(32)','表明','text',NULL,'String','表明',0,'',NULL,1,1,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3381,'izone','property_name','t_gen_field','propertyName','varchar(32)','实体类属性名','text',NULL,'String','实体类属性名',0,'',NULL,1,1,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3382,'izone','type','t_gen_field','type','varchar(32)','字段类型','text',NULL,'String','字段类型',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3383,'izone','title','t_gen_field','title','varchar(64)','字段标题','text',NULL,'String','字段标题',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3384,'izone','ctype','t_gen_field','ctype','varchar(16)','组件类型','text',NULL,'String','组件类型',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3385,'izone','dict_type','t_gen_field','dictType','varchar(32)','字典类型','text',NULL,'String','字典类型',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3386,'izone','column_type','t_gen_field','columnType','varchar(64)','属性类型','text',NULL,'String','属性类型',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3387,'izone','comment','t_gen_field','comment','varchar(128)','字段描述','text',NULL,'String','字段描述',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3388,'izone','key_flag','t_gen_field','keyFlag','tinyint(1)','是否是主键','radio',NULL,'Boolean','是否是主键',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3389,'izone','validate','t_gen_field','validate','varchar(128)','验证类型数组','text',NULL,'String','验证类型数组',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3390,'izone','remark','t_gen_field','remark','varchar(128)','','text',NULL,'String','',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3391,'izone','edit_flag','t_gen_field','editFlag','tinyint(1)','','radio',NULL,'Boolean','',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3392,'izone','search_flag','t_gen_field','searchFlag','tinyint(1)','是否是搜索字段','radio',NULL,'Boolean','是否是搜索字段',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3393,'izone','identity_flag','t_gen_field','identityFlag','tinyint(1)','主键是否自增','radio',NULL,'Boolean','主键是否自增',0,'',NULL,1,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3394,'izone','create_time','t_gen_field','createTime','datetime','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29'),(3395,'izone','update_time','t_gen_field','updateTime','timestamp','','date',NULL,'Date','',0,'',NULL,0,0,0,'2020-05-15 18:16:29','2020-05-15 10:16:29');
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
  `account` varchar(32) DEFAULT NULL COMMENT '数据登录账号',
  `password` varchar(32) DEFAULT NULL COMMENT '数据库登录密码',
  `db_name` varchar(32) DEFAULT NULL COMMENT '数据库名称',
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
INSERT INTO `t_gen_module` VALUES (3,'核心模块','core','','admin','iteaj','wqc88888','izone','t_','2020-04-30 19:15:03','2020-04-30 11:15:03'),(4,'开发模块','dev','','develop','iteaj','wqc88888','izone','t_','2020-04-30 19:15:58','2020-04-30 11:15:58'),(7,'人员模块','renyuan','','人员模块','iteaj','wqc88888','izone','t_','2020-05-15 22:06:49','2020-05-15 14:06:49');
/*!40000 ALTER TABLE `t_gen_module` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_gen_table`
--

DROP TABLE IF EXISTS `t_gen_table`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_gen_table` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `msn` varchar(16) NOT NULL COMMENT '所属模块',
  `name` varchar(32) NOT NULL COMMENT '表明',
  `prefix` char(3) NOT NULL DEFAULT '' COMMENT '关联表的列前缀',
  `db_name` varchar(32) DEFAULT '' COMMENT '数据库',
  `status` enum('已生成','未生成') NOT NULL DEFAULT '未生成' COMMENT '禁用的表将不会生成代码',
  `disabled` tinyint(1) DEFAULT '0' COMMENT '是否禁用',
  `cover` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否覆盖已存在的文件',
  `view_type` varchar(32) DEFAULT NULL COMMENT '视图类型',
  `entity_name` varchar(32) DEFAULT NULL COMMENT '表对应的实体名',
  `comment` varchar(128) NOT NULL DEFAULT '' COMMENT '表说明',
  `service_name` varchar(32) DEFAULT NULL COMMENT '业务接口名称',
  `mapper_name` varchar(32) DEFAULT NULL COMMENT '到接口名称',
  `service_impl_name` varchar(32) DEFAULT NULL COMMENT '业务接口实现名称',
  `controller_name` varchar(32) DEFAULT NULL COMMENT '视图层控制器名称',
  `xml_name` varchar(32) DEFAULT NULL COMMENT 'mapper配置文件名称',
  `remark` varchar(128) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_gen_table_pk` (`msn`,`name`)
) ENGINE=InnoDB AUTO_INCREMENT=420 DEFAULT CHARSET=utf8 COMMENT='代码生成器表配置';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_gen_table`
--

LOCK TABLES `t_gen_table` WRITE;
/*!40000 ALTER TABLE `t_gen_table` DISABLE KEYS */;
INSERT INTO `t_gen_table` VALUES (403,'dev','t_gen_module','gme','izone','已生成',0,0,'ivz-basic-view','GenModule','代码生成模块','IGenModuleService','IGenModuleMapper','GenModuleServiceImpl','GenModuleController','GenModuleMapper',NULL,NULL,'2020-05-02 15:24:41'),(404,'dev','t_gen_table','gte','izone','已生成',0,0,'ivz-basic-view','GenTable','代码生成器表配置','IGenTableService','IGenTableMapper','GenTableServiceImpl','GenTableController','GenTableMapper',NULL,NULL,'2020-05-02 15:24:41'),(405,'core','t_menu','mu','izone','已生成',0,0,'ivz-basic-view','Menu','菜单权限表','IMenuService','IMenuMapper','MenuServiceImpl','MenuController','MenuMapper',NULL,NULL,'2020-05-02 15:24:41'),(406,'core','t_menu_view','mvw','izone','已生成',0,0,'ivz-basic-view','MenuView','菜单视图','IMenuViewService','IMenuViewMapper','MenuViewServiceImpl','MenuViewController','MenuViewMapper',NULL,NULL,'2020-05-02 15:24:41'),(407,'core','t_opera_log','olg','izone','已生成',0,0,'ivz-basic-view','OperaLog','操作日志记录','IOperaLogService','IOperaLogMapper','OperaLogServiceImpl','OperaLogController','OperaLogMapper',NULL,NULL,'2020-05-02 15:24:41'),(408,'core','t_organize','oe','izone','已生成',0,0,'ivz-basic-view','Organize','组织机构表','IOrganizeService','IOrganizeMapper','OrganizeServiceImpl','OrganizeController','OrganizeMapper',NULL,NULL,'2020-05-02 15:24:41'),(409,'core','t_role','re','izone','已生成',0,0,'ivz-basic-view','Role','角色信息表','IRoleService','IRoleMapper','RoleServiceImpl','RoleController','RoleMapper',NULL,NULL,'2020-05-02 15:24:41'),(410,'core','t_role_dept','rdt','izone','已生成',0,0,'ivz-drawer-view','RoleDept','角色和部门关联表','IRoleDeptService','IRoleDeptMapper','RoleDeptServiceImpl','RoleDeptController','RoleDeptMapper',NULL,NULL,'2020-05-02 15:24:41'),(411,'core','t_role_menu','rmu','izone','已生成',0,0,'ivz-drawer-view','RoleMenu','角色和菜单关联表','IRoleMenuService','IRoleMenuMapper','RoleMenuServiceImpl','RoleMenuController','RoleMenuMapper',NULL,NULL,'2020-05-02 15:24:41'),(413,'core','t_config','cg','izone','已生成',0,0,'ivz-basic-view','Config','参数配置表','IConfigService','IConfigMapper','ConfigServiceImpl','ConfigController','ConfigMapper',NULL,NULL,'2020-05-02 16:26:39'),(415,'core','t_admin','an','izone','已生成',0,0,'ivz-basic-view','Admin','管理员信息表','IAdminService','IAdminMapper','AdminServiceImpl','AdminController','AdminMapper',NULL,NULL,'2020-05-14 03:53:20'),(416,'core','t_admin_role','are','izone','未生成',0,0,'ivz-drawer-view','AdminRole','管理员角色表','IAdminRoleService','IAdminRoleMapper','AdminRoleServiceImpl','AdminRoleController','AdminRoleMapper',NULL,NULL,'2020-05-15 10:14:44'),(417,'core','t_dict_data','dda','izone','未生成',0,0,'ivz-basic-view','DictData','字典数据表','IDictDataService','IDictDataMapper','DictDataServiceImpl','DictDataController','DictDataMapper',NULL,NULL,'2020-05-15 10:15:48'),(418,'core','t_dict_type','dte','izone','未生成',0,0,'ivz-basic-view','DictType','字典类型表','IDictTypeService','IDictTypeMapper','DictTypeServiceImpl','DictTypeController','DictTypeMapper',NULL,NULL,'2020-05-15 10:16:07'),(419,'dev','t_gen_field','gfd','izone','未生成',0,0,'ivz-basic-view','GenField','代码生成器-字段信息','IGenFieldService','IGenFieldMapper','GenFieldServiceImpl','GenFieldController','GenFieldMapper',NULL,NULL,'2020-05-15 10:16:29');
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
  `type` enum('A','M','V') DEFAULT NULL COMMENT '菜单类型: A.权限、M.菜单、V.视图',
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
) ENGINE=InnoDB AUTO_INCREMENT=1156 DEFAULT CHARSET=utf8 COMMENT='菜单权限表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_menu`
--

LOCK TABLES `t_menu` WRITE;
/*!40000 ALTER TABLE `t_menu` DISABLE KEYS */;
INSERT INTO `t_menu` VALUES (1,'core','系统设置',0,0,'#1','M',NULL,1,'','view',NULL,'iz-icon-xitong',NULL,'显示位置','',NULL,'',NULL),(1058,'core','系统管理',1,1,'#1','M',NULL,1,'','view',NULL,'iz-icon-sys-setting',NULL,'','',NULL,'',NULL),(1059,'core','组织管理',1,2,'#2','M',NULL,1,'','view',NULL,'iz-icon-ziyuan',NULL,'','',NULL,'',NULL),(1060,'core','用户管理',1059,0,'/core/admin','V','View',0,'','view','core:admin:view','iz-icon-sys-user',NULL,'','',NULL,'',NULL),(1064,'core','权限管理',1,3,'#3','M',NULL,1,'','view',NULL,'iz-icon-quanxian',NULL,'','',NULL,'',NULL),(1065,'core','角色管理',1064,1,'/core/role','V','View',0,'','view','core:role:view','iz-icon-sys-role',NULL,'','',NULL,'',NULL),(1066,'core','菜单管理',1058,2,'/core/menu','V','View',0,'浏览菜单信息','view','core:menu:view','iz-icon-menu',NULL,'','',NULL,'',NULL),(1069,'core','新增',1066,1,'/core/menu/add','A','Add',1,'新增菜单','view','core:menu:add','#','M','','',NULL,'',NULL),(1070,'core','编辑',1066,2,'/core/menu/edit','A','Edit',1,'修改菜单信息','view','core:menu:edit','#','T','','',NULL,'',NULL),(1071,'core','删除',1066,3,'/core/menu/del','A','Del',1,'删除菜单','view','core:menu:del','#','T','','',NULL,'',NULL),(1072,'core','查询',1066,0,'/core/menu/view','A','View',1,'浏览菜单信息','view','core:menu:view','#','M','','',NULL,'',NULL),(1090,'core','字典管理',1058,8,'/core/dictType','V','View',0,'','view','core:dictType:view','iz-icon-zidianguanli',NULL,'','',NULL,'',NULL),(1092,'core','新增',1090,0,'/core/dictType/add','A','Add',1,'新增字典类型','view','core:dictType:add','#','M','','',NULL,'',NULL),(1093,'core','修改',1090,0,'/core/dictType/edit','A','Edit',1,'修改字典类型','view','core:dictType:edit','#','T','','',NULL,'',NULL),(1094,'core','查询',1090,0,'/core/dictType/view','A','View',1,'浏览字典信息','view','core:dictType:view','#','M','','',NULL,'',NULL),(1095,'core','字典数据',1090,0,'/core/dictData/listByType','A','DictData',1,'','view','core:dictData:view','iz-icon-dictData','T','','',NULL,'',NULL),(1101,'dev','代码生成',1107,0,'#','M',NULL,1,'','view',NULL,'iz-icon-kaifa',NULL,'','',NULL,'',NULL),(1102,'dev','多模块配置',1101,0,'/dev/module','V','View',0,'','view',NULL,'iz-icon-mgen',NULL,'','',NULL,'',NULL),(1103,'dev','查询',1102,0,'/dev/module/view','A','View',0,'','view',NULL,'#','M','','',NULL,'',NULL),(1104,'dev','新增',1102,0,'/dev/module/add','A','Add',0,'','view',NULL,'#','M','','',NULL,'',NULL),(1105,'dev','编辑',1102,0,'/dev/module/edit','A','Edit',0,'','view','','#','T','','',NULL,'',NULL),(1107,'dev','开发设置',0,0,'#','M',NULL,1,'','view',NULL,'iz-icon-kaifa',NULL,'','',NULL,'',NULL),(1108,'core','组织机构',1059,0,'/core/org','V','View',0,'','view','core:org:view','iz-icon-jigouguanli',NULL,'','',NULL,'',NULL),(1113,'core','新增',1108,0,'/core/org/add','A','Add',1,'新增组织机构','view','core:org:add','#','M','','',NULL,'',NULL),(1114,'core','删除',1108,0,'/core/org/del','A','Del',1,'删除组织机构','view','core:org:del','#','T','','',NULL,'',NULL),(1115,'core','修改',1108,0,'/core/org/edit','A','Edit',1,'修改组织机构','view','core:org:edit','#','T','','',NULL,'',NULL),(1116,'core','查询',1108,0,'/core/org/view','A','View',1,'浏览组织机构信息','view','core:org:view','#','M','','',NULL,'',NULL),(1117,'core','新增',1060,0,'/core/admin/add','A','Add',1,'','view','core:admin:add','#','M','','',NULL,'',NULL),(1118,'core','删除',1060,0,'/core/admin/del','A','Del',1,'删除用户信息','view','core:admin:del','#','T','','',NULL,'',NULL),(1119,'core','修改',1060,0,'/core/admin/edit','A','Edit',1,'修改用户信息','view','core:admin:edit','#','T','','',NULL,'',NULL),(1120,'core','查询',1060,0,'/core/admin/view','A','View',1,'浏览用户信息','view','core:admin:view','#','M','','',NULL,'',NULL),(1121,'core','新增',1065,0,'/core/role/add','A','Add',1,'新增用户角色','view','core:role:add','#','M','','',NULL,'',NULL),(1122,'core','删除',1065,0,'/core/role/del','A','Del',1,'删除用户角色','view','core:role:del','#','T','','',NULL,'',NULL),(1123,'core','修改',1065,0,'/core/role/edit','A','Edit',1,'修改用户角色','view','core:role:edit','#','T','','',NULL,'',NULL),(1124,'core','查询',1065,0,'/core/role/view','A','View',1,'浏览用户角色','view','core:role:view','#','M','','',NULL,'',NULL),(1125,'core','修改密码',1060,0,'/core/admin/pwd','A','Pwd',1,'修改用户密码','view','core:admin:pwd','#','T','修改用户密码','',NULL,'',NULL),(1126,'dev','开发文档',1107,0,'#','M',NULL,1,'','view',NULL,'iz-icon-devdoc',NULL,'','',NULL,'',NULL),(1127,'core','系统配置',1058,5,'/core/config','V','View',0,'','view','core:config:view','iz-icon-sysconfig',NULL,'','',NULL,'',NULL),(1128,'core','新增',1127,0,'/core/config/add','A','Add',1,'新增系统配置','view','core:config:add','#','M','','',NULL,'',NULL),(1129,'core','删除',1127,0,'/core/config/del','A','Del',1,'删除系统配置','view','core:config:del','#','T','','',NULL,'',NULL),(1130,'core','修改',1127,0,'/core/config/edit','A','Edit',1,'修改系统配置','view','core:config:edit','#','T','','',NULL,'',NULL),(1131,'core','查询',1127,0,'/core/config/view','A','View',1,'浏览系统配置','view','core:config:view','#','M','','',NULL,'',NULL),(1132,'core','访问日志',1058,18,'/core/log','V','View',0,'','view','core:log:view','iz-icon-log',NULL,'','',NULL,'',NULL),(1133,'core','新增',1132,0,'/core/log/add','A','Add',1,'','disabled','core:log:add','#','M','访问日志无需删除','',NULL,'',NULL),(1134,'core','删除',1132,0,'/core/log/del','A','Del',1,'删除访问日志','view','core:log:del','#','M','','',NULL,'',NULL),(1135,'core','修改',1132,0,'/core/log/edit','A','Edit',1,'','disabled','core:log:edit','#','T','操作日志无需编辑','',NULL,'',NULL),(1136,'core','查询',1132,0,'/core/log/view','A','View',0,'','view','core:log:view','#','M','','',NULL,'',NULL),(1137,'core','详情',1066,0,'','A','Detail',0,'','view','core:menu:detail','#','T','','',NULL,'',NULL),(1138,'dev','导入表',1102,0,'/dev/module/import','A','gen',0,'','view','dev:module:import','#','T','','',NULL,'',NULL),(1139,'dev','表信息配置',1101,0,'/dev/table','V','View',0,'','view','dev:table','iz-icon-table','M','','',NULL,'',NULL),(1140,'dev','新增',1139,0,'/dev/table/add','A','Add',1,'','disabled','dev:table:add','#','M','','',NULL,'',NULL),(1141,'dev','删除',1139,0,'/dev/table/del','A','Del',1,'','view','dev:table:del','#','M','','',NULL,'',NULL),(1142,'dev','修改',1139,0,'/dev/table/edit','A','Edit',1,'','view','dev:table:edit','#','T','','',NULL,'',NULL),(1143,'dev','查询',1139,0,'/dev/table/view','A','View',0,'','view','dev:table:view','#','M','','',NULL,'',NULL),(1144,'dev','生成',1139,0,'/dev/table/gen','A','gen',0,'','view','dev:table:gen','#','AM','','',NULL,'',NULL),(1145,'dev','表字段配置',1101,0,'/dev/field','V','View',0,'','hide','dev:field:view','iz-icon-field','M','','',NULL,'',NULL),(1146,'dev','新增',1145,0,'/dev/field/add','A','Add',1,'','view','dev:fiel:add','#','M','','',NULL,'',NULL),(1147,'dev','删除',1145,0,'/dev/field/del','A','Del',1,'','view','dev:fiel:del','#','T','','',NULL,'',NULL),(1148,'dev','修改',1145,0,'/dev/field/edit','A','Edit',1,'','view','dev:fiel:edit','#','T','','',NULL,'',NULL),(1149,'dev','查询',1145,0,'/dev/field/view','A','View',1,'','view','dev:fiel:view','#','M','','',NULL,'',NULL),(1150,'dev','重新导入',1139,0,'/dev/table/import','A','import',0,'','view','dev:table:import','#','T','','',NULL,'',NULL),(1151,'core','字典数据',1058,0,'/core/dictData','V','View',0,'','hide','core:dictData:view','#','M','','',NULL,'',NULL),(1152,'core','新增',1151,0,'/core/dictData/add','A','Add',1,'','view','core:dictData:add','#','M','','',NULL,'',NULL),(1153,'core','删除',1151,0,'/core/dictData/del','A','Del',1,'','view','core:dictData:del','#','T','','',NULL,'',NULL),(1154,'core','修改',1151,0,'/core/dictData/edit','A','Edit',1,'','view','core:dictData:edit','#','T','','',NULL,'',NULL),(1155,'core','查询',1151,0,'/core/dictData/view','A','View',0,'','view','core:dictData:view','#','M','','',NULL,'',NULL);
/*!40000 ALTER TABLE `t_menu` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `t_menu_view`
--

DROP TABLE IF EXISTS `t_menu_view`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `t_menu_view` (
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
-- Dumping data for table `t_menu_view`
--

LOCK TABLES `t_menu_view` WRITE;
/*!40000 ALTER TABLE `t_menu_view` DISABLE KEYS */;
INSERT INTO `t_menu_view` VALUES (1,'系统管理','默认','iz-icon-xitong',0,'正常','1058,1059,1064','2019-08-16 23:38:45','2019-08-16 15:38:45'),(4,'文章资讯','默认','iz-icon-xinwen',0,'正常',NULL,'2019-08-16 23:38:45','2019-08-16 15:38:45');
/*!40000 ALTER TABLE `t_menu_view` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=1262 DEFAULT CHARSET=utf8 COMMENT='操作日志记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `t_opera_log`
--

LOCK TABLES `t_opera_log` WRITE;
/*!40000 ALTER TABLE `t_opera_log` DISABLE KEYS */;
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
INSERT INTO `t_organize` VALUES (1,0,1,'厦门由源创科技',0,'Company','iteaj',NULL,'iteaj@outlook.com','正常','','2019-11-26 09:20:59','',NULL),(2,1,1,'运维部',0,'Dept','iteaj',NULL,'iteaj@live.cn','正常','','2019-11-26 01:21:01','',NULL),(3,1,1,'技术部',0,'Dept','iteaj',NULL,'iteaj@outlook.com','正常','','2019-11-26 18:19:23','',NULL);
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
INSERT INTO `t_role` VALUES (2,'管理员',2,'1','正常',NULL,'','2019-11-27 16:48:35','',NULL),(3,'技术总监',0,'1','正常','技术总监','','2019-11-27 18:37:24','',NULL),(4,'演示角色',0,'1','正常','用于演示','','2020-04-24 12:03:38','',NULL);
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
INSERT INTO `t_role_menu` VALUES (2,1069),(2,1071),(2,1072),(3,1),(3,1058),(3,1059),(3,1060),(3,1064),(3,1065),(3,1066),(3,1069),(3,1070),(3,1071),(3,1072),(3,1090),(3,1092),(3,1093),(3,1094),(3,1095),(3,1104),(3,1108),(3,1113),(3,1114),(3,1115),(3,1116),(3,1117),(3,1118),(3,1119),(3,1120),(3,1121),(3,1122),(3,1123),(3,1124),(3,1125),(3,1126),(3,1127),(3,1128),(3,1129),(3,1130),(3,1131),(4,1072),(4,1094),(4,1095),(4,1116),(4,1120),(4,1124),(4,1131),(4,1132),(4,1133),(4,1134),(4,1135),(4,1136),(4,1137);
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

-- Dump completed on 2020-05-17 19:36:45
