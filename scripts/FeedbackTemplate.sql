/*
Navicat MySQL Data Transfer

Source Server         : eie-web-pro-1-web
Source Server Version : 50095
Source Host           : localhost:3306
Source Database       : reviewer

Target Server Type    : MYSQL
Target Server Version : 50095
File Encoding         : 65001

Date: 2012-07-13 14:29:49
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for `FeedbackTemplate`
-- ----------------------------
DROP TABLE IF EXISTS `FeedbackTemplate`;
CREATE TABLE `FeedbackTemplate` (
  `id` bigint(20) NOT NULL auto_increment,
  `number` varchar(255) default NULL,
  `text` varchar(255) default NULL,
  `grade` varchar(255) default NULL,
  `gradeNum` int(11) NOT NULL,
  `descriptionA` longtext,
  `descriptionB` longtext,
  PRIMARY KEY  (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=86 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of FeedbackTemplate
-- ----------------------------
INSERT INTO FeedbackTemplate VALUES ('1', '1.1.1', 'Work does not meet The University’s Academic Board Policy', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('2', '1.1.2', 'The work is the students own; where it is not is indicated by acknowledgment; meets requirements found at http://db.usyd.edu.au/policy/policyindex.htm', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('3', '1.1.3', 'Places new results in a credible research context ', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('4', '1.1.4', 'Makes a valuable contribution to the topic', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('5', '1.1.5', 'Shows an original understanding which interests the wider engineering community; suggests new directions for further research/design development', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('6', '1.4.1', 'Is too short; lacks detail and analysis; does not cite important work', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('7', '1.4.2', 'Reports the literature; quotes paraphrases and summarizes appropriately; shows a competent grasp of key issues ', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('8', '1.4.3', 'Has a clear structure and groups literature into themes relevant to the research/design topic; makes a clear link to own project', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('9', '1.4.4', 'Provides a comprehensive and analytical examination of topic; makes links with research/design methodology; demonstrates sound understanding of key issues', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('10', '1.4.5', 'Critically analyses literature; uses the review to create a rationale for the whole thesis; demonstrates a scholarly grasp of the literature; appraises the relevant literature ', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('16', '1.2.1', 'Does not link theory to research', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('17', '1.2.2', 'Describes and uses theory to inform research/design question; uses set readings to develop topic', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('18', '1.2.3', 'Demonstrates understanding of topic; uses models to inform research/design aim', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('19', '1.2.4', 'Compares and contrasts several theories; reveals strengths and weaknesses of complex theoretical models', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('20', '1.2.5', 'Critically analyses competing theoretical models; use the literature review to demonstrate theoretical insights', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('21', '1.3.1', 'Is absent or is poorly structured or lacks essential elements', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('22', '1.3.2', 'Contains a structure; describes research/design project generally ', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('23', '1.3.3', 'Makes specific statements about the research/design field; introduces key authors; links aim to existing research/design work', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('24', '1.3.4', 'Analyses literature to indicate gap in existing research/design work; outlines scope of the study and provides some rationale for the research/design project', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('25', '1.3.5', 'Provides sound rationale for the research/design project; contextualizes project aim; well structured and sequenced ', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('26', '1.5.1', 'Uses inappropriate research/design methods; lacks a structure or argument ', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('27', '1.5.2', 'Describes research/design methods and materials used so that they could be repeated; methods show a structure and might yield appropriate data ', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('28', '1.5.3', 'Draws on published research to provide a rationale for  research/design methods; links methods and results sections logically', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('29', '1.5.4', 'Derives methods from an analysis of strengths and weaknesses of existing research/design work; provides sound rationale for research/design project', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('30', '1.5.5', 'Uses innovative methods; discusses methodology limitations', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('31', '1.6.1', 'Obtains insufficient data to yield results or to fulfill research purpose ', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('32', '1.6.2', 'Obtains sufficient reliable data to help answer the study purpose; supports data with figures and tables ', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('33', '1.6.3', 'Provides reproducible data in logical order to reflect the research aim; figures & tables are integrated into results with clear written legends', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('34', '1.6.4', 'Processes complete, precision data with the appropriate analytical technique; links results to research aim/question; discusses sources of error; ', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('35', '1.6.5', 'Uses best processing of data, including innovative use of tables and figures in response to research questions; processes data to develop theory ', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('36', '1.7.1', 'Cannot reasonably explain results', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('37', '1.7.2', 'Makes links to results with basic reasoning; states some usefulness of own research ', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('38', '1.7.3', 'Substantiates research/design claims with references; compares and explains (un) expected results with published results; suggests further work related to topic', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('39', '1.7.4', 'Clearly interprets results; links to a theoretical understanding from the literature; anticipates criticism; identifies limitations to study and how they might be resolved', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('40', '1.7.5', 'Uses results to critically interpret the theory/research supporting the study; explains how results advance the field; reveals an original understanding of own work', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('41', '1.8.1', 'Writing does not clearly communicate message ', 'Fail <50%', '49', null, null);
INSERT INTO FeedbackTemplate VALUES ('42', '1.8.2', 'Writes well; contains sections and subsections and a contents page; correctly employs departmental formatting and referencing guides', 'Pass 50-64%', '64', null, null);
INSERT INTO FeedbackTemplate VALUES ('43', '1.8.3', 'Writes in a consistently clear style without grammatical errors', 'Credit 65-74%', '74', null, null);
INSERT INTO FeedbackTemplate VALUES ('44', '1.8.4', 'Writes analytically; brings together all sections into a cohesive document', 'Distinction 75-84%', '84', null, null);
INSERT INTO FeedbackTemplate VALUES ('45', '1.8.5', 'Uses the resources of written communication similar to a published research paper. ', 'High Distinction 85%+', '85', null, null);
INSERT INTO FeedbackTemplate VALUES ('46', '2.1.1', 'Work does not meet minimum requirements, or project scope', 'Fail <50%', '49', 'unfortunately, your assignment did not meet the minimum requirements or project scope to get a pass grade. A pass on originality requires that you at least come up with a new way of presenting/approaching an existing business ', 'the objective of the task is to come with novel ideas. The idea you proposed is not new, because many products already do the same thing. You might need to learn about what counts as an original idea in the context of this assignment. ');
INSERT INTO FeedbackTemplate VALUES ('47', '2.1.2', 'The work is the student’s own and meets the basic requirements', 'Pass 50-64%', '64', 'the originality of your assignment is just enough to obtain a pass grade. To improve your grades try thinking how your product can be a bit more distinctive', 'the objective of the task is to come with novel ideas. The idea you proposed is not totally new, because some products already do things that are similar. You might want to learn about what counts as an original idea in the context of this assignment.');
INSERT INTO FeedbackTemplate VALUES ('48', '2.1.3', 'Places a new idea in a credible context', 'Credit 65-74%', '74', 'the originality of your assignment was average and deserves a credit. To improve your grades try thinking how your product can be a bit more distinctive', 'the objective of the task is to come with novel ideas. Your idea here is relatively new and credible, but still there are some products that do similar things.  You may learn to come up with more original ideas by thinking about your target audience needs.');
INSERT INTO FeedbackTemplate VALUES ('49', '2.1.4', 'Puts forward a valuable contribution', 'Distinction 75-84%', '84', 'the originality of your assignment was above average and deserves a distinction. You may be able to get a better grade in the next iteration by thinking about your target audience\'s needs', 'the objective of the task is to come with novel ideas. Your idea here indicates that you understand originality, because there are few products that do similar things. ');
INSERT INTO FeedbackTemplate VALUES ('50', '2.1.5', 'Shows an original understanding which interests the wider community, suggest new and promising directions for further design and development', 'High Distinction 85%+', '85', 'the originality of your assignment was outstanding and deserves a high distinction.', 'the objective of the task is to come with novel ideas. Your ideas here indicate that you understand originality, because there are no products that do the same things. You have displayed that you are capable of becoming an innovative professional designer/analyst.');
INSERT INTO FeedbackTemplate VALUES ('51', '2.2.1', 'Does not link proposal to reliable data or market research', 'Fail <50%', '49', 'your assignment does not meet the minimum requirement. To improve your grade for the introduction and background sections, please do the tutorial at: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>', 'the objective of the task was to learn about how to use reliable data or market research to justify your idea. Your assignment does not indicate that you have understood how to do this. You can learn how to do this in an introduction and background, by doing the tutorials at: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('52', '2.2.2', 'Describes and uses research data to inform design.  ', 'Pass 50-64%', '64', 'your assignment is of just enough quality to get a pass grade in this aspect. To improve your grade for the introduction and background sections, please do the tutorial at: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>', 'the objective of the task was to learn about how to use reliable data or market research to justify your idea. Your assignment indicates that you have a basic understanding about this. You can learn how to do this better in an introduction and background, by doing the tutorials at <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('53', '2.2.3', 'Demonstrates understanding of the topic. Uses models to inform design.', 'Credit 65-74%', '74', 'your assignment is good enough to get a credit in this aspect. To improve your grade for the introduction and background sections, please do the tutorial at: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>', 'the objective of the task was to learn about how to use reliable data or market research to justify your idea. Your assignment indicates that you have adequate understanding of how to do this. You can learn how to do this better in an introduction and background, by doing the tutorials at <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('54', '2.2.4', 'Compares and contrasts several design alternatives. Outlines the scope of the project and provides some rationale.', 'Distinction 75-84%', '84', 'your assignment is already good and deserves a distinction in this aspect. To improve your grade further for the introduction and background sections, please do the tutorial at: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>', 'the objective of the task was to learn about how to use reliable data or market research to justify your idea. Your assignment indicates that you have a good understanding of how to do this.  You can learn more about this from the tutorials at <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('55', '2.2.5', 'Critically analyses competing designs; uses research data to demonstrate design and implementation ideas ', 'High Distinction 85%+', '85', 'your assignment is excellent and deserves a distinction in this aspect. You can see a relevant tutorial here: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>', 'the objective of the task was to learn about how to use reliable data or market research to justify your idea. Your assignment indicates that you already have excellent understanding of how to do this. You can learn more about this from the tutorials at <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/introduction.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/background.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/background.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('56', '2.3.1', 'Is absent or lacks essential elements', 'Fail <50%', '49', 'your assignment does not meet the minimun requirement in this aspect. To improve your grade in this respect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>', 'the objective of this task was to learn how to analyse the various potential risks and opportunities in a project. Your assignment indicates that you have not attempted to do this. To learn about this further, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('57', '2.3.2', 'Reports key elements. Shows competent grasp of key issues', 'Pass 50-64%', '64', 'your assignment meets the minimum requirement. To improve your grade in this respect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>', 'the objective of this task was to learn how to analyse the various potential risks and opportunities in a project. Your assignment indicates that you have attempted to do this, and that you have a basic understanding. To learn about this further, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('58', '2.3.3', 'Has a clear structure and supporting data.', 'Credit 65-74%', '74', 'your assignment deserves a credit for this aspect. To improve your grade in this respect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>', 'the objective of this task was to learn how to analyse the various potential risks and opportunities in a project. Your assignment indicates that you have a decent understanding of this. To learn about this further, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('59', '2.3.4', 'Accurate analysis.', 'Distinction 75-84%', '84', 'your assignment is already good and deserves a distinction for this aspect. To improve your grade in this respect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>', 'the objective of this task was to learn how to analyse the various potential risks and opportunities in a project. Your assignment indicates that you have a good understanding of this. To learn about this further, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('60', '2.3.5', 'Critically analyses R&O.', 'High Distinction 85%+', '85', 'your assignment is excellent and deserves a high distinction for this aspect. You can see a relevant tutorial here: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>', 'the objective of this task was to learn how to analyse the various potential risks and opportunities in a project. Your assignment indicates that you have an excellent understanding of this. To learn about this further, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/risks_and_opportunities.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('61', '2.4.1', 'Is absent or lacks essential elements', 'Fail <50%', '49', 'your assignment has not met the minimum requirement. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>', 'the objective of this task was to learn about how to analyse the cost involved in different development and implementation approaches, and to argue for the best approach for developing and implementing your project. Your assignment indicates that you do not have an adequate understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('62', '2.4.2', 'Reports key elements. Shows competent grasp of key issues', 'Pass 50-64%', '64', 'your assignment has only met the minimum requirement, and thus deserves a pass. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>', 'the objective of this task was to learn about how to analyse the cost involved in different development and implementation approaches, and to argue for the best approach for developing and implementing your project. Your assignment indicates that you only have a basic understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('63', '2.4.3', 'Has a clear structure and supporting data.', 'Credit 65-74%', '74', 'your assignment has met the requirement, and thus deserves a credit. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>', 'the objective of this task was to learn about how to analyse the cost involved in different development and implementation approaches, and to argue for the best approach for developing and implementing your project. Your assignment indicates that you have fair understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('64', '2.4.4', 'Accurate analysis of costs and revenues', 'Distinction 75-84%', '84', 'your assignment has exceeded the requirement, and thus deserves a distinction. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>', 'the objective of this task was to learn about how to analyse the cost involved in different development and implementation approaches, and to argue for the best approach for developing and implementing your project. Your assignment indicates that you a good understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('65', '2.4.5', 'Critically analyses costs divers and discusses opportunities adequate for this project.', 'High Distinction 85%+', '85', 'your assignment is excellent in this aspect, and thus deserves a high distinction. You can see a relevant tutorial here: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>', 'the objective of this task was to learn about how to analyse the cost involved in different development and implementation approaches, and to argue for the best approach for developing and implementing your project. Your assignment indicates that you an excellent understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/total_cost_of_ownership.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('76', '2.5.1', 'Cannot explain the design ideas or argument for the project.', 'Fail <50%', '49', 'your assignment has not met the minimum requirement. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>', 'the objective of this task was to learn how to summarise and explain your design ideas and argument. Your assignment indicates that you don\'t have adequate understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('77', '2.5.2', 'Basic reasoning. It is only partially convincing as an argument for the project', 'Pass 50-64%', '64', 'your assignment has just met the minimum requirement and thus deserves a pass. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>', 'the objective of this task was to learn how to summarise and explain your design ideas and argument. Your assignment indicates that you have a basic understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('78', '2.5.3', 'Substantiates design claims with data. Makes clear the work ahead and its value.', 'Credit 65-74%', '74', 'your assignment has met the minimum requirement and thus deserves a credit. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>', 'the objective of this task was to learn how to summarise and explain your design ideas and argument. Your assignment indicates that you have an adequate understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('79', '2.5.4', 'Clearly closes the argument. Links to an understanding of the data and project brief. Anticipates criticism. Identifies limitations and how they might be resolved. ', 'Distinction 75-84%', '84', 'your assignment has exceeded the requirements and thus deserves a distinction. To increase your grade in this aspect, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>', 'the objective of this task was to learn how to summarise and explain your design ideas and argument. Your assignment indicates that you have a good  understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('80', '2.5.5', 'Critically interprets data and competing products. Suggest novel ideas and provides an original understanding of the issues.', 'High Distinction 85%+', '85', 'your assignment has far exceeded the requirements and thus deserves a high distinction. You can see a relevant tutorial here: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>', 'the objective of this task was to learn how to summarise and explain your design ideas and argument. Your assignment indicates that you have an excellent understanding of this. To learn more about this, please see this tutorial: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/concluding_remarks.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('81', '2.6.1', 'Writing does not clearly communicate message', 'Fail <50%', '49', 'your assignment has not met the minimum requirements. It does not communicate a clear message nor follow formatting guidelines. To obtain a better grade for this aspect, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>', 'the objective of this task was to learn how to present a clear and cohesive message or argument, in the form of a business proposal. Your assignment indicates that you do not have an adequate understanding of this learning goal. To learn more about this, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('82', '2.6.2', 'Well written. Contains section and subsections and contents page. Correctly employs departmental formatting and referencing guides.', 'Pass 50-64%', '64', 'your assignment has met the minimum requirements. It communicates the message clearly enough, and follows the formatting guidelines. It therefore deserves a pass. To obtain a better grade for this aspect, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>', 'the objective of this task was to learn how to present a clear and cohesive message or argument, in the form of a business proposal. Your assignment indicates that you have a basic understanding of this learning goal. To learn more about this, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('83', '2.6.3', 'Written in a consistently clear style without grammatical errors.', 'Credit 65-74%', '74', 'your assignment has met the requirements. It communicates the message clearly, and follows the formatting guidelines. It therefore deserves a credit. To obtain a better grade for this aspect, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>', 'the objective of this task was to learn how to present a clear and cohesive message or argument, in the form of a business proposal. Your assignment indicates that you have an adequate understanding of this learning goal. To learn more about this, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('84', '2.6.4', 'The document is written analytically. Brings all sections together in a cohesive manner.', 'Distinction 75-84%', '84', 'your assignment has exceeded the requirements. It communicates the message well, and follows the formatting guidelines. It therefore deserves a distinction. To obtain a better grade for this aspect, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>', 'the objective of this task was to learn how to present a clear and cohesive message or argument, in the form of a business proposal. Your assignment indicates that you have a good understanding of this learning goal. To learn more about this, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>');
INSERT INTO FeedbackTemplate VALUES ('85', '2.6.5', 'Contains the elements of a professional proposal. ', 'High Distinction 85%+', '85', 'your assignment has far exceeded the requirements. It communicates the message excellently, and follows the formatting guidelines. It therefore deserves a high distinction. Here are some relevant tutorials: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>', 'the objective of this task was to learn how to present a clear and cohesive message or argument, in the form of a business proposal. Your assignment indicates that you have an excellent good understanding of this learning goal. To learn more about this, please see the relevant tutorials, for example: <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/structure_your_argument.htm</a> and <a href=\'http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm\'>http://iwrite.sydney.edu.au/tutorials/proposal/cohesion.htm</a>');
