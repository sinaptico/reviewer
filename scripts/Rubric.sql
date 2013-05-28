/*
Navicat MySQL Data Transfer

Source Server         : eie-web-pro-1-web
Source Server Version : 50095
Source Host           : localhost:3306
Source Database       : reviewer

Target Server Type    : MYSQL
Target Server Version : 50095
File Encoding         : 65001

Date: 2012-07-13 14:29:25
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `Rubric`
-- ----------------------------
DROP TABLE IF EXISTS `Rubric`;
CREATE TABLE `Rubric` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `number` varchar(255) default NULL,
  `link` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=15 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of Rubric
-- ----------------------------
INSERT INTO Rubric VALUES ('1', 'Originality & personal contribution', '1.1', null);
INSERT INTO Rubric VALUES ('2', 'Command of subject', '1.2', null);
INSERT INTO Rubric VALUES ('3', 'Introduction', '1.3', null);
INSERT INTO Rubric VALUES ('4', 'Literature review', '1.4', null);
INSERT INTO Rubric VALUES ('5', 'Methodology', '1.5', null);
INSERT INTO Rubric VALUES ('6', 'Results', '1.6', null);
INSERT INTO Rubric VALUES ('7', 'Discussion / conclusion', '1.7', null);
INSERT INTO Rubric VALUES ('8', 'Presentation format & references', '1.8', null);
INSERT INTO Rubric VALUES ('9', 'Originality and personal contribution', '2.1', null);
INSERT INTO Rubric VALUES ('10', 'Introduction  and Background', '2.2', null);
INSERT INTO Rubric VALUES ('11', 'Risk and opportunities (or SWOT)', '2.3', null);
INSERT INTO Rubric VALUES ('12', 'Financial brief', '2.4', null);
INSERT INTO Rubric VALUES ('13', 'Concluding remarks', '2.5', null);
INSERT INTO Rubric VALUES ('14', 'Writing', '2.6', null);
