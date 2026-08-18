# 示例工程手册 | Example Guide

> 本手册仅适用于 `17.3.x` 版本线（JDK 17 / Spring Boot 3.x）。`8.2.x` 版本线不提供示例工程。

仓库中的 `example`（DDD 示例后端）与 `frontend`（前端示例 monorepo）均为**示例工程**，不属于框架核心内容，仅用于演示框架各能力的落地方式，可直接删除不影响框架本身。

## 一、环境要求

| 依赖 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | 17.3.x 版本线最低要求 |
| Maven | 3.6+ | 仓库自带 `mvnw` Wrapper，无需单独安装 |
| Node.js | 20+ | 仅运行前端示例时需要 |
| pnpm | 10+ | 仅运行前端示例时需要 |

默认配置下**无需**安装数据库与 Redis：后端使用 H2 文件数据库、JWT 无状态认证。

## 二、示例后端（example）

### 2.1 模块结构

`example` 是一个遵循 DDD 分层（`interface → app → domain ← infra`）的多模块工程：

```
example/
├── example-server           # Spring Boot 启动入口（端口 8090）
├── example-interface        # 接口层：Controller（API）、事件 Handler、ApplicationRunner
├── example-app              # 应用层
│   ├── example-app-query        # 查询服务（CQRS Query 侧）
│   ├── example-app-cmd-domain   # 命令服务（CQRS Command 侧，领域编排）
│   └── example-app-cmd-meta     # 元数据命令服务
├── example-domain           # 领域层
│   └── example-domain-user      # 用户领域（Entity、Repository 接口、Service、Event、Gateway）
└── example-infra            # 基础设施层
    ├── example-infra-jpa        # JPA 持久化实现（实现 domain 层 Repository 接口）
    └── example-infra-security   # 安全配置（UserDetailsService、PasswordEncoder 实现）
```

### 2.2 启动后端

在仓库根目录执行：

```bash
# 首次运行建议先安装所有模块
./mvnw clean install -DskipTests

# 启动示例应用（端口 8090）
./mvnw spring-boot:run -pl example/example-server
```

启动说明：

* 数据源为 H2 文件数据库（`jdbc:h2:file:./example.db`），`ddl-auto=update` 自动建表，无需初始化脚本；
* 应用启动时 `UserRunner` 会自动初始化内置管理员账号：**用户名 `admin`，密码 `admin`**；
* 已启用 JWT 无状态认证（`codingapi.security.jwt.enable=true`）。

### 2.3 登录获取 Token

框架的登录地址默认为 `POST /user/login`（可通过 `codingapi.security.login-processing-url` 修改），请求体为 JSON：

```bash
curl -X POST http://localhost:8090/user/login \
  -H 'Content-Type: application/json' \
  -d '{"username":"admin","password":"admin"}'
```

响应示例（`SingleResponse<LoginResponse>`）：

```json
{
  "success": true,
  "errCode": null,
  "errMessage": null,
  "data": {
    "username": "admin",
    "token": "eyJhbGciOi...",
    "authorities": [],
    "data": null
  }
}
```

### 2.4 携带 Token 调用业务接口

除 `codingapi.security.ignore-urls` 配置的免认证地址（默认 `/open/**` 等）外，业务接口需在请求头 `Authorization` 中携带登录返回的 `token`：

```bash
TOKEN="<登录返回的 token>"

# 分页查询用户列表（GET /api/query/user/list）
curl 'http://localhost:8090/api/query/user/list?current=0&pageSize=20' \
  -H "Authorization: $TOKEN"

# 按字段动态过滤（RequestFilter：等值查询）
curl 'http://localhost:8090/api/query/user/list?current=0&pageSize=20&username=admin' \
  -H "Authorization: $TOKEN"

# 新增/修改用户（id>0 为修改，id=0 为新增）
curl -X POST http://localhost:8090/api/cmd/user/save \
  -H "Authorization: $TOKEN" -H 'Content-Type: application/json' \
  -d '{"id":0,"name":"张三","username":"zhangsan","password":"123456"}'

# 删除用户
curl -X POST http://localhost:8090/api/cmd/user/remove \
  -H "Authorization: $TOKEN" -H 'Content-Type: application/json' \
  -d '{"id":1}'

# 免认证接口示例（框架内置）
curl http://localhost:8090/open/version
```

### 2.5 可选配置

`example/example-server/src/main/resources/application.properties` 中提供了注释形式的可选配置：

* **切换 MySQL**：取消注释 `com.mysql.cj.jdbc.Driver` 相关配置，注释 H2 配置，并自行创建 `example` 数据库；
* **切换 Redis 有状态认证**：取消注释 `codingapi.security.redis.enable=true` 与 `spring.data.redis.*` 配置，并启动本地 Redis。

常用框架配置项：

```properties
# 免认证 URL 列表
codingapi.security.ignore-urls=/open/**,/#/**,/,/**.css,/**.js,/**.svg,/**.png,/**.ico
# 事件异步线程池大小
codingapi.framework.handler-thread-pool-size=20
```

## 三、示例前端（frontend）

前端示例为 pnpm workspace monorepo，包含 `apps/pc`（PC 端管理后台）与 `apps/mobile`（移动端）两个应用，详细脚本说明见 [frontend/README.md](../frontend/README.md)。

```bash
cd frontend
pnpm install

pnpm dev:pc        # PC 端开发模式（代理后端 8090）
pnpm dev:mobile    # 移动端开发模式（代理后端 8090）
pnpm mock:pc       # PC 端 Mock 模式（无需后端）
pnpm mock:mobile   # 移动端 Mock 模式（无需后端）
pnpm build:pc      # PC 端生产构建
pnpm build:mobile  # 移动端生产构建
```

运行 `dev:*` 前请先启动示例后端（见 2.2），开发服务会将 `/api`、`/user` 等请求代理到 `http://localhost:8090`。

## 四、常见问题

**Q1：启动报端口占用？**
示例默认使用 `8090` 端口，可通过 `server.port` 修改。

**Q2：请求业务接口返回 `token must not null` / `token expire`？**
请求头未携带 `Authorization`，或 Token 已过期，请重新调用 `/user/login` 获取。

**Q3：如何清理 H2 数据？**
停止应用后删除运行目录下的 `example.db*` 文件，重启即自动重建。
