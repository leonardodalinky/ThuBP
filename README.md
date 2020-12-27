# ThuBP

![License](https://img.shields.io/github/license/leonardodalinky/ThuBP?color=blue) ![Build Status](https://www.travis-ci.com/leonardodalinky/ThuBP.svg?branch=dev) [![Coverage Status](https://coveralls.io/repos/github/leonardodalinky/ThuBP/badge.svg)](https://coveralls.io/github/leonardodalinky/ThuBP.svg) ![Top Language](https://img.shields.io/github/languages/top/leonardodalinky/ThuBP) ![Code Quality](https://img.shields.io/codacy/grade/8a8f274cab3d490799aa164133a3ba01) ![Total Line](https://img.shields.io/tokei/lines/github/leonardodalinky/ThuBP)

清华大学 2020秋 软件工程 球类赛事管理平台

**清球汇** (英文名: Tsinghua University Ball-game Platform)

此为后端地址，前端地址 [ThuBP 前端项目](https://github.com/diana-pwf/ThuBP-frontend)

## 产品目标

### 面向用户

举办和参加球类比赛的清华师生

解决的主要问题：

1. 学校球赛中原本采用手工管理模式，工作效率低而且容易产生错误。

2. 市场上现有的管理平台操作过于复杂，且面向校内用户时部分功能冗余。

### 功能目标

一个支持对阵模式的球类赛事管理系统，适用于篮球 、 乒乓球 、 网球 、 羽毛球等球类项目的校内赛事管理，具有比赛信息发布 、 选手报名 、 抽签分组 、 比赛编排 、 赛程管理 、 裁判管理 、 比赛记分以及赛况实时等功能， 简化人工操作，提高管理的准确性和效率。

**用户管理**:

|     功能     |                      详细说明                      |
| :----------: | :------------------------------------------------: |
|   用户注册   | 校内师生可在经过权威身份验证后，于平台注册相应账户 |
|   用户登录   |             已注册的师生可登录个人账户             |
| 个人信息管理 |          用户可以修改并完善自己的个人信息          |

**赛事管理**:

|   功能   |              详细说明              |
| :------: | :--------------------------------: |
| 创建赛事 |         创建并组织一个赛事         |
| 安排轮次 |        设立赛事中的一个轮次        |
| 编排比赛 |    在每个轮次编排实际的每场比赛    |
| 选手管理 | 选手创建或参加已有队伍，并进行管理 |
| 比赛实时 |         查看比赛的实时进展         |
| 裁判管理 |     邀请、删除、配置比赛的裁判     |

**通知管理**:

|   功能   |             详细说明             |
| :------: | :------------------------------: |
| 查看通知 |        用户可查看系统通知        |
| 通知发送 | 系统在某些操作后会向用户发送通知 |

### 性能目标

<a id="性能目标"></a>

吞吐量能够达到 500 RPS(Request per Second)，平均响应时间在 1 秒内，支持清华校内师生日常使用。

目前通过 JMeter 工具，在 60 秒的压力测试中，获得下面的折线图，X 轴表示并发数，Y 轴表示对应的结果。

|                         平均响应时间                         |                         95% 响应用时                         |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![平均响应时间](http://hackmd.thubp.iterator-traits.com/uploads/upload_2191fc1c8d12b14ce23c7131daaa1327.png) | ![95% 用户响应时间线](http://hackmd.thubp.iterator-traits.com/uploads/upload_1f80b1f810d6b5d4fcb695375ecdccc5.png) |

|                            异常率                            |                            吞吐量                            |
| :----------------------------------------------------------: | :----------------------------------------------------------: |
| ![异常率](http://hackmd.thubp.iterator-traits.com/uploads/upload_00759e63a28cf10e16e1396674140d6f.png) | ![吞吐量](http://hackmd.thubp.iterator-traits.com/uploads/upload_7ae14d6c95a7802e2a233b229a990a21.png) |

## 开发组织管理

### 过程管理

采用 Github 的 Issue & Kanban(看板) 对项目的开发进行管理。开发组人员在进行实际开发前，会在项目中提出相应的 Issue，并在开发完成后关闭 Issue。

![Issue 管理](http://hackmd.thubp.iterator-traits.com/uploads/upload_81ab6021f55e4dfb874aad06f080a3aa.png)

此外，通过开源的 [YApi](https://github.com/YMFE/yapi) 工具，预先设定好的 API 原型会提前发布在 YApi 平台，并被标记为“未完成”状态。由实际的开发人员实现后，再转为“已完成”状态。

![Yapi 任务](http://hackmd.thubp.iterator-traits.com/uploads/upload_982545f10f089636741d325f61828b0c.png)


### 人员分工

**林可**: 
开发后端逻辑业务的同时，负责组织团队开发事务。主要负责用户、评论和通知模块的实现，并接入图床和文本审核服务。

**彭维方**:
参与前端页面的设计及与后端API的交互，主要实现了个人中心和比赛实时模块，以及队伍详情组件和轮播图组件，学习实现了页面轮询、vue-Apollo发起graphQL请求、利用图床服务上传图片

**余齐齐**:
参与前端页面设计及与后端API的交互。主要实现了赛事主页、搜索页面和赛事详情页面，以及模糊搜索邀请成员组件。实现了页面到移动端的适配，学习了graphql相关知识。

**周恒宇**:
负责 API 制订与实现、系统逻辑架构的设计。负责 SSL 证书申请，实现赛事模块，进行逻辑测试以及部署服务。

### 开发环境

前端开发环境:

- Webstorm 2020.2.4 & chrome

后端开发环境:

- 开发环境: JDK 1.8 & Maven 3

- 数据库: MongoDB 4

- IDE: Idea Intellij

### 配置管理

采用 Git 软件配置管理工具，采用经典的分支模型，设立 Main 和 Dev 等分支进行开发。

## 系统设计

整体构架如下:

![](http://hackmd.thubp.iterator-traits.com/uploads/upload_870c421e0bc459b21f1a5c53423366ab.png)


### 前端交互

实现的页面及主要模块有：

#### 登录/注册页面

- 清华电子身份验证

- 为确保不重名、对用户输入有实时的检验，提高用户友好性。


| 注册页面                                                     | 登录页面                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![logon](http://hackmd.thubp.iterator-traits.com/uploads/upload_dfff9c1dd50b39d4b653c3f456886918.jpg) | ![login](http://hackmd.thubp.iterator-traits.com/uploads/upload_91dd4b1e5c2657641a6c7830301ce54a.jpg) |



#### 赛事概览页面

- 轮播图展示热门赛事

- 按赛事类型分类查看已有公开赛事

- 按赛事/组织者姓名分别搜索已有公开赛事，模糊匹配并实时展示结果

| 赛事概览页面                                                 |
| ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_3379efcc22a5b64e3fbb592e7693af2f.jpg) |

| 赛事综合页面                                                 | 特定赛事类型页面                                             | 搜索模糊匹配                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_3379efcc22a5b64e3fbb592e7693af2f.jpg) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_8615b3dd7d926068a4ca36c691dac263.jpg) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_835034f170b113ddc774f595e6723aff.jpg) |

#### 创建赛事页面

- 对输入进行简单的格式校验

- 可设定比赛形式：私有/公开，可设定比赛类型，可设定比赛赛制：个人/团体

- 可选择上传赛事图片

| 创建赛事页面                                                 |
| ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_be3f3bae117448c9cde5db5a3a5c02ec.jpg) |


#### 个人中心页面

- 上传头像及修改个人资料（修改个人信息和修改密码）

- 本人创建和参与赛事的展示

- 站内信的展示及数据统计，可以通过站内信内链接成为裁判、加入队伍，查看私有比赛等。

| 个人中心页面                                                 |
| ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_f6ef3e09a0478db3ca10554153da8c60.jpg) |


| 个人资料                                                     | 创建赛事                                                     | 参与赛事                                                     | 站内信                                                       |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_f6ef3e09a0478db3ca10554153da8c60.jpg) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_1ede06b7cf8109a5d25884cdc0fad964.jpg) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_23d8bb3e22d2b90f3e1036c1c2409c9b.jpg) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_9dd4d49f92926aaa991c394fb55e4460.jpg) |


#### 搜索结果展示页面

- 按赛事/组织者姓名分别搜索已有公开赛事，模糊匹配展示结果

| 搜索结果展示页面                                             |
| ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_27693ff23e3bbe75924802cf269ae9d8.jpg) |

#### 赛事详情页面

- 展示赛事详情，供赛事组织者修改赛事信息。

- 赛事组织者邀请他人查看私有比赛

- 赛事组织者添加/删除裁判

- 选手报名/取消报名/创建队伍/查看队伍

- 队长添加/删除队员，解散队伍

- 参赛人员的信息统计

- 组织者添加/删除轮次，每一轮次内包含比赛的展示。点击展示列表的“更多”可以进入比赛实时页面。



| 赛事详情页面                                                 |
| ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_e0533cb838f5507014cb5f147b049a13.jpg) |



| 赛事详情                                                     | 选手裁判列表                                                 | 比赛列表                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_dda0202351529c3b3f8b9857861441f2.jpg) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_31b519a7fa4ffdf2898b02a1b862c7a7.jpg) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_6a8e8ba1042a79ef7db82d96c8a72f02.jpg) |


#### 创建轮次页面

- 组织者可以创建轮次，选定要参与此轮次比赛的队伍。

- 选择好赛制/队伍（>=2），根据预定策略**自动编排**生成比赛

- 组织者也可以自定义添加/删除比赛

- 组织者可以编辑生成比赛的时间/地点/选定裁判

| 创建轮次页面                                                 |
| ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_b671b236193ff34ae74d3611f5c688ff.jpg)![](http://hackmd.thubp.iterator-traits.com/uploads/upload_d5e9c1abd4badf1ccd0c95c49aaf3b76.jpg) |


#### 比赛实时页面

- 实时播放比赛状况

- 观众发布/回复/删除评论

- 裁判实时改变比分/提交关键记录

| 比赛实时页面                                                 |
| ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_88a8bc8949c3cdcf0444b2f9700cb9dd.png) |




#### 链接跳转结果页面

- 注册成功/失败跳转结果页面

- 通过站内信点击链接成功页面

- 身份过期401页面


| 成功页面                                                     | 注册失败页面                                                 | 身份过期页面                                                 |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_7a2ef531296669a7cb29f01380247b90.png) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_e057fc4c092d744bb25c56beb2a6a80f.png) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_a266a1a84e3d13a69a22b67fe28e48cc.png) |

#### 移动端适配



| 登录/注册                                                    | 赛事主页                                                     | 搜索结果                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_f7ec78d7daf5bc88121f1e5319a7705e.png) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_83802b2f1193be053b330cbc60f3e8b0.png) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_f5e7fcc426f1d3611f6c32882008d79e.png) |


| 个人中心                                                     | 赛事详情                                                     | 赛事实时                                                     |
| ------------------------------------------------------------ | ------------------------------------------------------------ | ------------------------------------------------------------ |
| ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_c5f145348a2fae6d27190660ec6d11cf.png) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_940d2d2cef99b8117f97f2168945a677.png) | ![](http://hackmd.thubp.iterator-traits.com/uploads/upload_af77d39d04085f67c6b124fc7832e03d.png) |


### 后端模块

后端模块的具体体现，位于此文档同目录下的 `./doc/documentation` 文件夹中。

#### 用户模块 & 身份验证模块

用户和身份验证模块中，主要负责用户登录、用户注册、修改密码、个人信息修改、查看他人信息的功能。

#### 赛事模块

赛事模块主要负责赛事流程的处理。

在流程中，我们对赛事的举办流程进行了抽象，分为下面三个层次:

1. **赛事**: 代表一个总体的赛事，作为涵盖所有参赛实体、赛事总体信息的高层抽象单位，包含所有的比赛轮次。 

2. **轮次**: 代表一轮比赛，承载着一轮中所有的单场比赛。

3. **比赛**: 最小的单位，具体表现为两个队伍之间的比赛。

在上述三个抽象层中，赛事模块分别提供功能:

1. **赛事层面**: 支持赛事的创建、修改，允许组织者设置公开报名及公开查看，允许组织者邀请参赛选手及裁判。

2. **轮次层面**: 支持某场赛事中的轮次创建，允许组织者增加、修改、删除轮次中的比赛安排。

3. **比赛层面**: 支持单场比赛的信息管理，允许组织者增加、修改、删除单场比赛。

#### 通知模块

通知模块允许用户在进行某些操作(如邀请成员)后，系统可自动发送站内通知于目标用户。

同时，通知模块允许用户获取自己所收到的所有通知。

#### 评论模块 & 文本审核服务

评论模块允许用户对赛事整体或单场比赛进行实时评论。

此外，还允许部署方使用外部服务商所提供的文本审核服务。

#### 图片模块 & 图床服务

图片模块允许用户上传个人头像，或上传赛事预览图等图像。

此模块接入了外部服务商所提供的图床服务，以减轻服务器负担。

#### 定时模块

定时模块负责定期执行任务。目前执行的任务有:

- 清除过期的邀请码，并发送系统通知

- 计算当前赛事热度，决定广告版的展示内容

### Spring 框架

后端使用 Spring(SpringBoot) 框架，以 MVC 的方式运行。

### 接口规范

我们的接口分为 Restful 接口和 GraphQL 接口。

#### Restful 接口规范

Restful 接口使用 HTTP Status 区分正常与异常，载荷为 application/json。

对于正常的请求，响应为 200 OK，载荷一定包含一个 `message` 字段，值为 "ok"。

对于异常的请求，响应为 400/401/403/404 等，载荷包含

```json
{
    "message": string,     // 错误信息
    "code": int,           // 错误标识码
    "path": string,        // API 路径
    "timestamp": string,   // 时间
    "errorDetail": object  // 额外信息
}
```

身份验证错误一定为 401 Unauthorized。

#### GraphQL 接口规范

GraphQL 接口的详细定义请参考 `src/main/resources/graphql/schema.graphqls` 文件。

### 数据库设计

数据库的选择上，采用了 MongoDB。选用非结构化数据库，以应对赛事结构的易变性，同时 MongoDB 也具有不错的结构化查询效率。

#### 数据库表设计

在 MongoDB 中，共创建下述的集合(Colletions)

|    集合名    |  详细功能  |
| :----------: | :--------: |
|     user     |  用户信息  |
|    match     |  赛事信息  |
|    round     |  轮次信息  |
|     game     |  比赛信息  |
|     unit     |  队伍信息  |
| notification |  通知信息  |
|   comment    |  评论信息  |
|   bulletin   | 广告版信息 |

#### 数据库集群

数据库使用多个 MongoDB 服务器构成集群，目前实际使用 2 个。

为开启 MongoDB 的事务回滚功能，需要在多服务器集群的条件下允许(允许本地集群)，在 `prod` 部署环境中默认开启副本集。

## 重难点及解决方法

### 前端

#### 配置 GraphQL 请求---选择性获取对象的部分属性

参考了 Vue Apollo 的官方文档和网上的一些技术博客，配置了 Apollo client 和 Apollo provider，采用和 axios 请求类似的写法，在成功获取 graphql 返回的数据之外，也保证了代码风格的一致性。

#### 图片上传---用于用户头像和赛事图片的展示

参考图床“七牛云”的官方文档及 Ant Design Vue 上传图片组件 a-upload 的用法说明，在提供的 beforeUpload 函数中，先向后端发送待传图片的各项参数，然后通过设置组件的 action 属性，将后端返回的图片名称和 token，以及图片文件一并传给图床，最后将图床返回的 key 发给后端保存下来。

#### 轮询---用于比赛页面实时刷新比分动态、评论

在相应 Vue 页面的 create 函数中，在相关请求函数中判断当前页面路径，如果符合条件则调用 setTimeOut 函数，延时间隔递归调用该请求函数本身，这样不仅实现了进入当前页面开始轮询，同时也能做到离开该页面时停止轮询。

#### 模糊匹配

在搜索比赛、邀请用户、注册账户、修改个人信息等多处应用了模糊匹配，保证实时向用户反馈信息，提高用户体验。

#### 移动端适配

由于 web 端需要展示的信息较多，采用了 Tab/Table 等组件，在移动端的适配不够良好。考虑到比赛实际进行时，观众和裁判更多使用移动端，为了保证用户体验，重写了个人中心、赛事详情等页面的移动端适配，减少移动端展示的信息，保证页面简洁美观。使观众在移动端能够方便快捷的查找想要观看的比赛，观看赛事实时；裁判能够方便的进行比分的登记和比赛信息的纪律。

#### 根据用户、赛事状态对组件进行条件渲染

由于赛事状态多变（私有/公开、报名/开始比赛等）、用户角色较多（组织者/裁判/参与者/观众），因此根据赛事状态、用户角色做了各个页面的条件渲染，减少用户的误操作，提高用户使用感。

#### 考虑用户使用感，优化交互设计

充分的考虑到了用户使用感，优化交互设计。在用户进行操作后，会弹出消息提醒操作成功/失败；在用户进行关键操作时，会弹出确认框进行确认；根据用户操作的性质，使用了不同颜色的按钮；页面简洁，流程清晰。

### 后端

#### 插件化设计

在早期的设计中，比赛的编排以及比赛得分的设计，由项目中的插件模块承担，并且插件模块已经成型。

但是考虑到项目的人力成本，最后这个模块被暂时遗弃，其承载的功能由更简单的 `extra` 字段负责。

#### 赛事编排

在赛事模块中，最重要的一个逻辑功能就是对队伍进行自动编排。项目后端预置了多种类型的自动编排功能，包括淘汰赛、循环制等。

同时，为了满足用户的特殊需求，还允许用户自行加入、删除、修改已经编排的比赛，以此来实现多样化的赛程安排设计。

#### 性能突破

考虑到校内师生的数量以万为单位，并且前端对后端有采取轮询的访问方式，因此后端的性能是一个重要的评价指标。

目前，由于采用了 GraphQL 以及 Restful 的混用接口规范，后端的性能在这两种不同的接口规范下，性能表现有着不小的差异。粗略的估算，GraphQL 的性能约为 Restful 的 $33\%\sim50\%$ 左右。

在 [性能目标](#性能目标) 一节中我们提到，出现频率最高的“读”类型的 Restful 接口并发吞吐量约在 $600\sim700$ 之间，而同样类型的 GraphQL 接口吞吐量经过实际测试，大约在 $200\sim300$ 之间。

因此，在重要的高并发场景中，例如 `比赛的实时消息更新` 等接口，我们都采用了 Restful 接口以提高吞吐量。考虑到现有师生的规模，单服务器的吞吐量已经足够应付非大型活动的举办。

同时由于采用了 MongoDB 主从集群配置，使得数据库更加便于从单服务器扩展成多服务器，倘若项目给予足够的服务器来做负载均衡，本项目的能力将足够支撑全校的人员使用。

#### 文档维护

后端在开发过程中，进度领先于前端开发，因此后端有义务维护 API 文档。在开发中，借助 Spring 框架中的 Swagger 组件，同时通过在每个 Controller 函数、每个请求体和回复体结构之上，维护注解形式的文档，使得我们的 API 文档可以与应用同步发布。

目前只需要将部署环境设为 `dev`，即可在 `http://0.0.0.0:9960/swagger-ui/` 位置开放 swagger 文档页面，并可在 [http://127.0.0.1:9960/swagger-ui/](http://127.0.0.1:9960/swagger-ui/) 访问。

在 Swagger 之外，开发团队还采用 Yapi 文档管理工具进行 API 的预发布与测试。

## 测试总结

### YApi 测试

YApi 测试部署在 YApi 平台上，可以方便的供前后端测试 API 的正确性和返回值的具体内容，降低了前后端的沟通成本。利用 YApi 测试集，我们可以测试新的 API 的同时进行旧 API 的回归测试。

YApi 的测试集一共分为 9 组，分别为

#### 调试 API 集

调试 API 只有一条，重置数据库。这个 API 只能由 root 用户在 dev 模式下调用，并会导致所有数据库内容被清空，root 用户密码被重置。

#### 身份验证测试集

本测试集测试了用户的登录和注册功能

#### 赛事信息测试集

本测试集测试了

- 不同可见性赛事的创建

- 赛事邀请码机制

- 赛事裁判邀请码机制

- 队伍邀请码机制

- 队伍的解散

- 轮次和比赛的创建、修改、删除

并均通过测试

#### 用户信息测试集

本测试集测试了

- 获取用户信息

- 按用户名查找

- 模糊用户名查找

- 修改个人信息

并均通过测试

#### 图片上传测试集

本测试集测试了

- 请求图片上传 token

并通过了测试

#### 评论功能测试集

本测试集测试了

- 赛事评论的创建、修改、编辑、查看

并均通过测试

#### 通知功能测试集

本测试集测试了

- 系统通知发送

- 用户查看通知

- 用户将通知标为已读

- 删除通知

并均通过测试

#### 插件测试集

本测试集测试了

 - 列出赛事类型

 - 列出赛程策略

的 GraphQL API，并通过了测试

### JUnit 白盒测试

基于已有的代码结构，我们利用 JUnit 专门进行了白盒测试。

测试集共 61 个子测试项目，与 YApi 测试集基本一致，语句覆盖率为 85.33%。

## 系统部署

目前部署于腾讯云服务器，项目部署情况如下:

![License](https://img.shields.io/github/license/leonardodalinky/ThuBP?color=blue) ![Build Status](https://www.travis-ci.com/leonardodalinky/ThuBP.svg?branch=dev) [![Coverage Status](https://coveralls.io/repos/github/leonardodalinky/ThuBP/badge.svg)](https://coveralls.io/github/leonardodalinky/ThuBP.svg) ![Top Language](https://img.shields.io/github/languages/top/leonardodalinky/ThuBP) ![Code Quality](https://img.shields.io/codacy/grade/8a8f274cab3d490799aa164133a3ba01) ![Total Line](https://img.shields.io/tokei/lines/github/leonardodalinky/ThuBP)

### 部署方法

#### 部署流程

目前，本项目的部署流程如下:

1. MongoDB 搭建(可选)

2. 后端部署

3. 前端部署

#### MongoDB 搭建

**如果使用 `docker-compose` 一键布置后端，则此处无需单独搭建 MongoDB**

目前**推荐**使用 docker-compose 搭建 MongoDB 本地集群。为方便起见，此处采用 `host` 网络桥接方式，docker 容器与主机共享网络和端口(仅 linux 可用)。

首先复制下列脚本为 `docker-compose.yml`

```dockerfile
version: '3'
services:
  master:
    image: mongo
    container_name: mongo_thubp1
    volumes:
      - /home/ubuntu/mongo/thubp1/db/:/data/db/
      - /home/ubuntu/mongo/thubp1/configdb/:/data/configdb/
      - /home/ubuntu/mongo/thubp1/tmp/:/thubp/tmp/
    restart: always
    command: ["mongod", "--port", "27117", "--replSet", "rs_thubp"]
    network_mode: "host"
    
  slave:
    image: mongo
    container_name: mongo_thubp2
    volumes:
      - /home/ubuntu/mongo/thubp2/db/:/data/db/
      - /home/ubuntu/mongo/thubp2/configdb/:/data/configdb/
    restart: always
    command: ["mongod", "--port", "27118", "--replSet", "rs_thubp"]
    network_mode: "host"
```

之后，在 `docker-compose.yml` 文件同级目录下，运行 shell 命令启动集群

```shell
docker-compose up -d
```

等 docker 开启完毕，连续执行下述命令，进入主 Mongo 节点内

```shell
docker exec -it mongo_thubp1 sh
```

在 docker 容器内，输入命令进入 mongo 控制面板:

```shell
mongo localhost:27117
```

之后依次输入下述命令，将 mongo_thubp2 节点加入集群:

```shell
rs.initiate()
rs.add("localhost:27118")
```

到此为止，本地 MongoDB 集群已经搭建完毕，可以通过 `mongodb://localhost:27117/thubp?replicaSet=rs_thubp` 来访问 docker 中的 MongoDB 集群。

**注**: 集群方式的 MongoDB 也是 `prod` 部署环境下的默认配置。

(*不推荐*) 若希望通过单 Mongo 实例运行 `dev` 部署环境，则无需上述的步骤，只需要在 `application-dev.properties` 文件中指定单 Mongo 实例的访问 URL，并切换至 `dev` 部署环境，即可运行。

**注**: `dev` 环境下的单 Mongo 实例不可采用集群方式启动。

#### 后端部署

后端的正确运行需要先配置 `backend/code/src/main/resources/config/config.properties` 文件，文件的内容可以参考同级的 `config-template.properties` 文件。

(*推荐*) 使用 Docker 部署

直接通过 `docker` 目录下的 docker-compose 配置运行。

(*不推荐*) 使用 Maven 直接运行

在 `code` 路径下运行

```shell
mvn spring-boot:run
```

即可启动。需要事先安装 JDK 8 和 Maven 3

(*不推荐*) 使用 Maven 构建后，直接运行

使用 `mvn package -DskipTests` 打包到 `target` 目录下的 jar 文件中，直接运行该打包文件。

```shell
java -jar thubp-SNAPSHOT-xxx.jar
```

#### 前端部署

前端代码在 `frontend` 目录下，需要进行编译部署

首先需要环境中已安装 node 和 vue-cli。node 的安装方式需要与操作系统等有关。

使用下面的命令安装 vue-cli

```shell
npm install -g vue-cli
```

接下来，需要安装依赖库，在 `frontend` 目录下运行

```shell
npm install
```

完成后，通过

```shell
npm run build
```

进行编译，并将 `dist` 目录下的编译输出复制到 http server 的文件根即可。

示例 nginx 配置:

```nginx
server {
    listen 443 ssl;
    listen [::]:443 ssl;

    server_name thubp.iterator-traits.com;
    ssl_certificate /path/to/certificate;
    ssl_certificate_key /path/to/private_key;

    ssl_session_timeout 5m;
    ssl_ciphers ECDHE-RSA-AES128-GCM-SHA256:ECDHE:ECDH:AES:HIGH:!NULL:!aNULL:!MD5:!ADH:!RC4;
    ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
    ssl_prefer_server_ciphers on;

	location / {
		root /path/to/compilation/results;
		try_files $uri $uri/ /index.html;
		index index.html index.html;
	}

	location ~ ^/api/ {
		proxy_pass http://localhost:9960;
	}
}
```

## 附录

### 目录结构说明

```
/
├── data            # 数据目录，运行时的数据文件会存放在该目录
│   ├── mongo       # 数据库集群数据
│   │   ├── thubp1
│   │   └── thubp2
│   └── thubp       # 后端数据
├── docker          # docker-compose 配置
│   ├── docker-compose.yml
│   └── thubp       # 后端 Dockerfile
├── doc             # 文档目录
├── backend         # 后端代码仓库
├── frontend        # 前端代码仓库
└── Makefile        # 打包用的 makefile
```