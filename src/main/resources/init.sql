-- =========================
-- 初始化数据（可选：部门/员工）
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
-- 初始化 admin 账号
-- username=admin, password=admin123
-- =========================
INSERT INTO `user` (`username`, `password`, `role`, `name`) VALUES
    ('admin', 'admin123', 'ADMIN', '管理员');