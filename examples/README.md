# springboot-framework-example

SpringBoot DDD Architecture | SpringBoot DDD 框架图

![](./docs/img/ddd_architecture.png)

## 依赖框架

[springboot-framework](https://github.com/codingapi/springboot-framework)  
[antd-pro](https://pro.ant.design/zh-CN/)

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

## 管理系统界面

![login](docs/img/login.png)
![home](docs/img/home.png)
![node](docs/img/node.png)

### antd-pro
antd-pro基于react的管理系统界面框架，提供了丰富的组件，可以快速的开发管理系统界面。本项目基于antd-pro umi@4版本开发。

运行步骤：
```shell
cd web
npm install
npm start
```

打包步骤：
```shell
cd scripts
# linux 环境 
sh package.sh
# windows 环境
./package.bat
```
更多详情请参考：https://pro.ant.design/docs/getting-started-cn

## 推荐的框架与软件

### SpringJPA

JPA是ORM框架，可以非常简单的与领域对象相关联。 https://spring.io/projects/spring-data-jpa

### StartUML

StartUML是UML设计软件。https://staruml.io/

### Mural

Mural 是用于画事件风暴图的软件。https://www.mural.co/
