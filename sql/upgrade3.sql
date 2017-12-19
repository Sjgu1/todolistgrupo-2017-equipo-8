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

ALTER TABLE Tarea ADD descripcion varchar(255);
ALTER TABLE Tarea ADD tableroId bigint(20) DEFAULT NULL;
ALTER TABLE Tarea ADD usuarioTareaId bigint(20) DEFAULT NULL;
ALTER TABLE Tarea ADD FOREIGN KEY (`tableroId`) REFERENCES `Tablero` (`id`);
ALTER TABLE Tarea ADD FOREIGN KEY (`usuarioTareaId`) REFERENCES `Usuario` (`id`);


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

CREATE TABLE `Etiqueta_Tarea` (
  `tareas_id` bigint(20) NOT NULL,
  `etiquetas_id` bigint(20) NOT NULL,
  PRIMARY KEY (`tareas_id`,`etiquetas_id`),
  KEY `FKs2uv1ib7x49tce3qw115vgj74` (`etiquetas_id`),
  CONSTRAINT `FK16crpvejl60miklj55u6gj5ye` FOREIGN KEY (`tareas_id`) REFERENCES `Tarea` (`id`),
  CONSTRAINT `FKs2uv1ib7x49tce3qw115vgj74` FOREIGN KEY (`etiquetas_id`) REFERENCES `Etiqueta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

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

CREATE TABLE `Etiqueta_Tablero` (
  `Tablero_id` bigint(20) NOT NULL,
  `etiquetas_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Tablero_id`,`etiquetas_id`),
  UNIQUE KEY `UK_2wq7ayn2gr51lgrbkrj714lnc` (`etiquetas_id`),
  CONSTRAINT `FK2g92kpit37bkfp8ivlgfxee1h` FOREIGN KEY (`etiquetas_id`) REFERENCES `Etiqueta` (`id`),
  CONSTRAINT `FKc348ea5iif9i9nwxngdadruay` FOREIGN KEY (`Tablero_id`) REFERENCES `Tablero` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

CREATE TABLE `Etiqueta_Usuario` (
  `Usuario_id` bigint(20) NOT NULL,
  `etiquetas_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Usuario_id`,`etiquetas_id`),
  UNIQUE KEY `UK_659jrdwm7wf19uae6sso99908` (`etiquetas_id`),
  CONSTRAINT `FK6h1xsrfj4dnd0gbrjwxyghxpd` FOREIGN KEY (`Usuario_id`) REFERENCES `Usuario` (`id`),
  CONSTRAINT `FKs0plaj93j7w7n4iloeda9e897` FOREIGN KEY (`etiquetas_id`) REFERENCES `Etiqueta` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
