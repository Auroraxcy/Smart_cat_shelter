-- 创建数据库
CREATE DATABASE IF NOT EXISTS smart_cat_shelter DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE smart_cat_shelter;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    nickname VARCHAR(50),
    email VARCHAR(100),
    phone VARCHAR(20),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 猫咪表
CREATE TABLE IF NOT EXISTS cats (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    breed VARCHAR(50),
    color VARCHAR(50),
    age INT,
    gender VARCHAR(10),
    weight DOUBLE,
    health_status VARCHAR(50),
    photo VARCHAR(255),
    description TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 喂食记录表
CREATE TABLE IF NOT EXISTS feeding_records (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cat_id BIGINT NOT NULL,
    feeding_time DATETIME NOT NULL,
    food_type VARCHAR(50),
    amount DOUBLE,
    keeper_id BIGINT,
    notes TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cat_id) REFERENCES cats(id) ON DELETE CASCADE,
    FOREIGN KEY (keeper_id) REFERENCES users(id) ON DELETE SET NULL
);

-- 健康提醒表
CREATE TABLE IF NOT EXISTS health_reminders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    cat_id BIGINT,
    remind_time DATETIME,
    priority VARCHAR(20),
    completed BOOLEAN DEFAULT FALSE,
    created_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (cat_id) REFERENCES cats(id) ON DELETE SET NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 环境数据表
CREATE TABLE IF NOT EXISTS environment_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    temperature DOUBLE,
    humidity DOUBLE,
    air_quality DOUBLE,
    area VARCHAR(50),
    recorded_by BIGINT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (recorded_by) REFERENCES users(id) ON DELETE SET NULL
);

-- 插入示例数据
-- 管理员用户 (密码: admin123)
INSERT INTO users (username, password, role, nickname) VALUES
('admin', '$2a$10$MgVmSm4T3OQtVFU/enObF.Dfxn9o87BoGSnTUwcPrU57sr.bLuV2m', 'ADMIN', '管理员');

-- 饲养员用户 (密码: keeper123)
INSERT INTO users (username, password, role, nickname) VALUES
('keeper', '$2a$10$FegEamIwSRk/ekYTS7aRKutfxu/hPPHSNRDQuOUlgXHDqO9d8IIES', 'KEEPER', '饲养员');

-- 示例猫咪数据
INSERT INTO cats (name, breed, color, age, gender, weight, health_status, description) VALUES
('小橘', '橘猫', '橘色', 2, '公', 4.5, '健康', '活泼可爱'),
('咪咪', '英短', '蓝白', 1, '母', 3.2, '健康', '温柔乖巧'),
('大黄', '中华田园猫', '黄色', 3, '公', 5.0, '健康', '性格温顺');

-- 示例喂食记录（使用子查询获取正确的用户ID）
INSERT INTO feeding_records (cat_id, feeding_time, food_type, amount, keeper_id, notes) 
SELECT 1, NOW(), '猫粮', 100, id, '早餐' FROM users WHERE username = 'keeper';

INSERT INTO feeding_records (cat_id, feeding_time, food_type, amount, keeper_id, notes) 
SELECT 2, NOW(), '罐头', 80, id, '午餐' FROM users WHERE username = 'keeper';

-- 示例健康提醒（使用子查询获取正确的用户ID）
INSERT INTO health_reminders (title, description, cat_id, remind_time, priority, completed, created_by) 
SELECT '疫苗接种', '需要接种狂犬疫苗', 1, DATE_ADD(NOW(), INTERVAL 7 DAY), 'HIGH', FALSE, id 
FROM users WHERE username = 'admin';

INSERT INTO health_reminders (title, description, cat_id, remind_time, priority, completed, created_by) 
SELECT '体检', '定期体检', 2, DATE_ADD(NOW(), INTERVAL 30 DAY), 'MEDIUM', FALSE, id 
FROM users WHERE username = 'keeper';

-- 示例环境数据（使用子查询获取正确的用户ID）
INSERT INTO environment_data (temperature, humidity, air_quality, area, recorded_by) 
SELECT 22.5, 55.0, 85.0, 'A区', id FROM users WHERE username = 'keeper';

INSERT INTO environment_data (temperature, humidity, air_quality, area, recorded_by) 
SELECT 23.0, 58.0, 88.0, 'B区', id FROM users WHERE username = 'keeper';
