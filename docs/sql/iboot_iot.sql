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
INSERT INTO `iot_device` VALUES (3,NULL,1537990013924249602,'电箱1号',NULL,NULL,'offline',NULL,'dx0000001','2022-07-05 16:15:13','2022-06-18 10:46:08',4,NULL);
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
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-07-06  9:16:46
