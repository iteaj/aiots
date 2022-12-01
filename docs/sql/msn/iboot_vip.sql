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
-- Table structure for table `vip_user`
--

DROP TABLE IF EXISTS `vip_user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vip_user` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `sex` enum('man','woman') COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '性别',
  `email` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `phone` char(11) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '手机号',
  `identity` char(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '身份证号码',
  `account` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '会员账号',
  `password` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '会员密码',
  `nick_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '会员昵称',
  `real_name` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '姓名',
  `wechat_id` varchar(32) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '微信openId',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='会员';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vip_user`
--

LOCK TABLES `vip_user` WRITE;
/*!40000 ALTER TABLE `vip_user` DISABLE KEYS */;
/*!40000 ALTER TABLE `vip_user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2022-05-14 14:01:44
