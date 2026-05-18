# workrecode

在工状态审计系统，包含管理员网页端、员工网页端和员工端钉钉小程序演示版，当前版本已完成“项目管理 + 员工填报 + 管理员审批 + 多维统计分析 + 待审批撤回”的基础业务闭环。

## 功能概览

### 管理员网页端

- 员工管理
- 部门管理
- 项目管理
- 填报记录审批
- 独立统计分析
- 按项目、人员、日期、状态联动筛选
- 首页数据看板与最近记录展示
- 已撤回记录状态展示

### 员工端

- 网页版填报入口
- 钉钉小程序演示版
- 按日期和项目提交权重
- 查看今日填报记录、剩余权重和待审批数量
- 撤回待审批填报记录

## 技术栈

- 后端：Spring Boot 2.7、MyBatis-Plus、MySQL
- 前端：Vue 3、Vite、Element Plus、Axios
- 小程序：钉钉小程序原生写法
- 运行环境：Java 17、Node.js、MySQL 8

## 目录结构

```text
workrecode
├─ src/                    Spring Boot 后端
├─ frontend/               管理员网页端 + 员工网页端
├─ dingtalk-miniapp/       员工端钉钉小程序演示版
├─ clear_demo_data.sql     演示数据清理脚本
└─ README.md               项目说明
```

## 本地启动

### 1. 数据库准备

创建数据库：

```sql
CREATE DATABASE workrecode CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
```

然后手动执行：

- `src/main/resources/schema.sql`
- `src/main/resources/init.sql`

默认数据库配置位于 `src/main/resources/application.yml`：

- 端口：`9090`
- 数据库：`workrecode`
- 用户：`root`
- 密码：`020222`

### 2. 启动后端

```bash
./.mvn-local/apache-maven-3.9.6/bin/mvn spring-boot:run
```

如果本地已经安装 Maven，也可以直接：

```bash
mvn spring-boot:run
```

### 3. 启动前端

```bash
cd frontend
npm install
npm run dev
```

默认访问地址：

- 前端：`http://127.0.0.1:3000`
- 后端：`http://127.0.0.1:9090`

员工网页端可访问：

```text
http://127.0.0.1:3000/login
```

## 钉钉小程序演示版

员工端小程序目录：

```text
dingtalk-miniapp/
```

使用钉钉小程序开发者工具打开该目录即可运行。

演示前请确认 `dingtalk-miniapp/config.js` 中的后端地址配置正确，例如：

```js
apiBase: 'http://127.0.0.1:9090/api'
```

说明：

- 当前版本为会议演示版，员工身份使用固定测试员工。
- 正式接入时，应通过钉钉免登获取真实用户身份并绑定员工表。

## 演示数据清理

会议演示前可执行：

```text
clear_demo_data.sql
```

该脚本会清理：

- 员工填报记录
- 员工表
- 员工账号

并保留管理员维护的项目数据，避免小程序项目下拉为空。

## 当前版本

当前正式版本标签：

```text
v1.3.0
```

版本内容包括：

- 管理员端新增独立“统计分析”模块
- 统计分析支持项目、人员、日期、状态下拉联动筛选
- 支持按项目、按人员、按日期和明细多视角查看
- 展示参与人数、项目数、记录数、总权重和加班权重
- 保留原“审批记录”页面用于待审批和登记记录管理
- 前端启动后自动打开登录页

详细发布说明见：

```text
RELEASE_NOTES.md
```
