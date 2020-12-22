# 清球汇后端说明
[![Build Status](https://www.travis-ci.com/leonardodalinky/ThuBP-backend.svg?branch=dev)](https://www.travis-ci.com/leonardodalinky/ThuBP-backend) [![Coverage Status](https://coveralls.io/repos/github/leonardodalinky/ThuBP-backend/badge.svg)](https://coveralls.io/github/leonardodalinky/ThuBP-backend)

## 部署环境

* JRE 8
* Maven 3
* （可选）Make 构建工具

## 部署方式

### Make 脚本（推荐）

如果拥有 Make 工具，可以在当前目录运行下述命令：

```shell
# 运行
make run
# 单元测试
make test
# 清理
make clean
```

### Maven 启动

也可以使用 maven 工具来运行

```shell
# 运行
mvn spring-boot:run
# 单元测试
mvn test
# 清理
mvn clean
```

## 配置说明

通过修改 `src/main/resources/application.properties` 文件，可以修改服务器布置的参数。