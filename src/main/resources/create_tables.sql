
DROP TABLE IF EXISTS `archery_club`;
CREATE TABLE `archery_club` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
);

INSERT INTO `archery_club` (`id`, `name`) VALUES
(1,	'Mana Club');

DROP TABLE IF EXISTS `archery_archer`;
CREATE TABLE `archery_archer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) NOT NULL,
  `club_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `username` (`username`),
  KEY `club_id` (`club_id`),
  CONSTRAINT `archery_archer_ibfk_1` FOREIGN KEY (`club_id`) REFERENCES `archery_club` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_archer` (`id`, `username`, `password`, `first_name`, `last_name`, `club_id`) VALUES
(1,	'tester',	'tester',	'Vasya',	'Pupkin',	1);

DROP TABLE IF EXISTS `archery_bow`;
CREATE TABLE `archery_bow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `archer_id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type` enum('RECURVE','COMPOUND') NOT NULL,
  `level` enum('BEGINNER','INTERMEDIATE','ADVANCED') DEFAULT NULL,
  `poundage` varchar(50) DEFAULT NULL,
  `compound_model` varchar(50) DEFAULT NULL,
  `riser_model` varchar(50) DEFAULT NULL,
  `limbs_model` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `archer_id` (`archer_id`),
  CONSTRAINT `archery_bow_ibfk_1` FOREIGN KEY (`archer_id`) REFERENCES `archery_archer` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_bow` (`id`, `archer_id`, `name`, `type`, `level`, `poundage`, `compound_model`, `riser_model`, `limbs_model`) VALUES
(1,	1,	'My bow',	'RECURVE',	'INTERMEDIATE',	'28-32',	NULL,	'Black',	'hren'),
(5,	1,	'My bets bow',	'RECURVE',	'INTERMEDIATE',	'28-32',	NULL,	'Black',	NULL);



DROP TABLE IF EXISTS `archery_distance_settings`;
CREATE TABLE `archery_distance_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bow_id` bigint(20) NOT NULL,
  `distance` int(11) NOT NULL,
  `sight` int(11) NOT NULL,
  `is_tested` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bow_id` (`bow_id`,`distance`),
  CONSTRAINT `archery_distance_settings_ibfk_1` FOREIGN KEY (`bow_id`) REFERENCES `archery_bow` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_distance_settings` (`id`, `bow_id`, `distance`, `sight`, `is_tested`) VALUES
(1,	1,	20,	5,	'0'),
(2,	5,	30,	6,	'0');


DROP TABLE IF EXISTS `archery_score`;
CREATE TABLE `archery_score` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `archer_id` bigint(20) NOT NULL,
  `bow_id` bigint(20),
  `score_date` datetime NOT NULL DEFAULT current_timestamp(),
  `distance` int(11) NOT NULL,
  `comment` longtext,
  PRIMARY KEY (`id`),
  KEY `archery_score_ibfk_1` (`archer_id`),
  KEY `archery_score_ibfk_2` (`bow_id`),
  CONSTRAINT `archery_score_ibfk_1` FOREIGN KEY (`archer_id`) REFERENCES `archery_archer` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `archery_score_ibfk_2` FOREIGN KEY (`bow_id`) REFERENCES `archery_bow` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO `archery_score` (`id`, `archer_id`, `bow_id`, `score_date`, `distance`, `comment`) VALUES
(1,	1,	1,	'2023-02-16 22:29:32',	20,	'for fun');

DROP TABLE IF EXISTS `archery_end`;
CREATE TABLE `archery_end` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `score_id` bigint(20) NOT NULL,
  `end_number` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `archery_end_unq` (`score_id`,`end_number`),
  CONSTRAINT `archery_end_ibfk_1` FOREIGN KEY (`score_id`) REFERENCES `archery_score` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_end` (`id`, `score_id`, `end_number`) VALUES
(1,	1,	1),
(2,	1,	2),
(3,	1,	3);

DROP TABLE IF EXISTS `archery_round`;
CREATE TABLE `archery_round` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_id` bigint(20) NOT NULL,
  `round_number` int(11) NOT NULL,
  `round_score` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `archery_round_unq` (`end_id`,`round_number`),
  CONSTRAINT `archery_round_ibfk_1` FOREIGN KEY (`end_id`) REFERENCES `archery_end` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_round` (`id`, `end_id`, `round_number`, `round_score`) VALUES
(1,	1,	1,	8),
(3,	1,	2,	6),
(4,	1,	3,	7),
(5,	2,	1,	8),
(6,	2,	2,	9),
(7,	2,	3,	10),
(8,	3,	1,	8),
(9,	3,	2,	9),
(10,3,	3,	9);