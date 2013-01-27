DROP TABLE IF EXISTS `mavenrip`.`maven`;
CREATE TABLE  `mavenrip`.`maven` (
  `groupId` varchar(1000) NOT NULL,
  `artifactId` varchar(1000) NOT NULL,
  `version` varchar(1000) NOT NULL,
  `pomUrl` varchar(1000) NOT NULL,
  `digest` varchar(200) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;