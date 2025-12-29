-- V1__init_schema.sql
-- 说明：Flyway 迁移脚本只会执行一次，不要写 DROP TABLE

-- =========================
-- 1) 部门表 department
-- =========================
CREATE TABLE IF NOT EXISTS `department` (
                                            `id` BIGINT NOT NULL AUTO_INCREMENT,
                                            `name` VARCHAR(100) NOT NULL COMMENT '部门名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '部门描述',
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 2) 员工表 employee
-- =========================
CREATE TABLE IF NOT EXISTS `employee` (
                                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                                          `name` VARCHAR(100) NOT NULL COMMENT '员工姓名',
    `gender` VARCHAR(10) DEFAULT NULL COMMENT '员工性别',
    `department_id` BIGINT DEFAULT NULL COMMENT '部门ID',
    PRIMARY KEY (`id`),
    KEY `idx_employee_department_id` (`department_id`),
    CONSTRAINT `fk_employee_department`
    FOREIGN KEY (`department_id`)
    REFERENCES `department` (`id`)
    ON DELETE SET NULL
    ON UPDATE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 3) 用户表 user（注意：user 用反引号）
-- =========================
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(100) NOT NULL,
    `role` VARCHAR(20) NOT NULL,
    `name` VARCHAR(50) DEFAULT NULL,
    PRIMARY KEY (`id`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 4) 初始化数据（可选：部门/员工）
-- =========================
INSERT INTO `department` (`name`, `description`) VALUES
                                                     ('技术部', '负责系统开发和维护'),
                                                     ('人事部', '负责员工招聘和管理'),
                                                     ('财务部', '负责公司财务核算');

INSERT INTO `employee` (`name`, `gender`, `department_id`) VALUES
                                                               ('张三', '男', 1),
                                                               ('李四', '女', 2),
                                                               ('王五', '男', 1),
                                                               ('赵六', '女', 3);

-- =========================
-- 5) 初始化 admin 账号
-- username=admin, password=admin123
-- =========================
INSERT INTO `user` (`username`, `password`, `role`, `name`) VALUES
    ('admin', 'admin123', 'ADMIN', '管理员');
