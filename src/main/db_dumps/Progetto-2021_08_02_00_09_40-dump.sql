-- MySQL dump 10.13  Distrib 8.0.26, for Linux (x86_64)
--
-- Host: 127.0.0.1    Database: db_exam_management
-- ------------------------------------------------------
-- Server version	8.0.26

/*!40101 SET @OLD_CHARACTER_SET_CLIENT = @@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS = @@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION = @@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE = @@TIME_ZONE */;
/*!40103 SET TIME_ZONE = '+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS = @@UNIQUE_CHECKS, UNIQUE_CHECKS = 0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS = @@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS = 0 */;
/*!40101 SET @OLD_SQL_MODE = @@SQL_MODE, SQL_MODE = 'NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES = @@SQL_NOTES, SQL_NOTES = 0 */;

--
-- Table structure for table `courses`
--

DROP TABLE IF EXISTS `courses`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `courses`
(
    `course_id`    int         NOT NULL AUTO_INCREMENT,
    `code`         varchar(10) NOT NULL,
    `name`         varchar(50) NOT NULL,
    `professor_id` int         NOT NULL,
    PRIMARY KEY (`course_id`),
    KEY `professorReference` (`professor_id`),
    CONSTRAINT `courses_ibfk_1` FOREIGN KEY (`professor_id`) REFERENCES `professors` (`professor_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `courses`
--

LOCK TABLES `courses` WRITE;
/*!40000 ALTER TABLE `courses`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `courses`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_reports`
--

DROP TABLE IF EXISTS `exam_reports`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_reports`
(
    `exam_report_id` int       NOT NULL AUTO_INCREMENT,
    `created_at`     timestamp NOT NULL,
    PRIMARY KEY (`exam_report_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_reports`
--

LOCK TABLES `exam_reports` WRITE;
/*!40000 ALTER TABLE `exam_reports`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `exam_reports`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exam_sessions`
--

DROP TABLE IF EXISTS `exam_sessions`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exam_sessions`
(
    `exam_session_id` int  NOT NULL AUTO_INCREMENT,
    `date`            date NOT NULL,
    `course_id`       int  NOT NULL,
    PRIMARY KEY (`exam_session_id`),
    UNIQUE KEY `avoidDuplication` (`date`, `course_id`),
    KEY `courseReference` (`course_id`),
    CONSTRAINT `exam_sessions_ibfk_1` FOREIGN KEY (`course_id`) REFERENCES `courses` (`course_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exam_sessions`
--

LOCK TABLES `exam_sessions` WRITE;
/*!40000 ALTER TABLE `exam_sessions`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `exam_sessions`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `exams`
--

DROP TABLE IF EXISTS `exams`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `exams`
(
    `exam_id`         int                                                                   NOT NULL AUTO_INCREMENT,
    `exam_session_id` int                                                                   NOT NULL,
    `student_id`      int                                                                   NOT NULL,
    `exam_report_id`  int                                                                            DEFAULT NULL,
    `status`          enum ('not inserted yet','inserted','published','refused','reported') NOT NULL DEFAULT 'not inserted yet',
    `result`          enum ('missing','rejected','sent back','passed')                               DEFAULT NULL,
    `grade`           int                                                                            DEFAULT NULL,
    `laude`           tinyint(1)                                                                     DEFAULT NULL,
    PRIMARY KEY (`exam_id`),
    KEY `sessionReference` (`exam_session_id`),
    KEY `studentReference` (`student_id`),
    KEY `reportReference` (`exam_report_id`),
    CONSTRAINT `exams_ibfk_1` FOREIGN KEY (`exam_session_id`) REFERENCES `exam_sessions` (`exam_session_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `exams_ibfk_2` FOREIGN KEY (`student_id`) REFERENCES `students` (`student_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `exams_ibfk_3` FOREIGN KEY (`exam_report_id`) REFERENCES `exam_reports` (`exam_report_id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `gradeLaudeCompatibility` CHECK ((((`grade` is null) and (`laude` is null)) or
                                                 ((`grade` <> 30) and (`laude` = false)) or (`grade` = 30))),
    CONSTRAINT `resultGradeCompatibility` CHECK (((`result` is null) or
                                                  ((`result` = _utf8mb4'missing') and (`grade` is null)) or
                                                  ((`result` = _utf8mb4'passed') and (`grade` between 18 and 30)) or
                                                  (((`result` = _utf8mb4'rejected') or (`result` = _utf8mb4'sent back')) and
                                                   (`grade` between 0 and 17)))),
    CONSTRAINT `statusResultCompatibility` CHECK ((((`status` = _utf8mb4'not inserted yet') and (`result` is null)) or
                                                   (`result` is not null)))
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exams`
--

LOCK TABLES `exams` WRITE;
/*!40000 ALTER TABLE `exams`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `exams`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `professors`
--

DROP TABLE IF EXISTS `professors`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `professors`
(
    `professor_id` int         NOT NULL,
    `name`         varchar(40) NOT NULL,
    `surname`      varchar(40) NOT NULL,
    PRIMARY KEY (`professor_id`),
    CONSTRAINT `professors_ibfk_1` FOREIGN KEY (`professor_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `professors`
--

LOCK TABLES `professors` WRITE;
/*!40000 ALTER TABLE `professors`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `professors`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `students`
--

DROP TABLE IF EXISTS `students`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `students`
(
    `student_id`      int         NOT NULL,
    `name`            varchar(40) NOT NULL,
    `surname`         varchar(40) NOT NULL,
    `email`           varchar(60) NOT NULL,
    `bachelor_course` varchar(60) NOT NULL,
    PRIMARY KEY (`student_id`),
    UNIQUE KEY `email` (`email`),
    CONSTRAINT `students_ibfk_1` FOREIGN KEY (`student_id`) REFERENCES `users` (`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `students`
--

LOCK TABLES `students` WRITE;
/*!40000 ALTER TABLE `students`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `students`
    ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users`
(
    `user_id`  int                          NOT NULL AUTO_INCREMENT,
    `username` varchar(20)                  NOT NULL,
    `password` varchar(20)                  NOT NULL,
    `role`     enum ('student','professor') NOT NULL,
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `username` (`username`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users`
    DISABLE KEYS */;
/*!40000 ALTER TABLE `users`
    ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE = @OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE = @OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS = @OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS = @OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT = @OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS = @OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION = @OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES = @OLD_SQL_NOTES */;

-- Dump completed on 2021-08-02  0:09:41
