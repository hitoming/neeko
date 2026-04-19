-- ============================================
-- NeekoMap 数据库设计
-- 基于Web电子地图的照片服务系统
-- ============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS neeko_map DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE neeko_map;

-- ============================================
-- 1. 用户表 (sys_user)
-- ============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    username    VARCHAR(50) NOT NULL COMMENT '用户名',
    password    VARCHAR(100) NOT NULL COMMENT '密码(加密存储)',
    nickname    VARCHAR(50) COMMENT '昵称',
    avatar      VARCHAR(255) COMMENT '头像URL',
    email       VARCHAR(100) COMMENT '邮箱',
    phone       VARCHAR(20) COMMENT '手机号',
    status      TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    last_login_time DATETIME COMMENT '最后登录时间',
    last_login_ip VARCHAR(50) COMMENT '最后登录IP',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================
-- 2. 相册表 (photo_album)
-- ============================================
DROP TABLE IF EXISTS photo_album;
CREATE TABLE photo_album (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '相册ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    title       VARCHAR(100) NOT NULL COMMENT '相册名称',
    description VARCHAR(500) COMMENT '相册描述',
    cover_url   VARCHAR(255) COMMENT '封面图片URL',
    is_public   TINYINT DEFAULT 2 COMMENT '隐私级别: 0-仅自己, 1-好友可见, 2-公开',
    photo_count INT DEFAULT 0 COMMENT '照片数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_privacy (is_public)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='相册表';

-- ============================================
-- 3. 照片表 (photo_image)
-- ============================================
DROP TABLE IF EXISTS photo_image;
CREATE TABLE photo_image (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '照片ID',
    user_id     BIGINT NOT NULL COMMENT '上传用户ID',
    album_id    BIGINT COMMENT '所属相册ID',
    name        VARCHAR(200) NOT NULL COMMENT '照片名称',
    description VARCHAR(500) COMMENT '照片描述',
    url         VARCHAR(500) NOT NULL COMMENT '图片URL',
    thumbnail_url VARCHAR(500) COMMENT '缩略图URL',
    longitude   DECIMAL(10, 6) COMMENT '经度',
    latitude    DECIMAL(10, 6) COMMENT '纬度',
    location_name VARCHAR(200) COMMENT '位置描述',
    tag_ids     VARCHAR(200) COMMENT '标签ID列表,逗号分隔',
    is_public   TINYINT DEFAULT 2 COMMENT '隐私级别: 0-仅自己, 1-好友可见, 2-公开',
    view_count  INT DEFAULT 0 COMMENT '浏览次数',
    like_count  INT DEFAULT 0 COMMENT '点赞次数',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_album_id (album_id),
    KEY idx_location (longitude, latitude),
    -- KEY idx_privacy (privacy)
    KEY idx_privacy (is_public)  -- 把 privacy 改成 is_public
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='照片表';

-- ============================================
-- 4. 标签表 (sys_tag)
-- ============================================
DROP TABLE IF EXISTS sys_tag;
CREATE TABLE sys_tag (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '标签ID',
    name        VARCHAR(50) NOT NULL COMMENT '标签名称',
    color       VARCHAR(20) COMMENT '标签颜色',
    icon        VARCHAR(50) COMMENT '标签图标',
    sort_order  INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='标签表';

-- 预设标签
INSERT INTO sys_tag (name, color, sort_order) VALUES
('建筑', '#3498db', 1),
('美食', '#e74c3c', 2),
('人物', '#9b59b6', 3),
('动物', '#2ecc71', 4),
('植物', '#1abc9c', 5),
('古镇', '#f39c12', 6),
('日出', '#e67e22', 7),
('星空', '#34495e', 8),
('沙漠', '#d35400', 9),
('山水', '#16a085', 10),
('峡谷', '#27ae60', 11),
('其他', '#95a5a6', 12);

-- ============================================
-- 5. 好友关系表 (friend_relation)
-- ============================================
DROP TABLE IF EXISTS friend_relation;
CREATE TABLE friend_relation (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT 'ID',
    user_id     BIGINT NOT NULL COMMENT '用户ID',
    friend_id   BIGINT NOT NULL COMMENT '好友ID',
    status      TINYINT DEFAULT 0 COMMENT '状态: 0-待确认, 1-已是好友',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_friend (user_id, friend_id),
    KEY idx_user_id (user_id),
    KEY idx_friend_id (friend_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='好友关系表';

-- ============================================
-- 6. 消息表 (chat_message)
-- ============================================
DROP TABLE IF EXISTS chat_message;
-- CREATE TABLE chat_message (
--     id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
--     sender_id   BIGINT NOT NULL COMMENT '发送者ID',
--     receiver_id BIGINT NOT NULL COMMENT '接收者ID',
--     content     TEXT COMMENT '消息内容',
--     is_read     TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
--     create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
--     PRIMARY KEY (id),
--     KEY idx_from_user (from_user_id),
--     KEY idx_to_user (to_user_id),
--     KEY idx_create_time (create_time)
-- ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';
CREATE TABLE chat_message (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '消息ID',
    sender_id   BIGINT NOT NULL COMMENT '发送者ID',
    receiver_id BIGINT NOT NULL COMMENT '接收者ID',
    content     TEXT COMMENT '消息内容',
    is_read     TINYINT DEFAULT 0 COMMENT '是否已读: 0-未读, 1-已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发送时间',
    PRIMARY KEY (id),
    KEY idx_sender (sender_id),      -- 修正：from_user_id → sender_id
    KEY idx_receiver (receiver_id),  -- 修正：to_user_id → receiver_id
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息表';
-- ============================================
-- 7. 操作日志表 (sys_log)
-- ============================================
DROP TABLE IF EXISTS sys_log;
CREATE TABLE sys_log (
    id          BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    user_id     BIGINT COMMENT '操作用户ID',
    username    VARCHAR(50) COMMENT '用户名',
    operation   VARCHAR(100) COMMENT '操作描述',
    method      VARCHAR(200) COMMENT '请求方法',
    params      TEXT COMMENT '请求参数',
    ip          VARCHAR(50) COMMENT 'IP地址',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 初始化测试数据
-- ============================================
-- 插入测试用户 (密码是 123456, BCrypt加密)
INSERT INTO sys_user (username, password, nickname, email) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'admin@neeko.com'),
('test', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '测试用户', 'test@neeko.com');

-- ============================================
-- 索引优化建议
-- ============================================
-- 对于照片的地理位置查询，可以创建空间索引
-- ALTER TABLE photo_image ADD SPATIAL INDEX idx_geo (longitude, latitude);
