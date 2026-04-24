
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
-- 3) 用户表 user
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
-- 4) 项目表 work_project
-- =========================
CREATE TABLE IF NOT EXISTS `work_project` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
    `description` VARCHAR(255) DEFAULT NULL COMMENT '项目说明',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_work_project_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- 5) 工时登记表 work_record
-- =========================
CREATE TABLE IF NOT EXISTS `work_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `project_id` BIGINT DEFAULT NULL COMMENT '项目ID（历史兼容字段）',
    `work_content` VARCHAR(255) NOT NULL COMMENT '工作内容',
    `employee_id` BIGINT DEFAULT NULL COMMENT '员工用户ID',
    `employee_name` VARCHAR(50) NOT NULL COMMENT '填报人',
    `work_date` DATE NOT NULL COMMENT '登记日期',
    `work_hours` DECIMAL(5,2) NOT NULL COMMENT '项目权重',
    `stat_hours` DECIMAL(5,2) NOT NULL COMMENT '正常计入权重',
    `overtime_hours` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '超出1的加班权重',
    `status` VARCHAR(20) NOT NULL DEFAULT 'APPROVED' COMMENT '状态：APPROVED/PENDING/REJECTED',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `admin_remark` VARCHAR(255) DEFAULT NULL COMMENT '管理员处理备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_work_record_project_id` (`project_id`),
    KEY `idx_work_record_employee_id` (`employee_id`),
    KEY `idx_work_record_work_date` (`work_date`),
    CONSTRAINT `fk_work_record_project`
        FOREIGN KEY (`project_id`)
        REFERENCES `work_project` (`id`)
        ON DELETE SET NULL
        ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
