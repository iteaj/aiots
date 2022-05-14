-- MySQL dump 10.13  Distrib 8.0.28, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: izone
-- ------------------------------------------------------
-- Server version	8.0.28

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
  `id` bigint NOT NULL,
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备名称',
  `status` enum('online','offline') COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'offline' COMMENT '设备状态',
  `ip` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '设备ip',
  `port` int DEFAULT NULL COMMENT '设备端口',
  `device_sn` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '设备编号',
  `product_id` bigint NOT NULL COMMENT '设备所属产品',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NULL DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设备表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_device`
--

LOCK TABLES `iot_device` WRITE;
/*!40000 ALTER TABLE `iot_device` DISABLE KEYS */;
/*!40000 ALTER TABLE `iot_device` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `iot_product`
--

DROP TABLE IF EXISTS `iot_product`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `iot_product` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `pid` bigint NOT NULL DEFAULT '0' COMMENT '父id',
  `path` varchar(128) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品id路径',
  `name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '产品名称',
  `remark` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '产品说明',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设备产品';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `iot_product`
--

LOCK TABLES `iot_product` WRITE;
/*!40000 ALTER TABLE `iot_product` DISABLE KEYS */;
/*!40000 ALTER TABLE `iot_product` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-14 14:00:40
