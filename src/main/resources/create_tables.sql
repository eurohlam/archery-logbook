DROP TABLE IF EXISTS `archery_shot`;
DROP TABLE IF EXISTS `archery_end`;
DROP TABLE IF EXISTS `archery_round`;
DROP TABLE IF EXISTS `archery_competition`;
DROP TABLE IF EXISTS `archery_distance_settings`;
DROP TABLE IF EXISTS `archery_bow`;
DROP TABLE IF EXISTS `archery_archer`;
DROP TABLE IF EXISTS `archery_club`;
DROP TABLE IF EXISTS `archery_api_subscriber`;

CREATE TABLE `archery_api_subscriber` (
  `access_key` varchar(20) NOT NULL,
  `secret_key` varchar(50) NOT NULL,
  PRIMARY KEY (`access_key`),
  UNIQUE KEY `secret_key` (`secret_key`)
);

INSERT INTO `archery_api_subscriber` (`access_key`, `secret_key`) VALUES
('testAccessKey', 'testSecret');

CREATE TABLE `archery_club` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(50) NOT NULL,
  `country` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `url` varchar(250) DEFAULT NULL,
  `archived` boolean DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`),
  INDEX `idx_archery_club_idarc` (`id`, `archived`),
  INDEX `idx_archery_club_namearc` (`name`, `archived`)
);

INSERT INTO `archery_club` (`id`, `name`, `country`, `city`) VALUES
(1,	'Mana Club', 'NZ', 'WGT');


CREATE TABLE `archery_archer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `first_name` varchar(50) NOT NULL,
  `last_name` varchar(50) DEFAULT NULL,
  `email` varchar(100) NOT NULL,
  `club_id` bigint(20) DEFAULT NULL,
  `archived` boolean DEFAULT false,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  KEY `club_id` (`club_id`),
  INDEX `idx_archery_archer_idarc` (`id`, `archived`),
  INDEX `idx_archery_archer_clubarc` (`club_id`, `archived`),
  INDEX `idx_archery_archer_emailarc` (`email`, `archived`),
  CONSTRAINT `archery_archer_ibfk_1` FOREIGN KEY (`club_id`) REFERENCES `archery_club` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_archer` (`id`, `first_name`, `last_name`, `email`, `club_id`) VALUES
(1,	'Vasya',	'Pupkin', 'vasya@pupkin.org',	1);


CREATE TABLE `archery_bow` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `archer_id` bigint(20) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type` enum('RECURVE','COMPOUND','TRADITIONAL','BAREBOW','LONGBOW') NOT NULL,
  `level` enum('BEGINNER','INTERMEDIATE','ADVANCED') DEFAULT NULL,
  `poundage` varchar(50) DEFAULT NULL,
  `compound_model` varchar(50) DEFAULT NULL,
  `riser_model` varchar(50) DEFAULT NULL,
  `limbs_model` varchar(50) DEFAULT NULL,
  `traditional_model` varchar(50) DEFAULT NULL,
  `longbow_model` varchar(50) DEFAULT NULL,
  `archived` boolean DEFAULT false,
  PRIMARY KEY (`id`),
  KEY `archer_id` (`archer_id`),
  INDEX `idx_archery_bow_idarc` (`id`, `archived`),
  INDEX `idx_archery_bow_idarcher` (`id`, `archer_id`),
  INDEX `idx_archery_bow_archerarc` (`archer_id`, `archived`),
  CONSTRAINT `archery_bow_ibfk_1` FOREIGN KEY (`archer_id`) REFERENCES `archery_archer` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_bow` (`id`, `archer_id`, `name`, `type`, `level`, `poundage`, `compound_model`, `riser_model`, `limbs_model`) VALUES
(1,	1,	'My bow',	'RECURVE',	'INTERMEDIATE',	'28-32',	NULL,	'Black',	'hren'),
(5,	1,	'My best bow',	'BAREBOW',	'INTERMEDIATE',	'28-32',	NULL,	'Black',	NULL);

CREATE TABLE `archery_distance_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `bow_id` bigint(20) NOT NULL,
  `distance` int(11) NOT NULL,
  `sight` varchar(50) NOT NULL,
  `is_tested` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`),
  UNIQUE KEY `bow_id` (`bow_id`,`distance`),
  CONSTRAINT `archery_distance_settings_ibfk_1` FOREIGN KEY (`bow_id`) REFERENCES `archery_bow` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_distance_settings` (`id`, `bow_id`, `distance`, `sight`, `is_tested`) VALUES
(1,	1,	20,	5,	'0'),
(2,	5,	30,	6,	'0');


CREATE TABLE `archery_competition` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `archer_id` bigint(20) NOT NULL,
  `competition_type` varchar(50) NOT NULL,
  `competition_date` datetime NOT NULL DEFAULT current_timestamp(),
  `age_class` varchar(50) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `comment` longtext,
  `archived` boolean DEFAULT false,
  PRIMARY KEY (`id`),
  INDEX `idx_archery_cmpt_type` (`archer_id`, `competition_type`, `archived`),
  INDEX `idx_archery_cmpt_archer` (`archer_id`, `archived`),
  CONSTRAINT `archery_cmpt_arc_ibfk_1` FOREIGN KEY (`archer_id`) REFERENCES `archery_archer` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_competition` (`id`, `archer_id`, `competition_type`, `comment`, `country`, `city`) VALUES
(1,	1,	'SHORT_CANADIAN_1200', 'test', 'NZ', 'WGT');

CREATE TABLE `archery_round` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `archer_id` bigint(20) NOT NULL,
  `bow_id` bigint(20) NOT NULL,
  `round_date` datetime NOT NULL DEFAULT current_timestamp(),
  `distance` varchar(50) NOT NULL,
  `target_face` varchar(50) NOT NULL,
  `competition_id` bigint(20),
  `comment` longtext,
  `country` varchar(50) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `archived` boolean DEFAULT false,
  PRIMARY KEY (`id`),
  KEY `archery_round_ibfk_1` (`archer_id`),
  KEY `archery_round_ibfk_2` (`bow_id`),
  INDEX `idx_archery_round_idarc` (`id`, `archived`),
  INDEX `idx_archery_round_archerarc` (`archer_id`, `archived`),
  INDEX `idx_archery_round_bowarc` (`bow_id`, `archived`),
  CONSTRAINT `archery_round_arc_ibfk_1` FOREIGN KEY (`archer_id`) REFERENCES `archery_archer` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  CONSTRAINT `archery_round_bow_ibfk_2` FOREIGN KEY (`bow_id`, `archer_id`) REFERENCES `archery_bow` (`id`,`archer_id`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `archery_round_cmpt_ibfk_3` FOREIGN KEY (`competition_id`) REFERENCES `archery_competition` (`id`) ON DELETE NO ACTION ON UPDATE NO ACTION
);

INSERT INTO `archery_round` (`id`, `archer_id`, `bow_id`, `round_date`, `distance`, `target_face`, `competition_id`, `comment`, `country`, `city`) VALUES
(1,	1,	1,	'2023-02-16 22:29:32',	'50', 'TF_122cm', 1, 'for fun', 'NZ', 'WGT');


CREATE TABLE `archery_end` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `round_id` bigint(20) NOT NULL,
  `end_number` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `archery_end_unq` (`round_id`,`end_number`),
  CONSTRAINT `archery_end_ibfk_1` FOREIGN KEY (`round_id`) REFERENCES `archery_round` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_end` (`id`, `round_id`, `end_number`) VALUES
(1,	1,	1),
(2,	1,	2),
(3,	1,	3);


CREATE TABLE `archery_shot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_id` bigint(20) NOT NULL,
  `shot_number` int(11) NOT NULL,
  `shot_score` int(11) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `archery_shot_unq` (`end_id`,`shot_number`),
  CONSTRAINT `archery_shot_ibfk_1` FOREIGN KEY (`end_id`) REFERENCES `archery_end` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION
);

INSERT INTO `archery_shot` (`id`, `end_id`, `shot_number`, `shot_score`) VALUES
(1,	1,	1,	8),
(3,	1,	2,	6),
(4,	1,	3,	7),
(5,	2,	1,	8),
(6,	2,	2,	9),
(7,	2,	3,	10),
(8,	3,	1,	8),
(9,	3,	2,	9),
(10,3,	3,	9);


/* best score by distance calculation*/
select * from
             (select sums.*,
                      row_number() over (partition by sums.distance order by sums.round_sum desc) ord
               from (select distinct r.*,
                                     sum(s.shot_score) over (partition by r.id order by null) as round_sum
                     from archery_round r,
                          archery_end e,
                          archery_shot s
                     where r.id = e.round_id
                       and e.id = s.end_id
                       and r.archer_id = 60
                       and r.archived = false) sums) mx
where mx.ord=1;
