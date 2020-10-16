/*
 Navicat Premium Data Transfer

 Source Server         : PC
 Source Server Type    : MySQL
 Source Server Version : 80021
 Source Host           : localhost:3306
 Source Schema         : test

 Target Server Type    : MySQL
 Target Server Version : 80021
 File Encoding         : 65001

 Date: 16/10/2020 15:47:42
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for user_password
-- ----------------------------
DROP TABLE IF EXISTS `user_password`;
CREATE TABLE `user_password`  (
  `id` int NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `creator_id` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `FK3hu43tfbgg4ei7msxgin7hg8x`(`creator_id`) USING BTREE,
  CONSTRAINT `FK3hu43tfbgg4ei7msxgin7hg8x` FOREIGN KEY (`creator_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_password
-- ----------------------------
INSERT INTO `user_password` VALUES (1, '$2a$10$xiXBBBz.NyVuDN5DUWp2T.xgs0kchYYAnEBETaFmkdxPo1ryi8NZC', 1);
INSERT INTO `user_password` VALUES (2, '$2a$10$xiXBBBz.NyVuDN5DUWp2T.xgs0kchYYAnEBETaFmkdxPo1ryi8NZC', 2);
INSERT INTO `user_password` VALUES (3, '$2a$10$xiXBBBz.NyVuDN5DUWp2T.xgs0kchYYAnEBETaFmkdxPo1ryi8NZC', 3);

SET FOREIGN_KEY_CHECKS = 1;
