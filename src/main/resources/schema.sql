-- MySQL dump 10.17  Distrib 10.3.12-MariaDB, for Win64 (AMD64)
--
-- Host: 127.0.0.1    Database: rsa_gia
-- ------------------------------------------------------
-- Server version	10.3.12-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `dataset`
--

DROP TABLE IF EXISTS `dataset`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataset` (
  `dataset_id` int(11) NOT NULL AUTO_INCREMENT,
  `seed_id` int(11) NOT NULL,
  `timepoint` varchar(10) NOT NULL,
  PRIMARY KEY (`dataset_id`),
  KEY `dataset_seed_seed_id_fk` (`seed_id`),
  KEY `dataset_timepoint_index` (`timepoint`),
  CONSTRAINT `dataset_seed_seed_id_fk` FOREIGN KEY (`seed_id`) REFERENCES `seed` (`seed_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dataset_image_type`
--

DROP TABLE IF EXISTS `dataset_image_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dataset_image_type` (
  `dataset_id` int(11) NOT NULL,
  `image_type` varchar(10) NOT NULL,
  PRIMARY KEY (`dataset_id`,`image_type`),
  CONSTRAINT `dataset_image_type_dataset_dataset_id_fk` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `experiment`
--

DROP TABLE IF EXISTS `experiment`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `experiment` (
  `experiment_id` int(11) NOT NULL AUTO_INCREMENT,
  `experiment_code` varchar(10) NOT NULL,
  `organism_name` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `description` varchar(100),
  PRIMARY KEY (`experiment_id`),
  UNIQUE KEY `experiment_experiment_code_organism_name_uindex` (`experiment_code`,`organism_name`),
  KEY `experiment_organism_organism_name_fk` (`organism_name`),
  KEY `experiment_user_user_id_fk` (`user_id`),
  CONSTRAINT `experiment_organism_organism_name_fk` FOREIGN KEY (`organism_name`) REFERENCES `organism` (`organism_name`) ON UPDATE CASCADE,
  CONSTRAINT `experiment_user_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `genotype`
--

DROP TABLE IF EXISTS `genotype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `genotype` (
  `genotype_id` int(11) NOT NULL AUTO_INCREMENT,
  `genotype_name` varchar(20),
  `organism_name` varchar(20),
  PRIMARY KEY (`genotype_id`),
  UNIQUE KEY `genotype_genotype_name_organism_name_uindex` (`genotype_name`,`organism_name`),
  KEY `genotype_organism_organism_name_fk` (`organism_name`),
  CONSTRAINT `genotype_organism_organism_name_fk` FOREIGN KEY (`organism_name`) REFERENCES `organism` (`organism_name`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `organism`
--

DROP TABLE IF EXISTS `organism`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organism` (
  `organism_name` varchar(20) NOT NULL,
  `species_code` varchar(10) NOT NULL,
  `species` varchar(50),
  `subspecies` varchar(50),
  `description` varchar(100),
  PRIMARY KEY (`organism_name`),
  UNIQUE KEY `organism_species_code_uindex` (`species_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program`
--

DROP TABLE IF EXISTS `program`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program` (
  `program_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50),
  `description` varchar(100),
  `config_format` enum('xml'),
  PRIMARY KEY (`program_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program_dependency`
--

DROP TABLE IF EXISTS `program_dependency`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_dependency` (
  `program_id` int(11) NOT NULL,
  `program_dependency_id` int(11) NOT NULL,
  PRIMARY KEY (`program_id`,`program_dependency_id`),
  KEY `program_dependency_program_program_id_fk_2` (`program_dependency_id`),
  CONSTRAINT `program_dependency_program_program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON UPDATE CASCADE,
  CONSTRAINT `program_dependency_program_program_id_fk_2` FOREIGN KEY (`program_dependency_id`) REFERENCES `program` (`program_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `program_run`
--

DROP TABLE IF EXISTS `program_run`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `program_run` (
  `run_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `program_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `saved` tinyint(1) NOT NULL,
  `red_flag` tinyint(1) NOT NULL,
  `run_date` datetime NOT NULL,
  `input_runs` mediumtext CHECK (json_valid(`input_runs`)),
  `saved_config_id` int(11),
  `unsaved_config_contents` mediumtext,
  `descriptors` mediumtext,
  `results` mediumtext CHECK (json_valid(`results`)),
  PRIMARY KEY (`run_id`),
  KEY `program_run_program_program_id_fk` (`program_id`),
  KEY `program_run_saved_config_config_id_fk` (`saved_config_id`),
  KEY `program_run_dataset_dataset_id_fk` (`dataset_id`),
  KEY `program_run_red_flag_index` (`red_flag`),
  KEY `program_run_user_user_id_fk` (`user_id`),
  CONSTRAINT `program_run_dataset_dataset_id_fk` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON UPDATE CASCADE,
  CONSTRAINT `program_run_program_program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON UPDATE CASCADE,
  CONSTRAINT `program_run_saved_config_config_id_fk` FOREIGN KEY (`saved_config_id`) REFERENCES `saved_config` (`config_id`) ON UPDATE CASCADE,
  CONSTRAINT `program_run_user_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `saved_config`
--

DROP TABLE IF EXISTS `saved_config`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `saved_config` (
  `config_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) NOT NULL,
  `name` varchar(50),
  `contents` mediumtext,
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `saved_config_program_id_name_uindex` (`program_id`,`name`),
  CONSTRAINT `saved_config_program_program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `seed`
--

DROP TABLE IF EXISTS `seed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `seed` (
  `seed_id` int(11) NOT NULL AUTO_INCREMENT,
  `experiment_id` int(11) NOT NULL,
  `seed_name` varchar(20) NOT NULL,
  `genotype_id` int(11),
  `dry_shoot` float,
  `dry_root` float,
  `wet_shoot` float,
  `wet_root` float,
  `sterilization_chamber` varchar(10),
  `description` varchar(100),
  `imaging_interval_unit` enum('day','hour') NOT NULL,
  `imaging_start_date` datetime,
  PRIMARY KEY (`seed_id`),
  UNIQUE KEY `seed_experiment_id_seed_name_uindex` (`experiment_id`,`seed_name`),
  KEY `seed_seed_name_index` (`seed_name`),
  KEY `seed_genotype_genotype_id_fk` (`genotype_id`),
  CONSTRAINT `seed_experiment_experiment_id_fk` FOREIGN KEY (`experiment_id`) REFERENCES `experiment` (`experiment_id`) ON UPDATE CASCADE,
  CONSTRAINT `seed_genotype_genotype_id_fk` FOREIGN KEY (`genotype_id`) REFERENCES `genotype` (`genotype_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user` (
  `user_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_name` varchar(20) NOT NULL,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `lab_name` varchar(20) NOT NULL,
  `access_level` enum('Researcher','Admin') NOT NULL,
  `active` tinyint(1) NOT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `user_user_name_uindex` (`user_name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `program`
--

LOCK TABLES `program` WRITE;
/*!40000 ALTER TABLE `program` DISABLE KEYS */;
INSERT INTO `program` VALUES (1,'scale',NULL,NULL),(2,'crop',NULL,NULL),(3,'giaroot_2d',NULL,'xml'),(4,'rootwork_3d',NULL,NULL),(5,'rootwork_3d_perspective',NULL,NULL),(6,'gia3d_v2',NULL,'xml'),(7,'qc',NULL,NULL),(8,'qc2',NULL,NULL),(9,'qc3',NULL,NULL);
/*!40000 ALTER TABLE `program` ENABLE KEYS */;
UNLOCK TABLES;

/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
