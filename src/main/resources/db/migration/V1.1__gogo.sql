-- ----------------------------
-- Table structure for `t_audit_video`
-- ----------------------------
DROP TABLE IF EXISTS `t_audit_video`;
CREATE TABLE `t_audit_video` (
  `id` bigint(20) NOT NULL,
  `targetid` bigint(20) DEFAULT NULL,
  `sn` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `username` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `deviceName` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `path` varchar(128) COLLATE utf8_bin NOT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_audit_video_path` (`path`) USING BTREE,
  KEY `t_audit_video_username` (`username`) USING BTREE,
  KEY `t_audit_video_devicename` (`deviceName`) USING BTREE,
  KEY `t_audit_video_targetid` (`targetid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

INSERT INTO `t_right` VALUES ('5', '审计录像', 'browser_audit_video');