-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Erstellungszeit: 10. Dez 2021 um 20:36
-- Server-Version: 10.4.11-MariaDB
-- PHP-Version: 7.4.5

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Datenbank: `f1times`
--

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `circuit`
--

CREATE TABLE `circuit` (
  `id_circuit` int(11) NOT NULL COMMENT 'Primary Key',
  `country` text NOT NULL,
  `city` text NOT NULL COMMENT 'City',
  `circuit` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `circuit`
--

INSERT INTO `circuit` (`id_circuit`, `country`, `city`, `circuit`) VALUES
(1, 'Bahrain', 'Sakhir', 'Bahrain International Circuit'),
(2, 'Emilia-Romagna', 'Imola', 'Autodromo Enzo e Dino Ferrari'),
(3, 'Portugal', 'Portimão', 'Autódromo Internacional do Algarve'),
(4, 'Spain', 'Barcelona', 'Circuit de Barcelona-Catalunya'),
(5, 'Monaco', 'Monte Carlo', 'Circuit de Monaco'),
(6, 'Azerbaijan', 'Baku', 'Baku City Circuit'),
(7, 'Canada', 'Montréal', 'Circuit Gilles-Villeneuve'),
(8, 'France', 'Le Castellet', 'Circuit Paul Ricard'),
(9, 'Austria', 'Spielberg', 'Red Bull Ring'),
(10, 'Great Britian', 'Silverstone', 'Silverstone Circuit'),
(11, 'Hungary', 'Budapest', 'Hungaroring'),
(12, 'Belgium', 'Spa-Francorchamps', 'Circuit de Spa-Francorchamps'),
(13, 'Netherlands', 'Zandvoort', 'Cicuit Zandvoort'),
(14, 'Italy', 'Monza ', 'Autodromo Nazionale Monza'),
(15, 'Russia', 'Sochi', 'Sochi Autodrom'),
(16, 'Singapore', 'Singapore', 'Marina Bay Street Circuit'),
(17, 'Japan', 'Suzuka', 'Suzuka International Racing Course'),
(18, 'United States', 'Austin', 'Circuit of The Americas'),
(19, 'Mexico', 'Mexico City', 'Autódromo Hermanos Rodríguez'),
(20, 'Brazil', 'São Paulo', 'Autódromo José Carlos Pace'),
(21, 'Australia', 'Melbourne', 'Melbourne Grand Prix Circuit'),
(22, 'Saudi Arabia', 'Jeddah', 'Jeddah Corniche Circuit'),
(23, 'Abu Dhabi', 'Yas Island', 'Yas Marina Circuit'),
(24, 'China', 'Shanghai', 'Shanghai International Circuit');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `driver`
--

CREATE TABLE `driver` (
  `id_driver` int(11) NOT NULL COMMENT 'Primary Key',
  `username` text NOT NULL COMMENT 'Username',
  `password` text NOT NULL COMMENT 'Password'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `driver`
--

INSERT INTO `driver` (`id_driver`, `username`, `password`) VALUES
(14, 'das', '123'),
(15, 'Testuser', 'abcd1234');

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `laptime`
--

CREATE TABLE `laptime` (
  `id_laptime` int(11) NOT NULL COMMENT 'Primary Key',
  `lapMinutes` text DEFAULT NULL,
  `lapSeconds` text DEFAULT NULL,
  `lapMillis` text DEFAULT NULL,
  `id_circuit` int(11) DEFAULT NULL,
  `id_tc` int(11) DEFAULT NULL,
  `id_driver` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `laptime`
--

INSERT INTO `laptime` (`id_laptime`, `lapMinutes`, `lapSeconds`, `lapMillis`, `id_circuit`, `id_tc`, `id_driver`) VALUES
(36, '00', '00', '000', 1, 1, 14),
(37, '00', '00', '000', 2, 1, 15);

-- --------------------------------------------------------

--
-- Tabellenstruktur für Tabelle `trackcondition`
--

CREATE TABLE `trackcondition` (
  `id_tc` int(11) NOT NULL COMMENT 'Primary Key',
  `track_condition` text NOT NULL COMMENT 'Track Condition'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

--
-- Daten für Tabelle `trackcondition`
--

INSERT INTO `trackcondition` (`id_tc`, `track_condition`) VALUES
(1, 'Dry'),
(2, 'Wet');

--
-- Indizes der exportierten Tabellen
--

--
-- Indizes für die Tabelle `circuit`
--
ALTER TABLE `circuit`
  ADD PRIMARY KEY (`id_circuit`);

--
-- Indizes für die Tabelle `driver`
--
ALTER TABLE `driver`
  ADD PRIMARY KEY (`id_driver`);

--
-- Indizes für die Tabelle `laptime`
--
ALTER TABLE `laptime`
  ADD PRIMARY KEY (`id_laptime`),
  ADD KEY `FK_circuit` (`id_circuit`),
  ADD KEY `FK_tc` (`id_tc`),
  ADD KEY `FK_driver` (`id_driver`);

--
-- Indizes für die Tabelle `trackcondition`
--
ALTER TABLE `trackcondition`
  ADD PRIMARY KEY (`id_tc`);

--
-- AUTO_INCREMENT für exportierte Tabellen
--

--
-- AUTO_INCREMENT für Tabelle `circuit`
--
ALTER TABLE `circuit`
  MODIFY `id_circuit` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key', AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT für Tabelle `driver`
--
ALTER TABLE `driver`
  MODIFY `id_driver` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key', AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT für Tabelle `laptime`
--
ALTER TABLE `laptime`
  MODIFY `id_laptime` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key', AUTO_INCREMENT=38;

--
-- AUTO_INCREMENT für Tabelle `trackcondition`
--
ALTER TABLE `trackcondition`
  MODIFY `id_tc` int(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary Key', AUTO_INCREMENT=3;

--
-- Constraints der exportierten Tabellen
--

--
-- Constraints der Tabelle `laptime`
--
ALTER TABLE `laptime`
  ADD CONSTRAINT `FK_circuit` FOREIGN KEY (`id_circuit`) REFERENCES `circuit` (`id_circuit`),
  ADD CONSTRAINT `FK_driver` FOREIGN KEY (`id_driver`) REFERENCES `driver` (`id_driver`),
  ADD CONSTRAINT `FK_tc` FOREIGN KEY (`id_tc`) REFERENCES `trackcondition` (`id_tc`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
