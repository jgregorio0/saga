CREATE TABLE IF NOT EXISTS `UPLOAD_FILES` (
  `FILE_ID` int(11) NOT NULL,
  `SITE_PATH` varchar(128) NOT NULL,
  `USER_NAME` varchar(128) NOT NULL,
  `FILE_NAME` varchar(128) NOT NULL,
  `FILE_CONTENT` mediumblob NOT NULL,
  PRIMARY KEY (`FILE_ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8