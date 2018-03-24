-- MySQL dump 10.13  Distrib 5.7.20, for Win64 (x86_64)
--
-- Host: localhost    Database: haiyun
-- ------------------------------------------------------
-- Server version	5.7.20

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
-- Table structure for table `alive_proxy`
--

DROP TABLE IF EXISTS `alive_proxy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `alive_proxy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `proxy` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=463986 DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `area`
--

DROP TABLE IF EXISTS `area`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `area` (
  `id` int(11) NOT NULL,
  `name` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `parent_id` int(11) DEFAULT NULL,
  `short_name` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `level_type` int(11) DEFAULT NULL,
  `city_code` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `post_code` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `merger_name` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `port`
--

DROP TABLE IF EXISTS `port`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `port` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `code` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name2` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name_cn` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name_cn2` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `longitude` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `latitude` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `nearby` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `area_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `port_distance`
--

DROP TABLE IF EXISTS `port_distance`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `port_distance` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `from_port` varchar(45) CHARACTER SET utf8 DEFAULT NULL,
  `to_port` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `distance` float DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ship_chinaports_all`
--

DROP TABLE IF EXISTS `ship_chinaports_all`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ship_chinaports_all` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name_cn` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name_en` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `flag` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mmsi` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `call_sign` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `imo` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ship_chinaports_cn`
--

DROP TABLE IF EXISTS `ship_chinaports_cn`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ship_chinaports_cn` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name_cn` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name_en` varchar(60) COLLATE utf8_unicode_ci DEFAULT NULL,
  `flag` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `mmsi` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `call_sign` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `imo` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `type` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ship_shipxy`
--

DROP TABLE IF EXISTS `ship_shipxy`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ship_shipxy` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `mmsi` varchar(45) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name_cn` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `name_en` varchar(100) COLLATE utf8_unicode_ci DEFAULT NULL,
  `imo` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `call_sign` varchar(20) COLLATE utf8_unicode_ci DEFAULT NULL,
  `length` float DEFAULT NULL,
  `breadth` float DEFAULT NULL,
  `draught` float DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  `type_str` varchar(60) COLLATE utf8_unicode_ci NOT NULL,
  `crawled` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-03-22 11:42:40
