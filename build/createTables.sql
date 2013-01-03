DROP TABLE IF EXISTS `softwarefm`.`usage_table`;
CREATE TABLE  `softwarefm`.`usage_table` (
  `user` varchar(255) NOT NULL DEFAULT '',
  `ip` varchar(45) NOT NULL,
  `path` varchar(255) NOT NULL,
  `times` int(10) unsigned NOT NULL,
  `time` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
