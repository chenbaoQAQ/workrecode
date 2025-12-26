-- =========================
-- 删表（顺序很重要）
-- =========================
DROP TABLE IF EXISTS employee;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS user;

-- =========================
-- 部门表
-- =========================
CREATE TABLE department (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            name VARCHAR(100) NOT NULL,
                            description VARCHAR(255) NULL,
                            create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);


-- =========================
-- 员工表
-- 部门删除后 department_id 自动变 NULL
-- =========================
CREATE TABLE employee (
                          id BIGINT PRIMARY KEY AUTO_INCREMENT,
                          name VARCHAR(100) NOT NULL,
                          department_id BIGINT NULL,
                          create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                          CONSTRAINT fk_employee_department
                              FOREIGN KEY (department_id)
                                  REFERENCES department(id)
                                  ON DELETE SET NULL
                                  ON UPDATE CASCADE
);

-- =========================
-- 用户表（仅用于账号，不参与业务）
-- =========================
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL,
                      role VARCHAR(20) NOT NULL,
                      name VARCHAR(50),
                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- =========================
-- 初始化用户数据
-- =========================
INSERT INTO user (username, password, role, name) VALUES
                                                      ('admin', 'admin123', 'ADMIN', '管理员'),
                                                      ('user',  'user123',  'USER',  '普通用户');
