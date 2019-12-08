# ************************************************************
# Sequel Pro SQL dump
# Version 5446
#
# https://www.sequelpro.com/
# https://github.com/sequelpro/sequelpro
#
# Host: 101.6.5.213 (MySQL 5.7.28-0ubuntu0.18.04.4)
# Database: healthqa
# Generation Time: 2019-12-08 02:36:36 +0000
# ************************************************************


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
SET NAMES utf8mb4;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;


# Dump of table answer
# ------------------------------------------------------------

CREATE TABLE `answer` (
  `id` bigint(20) NOT NULL,
  `plength` int(11) DEFAULT NULL,
  `anno_content` mediumtext,
  `content` mediumtext,
  `question` bigint(20) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `index` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `question_i` (`question`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table piece
# ------------------------------------------------------------

CREATE TABLE `piece` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT,
  `pos` int(11) DEFAULT NULL,
  `category` char(31) DEFAULT NULL,
  `category_2` char(31) DEFAULT NULL,
  `context` int(11) DEFAULT NULL,
  `anno_content` mediumtext,
  `content` mediumtext,
  `question` bigint(20) DEFAULT NULL,
  `answer` bigint(20) DEFAULT NULL,
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `index` int(11) DEFAULT '0',
  PRIMARY KEY (`id`),
  KEY `question_i` (`question`),
  KEY `answer_i` (`answer`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



# Dump of table question
# ------------------------------------------------------------

CREATE TABLE `question` (
  `id` bigint(20) NOT NULL DEFAULT '0',
  `anno_content` mediumtext,
  `title` mediumtext,
  `content` mediumtext,
  `structure` mediumtext,
  `interrogative` text COMMENT '疑问句',
  `interrogative_word` varchar(15) DEFAULT NULL,
  `keyword` varchar(511) DEFAULT NULL COMMENT '疑问句中的关键词',
  `type` char(15) DEFAULT NULL COMMENT '问题类型',
  `answer_type` char(15) DEFAULT NULL COMMENT '答案类型',
  `time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `index` int(11) DEFAULT '0',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;




/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
