/*
Navicat MySQL Data Transfer

Source Server         : eie-web-pro-1-web
Source Server Version : 50095
Source Host           : localhost:3306
Source Database       : reviewer

Target Server Type    : MYSQL
Target Server Version : 50095
File Encoding         : 65001

Date: 2012-07-13 14:29:07
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `DocumentType`
-- ----------------------------
DROP TABLE IF EXISTS `DocumentType`;
CREATE TABLE `DocumentType` (
  `id` bigint(20) NOT NULL auto_increment,
  `name` varchar(255) default NULL,
  `number` int(11) default NULL,
  `genre` varchar(255) default NULL,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of DocumentType
-- ----------------------------
INSERT INTO DocumentType VALUES ('1', 'Thesis', '1', null);
INSERT INTO DocumentType VALUES ('2', 'Proposal', '2', 'proposal');
