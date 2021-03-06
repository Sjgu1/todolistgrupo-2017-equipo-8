-- MySQL dump 10.13  Distrib 5.7.19, for Linux (x86_64)
--
-- Host: localhost    Database: mads
-- ------------------------------------------------------
-- Server version	5.7.19

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
-- Table structure for table `Comentario`
--

DROP TABLE IF EXISTS `Comentario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Comentario` (
  `id` bigint(20) NOT NULL,
  `comentario` varchar(255) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT NULL,
  `fechaModificacion` datetime DEFAULT NULL,
  `usuario` varchar(255) DEFAULT NULL,
  `tareaId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqaf2c0nv49vyle281btvqbluw` (`tareaId`),
  CONSTRAINT `FKqaf2c0nv49vyle281btvqbluw` FOREIGN KEY (`tareaId`) REFERENCES `Tarea` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Etiqueta`
--

DROP TABLE IF EXISTS `Etiqueta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Etiqueta` (
  `id` bigint(20) NOT NULL,
  `color` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `tableroId` bigint(20) DEFAULT NULL,
  `usuarioId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKqhvnrs00mpblhwhp2ondwu2vo` (`tableroId`),
  KEY `FK50l4987uvmbkiqihaqymy2hlo` (`usuarioId`),
  CONSTRAINT `FK50l4987uvmbkiqihaqymy2hlo` FOREIGN KEY (`usuarioId`) REFERENCES `Usuario` (`id`),
  CONSTRAINT `FKqhvnrs00mpblhwhp2ondwu2vo` FOREIGN KEY (`tableroId`) REFERENCES `Tablero` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Etiqueta_Tablero`
--

DROP TABLE IF EXISTS `Etiqueta_Tablero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Etiqueta_Tablero` (
  `Tablero_id` bigint(20) NOT NULL,
  `etiquetas_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Tablero_id`,`etiquetas_id`),
  UNIQUE KEY `UK_2wq7ayn2gr51lgrbkrj714lnc` (`etiquetas_id`),
  CONSTRAINT `FK2g92kpit37bkfp8ivlgfxee1h` FOREIGN KEY (`etiquetas_id`) REFERENCES `Etiqueta` (`id`),
  CONSTRAINT `FKc348ea5iif9i9nwxngdadruay` FOREIGN KEY (`Tablero_id`) REFERENCES `Tablero` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Etiqueta_Tarea`
--

DROP TABLE IF EXISTS `Etiqueta_Tarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Etiqueta_Tarea` (
  `tareas_id` bigint(20) NOT NULL,
  `etiquetas_id` bigint(20) NOT NULL,
  PRIMARY KEY (`tareas_id`,`etiquetas_id`),
  KEY `FKs2uv1ib7x49tce3qw115vgj74` (`etiquetas_id`),
  CONSTRAINT `FK16crpvejl60miklj55u6gj5ye` FOREIGN KEY (`tareas_id`) REFERENCES `Tarea` (`id`),
  CONSTRAINT `FKs2uv1ib7x49tce3qw115vgj74` FOREIGN KEY (`etiquetas_id`) REFERENCES `Etiqueta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Etiqueta_Usuario`
--

DROP TABLE IF EXISTS `Etiqueta_Usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Etiqueta_Usuario` (
  `Usuario_id` bigint(20) NOT NULL,
  `etiquetas_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Usuario_id`,`etiquetas_id`),
  UNIQUE KEY `UK_659jrdwm7wf19uae6sso99908` (`etiquetas_id`),
  CONSTRAINT `FK6h1xsrfj4dnd0gbrjwxyghxpd` FOREIGN KEY (`Usuario_id`) REFERENCES `Usuario` (`id`),
  CONSTRAINT `FKs0plaj93j7w7n4iloeda9e897` FOREIGN KEY (`etiquetas_id`) REFERENCES `Etiqueta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Persona_Tablero`
--

DROP TABLE IF EXISTS `Persona_Tablero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Persona_Tablero` (
  `tableros_id` bigint(20) NOT NULL,
  `participantes_id` bigint(20) NOT NULL,
  PRIMARY KEY (`tableros_id`,`participantes_id`),
  KEY `FKnghbrhyh7eal30o78h3293n72` (`participantes_id`),
  CONSTRAINT `FKbpw5yq3ofgud0ra8a916kddjm` FOREIGN KEY (`tableros_id`) REFERENCES `Tablero` (`id`),
  CONSTRAINT `FKnghbrhyh7eal30o78h3293n72` FOREIGN KEY (`participantes_id`) REFERENCES `Usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Tablero`
--

DROP TABLE IF EXISTS `Tablero`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tablero` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `administradorId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKq82919iay2b8h77msdj8289p0` (`administradorId`),
  CONSTRAINT `FKq82919iay2b8h77msdj8289p0` FOREIGN KEY (`administradorId`) REFERENCES `Usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Tarea`
--

DROP TABLE IF EXISTS `Tarea`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Tarea` (
  `id` bigint(20) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `fechaCreacion` datetime DEFAULT NULL,
  `fechaLimite` date DEFAULT NULL,
  `terminada` bit(1) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `usuarioTareaId` bigint(20) DEFAULT NULL,
  `tableroId` bigint(20) DEFAULT NULL,
  `usuarioId` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK90tirn6fqwxohlg4kafef7tpw` (`usuarioTareaId`),
  KEY `FK2r7tsv4xu3bjvst83o8xuspud` (`tableroId`),
  KEY `FKepne2t52y8dmn8l9da0dd7l51` (`usuarioId`),
  CONSTRAINT `FK2r7tsv4xu3bjvst83o8xuspud` FOREIGN KEY (`tableroId`) REFERENCES `Tablero` (`id`),
  CONSTRAINT `FK90tirn6fqwxohlg4kafef7tpw` FOREIGN KEY (`usuarioTareaId`) REFERENCES `Usuario` (`id`),
  CONSTRAINT `FKepne2t52y8dmn8l9da0dd7l51` FOREIGN KEY (`usuarioId`) REFERENCES `Usuario` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `Usuario`
--

DROP TABLE IF EXISTS `Usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `Usuario` (
  `id` bigint(20) NOT NULL,
  `apellidos` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `fechaNacimiento` date DEFAULT NULL,
  `login` varchar(255) DEFAULT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `hibernate_sequence`
--

DROP TABLE IF EXISTS `hibernate_sequence`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` VALUES (1),(1);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;
