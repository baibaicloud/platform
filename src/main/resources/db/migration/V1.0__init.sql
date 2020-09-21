/*
Navicat MySQL Data Transfer

Source Server         : 本地
Source Server Version : 50703
Source Host           : localhost:3306
Source Database       : lbridge

Target Server Type    : MYSQL
Target Server Version : 50703
File Encoding         : 65001


*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `t_device`
-- ----------------------------
DROP TABLE IF EXISTS `t_device`;
CREATE TABLE `t_device` (
  `id` bigint(20) NOT NULL,
  `sn` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `ip` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `name` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `devdesc` varchar(64) COLLATE utf8_bin DEFAULT '',
  `cpu` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `memony` varchar(16) COLLATE utf8_bin DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_device_sn` (`sn`) USING BTREE,
  KEY `t_device_ctime` (`ctime`) USING BTREE,
  KEY `t_device_devdesc` (`devdesc`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_device
-- ----------------------------

-- ----------------------------
-- Table structure for `t_device_last_use`
-- ----------------------------
DROP TABLE IF EXISTS `t_device_last_use`;
CREATE TABLE `t_device_last_use` (
  `id` bigint(20) NOT NULL,
  `uid` bigint(20) DEFAULT NULL,
  `deviceUserId` bigint(20) DEFAULT NULL,
  `lasttime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_device_last_use_deviceuserid_uid` (`uid`,`deviceUserId`) USING BTREE,
  KEY `t_device_last_use_uid` (`uid`) USING BTREE,
  KEY `t_device_last_use_deviceuserid` (`deviceUserId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_device_last_use
-- ----------------------------

-- ----------------------------
-- Table structure for `t_device_settings`
-- ----------------------------
DROP TABLE IF EXISTS `t_device_settings`;
CREATE TABLE `t_device_settings` (
  `id` bigint(20) NOT NULL,
  `deviceId` varchar(20) COLLATE utf8_bin DEFAULT NULL,
  `autoRemote` enum('NO','YES') COLLATE utf8_bin DEFAULT NULL,
  `autoRun` enum('NO','YES') COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_device_settings_deviceid` (`deviceId`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_device_settings
-- ----------------------------

-- ----------------------------
-- Table structure for `t_device_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_device_user`;
CREATE TABLE `t_device_user` (
  `id` bigint(20) NOT NULL,
  `deviceId` bigint(20) DEFAULT NULL,
  `nodeId` bigint(20) DEFAULT NULL,
  `targetId` bigint(20) DEFAULT NULL,
  `userType` enum('ENTERPRISE','SOLE') COLLATE utf8_bin DEFAULT NULL,
  `sn` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_device_user_deviceid` (`deviceId`) USING BTREE,
  KEY `t_device_user_nodeid` (`nodeId`) USING BTREE,
  KEY `t_device_user_uid` (`targetId`) USING BTREE,
  KEY `t_device_user_sn` (`sn`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_device_user
-- ----------------------------

-- ----------------------------
-- Table structure for `t_enterprise`
-- ----------------------------
DROP TABLE IF EXISTS `t_enterprise`;
CREATE TABLE `t_enterprise` (
  `id` bigint(20) NOT NULL,
  `title` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `description` varchar(256) COLLATE utf8_bin DEFAULT NULL,
  `cuid` bigint(20) DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_enterprise_title` (`title`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_enterprise
-- ----------------------------

-- ----------------------------
-- Table structure for `t_enterprise_nenode_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_enterprise_nenode_role`;
CREATE TABLE `t_enterprise_nenode_role` (
  `id` bigint(20) NOT NULL,
  `eid` bigint(20) DEFAULT NULL,
  `nenodeId` bigint(20) DEFAULT NULL,
  `roleId` bigint(20) DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_enterprise_nenode_role_eid` (`eid`) USING BTREE,
  KEY `t_enterprise_nenode_role_nenodeId` (`nenodeId`) USING BTREE,
  KEY `t_enterprise_nenode_role_roleid` (`roleId`) USING BTREE,
  KEY `t_enterprise_nenode_role_ctime` (`ctime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_enterprise_nenode_role
-- ----------------------------

-- ----------------------------
-- Table structure for `t_enterprise_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_enterprise_role`;
CREATE TABLE `t_enterprise_role` (
  `id` bigint(20) NOT NULL,
  `eid` bigint(20) DEFAULT NULL,
  `title` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `isdefault` enum('YES','NO') COLLATE utf8_bin DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_enterprise_role_eid` (`eid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_enterprise_role
-- ----------------------------

-- ----------------------------
-- Table structure for `t_enterprise_role_right`
-- ----------------------------
DROP TABLE IF EXISTS `t_enterprise_role_right`;
CREATE TABLE `t_enterprise_role_right` (
  `id` bigint(20) NOT NULL,
  `eid` bigint(20) DEFAULT NULL,
  `roleId` bigint(20) DEFAULT NULL,
  `target` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_enterprise_role_right_all` (`eid`,`roleId`,`target`) USING BTREE,
  KEY `t_enterprise_role_right_eid` (`eid`) USING BTREE,
  KEY `t_enterprise_role_right_roleid` (`roleId`) USING BTREE,
  KEY `t_enterprise_role_right_target` (`target`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_enterprise_role_right
-- ----------------------------

-- ----------------------------
-- Table structure for `t_enterprise_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_enterprise_user`;
CREATE TABLE `t_enterprise_user` (
  `id` bigint(20) NOT NULL,
  `eid` bigint(20) DEFAULT NULL,
  `uid` bigint(20) DEFAULT NULL,
  `username` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_enterprise_user_eiduid` (`eid`,`uid`) USING BTREE,
  KEY `t_enterprise_user_eid` (`eid`) USING BTREE,
  KEY `t_enterprise_user_uid` (`uid`) USING BTREE,
  KEY `t_enterprise_user_ctime` (`ctime`) USING BTREE,
  KEY `t_enterprise_user_username` (`username`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_enterprise_user
-- ----------------------------

-- ----------------------------
-- Table structure for `t_enterprise_user_role`
-- ----------------------------
DROP TABLE IF EXISTS `t_enterprise_user_role`;
CREATE TABLE `t_enterprise_user_role` (
  `id` bigint(20) NOT NULL,
  `eid` bigint(20) DEFAULT NULL,
  `uid` bigint(20) DEFAULT NULL,
  `roleId` bigint(20) DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_enterprise_user_role_eid_uid_roleid` (`eid`,`uid`,`roleId`) USING BTREE,
  KEY `t_enterprise_user_role_eid` (`eid`) USING BTREE,
  KEY `t_enterprise_user_role_uid` (`uid`) USING BTREE,
  KEY `t_enterprise_user_role_roleid` (`roleId`) USING BTREE,
  KEY `t_enterprise_user_role_ctime` (`ctime`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_enterprise_user_role
-- ----------------------------

-- ----------------------------
-- Table structure for `t_log`
-- ----------------------------
DROP TABLE IF EXISTS `t_log`;
CREATE TABLE `t_log` (
  `id` bigint(20) NOT NULL,
  `uid` bigint(20) NOT NULL,
  `username` varchar(64) COLLATE utf8_bin NOT NULL,
  `module` varchar(128) COLLATE utf8_bin NOT NULL,
  `content` varchar(255) COLLATE utf8_bin NOT NULL,
  `ctime` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `t_log_username` (`username`) USING BTREE,
  KEY `t_log_module` (`module`) USING BTREE,
  KEY `t_log_ctime` (`ctime`) USING BTREE,
  KEY `t_log_content` (`content`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_log
-- ----------------------------

-- ----------------------------
-- Table structure for `t_nenode`
-- ----------------------------
DROP TABLE IF EXISTS `t_nenode`;
CREATE TABLE `t_nenode` (
  `id` bigint(20) NOT NULL,
  `operId` bigint(20) DEFAULT NULL,
  `title` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `pid` bigint(20) DEFAULT NULL,
  `path` varchar(255) COLLATE utf8_bin DEFAULT NULL,
  `orderIndex` int(8) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `t_nenode_operid` (`operId`) USING BTREE,
  KEY `t_nenode_pid` (`pid`) USING BTREE,
  KEY `t_nenode_path` (`path`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_nenode
-- ----------------------------

-- ----------------------------
-- Table structure for `t_right`
-- ----------------------------
DROP TABLE IF EXISTS `t_right`;
CREATE TABLE `t_right` (
  `id` bigint(20) NOT NULL,
  `title` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `target` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_right
-- ----------------------------
INSERT INTO `t_right` VALUES ('1', '节点管理', 'nenode_manage');
INSERT INTO `t_right` VALUES ('2', '企业管理', 'enterprise_manage');
INSERT INTO `t_right` VALUES ('3', '网元管理', 'add_device');
INSERT INTO `t_right` VALUES ('4', '内网穿透', 'tunnel_manage');

-- ----------------------------
-- Table structure for `t_tunnel`
-- ----------------------------
DROP TABLE IF EXISTS `t_tunnel`;
CREATE TABLE `t_tunnel` (
  `id` bigint(20) NOT NULL,
  `uid` bigint(20) DEFAULT NULL,
  `deviceId` bigint(20) DEFAULT NULL,
  `tunnetType` enum('TCP','UDP') COLLATE utf8_bin DEFAULT NULL,
  `isactive` enum('NO','YES') COLLATE utf8_bin DEFAULT NULL,
  `localIp` varchar(128) COLLATE utf8_bin DEFAULT NULL,
  `localPort` int(9) DEFAULT NULL,
  `remotePort` int(9) DEFAULT NULL,
  `remarks` varchar(512) COLLATE utf8_bin DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_tunnel_remoteport` (`remotePort`) USING BTREE,
  KEY `t_tunnel_deviceId` (`deviceId`) USING BTREE,
  KEY `t_tunnel_uid` (`uid`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_tunnel
-- ----------------------------

-- ----------------------------
-- Table structure for `t_user`
-- ----------------------------
DROP TABLE IF EXISTS `t_user`;
CREATE TABLE `t_user` (
  `id` bigint(20) NOT NULL,
  `orgid` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `email` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `selfname` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `username` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `password` varchar(64) COLLATE utf8_bin DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  `lastdate` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `t_user_email` (`email`) USING BTREE,
  UNIQUE KEY `t_user_username` (`username`) USING BTREE,
  KEY `orgid` (`orgid`) USING BTREE,
  KEY `t_user_selfname` (`selfname`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_user
-- ----------------------------

-- ----------------------------
-- Table structure for `t_user_account`
-- ----------------------------
DROP TABLE IF EXISTS `t_user_account`;
CREATE TABLE `t_user_account` (
  `id` bigint(20) NOT NULL,
  `owneruid` bigint(20) DEFAULT NULL,
  `uid` bigint(20) DEFAULT NULL,
  `ctime` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of t_user_account
-- ----------------------------
