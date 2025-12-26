-- 创建部门表
CREATE TABLE IF NOT EXISTS `department` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL COMMENT '部门名称',
  `description` VARCHAR(500) DEFAULT NULL COMMENT '部门描述',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 创建员工表
CREATE TABLE IF NOT EXISTS `employee` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(255) NOT NULL COMMENT '员工姓名',
  `gender` VARCHAR(10) DEFAULT NULL COMMENT '员工性别',
  `department_id` BIGINT DEFAULT NULL COMMENT '部门ID',
  PRIMARY KEY (`id`),
  KEY `idx_department_id` (`department_id`),
  CONSTRAINT `fk_employee_department` FOREIGN KEY (`department_id`) REFERENCES `department` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入初始部门数据
INSERT INTO `department` (`name`, `description`) VALUES 
('技术部', '负责系统开发和维护'),
('人事部', '负责员工招聘和管理'),
('财务部', '负责公司财务核算');

-- 插入初始员工数据
INSERT INTO `employee` (`name`, `gender`, `department_id`) VALUES 
('张三', '男', 1),
('李四', '女', 2),
('王五', '男', 1),
('赵六', '女', 3);