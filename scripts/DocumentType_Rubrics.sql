/*
Navicat MySQL Data Transfer

Source Server         : eie-web-pro-1-web
Source Server Version : 50095
Source Host           : localhost:3306
Source Database       : reviewer

Target Server Type    : MYSQL
Target Server Version : 50095
File Encoding         : 65001

Date: 2012-07-13 14:29:13
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `DocumentType_Rubrics`
-- ----------------------------
DROP TABLE IF EXISTS `DocumentType_Rubrics`;
CREATE TABLE `DocumentType_Rubrics` (
  `DocumentType_id` bigint(20) NOT NULL,
  `rubrics_id` bigint(20) NOT NULL,
  UNIQUE KEY `rubrics_id` (`rubrics_id`),
  KEY `FKC656489CF1DB4CDC` (`DocumentType_id`),
  KEY `FKC656489C40CD22E3` (`rubrics_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of DocumentType_Rubrics
-- ----------------------------
INSERT INTO DocumentType_Rubrics VALUES ('1', '1');
INSERT INTO DocumentType_Rubrics VALUES ('1', '2');
INSERT INTO DocumentType_Rubrics VALUES ('1', '3');
INSERT INTO DocumentType_Rubrics VALUES ('1', '4');
INSERT INTO DocumentType_Rubrics VALUES ('1', '5');
INSERT INTO DocumentType_Rubrics VALUES ('1', '6');
INSERT INTO DocumentType_Rubrics VALUES ('1', '7');
INSERT INTO DocumentType_Rubrics VALUES ('1', '8');
INSERT INTO DocumentType_Rubrics VALUES ('2', '9');
INSERT INTO DocumentType_Rubrics VALUES ('2', '10');
INSERT INTO DocumentType_Rubrics VALUES ('2', '11');
INSERT INTO DocumentType_Rubrics VALUES ('2', '12');
INSERT INTO DocumentType_Rubrics VALUES ('2', '13');
INSERT INTO DocumentType_Rubrics VALUES ('2', '14');
