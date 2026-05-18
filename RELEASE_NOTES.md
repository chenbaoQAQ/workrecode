# workrecode v1.2.0 本地联调版发布说明

发布日期：2026-05-18

## 一、版本定位

本版本面向本地开发与功能验收场景，重点完成员工端“待审批记录撤回”闭环，并将前端运行方式统一收敛为本地访问，避免开发阶段出现多个局域网地址、历史测试地址或部署环境地址混用的问题。

当前版本适用于：

- Spring Boot 后端本地运行在 `127.0.0.1:9090`
- 管理员/员工网页端本地运行在 `127.0.0.1:3000`
- 钉钉小程序开发者工具本地模拟器访问 `127.0.0.1:9090/api`

## 二、核心功能变更

### 1. 员工待审批记录撤回

新增员工撤回待审批填报记录能力，覆盖后端接口、管理员网页端状态展示、员工网页端操作入口和钉钉小程序操作入口。

业务规则如下：

- 仅 `PENDING` 待审批记录允许撤回。
- 仅允许员工撤回自己的填报记录。
- 撤回后记录状态变更为 `CANCELLED`。
- 撤回记录保留原始填报权重，便于追溯。
- 撤回记录的 `statHours` 与 `overtimeHours` 清零，不参与统计。
- 今日已填权重和剩余权重计算排除 `REJECTED` 与 `CANCELLED` 记录。

新增后端接口：

```http
POST /api/work-records/{id}/cancel
```

请求体示例：

```json
{
  "employeeId": 1
}
```

### 2. 员工网页端补齐撤回入口

员工网页端“我的权重记录”表格新增操作列：

- 待审批记录显示“撤回”按钮。
- 点击撤回前弹出二次确认。
- 撤回成功后自动刷新记录列表。
- 已通过、已驳回、已撤回记录不显示撤回按钮。

### 3. 钉钉小程序补齐撤回入口

钉钉小程序今日记录列表新增撤回能力：

- 待审批记录展示“撤回”按钮。
- 撤回前弹出确认框。
- 撤回成功后提示“已撤回”并刷新今日记录。
- 状态展示新增“已撤回”样式。

### 4. 管理员端状态展示完善

管理员首页、审批页、员工填报记录页统一识别 `CANCELLED` 状态，并展示为“已撤回”，避免页面直接显示英文状态值。

### 5. 本地化前端运行配置

前端开发配置已统一为本地运行模式：

- Vite 仅监听 `127.0.0.1:3000`。
- 开发环境接口统一走 `/api` 代理。
- `/api` 代理目标固定为 `http://127.0.0.1:9090`。
- 移除 Windows 测试构建脚本与多环境地址配置。
- 清理历史 `dist`、`dist.zip`、Windows 测试说明文件，避免误拿旧包。

本地前端启动后只应看到：

```text
Local: http://127.0.0.1:3000/
```

### 6. 登录接口兼容性增强

后端登录接口兼容两种请求格式：

- `application/json`
- `application/x-www-form-urlencoded`

前端登录页改为表单方式提交，避免部分运行环境下 JSON/Form 请求格式不一致导致登录失败。

## 三、涉及文件

后端：

- `src/main/java/com/example/workrecode/controller/WorkRecordController.java`
- `src/main/java/com/example/workrecode/service/WorkRecordService.java`
- `src/main/java/com/example/workrecode/service/impl/WorkRecordServiceImpl.java`
- `src/main/java/com/example/workrecode/controller/UserController.java`
- `src/main/resources/schema.sql`

管理员/员工网页端：

- `frontend/vite.config.js`
- `frontend/package.json`
- `frontend/src/views/LoginView.vue`
- `frontend/src/views/WorkRecordView.vue`
- `frontend/src/views/HomeView.vue`
- `frontend/src/views/WorkAdminView.vue`

钉钉小程序员工端：

- `dingtalk-miniapp/config.js`
- `dingtalk-miniapp/pages/index/index.js`
- `dingtalk-miniapp/pages/index/index.axml`
- `dingtalk-miniapp/pages/index/index.acss`
- `dingtalk-miniapp/README.md`

文档：

- `README.md`
- `RELEASE_NOTES.md`

## 四、本地启动方式

### 1. 启动后端

```bash
cd /Users/lijunchen/Documents/ljc/workrecode
./.mvn-local/apache-maven-3.9.6/bin/mvn spring-boot:run
```

后端接口地址：

```text
http://127.0.0.1:9090/api
```

### 2. 启动网页端

```bash
cd /Users/lijunchen/Documents/ljc/workrecode/frontend
npm run dev
```

网页端访问地址：

```text
http://127.0.0.1:3000/login
```

### 3. 启动钉钉小程序

使用钉钉开发者工具打开：

```text
/Users/lijunchen/Documents/ljc/workrecode/dingtalk-miniapp
```

当前本地接口配置：

```js
apiBase: 'http://127.0.0.1:9090/api'
```

## 五、验证结果

本版本已完成以下验证：

```text
后端 mvn -q -DskipTests package：通过
前端 npm run build：通过
小程序 index.js 语法检查：通过
Vite 本地启动地址检查：仅输出 127.0.0.1:3000
```

说明：前端构建过程中存在 Vite chunk size warning，为依赖体积提示，不影响本地运行和功能验收。

## 六、验收建议

建议按以下流程验收：

1. 员工登录网页端或打开钉钉小程序。
2. 新增一条项目权重填报记录。
3. 确认记录状态为“待审批”。
4. 点击“撤回”并确认。
5. 确认记录状态变为“已撤回”。
6. 确认今日已填权重和剩余权重随撤回恢复。
7. 管理员端查看历史记录，确认状态展示为“已撤回”。

## 七、交付结论

`workrecode v1.2.0 本地联调版` 已完成员工撤回待审批记录闭环，并完成前端本地化运行配置收敛。当前代码适合进入本地验收与后续钉钉真实身份接入前的功能评审阶段。
