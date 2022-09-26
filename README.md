[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/codingapi/springboot-framework/blob/main/LICENSE)
[![Maven Central](https://img.shields.io/maven-central/v/com.codingapi.springboot/springboot-starter.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.codingapi.springboot%22%20AND%20a:%22springboot-starter%22)
[![Build Status](https://app.travis-ci.com/codingapi/springboot-framework.svg?branch=main)](https://app.travis-ci.com/codingapi/springboot-framework)
[![codecov](https://codecov.io/gh/codingapi/springboot-framework/branch/main/graph/badge.svg?token=Gl9LjJV6y4)](https://codecov.io/gh/codingapi/springboot-framework)

# springboot-framework | Springboot领域驱动开发

> 当你无意间推开这一扇门，将会感叹原来生活可以如此的美好。

本框架基于springboot为提供领域驱动设计与事件风暴开发落地，提供的范式开源框架。

## Project Modules Description | 项目模块介绍

* springboot-example | 示例项目
* springboot-starter | Springboot领域驱动框架
* springboot-starter-data-fast | 快速数据呈现框架
* springboot-starter-id-generator | Id自增策略框架
* springboot-starter-security-jwt | security&jwt权限框架

## SpringBoot DDD Architecture | SpringBoot DDD 框架图

![](./docs/img/ddd_architecture.png)

## maven install

```
    <!-- Springboot领域驱动框架 -->
    <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter</artifactId>
        <version>${last.version}</version>
    </dependency>
    
     <!-- 快速数据呈现框架 -->
     <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-data-fast</artifactId>
        <version>${last.version}</version>
    </dependency>
    
     <!-- Id自增策略框架 -->
    <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-id-generator</artifactId>
        <version>${last.version}</version>
    </dependency>
    
     <!-- security&jwt权限框架 -->
     <dependency>
        <groupId>com.codingapi.springboot</groupId>
        <artifactId>springboot-starter-security-jwt</artifactId>
        <version>${last.version}</version>
     </dependency>
     
```

## CONTRIBUTING

Welcome to springboot-framework ! This document is a guideline about how to contribute to springboot-framework.
If you find something incorrect or missing, please leave comments / suggestions.

[CONTRIBUTING](./CONTRIBUTING.md)

## example

见 [springboot-ddd-examples](https://github.com/1991wangliang/springboot-ddd-examples) 完善中...

## YouTube|哔哩哔哩

欢迎订阅我的YouTube账号 [codingapi](https://www.youtube.com/channel/UCdAsCAxh453D7MfLfYWj0Eg),
哔哩哔哩账号[vip_lorne](https://space.bilibili.com/386239614) 带你从0到1落地springboot与领域驱动设计

领域驱动设计001--了解为什么采用领域驱动设计开发 [YouTube](https://www.youtube.com/watch?v=09uP_sMvhY8) [哔哩哔哩](https://www.bilibili.com/video/BV1WB4y157kv)     
领域驱动设计002--SpringBoot DDD Example
HelloWorld [YouTube](https://www.youtube.com/watch?v=d7LnYy8rTYI&t=149s) [哔哩哔哩](https://www.bilibili.com/video/BV1qU4y1k7DV)    
领域驱动设计003--事件风暴 [YouTube](https://www.youtube.com/watch?v=EiMvgIKT46I) [哔哩哔哩](https://www.bilibili.com/video/BV1tG41147AU)   
领域驱动设计004--领域边界 [YouTube](https://www.youtube.com/watch?v=l80I3LkvGdE) [哔哩哔哩](https://www.bilibili.com/video/BV1Za411d78d)   
领域驱动设计005--统一模型语言UML [YouTube](https://www.youtube.com/watch?v=FESDalckNQ4) [哔哩哔哩](https://www.bilibili.com/video/BV1gg411D7oe)      
领域驱动设计006--领域建模01 [YouTube](https://www.youtube.com/watch?v=Fee0oXCDZxA) [哔哩哔哩](https://www.bilibili.com/video/BV1DV4y1p7Rw)      
领域驱动设计006--领域建模02 [YouTube](https://www.youtube.com/watch?v=_VNmArHYZSI) [哔哩哔哩](https://www.bilibili.com/video/BV1i14y1t7wE)   
领域驱动设计007--模型测试01 [YouTube](https://www.youtube.com/watch?v=jCyGLAgjH8A) [哔哩哔哩](https://www.bilibili.com/video/BV1Jt4y1E7EX)      
领域驱动设计007--模型测试02 [YouTube](https://www.youtube.com/watch?v=dBFhNbb8LHg) [哔哩哔哩](https://www.bilibili.com/video/BV1HB4y1478r)      
领域驱动设计008--模型集成01 [YouTube](https://www.youtube.com/watch?v=lmLFHGxkL1g) [哔哩哔哩](https://www.bilibili.com/video/BV14P411V7kS)   
领域驱动设计008--模型集成02 [YouTube](https://www.youtube.com/watch?v=1A2OUn26sMc) [哔哩哔哩](https://www.bilibili.com/video/BV1DD4y167ea)   
领域驱动设计008--模型集成03 [YouTube](https://www.youtube.com/watch?v=vu-dyolhRJM) [哔哩哔哩](https://www.bilibili.com/video/BV1We4y1o7ob)    
领域驱动设计009--回顾总结 [YouTube](https://www.youtube.com/watch?v=VLPzblm1qPc) [哔哩哔哩](https://www.bilibili.com/video/BV1ZW4y1v7b5)    


## books

实战书籍正在编写中...

## 推荐DDD的三个理由

* 业务逻辑可视化
* 灵活的业务拓展性
* 轻量化的业务单元测试

## 领域驱动设计开发流程

* 事件风暴
* 划分子域
* 设计模型
* 单元测试
* 集成业务

## 推荐的框架与软件

### SpringJPA

JPA是ORM框架，可以非常简单的与领域对象相关联。 https://spring.io/projects/spring-data-jpa

### StartUML

StartUML是UML设计软件。https://staruml.io/

### Mural

Mural 是用于画事件风暴图的软件。https://www.mural.co/

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
