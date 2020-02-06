CREATE TABLE `organism` (
  `organism_name` varchar(20) NOT NULL,
  `species_code` varchar(10) NOT NULL,
  `species` varchar(50) DEFAULT NULL,
  `subspecies` varchar(50) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`organism_name`),
  UNIQUE KEY `organism_species_code_uindex` (`species_code`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1
CREATE TABLE `program` (
  `program_id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `config_format` enum('xml') DEFAULT NULL,
  PRIMARY KEY (`program_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=latin1
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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=latin1
CREATE TABLE `experiment` (
  `experiment_id` int(11) NOT NULL AUTO_INCREMENT,
  `experiment_code` varchar(10) NOT NULL,
  `organism_name` varchar(20) NOT NULL,
  `user_id` int(11) NOT NULL,
  `description` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`experiment_id`),
  UNIQUE KEY `experiment_experiment_code_organism_name_uindex` (`experiment_code`,`organism_name`),
  KEY `experiment_organism_organism_name_fk` (`organism_name`),
  KEY `experiment_user_user_id_fk` (`user_id`),
  CONSTRAINT `experiment_organism_organism_name_fk` FOREIGN KEY (`organism_name`) REFERENCES `organism` (`organism_name`) ON UPDATE CASCADE,
  CONSTRAINT `experiment_user_user_id_fk` FOREIGN KEY (`user_id`) REFERENCES `user` (`user_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=latin1
CREATE TABLE `genotype` (
  `genotype_id` int(11) NOT NULL AUTO_INCREMENT,
  `genotype_name` varchar(20) DEFAULT NULL,
  `organism_name` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`genotype_id`),
  UNIQUE KEY `genotype_genotype_name_organism_name_uindex` (`genotype_name`,`organism_name`),
  KEY `genotype_organism_organism_name_fk` (`organism_name`),
  CONSTRAINT `genotype_organism_organism_name_fk` FOREIGN KEY (`organism_name`) REFERENCES `organism` (`organism_name`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1
CREATE TABLE `program_dependency` (
  `program_id` int(11) NOT NULL,
  `program_dependency_id` int(11) NOT NULL,
  PRIMARY KEY (`program_id`,`program_dependency_id`),
  KEY `program_dependency_program_program_id_fk_2` (`program_dependency_id`),
  CONSTRAINT `program_dependency_program_program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON UPDATE CASCADE,
  CONSTRAINT `program_dependency_program_program_id_fk_2` FOREIGN KEY (`program_dependency_id`) REFERENCES `program` (`program_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1
CREATE TABLE `saved_config` (
  `config_id` int(11) NOT NULL AUTO_INCREMENT,
  `program_id` int(11) NOT NULL,
  `name` varchar(50) DEFAULT NULL,
  `contents` mediumtext DEFAULT NULL,
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `saved_config_program_id_name_uindex` (`program_id`,`name`),
  CONSTRAINT `saved_config_program_program_id_fk` FOREIGN KEY (`program_id`) REFERENCES `program` (`program_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=442 DEFAULT CHARSET=latin1
CREATE TABLE `seed` (
  `seed_id` int(11) NOT NULL AUTO_INCREMENT,
  `experiment_id` int(11) NOT NULL,
  `seed_name` varchar(20) NOT NULL,
  `genotype_id` int(11) DEFAULT NULL,
  `dry_shoot` float DEFAULT NULL,
  `dry_root` float DEFAULT NULL,
  `wet_shoot` float DEFAULT NULL,
  `wet_root` float DEFAULT NULL,
  `sterilization_chamber` varchar(10) DEFAULT NULL,
  `description` varchar(100) DEFAULT NULL,
  `imaging_interval_unit` enum('day','hour') NOT NULL,
  `imaging_start_date` datetime DEFAULT NULL,
  PRIMARY KEY (`seed_id`),
  UNIQUE KEY `seed_experiment_id_seed_name_uindex` (`experiment_id`,`seed_name`),
  KEY `seed_seed_name_index` (`seed_name`),
  KEY `seed_genotype_genotype_id_fk` (`genotype_id`),
  CONSTRAINT `seed_experiment_experiment_id_fk` FOREIGN KEY (`experiment_id`) REFERENCES `experiment` (`experiment_id`) ON UPDATE CASCADE,
  CONSTRAINT `seed_genotype_genotype_id_fk` FOREIGN KEY (`genotype_id`) REFERENCES `genotype` (`genotype_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3329 DEFAULT CHARSET=latin1
CREATE TABLE `dataset` (
  `dataset_id` int(11) NOT NULL AUTO_INCREMENT,
  `seed_id` int(11) NOT NULL,
  `timepoint` varchar(10) NOT NULL,
  PRIMARY KEY (`dataset_id`),
  KEY `dataset_seed_seed_id_fk` (`seed_id`),
  KEY `dataset_timepoint_index` (`timepoint`),
  CONSTRAINT `dataset_seed_seed_id_fk` FOREIGN KEY (`seed_id`) REFERENCES `seed` (`seed_id`) ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7334 DEFAULT CHARSET=latin1
CREATE TABLE `dataset_image_type` (
  `dataset_id` int(11) NOT NULL,
  `image_type` varchar(10) NOT NULL,
  PRIMARY KEY (`dataset_id`,`image_type`),
  CONSTRAINT `dataset_image_type_dataset_dataset_id_fk` FOREIGN KEY (`dataset_id`) REFERENCES `dataset` (`dataset_id`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=latin1
CREATE TABLE `program_run` (
  `run_id` int(11) NOT NULL AUTO_INCREMENT,
  `user_id` int(11) NOT NULL,
  `program_id` int(11) NOT NULL,
  `dataset_id` int(11) NOT NULL,
  `saved` tinyint(1) NOT NULL,
  `red_flag` tinyint(1) NOT NULL,
  `run_date` datetime NOT NULL,
  `input_runs` mediumtext DEFAULT NULL CHECK (json_valid(`input_runs`)),
  `saved_config_id` int(11) DEFAULT NULL,
  `unsaved_config_contents` mediumtext DEFAULT NULL,
  `descriptors` mediumtext DEFAULT NULL,
  `results` mediumtext DEFAULT NULL CHECK (json_valid(`results`)),
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
) ENGINE=InnoDB AUTO_INCREMENT=43722 DEFAULT CHARSET=latin1