[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/codingapi/springboot-framework/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.codingapi.springboot/springboot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.codingapi.springboot%22%20AND%20a:%22springboot-starter%22)
[![Build](https://img.shields.io/github/actions/workflow/status/codingapi/springboot-framework/ci.yml?label=Build&logo=github)](https://github.com/codingapi/springboot-framework/actions)
[![Codecov](https://codecov.io/gh/codingapi/springboot-framework/branch/17.3.x/graph/badge.svg)](https://codecov.io/gh/codingapi/springboot-framework)

# springboot-framework | Springboot领域驱动开发

> 当你无意间推开这一扇门，将会感叹原来生活可以如此的美好。

本框架基于springboot为提供领域驱动设计与事件风暴开发落地，提供的范式开源框架。

## Project Version | 项目版本说明

自 `17.3.0` 起，项目采用 CI-Friendly 版本管理方式（与 [flow-engine](https://github.com/codingapi/flow-engine) 相同）：pom 中的版本声明为 `${revision}`，开发期锁定为 SNAPSHOT 版本（当前为 `17.3.0-SNAPSHOT`），正式发布时通过 `-Drevision=x.y.z` 指定正式版本号，无需修改 pom。

版本号含义：第一位为 JDK 版本，第二位为 Spring Boot 大版本，第三位为补丁版本。例如 `17.3.0` 表示基于 JDK 17、Spring Boot 3.x 的第 0 个补丁版本。

（历史版本线：v.2.x 对应 Spring Boot 2.x / JDK 8；v.3.x 对应 Spring Boot 3.x / JDK 17。）

Since `17.3.0`, the project uses a CI-Friendly versioning scheme (same approach as [flow-engine](https://github.com/codingapi/flow-engine)): the pom declares its version as `${revision}`, which is pinned to a SNAPSHOT during development (currently `17.3.0-SNAPSHOT`); the release version is supplied via `-Drevision=x.y.z` at release time, with no pom changes needed.

Version number convention: first segment = JDK version, second segment = Spring Boot major version, third segment = patch version. For example, `17.3.0` targets JDK 17 and Spring Boot 3.x, patch 0.

(Legacy lines: v.2.x for Spring Boot 2.x on JDK 8; v.3.x for Spring Boot 3.x on JDK 17.)

## Frontend Framework Version | 前端框架版本说明

| Package                                                               | Description  | Version                                                                                                                   |
|-----------------------------------------------------------------------|--------------|---------------------------------------------------------------------------------------------------------------------------|
| [@codingapi/ui-framework](https://github.com/codingapi/ui-compoments)  | UI-Framework | [![npm](https://img.shields.io/npm/v/@codingapi/ui-framework.svg)](https://www.npmjs.com/package/@codingapi/ui-framework) |
| [@codingapi/form-pc](https://github.com/codingapi/ui-compoments)            | Form-PC      | [![npm](https://img.shields.io/npm/v/@codingapi/form-pc.svg)](https://www.npmjs.com/package/@codingapi/form-pc)           |
| [@codingapi/form-mobile](https://github.com/codingapi/ui-compoments)    | Form-Mobile  | [![npm](https://img.shields.io/npm/v/@codingapi/form-mobile.svg)](https://www.npmjs.com/package/@codingapi/form-mobile)   |

前端代码位于 [frontend](./frontend) 目录，采用 pnpm workspace 管理：包含 `apps/pc`（原 admin-ui）与 `apps/mobile`（原 mobile-ui）两个应用，公共代码抽取在 `packages/shared`（@springboot-framework/shared）。

The frontend code lives in the [frontend](./frontend) directory, managed as a pnpm workspace: it contains two apps `apps/pc` (formerly admin-ui) and `apps/mobile` (formerly mobile-ui), with shared code extracted into `packages/shared` (@springboot-framework/shared).

```bash
cd frontend
pnpm install
pnpm dev:pc        # PC 端开发模式（代理后端） / PC dev mode (proxies backend)
pnpm dev:mobile    # 移动端开发模式（代理后端） / Mobile dev mode (proxies backend)
pnpm build:pc      # PC 端生产构建 / PC production build
pnpm build:mobile  # 移动端生产构建 / Mobile production build
```

更多指令见 [frontend/README.md](./frontend/README.md)。

See [frontend/README.md](./frontend/README.md) for more scripts.


## Project Modules Description | 项目模块介绍

* springboot-starter | Springboot领域驱动框架
* springboot-starter-script | 脚本引擎框架
* springboot-starter-data-fast | 快速数据呈现框架
* springboot-starter-data-authorization | 数据权限框架
* springboot-starter-security | security权限框架支持基于JWT的无状态权限认证与Redis的有状态权限认证
* example | 示例DDD项目
* frontend | 前端 monorepo（pnpm workspace），包含 apps/pc（原 admin-ui）管理后台UI脚手架、apps/mobile（原 mobile-ui）移动端UI脚手架与 packages/shared 公共代码

## Flow Engine Migration | 流程引擎迁移说明

工作流引擎（springboot-starter-flow）已从本框架移除，重构为独立仓库维护，新地址：[https://github.com/codingapi/flow-engine](https://github.com/codingapi/flow-engine)。需要使用流程引擎的项目请改用独立仓库。

The workflow engine (springboot-starter-flow) has been removed from this framework and refactored into an independently maintained repository: [https://github.com/codingapi/flow-engine](https://github.com/codingapi/flow-engine). Projects that need the workflow engine should migrate to the standalone repository.

## SpringBoot DDD Architecture | SpringBoot DDD 框架图

![](./docs/img/ddd_architecture.png)

## maven install

以下示例使用正式发布版本（如 `17.3.0`，最新发布版见上方 Maven Central 徽章）；如需体验开发快照版本，请使用 `17.3.0-SNAPSHOT`。

The examples below use an official release version (e.g. `17.3.0`; see the Maven Central badge above for the latest release). Use `17.3.0-SNAPSHOT` for the development snapshot.

```
    <!-- Springboot领域驱动框架 -->
    <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter</artifactId>
        <version>17.3.0</version>
    </dependency>
    
    <!-- 脚本引擎框架 -->
    <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-script</artifactId>
        <version>17.3.0</version>
    </dependency>
    
     <!-- 快速数据呈现框架 -->
     <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-data-fast</artifactId>
        <version>17.3.0</version>
    </dependency>
    
     <!-- 数据权限框架 -->
     <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-data-authorization</artifactId>
        <version>17.3.0</version>
    </dependency>

     <!-- security&jwt权限框架 -->
     <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-security</artifactId>
        <version>17.3.0</version>
     </dependency>
     
```

## CONTRIBUTING

Welcome to springboot-framework ! This document is a guideline about how to contribute to springboot-framework.
If you find something incorrect or missing, please leave comments / suggestions.

[CONTRIBUTING](./CONTRIBUTING.md)

## Documentation

https://github.com/codingapi/springboot-framework/wiki

## Example

见 [example](./example)

## Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.1/maven-plugin/reference/html/#build-image)
* [Spring Security](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#web.security)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#appendix.configuration-metadata.annotation-processor)
* [Spring Web](https://docs.spring.io/spring-boot/docs/2.7.1/reference/htmlsingle/#web)
* [securing-web](https://spring.io/guides/gs/securing-web/)
* [spring-security-without-the-websecurityconfigureradapter](https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter)
* [springboot-security&jwt](https://blog.csdn.net/u014553029/article/details/112759382)
* [Meituan-Dianping/Leaf](https://github.com/Meituan-Dianping/Leaf)
* [SpringBoot Test](https://spring.io/guides/gs/testing-web/)
* [SpringBoot Web Test](https://spring.io/guides/gs/testing-web/)  
