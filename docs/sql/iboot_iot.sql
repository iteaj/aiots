-- MySQL dump 10.13  Distrib 5.7.17, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: iboot
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
-- Table structure for table `iot_device`
--

DROP TABLE IF EXISTS `iot_device`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iot_device` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `ip` varchar(32) DEFAULT NULL COMMENT '设备ip',
  `uid` bigint(20) NOT NULL COMMENT '设备唯一的uid',
  `name` varchar(64) NOT NULL COMMENT '设备名称',
  `port` int(11) DEFAULT NULL COMMENT '设备端口',
  `model` int(11) DEFAULT NULL COMMENT '设备型号',
  `status` enum('online','offline') NOT NULL DEFAULT 'offline' COMMENT '设备状态',
  `user_id` bigint(20) DEFAULT NULL COMMENT '所属会员',
  `device_sn` varchar(64) NOT NULL COMMENT '设备编号',
  `switch_time` timestamp NULL DEFAULT NULL COMMENT '设备状态切换时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  `device_type_id` bigint(20) NOT NULL COMMENT '设备所属类型',
  PRIMARY KEY (`id`),
  UNIQUE KEY `iot_device_uid_uindex` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='设备表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_device`
--

LOCK TABLES `iot_device` WRITE;
/*!40000 ALTER TABLE `iot_device` DISABLE KEYS */;
INSERT INTO `iot_device` VALUES (3,NULL,1537990013924249600,'DTU模拟设备',NULL,NULL,'offline',NULL,'TCP_IOT_MODBUS_01','2022-07-24 09:41:29','2022-06-18 10:46:08',NULL,4),(4,'127.0.0.1',1550795103030583300,'西门子S200',102,3,'offline',NULL,'sieme001','2022-07-24 09:41:29','2022-07-23 18:48:59',NULL,2),(5,'127.0.0.1',1550801539290628000,'欧姆龙PLC',9600,7,'offline',NULL,'omron001','2022-07-24 09:41:29','2022-07-23 19:14:34',NULL,3);
/*!40000 ALTER TABLE `iot_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_device_child`
--

DROP TABLE IF EXISTS `iot_device_child`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iot_device_child` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '子设备名称',
  `uid` bigint(20) NOT NULL COMMENT '设备uid',
  `child_sn` varchar(64) NOT NULL COMMENT '子设备编号',
  `protocol_type` smallint(6) NOT NULL COMMENT '协议类型',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `iot_device_child_child_sn_index` (`child_sn`),
  KEY `iot_device_child_uid_index` (`uid`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 COMMENT='子设备';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_device_child`
--

LOCK TABLES `iot_device_child` WRITE;
/*!40000 ALTER TABLE `iot_device_child` DISABLE KEYS */;
INSERT INTO `iot_device_child` VALUES (1,'继电器',1537990013924249600,'1',1,'2022-06-18 10:48:32');
/*!40000 ALTER TABLE `iot_device_child` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_device_model`
--

DROP TABLE IF EXISTS `iot_device_model`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iot_device_model` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `type_id` int(11) NOT NULL COMMENT '设备类型id',
  `model` varchar(36) DEFAULT NULL COMMENT '型号',
  `remark` varchar(128) DEFAULT NULL COMMENT '型号说明',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8 COMMENT='设备型号';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_device_model`
--

LOCK TABLES `iot_device_model` WRITE;
/*!40000 ALTER TABLE `iot_device_model` DISABLE KEYS */;
INSERT INTO `iot_device_model` VALUES (1,2,'S7-200',NULL),(2,2,'S7-300',NULL),(3,2,'S7-400',NULL),(4,2,'S7-1200',NULL),(5,2,'S7-1500',NULL),(6,2,'S7-200Smart',NULL),(7,3,'omron',NULL);
/*!40000 ALTER TABLE `iot_device_model` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_device_type`
--

DROP TABLE IF EXISTS `iot_device_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iot_device_type` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `pid` bigint(20) NOT NULL DEFAULT '0' COMMENT '父id',
  `path` varchar(128) DEFAULT NULL COMMENT '路径',
  `name` varchar(64) NOT NULL COMMENT '类型名称',
  `alias` varchar(36) DEFAULT NULL COMMENT '类型别名',
  `image` varchar(128) DEFAULT NULL COMMENT '类型图地址',
  `remark` varchar(256) DEFAULT NULL COMMENT '类型说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `iot_device_type_alias_index` (`alias`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COMMENT='设备类型';
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
-- Table structure for table `iot_group_point`
--

DROP TABLE IF EXISTS `iot_group_point`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iot_group_point` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `point_id` bigint(20) NOT NULL COMMENT '所属点位',
  `group_id` bigint(20) NOT NULL COMMENT '所属点位组',
  PRIMARY KEY (`id`),
  KEY `iot_group_point_group_id_index` (`group_id`),
  KEY `iot_group_point_point_id_index` (`point_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='组点位';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_group_point`
--

LOCK TABLES `iot_group_point` WRITE;
/*!40000 ALTER TABLE `iot_group_point` DISABLE KEYS */;
/*!40000 ALTER TABLE `iot_group_point` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_point_group`
--

DROP TABLE IF EXISTS `iot_point_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iot_point_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(36) NOT NULL COMMENT '组名称',
  `model_id` int(11) NOT NULL COMMENT '所属型号',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='点位组';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_point_group`
--

LOCK TABLES `iot_point_group` WRITE;
/*!40000 ALTER TABLE `iot_point_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `iot_point_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_point_position`
--

DROP TABLE IF EXISTS iot_signal;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `iot_point_position` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(64) NOT NULL COMMENT '点位名称',
  `address` varchar(16) NOT NULL COMMENT '点位地址',
  `model_id` int(11) NOT NULL COMMENT '型号id',
  `field_name` varchar(32) NOT NULL COMMENT '点位字段名称',
  `field_type` char(16) DEFAULT NULL COMMENT '字段类型(int、float、...)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='寄存器点位';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_point_position`
--

LOCK TABLES iot_signal WRITE;
/*!40000 ALTER TABLE iot_signal DISABLE KEYS */;
/*!40000 ALTER TABLE iot_signal ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-07-24 22:49:33
