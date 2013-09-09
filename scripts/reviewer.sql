-- MySQL dump 10.13  Distrib 5.5.25, for osx10.7 (i386)
--
-- Host: localhost    Database: reviewer
-- ------------------------------------------------------
-- Server version	5.5.25

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `Activity`
--

DROP TABLE IF EXISTS `Activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Activity` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL,
  `documentTemplate` varchar(255) DEFAULT NULL,
  `documentType` varchar(255) DEFAULT NULL,
  `earlySubmit` bit(1) DEFAULT NULL,
  `emailStudents` bit(1) DEFAULT NULL,
  `excludeEmptyDocsInReviews` bit(1) DEFAULT NULL,
  `folderId` varchar(255) DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  `glosserSite` bigint(20) DEFAULT NULL,
  `groups` bit(1) DEFAULT NULL,
  `showStats` bit(1) DEFAULT NULL,
  `startDate` datetime DEFAULT NULL,
  `trackReviews` bit(1) DEFAULT NULL,
  `tutorial` varchar(255) DEFAULT NULL,
  `allocationStrategy` varchar(255) DEFAULT NULL,
  `feedbackTemplateType` varchar(255) DEFAULT NULL,
  `finishDate` datetime DEFAULT NULL,
  `formType` varchar(255) DEFAULT NULL,
  `maxGrade` int(11) DEFAULT NULL,
  `numAutomaticReviewers` int(11) DEFAULT NULL,
  `numLecturerReviewers` int(11) DEFAULT NULL,
  `numStudentReviewers` int(11) DEFAULT NULL,
  `numTutorReviewers` int(11) DEFAULT NULL,
  `ratings` bit(1) DEFAULT NULL,
  `reviewTemplateId` bigint(20) DEFAULT NULL,
  `startDate_id` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `saving` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKA126572F4F68B324` (`startDate_id`),
  CONSTRAINT `FKA126572F4F68B324` FOREIGN KEY (`startDate_id`) REFERENCES `Deadline` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Activity_Entries_Entry`
--

DROP TABLE IF EXISTS `Activity_Entries_Entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Activity_Entries_Entry` (
  `Activity_id` bigint(20) NOT NULL,
  `entries_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Activity_id`,`entries_id`),
  UNIQUE KEY `entries_id` (`entries_id`),
  KEY `FK95C4EEB3AD38177C` (`Activity_id`),
  KEY `FK95C4EEB36C5B7ABA` (`entries_id`),
  CONSTRAINT `FK95C4EEB36C5B7ABA` FOREIGN KEY (`entries_id`) REFERENCES `Entry` (`id`),
  CONSTRAINT `FK95C4EEB3AD38177C` FOREIGN KEY (`Activity_id`) REFERENCES `Activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Choice`
--

DROP TABLE IF EXISTS `Choice`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Choice` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `number` int(11) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course`
--

DROP TABLE IF EXISTS `Course`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `domainName` varchar(255) DEFAULT NULL,
  `folderId` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `semester` int(11) NOT NULL,
  `spreadsheetId` varchar(255) DEFAULT NULL,
  `templatesFolderId` varchar(255) DEFAULT NULL,
  `year` int(11) NOT NULL,
  `organizationId` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `saving` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK78A7CC3B340139E3` (`organizationId`),
  CONSTRAINT `FK78A7CC3B340139E3` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_Activities_Activity`
--

DROP TABLE IF EXISTS `Course_Activities_Activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_Activities_Activity` (
  `Course_id` bigint(20) NOT NULL,
  `writingActivities_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Course_id`,`writingActivities_id`),
  UNIQUE KEY `writingActivities_id` (`writingActivities_id`),
  KEY `FK19AD1D1DD16AD4FA` (`writingActivities_id`),
  KEY `FK19AD1D1DE6BD0C7C` (`Course_id`),
  CONSTRAINT `FK19AD1D1DD16AD4FA` FOREIGN KEY (`writingActivities_id`) REFERENCES `Activity` (`id`),
  CONSTRAINT `FK19AD1D1DE6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_Automatic_Reviewers_User`
--

DROP TABLE IF EXISTS `Course_Automatic_Reviewers_User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_Automatic_Reviewers_User` (
  `Course_id` bigint(20) NOT NULL,
  `automaticReviewers_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Course_id`,`automaticReviewers_id`),
  KEY `FKB4B93ED4B6000684` (`automaticReviewers_id`),
  KEY `FKB4B93ED4E6BD0C7C` (`Course_id`),
  CONSTRAINT `FKB4B93ED4B6000684` FOREIGN KEY (`automaticReviewers_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FKB4B93ED4E6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_Lecturers_User`
--

DROP TABLE IF EXISTS `Course_Lecturers_User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_Lecturers_User` (
  `Course_id` bigint(20) NOT NULL,
  `lecturers_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Course_id`,`lecturers_id`),
  KEY `FK6F36448FC2DDE208` (`lecturers_id`),
  KEY `FK6F36448FE6BD0C7C` (`Course_id`),
  CONSTRAINT `FK6F36448FC2DDE208` FOREIGN KEY (`lecturers_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FK6F36448FE6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_StudentGroups_UserGroup`
--

DROP TABLE IF EXISTS `Course_StudentGroups_UserGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_StudentGroups_UserGroup` (
  `Course_id` bigint(20) NOT NULL,
  `studentGroups_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Course_id`,`studentGroups_id`),
  UNIQUE KEY `studentGroups_id` (`studentGroups_id`),
  KEY `FK8AAED4E06D31B91D` (`studentGroups_id`),
  KEY `FK8AAED4E0E6BD0C7C` (`Course_id`),
  CONSTRAINT `FK8AAED4E06D31B91D` FOREIGN KEY (`studentGroups_id`) REFERENCES `UserGroup` (`id`),
  CONSTRAINT `FK8AAED4E0E6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_Supervisors_User`
--

DROP TABLE IF EXISTS `Course_Supervisors_User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_Supervisors_User` (
  `Course_id` bigint(20) NOT NULL,
  `supervisors_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Course_id`,`supervisors_id`),
  KEY `FKE38B05433A6718BC` (`supervisors_id`),
  KEY `FKE38B0543E6BD0C7C` (`Course_id`),
  CONSTRAINT `FKE38B05433A6718BC` FOREIGN KEY (`supervisors_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FKE38B0543E6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_Templates_DocEntry`
--

DROP TABLE IF EXISTS `Course_Templates_DocEntry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_Templates_DocEntry` (
  `Course_id` bigint(20) NOT NULL,
  `templates_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Course_id`,`templates_id`),
  UNIQUE KEY `templates_id` (`templates_id`),
  KEY `FK943697243D33821D` (`templates_id`),
  KEY `FK94369724E6BD0C7C` (`Course_id`),
  CONSTRAINT `FK943697243D33821D` FOREIGN KEY (`templates_id`) REFERENCES `Entry` (`id`),
  CONSTRAINT `FK94369724E6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_Tutorials`
--

DROP TABLE IF EXISTS `Course_Tutorials`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_Tutorials` (
  `Course_id` bigint(20) NOT NULL,
  `tutorials` varchar(255) DEFAULT NULL,
  KEY `FK26E159B1E6BD0C7C` (`Course_id`),
  CONSTRAINT `FK26E159B1E6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Course_Tutors_User`
--

DROP TABLE IF EXISTS `Course_Tutors_User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Course_Tutors_User` (
  `Course_id` bigint(20) NOT NULL,
  `tutors_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Course_id`,`tutors_id`),
  KEY `FK4DB90C9C827C58A` (`tutors_id`),
  KEY `FK4DB90C9E6BD0C7C` (`Course_id`),
  CONSTRAINT `FK4DB90C9C827C58A` FOREIGN KEY (`tutors_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FK4DB90C9E6BD0C7C` FOREIGN KEY (`Course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Deadline`
--

DROP TABLE IF EXISTS `Deadline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Deadline` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `finishDate` datetime DEFAULT NULL,
  `maxGrade` double DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `status` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DocEntry_Reviews_Review`
--

DROP TABLE IF EXISTS `DocEntry_Reviews_Review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DocEntry_Reviews_Review` (
  `Entry_id` bigint(20) NOT NULL,
  `reviews_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Entry_id`,`reviews_id`),
  UNIQUE KEY `reviews_id` (`reviews_id`),
  KEY `FKE4C1ECE1FA870644` (`Entry_id`),
  KEY `FKE4C1ECE137738FB9` (`reviews_id`),
  CONSTRAINT `FKE4C1ECE137738FB9` FOREIGN KEY (`reviews_id`) REFERENCES `Review` (`id`),
  CONSTRAINT `FKE4C1ECE1FA870644` FOREIGN KEY (`Entry_id`) REFERENCES `Entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DocumentType`
--

DROP TABLE IF EXISTS `DocumentType`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DocumentType` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `number` int(11) DEFAULT NULL,
  `genre` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `DocumentType_Rubrics`
--

DROP TABLE IF EXISTS `DocumentType_Rubrics`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `DocumentType_Rubrics` (
  `DocumentType_id` bigint(20) NOT NULL,
  `rubrics_id` bigint(20) NOT NULL,
  UNIQUE KEY `rubrics_id` (`rubrics_id`),
  KEY `FKC656489CF1DB4CDC` (`DocumentType_id`),
  KEY `FKC656489C40CD22E3` (`rubrics_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Email`
--

DROP TABLE IF EXISTS `Email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Email` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `message` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `organization_id` bigint(20) DEFAULT NULL,
  `course_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK3FF5B7CBA5353FC` (`organization_id`),
  KEY `FK3FF5B7CE6BD0C7C` (`course_id`),
  CONSTRAINT `FK3FF5B7CBA5353FC` FOREIGN KEY (`organization_id`) REFERENCES `Organization` (`id`),
  CONSTRAINT `FK3FF5B7CE6BD0C7C` FOREIGN KEY (`course_id`) REFERENCES `Course` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Entry`
--

DROP TABLE IF EXISTS `Entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Entry` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `downloaded` bit(1) NOT NULL,
  `fileName` varchar(255) DEFAULT NULL,
  `localFile` bit(1) NOT NULL,
  `locked` bit(1) NOT NULL,
  `title` varchar(255) DEFAULT NULL,
  `uploaded` bit(1) NOT NULL,
  `documentId` varchar(255) DEFAULT NULL,
  `domainName` varchar(255) DEFAULT NULL,
  `earlySubmitDate` datetime DEFAULT NULL,
  `submitted` datetime DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `ownerGroup_id` bigint(20) DEFAULT NULL,
  `docEntry_id` bigint(20) DEFAULT NULL,
  `review_id` bigint(20) DEFAULT NULL,
  `reviewReply_id` bigint(20) DEFAULT NULL,
  `reviewTemplate_id` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `documentId` (`documentId`),
  KEY `FK4001852EE964196` (`reviewReply_id`),
  KEY `FK40018523AE4FF3C` (`reviewTemplate_id`),
  KEY `FK4001852C5CD675C` (`review_id`),
  KEY `FK4001852662BD37C` (`docEntry_id`),
  KEY `FK4001852C6249F14` (`owner_id`),
  KEY `FK40018521DB19820` (`ownerGroup_id`),
  CONSTRAINT `FK40018521DB19820` FOREIGN KEY (`ownerGroup_id`) REFERENCES `UserGroup` (`id`),
  CONSTRAINT `FK40018523AE4FF3C` FOREIGN KEY (`reviewTemplate_id`) REFERENCES `ReviewTemplate` (`id`),
  CONSTRAINT `FK4001852662BD37C` FOREIGN KEY (`docEntry_id`) REFERENCES `Entry` (`id`),
  CONSTRAINT `FK4001852C5CD675C` FOREIGN KEY (`review_id`) REFERENCES `Review` (`id`),
  CONSTRAINT `FK4001852C6249F14` FOREIGN KEY (`owner_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FK4001852EE964196` FOREIGN KEY (`reviewReply_id`) REFERENCES `TemplateReply` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Entry_Entry`
--

DROP TABLE IF EXISTS `Entry_Entry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Entry_Entry` (
  `Entry_id` bigint(20) NOT NULL,
  `pages_id` bigint(20) NOT NULL,
  `page` int(11) NOT NULL,
  PRIMARY KEY (`Entry_id`,`page`),
  UNIQUE KEY `pages_id` (`pages_id`),
  KEY `FK31698E25A9B5970D` (`Entry_id`),
  KEY `FK31698E258517B821` (`pages_id`),
  CONSTRAINT `FK31698E258517B821` FOREIGN KEY (`pages_id`) REFERENCES `Entry` (`id`),
  CONSTRAINT `FK31698E25A9B5970D` FOREIGN KEY (`Entry_id`) REFERENCES `Entry` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `FeedbackTemplate`
--

DROP TABLE IF EXISTS `FeedbackTemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `FeedbackTemplate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `number` varchar(255) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `grade` varchar(255) DEFAULT NULL,
  `gradeNum` int(11) NOT NULL,
  `descriptionA` longtext,
  `descriptionB` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Grade`
--

DROP TABLE IF EXISTS `Grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Grade` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `value` double DEFAULT NULL,
  `deadline_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK41DCFB7BB5BE6DC` (`deadline_id`),
  KEY `FK41DCFB75A3DEEFC` (`user_id`),
  CONSTRAINT `FK41DCFB75A3DEEFC` FOREIGN KEY (`user_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FK41DCFB7BB5BE6DC` FOREIGN KEY (`deadline_id`) REFERENCES `Deadline` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Organization`
--

DROP TABLE IF EXISTS `Organization`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Organization` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `activated` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Organization_Emails_Domains`
--

DROP TABLE IF EXISTS `Organization_Emails_Domains`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Organization_Emails_Domains` (
  `Organization_id` bigint(20) NOT NULL,
  `emailDomains` varchar(255) NOT NULL,
  PRIMARY KEY (`Organization_id`,`emailDomains`),
  KEY `FK83B3ACB3BA5353FC` (`Organization_id`),
  CONSTRAINT `FK83B3ACB3BA5353FC` FOREIGN KEY (`Organization_id`) REFERENCES `Organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Organization_Properties_ReviewerProperty`
--

DROP TABLE IF EXISTS `Organization_Properties_ReviewerProperty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Organization_Properties_ReviewerProperty` (
  `organizationId` bigint(20) NOT NULL,
  `propertyId` bigint(20) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`organizationId`,`propertyId`),
  KEY `FK49A63B9A340139E3` (`organizationId`),
  KEY `FK49A63B9AC8836CC` (`propertyId`),
  CONSTRAINT `FK49A63B9A340139E3` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`),
  CONSTRAINT `FK49A63B9AC8836CC` FOREIGN KEY (`propertyId`) REFERENCES `ReviewerProperty` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Question`
--

DROP TABLE IF EXISTS `Question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Question` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `Question` longtext,
  `docId` varchar(255) DEFAULT NULL,
  `sourceSentence` longtext,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `QuestionScore`
--

DROP TABLE IF EXISTS `QuestionScore`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `QuestionScore` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `QualityMeasure` int(11) NOT NULL,
  `comment` longtext,
  `grade` int(11) NOT NULL,
  `produced` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Question_Owner`
--

DROP TABLE IF EXISTS `Question_Owner`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Question_Owner` (
  `owner_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK5277691A11AFD23` (`id`),
  KEY `FK5277691AC6249F14` (`owner_id`),
  CONSTRAINT `FK5277691A11AFD23` FOREIGN KEY (`id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FK5277691AC6249F14` FOREIGN KEY (`owner_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Question_Score`
--

DROP TABLE IF EXISTS `Question_Score`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Question_Score` (
  `question_id` bigint(20) DEFAULT NULL,
  `id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK52A6B4F93CA23085` (`id`),
  KEY `FK52A6B4F93A94D41C` (`question_id`),
  CONSTRAINT `FK52A6B4F93A94D41C` FOREIGN KEY (`question_id`) REFERENCES `Question` (`id`),
  CONSTRAINT `FK52A6B4F93CA23085` FOREIGN KEY (`id`) REFERENCES `QuestionScore` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Rating`
--

DROP TABLE IF EXISTS `Rating`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Rating` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `comment` longtext,
  `contentScore` int(11) DEFAULT NULL,
  `evidenceScore` int(11) DEFAULT NULL,
  `overallScore` int(11) DEFAULT NULL,
  `usefulnessScore` int(11) DEFAULT NULL,
  `entry_id` bigint(20) DEFAULT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK917A9DBD7FAED398` (`entry_id`),
  KEY `FK917A9DBDC6249F14` (`owner_id`),
  CONSTRAINT `FK917A9DBD7FAED398` FOREIGN KEY (`entry_id`) REFERENCES `Entry` (`id`),
  CONSTRAINT `FK917A9DBDC6249F14` FOREIGN KEY (`owner_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Review`
--

DROP TABLE IF EXISTS `Review`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Review` (
  `DTYPE` varchar(31) NOT NULL,
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `content` longtext,
  `earlySubmitted` bit(1) DEFAULT NULL,
  `feedbackTemplateType` varchar(255) DEFAULT NULL,
  `saved` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReviewTemplate`
--

DROP TABLE IF EXISTS `ReviewTemplate`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReviewTemplate` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `organizationId` bigint(20) DEFAULT NULL,
  `deleted` tinyint(1) NOT NULL DEFAULT '0',
  `userId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK124A1C12340139E3` (`organizationId`),
  KEY `FK124A1C12312E3313` (`userId`),
  CONSTRAINT `FK124A1C12312E3313` FOREIGN KEY (`userId`) REFERENCES `User` (`id`),
  CONSTRAINT `FK124A1C12340139E3` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReviewTemplate_Sections`
--

DROP TABLE IF EXISTS `ReviewTemplate_Sections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReviewTemplate_Sections` (
  `ReviewTemplate_id` bigint(20) NOT NULL,
  `sections_id` bigint(20) NOT NULL,
  UNIQUE KEY `sections_id` (`sections_id`),
  KEY `FK583F4BDBBB0FFB4F` (`sections_id`),
  KEY `FK583F4BDB3AE4FF3C` (`ReviewTemplate_id`),
  CONSTRAINT `FK583F4BDB3AE4FF3C` FOREIGN KEY (`ReviewTemplate_id`) REFERENCES `ReviewTemplate` (`id`),
  CONSTRAINT `FK583F4BDBBB0FFB4F` FOREIGN KEY (`sections_id`) REFERENCES `Section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReviewTemplate_Users`
--

DROP TABLE IF EXISTS `ReviewTemplate_Users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReviewTemplate_Users` (
  `ReviewTemplate_id` bigint(20) NOT NULL,
  `sharedWith_id` bigint(20) NOT NULL,
  KEY `FK4075477B3F214C1C` (`sharedWith_id`),
  KEY `FK4075477B3AE4FF3C` (`ReviewTemplate_id`),
  CONSTRAINT `FK4075477B3AE4FF3C` FOREIGN KEY (`ReviewTemplate_id`) REFERENCES `ReviewTemplate` (`id`),
  CONSTRAINT `FK4075477B3F214C1C` FOREIGN KEY (`sharedWith_id`) REFERENCES `User` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReviewTemplates_Sections`
--

DROP TABLE IF EXISTS `ReviewTemplates_Sections`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReviewTemplates_Sections` (
  `ReviewTemplate_id` bigint(20) NOT NULL,
  `sections_id` bigint(20) NOT NULL,
  UNIQUE KEY `sections_id` (`sections_id`),
  KEY `FKB829722CBB0FFB4F` (`sections_id`),
  KEY `FKB829722C3AE4FF3C` (`ReviewTemplate_id`),
  CONSTRAINT `FKB829722C3AE4FF3C` FOREIGN KEY (`ReviewTemplate_id`) REFERENCES `ReviewTemplate` (`id`),
  CONSTRAINT `FKB829722CBB0FFB4F` FOREIGN KEY (`sections_id`) REFERENCES `Section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Review_FeedbackTemplates`
--

DROP TABLE IF EXISTS `Review_FeedbackTemplates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Review_FeedbackTemplates` (
  `Review_id` bigint(20) NOT NULL,
  `feedback_templates_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Review_id`,`feedback_templates_id`),
  KEY `FKAA013C0D3A340F9C` (`feedback_templates_id`),
  KEY `FKAA013C0DC5CD675C` (`Review_id`),
  CONSTRAINT `FKAA013C0D3A340F9C` FOREIGN KEY (`feedback_templates_id`) REFERENCES `FeedbackTemplate` (`id`),
  CONSTRAINT `FKAA013C0DC5CD675C` FOREIGN KEY (`Review_id`) REFERENCES `Review` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Review_Question`
--

DROP TABLE IF EXISTS `Review_Question`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Review_Question` (
  `Review_id` bigint(20) NOT NULL,
  `questions_id` bigint(20) NOT NULL,
  `questionIndex` int(11) NOT NULL,
  PRIMARY KEY (`Review_id`,`questionIndex`),
  UNIQUE KEY `questions_id` (`questions_id`),
  KEY `FKD2A54EDF6E68155` (`questions_id`),
  KEY `FKD2A54ED736FC8E2` (`Review_id`),
  CONSTRAINT `FKD2A54ED736FC8E2` FOREIGN KEY (`Review_id`) REFERENCES `Review` (`id`),
  CONSTRAINT `FKD2A54EDF6E68155` FOREIGN KEY (`questions_id`) REFERENCES `Question` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Review_TemplateReply`
--

DROP TABLE IF EXISTS `Review_TemplateReply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Review_TemplateReply` (
  `Review_id` bigint(20) NOT NULL,
  `templateReplies_id` bigint(20) NOT NULL,
  `replyIndex` int(11) NOT NULL,
  PRIMARY KEY (`Review_id`,`replyIndex`),
  UNIQUE KEY `templateReplies_id` (`templateReplies_id`),
  KEY `FKBFF885E957B7435A` (`templateReplies_id`),
  KEY `FKBFF885E9D1FE5E72` (`Review_id`),
  CONSTRAINT `FKBFF885E957B7435A` FOREIGN KEY (`templateReplies_id`) REFERENCES `TemplateReply` (`id`),
  CONSTRAINT `FKBFF885E9D1FE5E72` FOREIGN KEY (`Review_id`) REFERENCES `Review` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `ReviewerProperty`
--

DROP TABLE IF EXISTS `ReviewerProperty`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ReviewerProperty` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `name` (`name`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Rubric`
--

DROP TABLE IF EXISTS `Rubric`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Rubric` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `number` varchar(255) DEFAULT NULL,
  `link` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Rubric_FeedbackTemplates`
--

DROP TABLE IF EXISTS `Rubric_FeedbackTemplates`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Rubric_FeedbackTemplates` (
  `Rubric_id` bigint(20) NOT NULL,
  `feedbackTemplates_id` bigint(20) NOT NULL,
  UNIQUE KEY `feedbackTemplates_id` (`feedbackTemplates_id`),
  KEY `FKC94093A23246FB3C` (`Rubric_id`),
  KEY `FKC94093A2CFDABD27` (`feedbackTemplates_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Section`
--

DROP TABLE IF EXISTS `Section`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Section` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `number` int(11) DEFAULT NULL,
  `text` varchar(255) DEFAULT NULL,
  `tool` varchar(255) DEFAULT NULL,
  `type` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Sections_Choices`
--

DROP TABLE IF EXISTS `Sections_Choices`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Sections_Choices` (
  `Section_id` bigint(20) NOT NULL,
  `choices_id` bigint(20) NOT NULL,
  UNIQUE KEY `choices_id` (`choices_id`),
  KEY `FK67E929E1AEF20FCB` (`choices_id`),
  KEY `FK67E929E1B2A00B38` (`Section_id`),
  CONSTRAINT `FK67E929E1AEF20FCB` FOREIGN KEY (`choices_id`) REFERENCES `Choice` (`id`),
  CONSTRAINT `FK67E929E1B2A00B38` FOREIGN KEY (`Section_id`) REFERENCES `Section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `TemplateReply`
--

DROP TABLE IF EXISTS `TemplateReply`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `TemplateReply` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `choice` varchar(255) DEFAULT NULL,
  `mark` int(11) DEFAULT NULL,
  `text` longtext,
  `reviewTemplate_id` bigint(20) DEFAULT NULL,
  `section_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK242642703AE4FF3C` (`reviewTemplate_id`),
  KEY `FK24264270B2A00B38` (`section_id`),
  CONSTRAINT `FK242642703AE4FF3C` FOREIGN KEY (`reviewTemplate_id`) REFERENCES `ReviewTemplate` (`id`),
  CONSTRAINT `FK24264270B2A00B38` FOREIGN KEY (`section_id`) REFERENCES `Section` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User`
--

DROP TABLE IF EXISTS `User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `User` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `email` varchar(255) NOT NULL,
  `firstname` varchar(255) DEFAULT NULL,
  `lastname` varchar(255) DEFAULT NULL,
  `nativeSpeaker` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `username` varchar(255) DEFAULT NULL,
  `wasmuser` bit(1) DEFAULT NULL,
  `organizationId` bigint(20) DEFAULT NULL,
  `googleRefreshToken` varchar(255) DEFAULT NULL,
  `googleToken` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `email` (`email`),
  UNIQUE KEY `email_2` (`email`),
  KEY `FK285FEB340139E3` (`organizationId`),
  CONSTRAINT `FK285FEB340139E3` FOREIGN KEY (`organizationId`) REFERENCES `Organization` (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UserGroup`
--

DROP TABLE IF EXISTS `UserGroup`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserGroup` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `name` varchar(255) DEFAULT NULL,
  `tutorial` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `UserGroup_Users_User`
--

DROP TABLE IF EXISTS `UserGroup_Users_User`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `UserGroup_Users_User` (
  `UserGroup_id` bigint(20) NOT NULL,
  `users_id` bigint(20) NOT NULL,
  PRIMARY KEY (`UserGroup_id`,`users_id`),
  KEY `FKF36403AD8BF5F938` (`UserGroup_id`),
  KEY `FKF36403AD532EC79F` (`users_id`),
  CONSTRAINT `FKF36403AD532EC79F` FOREIGN KEY (`users_id`) REFERENCES `User` (`id`),
  CONSTRAINT `FKF36403AD8BF5F938` FOREIGN KEY (`UserGroup_id`) REFERENCES `UserGroup` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `User_roles`
--

DROP TABLE IF EXISTS `User_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `User_roles` (
  `email` varchar(255) NOT NULL,
  `role_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WritingActivity_Deadlines_Deadline`
--

DROP TABLE IF EXISTS `WritingActivity_Deadlines_Deadline`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WritingActivity_Deadlines_Deadline` (
  `Activity_id` bigint(20) NOT NULL,
  `deadlines_id` bigint(20) NOT NULL,
  `deadlineIndex` int(11) NOT NULL,
  PRIMARY KEY (`Activity_id`,`deadlineIndex`),
  UNIQUE KEY `deadlines_id` (`deadlines_id`),
  KEY `FK24278E10E5B33539` (`deadlines_id`),
  KEY `FK24278E108339E5F4` (`Activity_id`),
  CONSTRAINT `FK24278E108339E5F4` FOREIGN KEY (`Activity_id`) REFERENCES `Activity` (`id`),
  CONSTRAINT `FK24278E10E5B33539` FOREIGN KEY (`deadlines_id`) REFERENCES `Deadline` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WritingActivity_Grades_Grade`
--

DROP TABLE IF EXISTS `WritingActivity_Grades_Grade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WritingActivity_Grades_Grade` (
  `Activity_id` bigint(20) NOT NULL,
  `grades_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Activity_id`,`grades_id`),
  UNIQUE KEY `grades_id` (`grades_id`),
  KEY `FKF98A29288339E5F4` (`Activity_id`),
  KEY `FKF98A2928A20C03B3` (`grades_id`),
  CONSTRAINT `FKF98A29288339E5F4` FOREIGN KEY (`Activity_id`) REFERENCES `Activity` (`id`),
  CONSTRAINT `FKF98A2928A20C03B3` FOREIGN KEY (`grades_id`) REFERENCES `Grade` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `WritingActivity_ReviewingActivities_ReviewingActivity`
--

DROP TABLE IF EXISTS `WritingActivity_ReviewingActivities_ReviewingActivity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `WritingActivity_ReviewingActivities_ReviewingActivity` (
  `Activity_id` bigint(20) NOT NULL,
  `reviewingActivities_id` bigint(20) NOT NULL,
  `reviewIndex` int(11) NOT NULL,
  PRIMARY KEY (`Activity_id`,`reviewIndex`),
  UNIQUE KEY `reviewingActivities_id` (`reviewingActivities_id`),
  KEY `FK387F67FD66D0623A` (`reviewingActivities_id`),
  KEY `FK387F67FD8339E5F4` (`Activity_id`),
  CONSTRAINT `FK387F67FD66D0623A` FOREIGN KEY (`reviewingActivities_id`) REFERENCES `Activity` (`id`),
  CONSTRAINT `FK387F67FD8339E5F4` FOREIGN KEY (`Activity_id`) REFERENCES `Activity` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-08-27 16:37:21
