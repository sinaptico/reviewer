/*
Navicat MySQL Data Transfer

Source Server         : eie-web-pro-1-web
Source Server Version : 50095
Source Host           : localhost:3306
Source Database       : reviewer

Target Server Type    : MYSQL
Target Server Version : 50095
File Encoding         : 65001

Date: 2012-07-13 14:29:36
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `Rubric_FeedbackTemplates`
-- ----------------------------
DROP TABLE IF EXISTS `Rubric_FeedbackTemplates`;
CREATE TABLE `Rubric_FeedbackTemplates` (
  `Rubric_id` bigint(20) NOT NULL,
  `feedbackTemplates_id` bigint(20) NOT NULL,
  UNIQUE KEY `feedbackTemplates_id` (`feedbackTemplates_id`),
  KEY `FKC94093A23246FB3C` (`Rubric_id`),
  KEY `FKC94093A2CFDABD27` (`feedbackTemplates_id`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of Rubric_FeedbackTemplates
-- ----------------------------
INSERT INTO Rubric_FeedbackTemplates VALUES ('1', '1');
INSERT INTO Rubric_FeedbackTemplates VALUES ('1', '2');
INSERT INTO Rubric_FeedbackTemplates VALUES ('1', '3');
INSERT INTO Rubric_FeedbackTemplates VALUES ('1', '4');
INSERT INTO Rubric_FeedbackTemplates VALUES ('1', '5');
INSERT INTO Rubric_FeedbackTemplates VALUES ('4', '6');
INSERT INTO Rubric_FeedbackTemplates VALUES ('4', '7');
INSERT INTO Rubric_FeedbackTemplates VALUES ('4', '8');
INSERT INTO Rubric_FeedbackTemplates VALUES ('4', '9');
INSERT INTO Rubric_FeedbackTemplates VALUES ('4', '10');
INSERT INTO Rubric_FeedbackTemplates VALUES ('2', '16');
INSERT INTO Rubric_FeedbackTemplates VALUES ('2', '17');
INSERT INTO Rubric_FeedbackTemplates VALUES ('2', '18');
INSERT INTO Rubric_FeedbackTemplates VALUES ('2', '19');
INSERT INTO Rubric_FeedbackTemplates VALUES ('2', '20');
INSERT INTO Rubric_FeedbackTemplates VALUES ('3', '21');
INSERT INTO Rubric_FeedbackTemplates VALUES ('3', '22');
INSERT INTO Rubric_FeedbackTemplates VALUES ('3', '23');
INSERT INTO Rubric_FeedbackTemplates VALUES ('3', '24');
INSERT INTO Rubric_FeedbackTemplates VALUES ('3', '25');
INSERT INTO Rubric_FeedbackTemplates VALUES ('5', '26');
INSERT INTO Rubric_FeedbackTemplates VALUES ('5', '27');
INSERT INTO Rubric_FeedbackTemplates VALUES ('5', '28');
INSERT INTO Rubric_FeedbackTemplates VALUES ('5', '29');
INSERT INTO Rubric_FeedbackTemplates VALUES ('5', '30');
INSERT INTO Rubric_FeedbackTemplates VALUES ('6', '31');
INSERT INTO Rubric_FeedbackTemplates VALUES ('6', '32');
INSERT INTO Rubric_FeedbackTemplates VALUES ('6', '33');
INSERT INTO Rubric_FeedbackTemplates VALUES ('6', '34');
INSERT INTO Rubric_FeedbackTemplates VALUES ('6', '35');
INSERT INTO Rubric_FeedbackTemplates VALUES ('7', '36');
INSERT INTO Rubric_FeedbackTemplates VALUES ('7', '37');
INSERT INTO Rubric_FeedbackTemplates VALUES ('7', '38');
INSERT INTO Rubric_FeedbackTemplates VALUES ('7', '39');
INSERT INTO Rubric_FeedbackTemplates VALUES ('7', '40');
INSERT INTO Rubric_FeedbackTemplates VALUES ('8', '41');
INSERT INTO Rubric_FeedbackTemplates VALUES ('8', '42');
INSERT INTO Rubric_FeedbackTemplates VALUES ('8', '43');
INSERT INTO Rubric_FeedbackTemplates VALUES ('8', '44');
INSERT INTO Rubric_FeedbackTemplates VALUES ('8', '45');
INSERT INTO Rubric_FeedbackTemplates VALUES ('9', '46');
INSERT INTO Rubric_FeedbackTemplates VALUES ('9', '47');
INSERT INTO Rubric_FeedbackTemplates VALUES ('9', '48');
INSERT INTO Rubric_FeedbackTemplates VALUES ('9', '49');
INSERT INTO Rubric_FeedbackTemplates VALUES ('9', '50');
INSERT INTO Rubric_FeedbackTemplates VALUES ('10', '51');
INSERT INTO Rubric_FeedbackTemplates VALUES ('10', '52');
INSERT INTO Rubric_FeedbackTemplates VALUES ('10', '53');
INSERT INTO Rubric_FeedbackTemplates VALUES ('10', '54');
INSERT INTO Rubric_FeedbackTemplates VALUES ('10', '55');
INSERT INTO Rubric_FeedbackTemplates VALUES ('11', '56');
INSERT INTO Rubric_FeedbackTemplates VALUES ('11', '57');
INSERT INTO Rubric_FeedbackTemplates VALUES ('11', '58');
INSERT INTO Rubric_FeedbackTemplates VALUES ('11', '59');
INSERT INTO Rubric_FeedbackTemplates VALUES ('11', '60');
INSERT INTO Rubric_FeedbackTemplates VALUES ('12', '61');
INSERT INTO Rubric_FeedbackTemplates VALUES ('12', '62');
INSERT INTO Rubric_FeedbackTemplates VALUES ('12', '63');
INSERT INTO Rubric_FeedbackTemplates VALUES ('12', '64');
INSERT INTO Rubric_FeedbackTemplates VALUES ('12', '65');
INSERT INTO Rubric_FeedbackTemplates VALUES ('13', '76');
INSERT INTO Rubric_FeedbackTemplates VALUES ('13', '77');
INSERT INTO Rubric_FeedbackTemplates VALUES ('13', '78');
INSERT INTO Rubric_FeedbackTemplates VALUES ('13', '79');
INSERT INTO Rubric_FeedbackTemplates VALUES ('13', '80');
INSERT INTO Rubric_FeedbackTemplates VALUES ('14', '81');
INSERT INTO Rubric_FeedbackTemplates VALUES ('14', '82');
INSERT INTO Rubric_FeedbackTemplates VALUES ('14', '83');
INSERT INTO Rubric_FeedbackTemplates VALUES ('14', '84');
INSERT INTO Rubric_FeedbackTemplates VALUES ('14', '85');
