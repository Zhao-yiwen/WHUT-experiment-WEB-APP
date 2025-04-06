# 用户管理系统

这是一个基于Vue+ElementUI和SpringBoot+MyBatis的前后端分离用户管理系统。

## 功能特性

- 用户注册（包括头像上传功能）
- 用户登录/注销
- 用户资料查看与修改
- 密码修改
- 日历展示

## 技术栈

### 前端
- Vue.js 2.x
- ElementUI
- Vuex
- Vue Router
- Axios

### 后端
- Spring Boot 2.5.4
- Spring Security
- MyBatis
- JWT认证
- MySQL数据库

## 如何运行
maven可自己下
### 前端

```bash
cd frontend
npm install
npm run serve
```

### 后端

1. 创建MySQL数据库:

```sql
CREATE DATABASE user_management DEFAULT CHARACTER SET utf8mb4;
```

2. 配置数据库连接信息:

编辑 `backend/src/main/resources/application.properties` 文件中的数据库配置。

3. 运行应用:

```bash
cd backend
mvn spring-boot:run
```

## API文档

### 认证相关

- `POST /api/auth/register`: 用户注册
- `POST /api/auth/login`: 用户登录
- `POST /api/auth/logout`: 用户注销
- `PUT /api/auth/password`: 修改密码

### 用户相关

- `GET /api/users/profile`: 获取当前用户资料
- `PUT /api/users/profile`: 更新当前用户资料

## 项目结构

### 前端

```
frontend/
  ├── public/              # 静态资源
  ├── src/                 # 源代码
  │   ├── api/             # API请求
  │   ├── assets/          # 资源文件
  │   ├── components/      # 通用组件
  │   ├── router/          # 路由配置
  │   ├── store/           # Vuex状态管理
  │   ├── views/           # 页面组件
  │   ├── App.vue          # 根组件
  │   └── main.js          # 入口文件
  ├── .gitignore           # Git忽略文件
  ├── babel.config.js      # Babel配置
  ├── package.json         # 项目依赖
  └── README.md            # 项目说明
```

### 后端

```
backend/
  ├── src/                    # 源代码
  │   ├── main/               # 主要代码
  │   │   ├── java/           # Java代码
  │   │   │   └── com/
  │   │   │       └── example/
  │   │   │           └── demo/
  │   │   │               ├── config/         # 配置类
  │   │   │               ├── controller/     # 控制器
  │   │   │               ├── entity/         # 实体类
  │   │   │               ├── mapper/         # MyBatis映射接口
  │   │   │               ├── security/       # 安全相关类
  │   │   │               ├── service/        # 服务类
  │   │   │               └── DemoApplication.java  # 应用入口
  │   │   └── resources/      # 资源文件
  │   │       ├── mapper/     # MyBatis XML映射文件
  │   │       ├── static/     # 静态资源
  │   │       ├── templates/  # 模板文件
  │   │       ├── application.properties  # 应用配置
  │   │       └── schema.sql  # 数据库模式文件
  │   └── test/               # 测试代码
  ├── .gitignore              # Git忽略文件
  ├── pom.xml                 # Maven配置
  └── README.md               # 项目说明
```

## 许可证

MIT # WHUT-experiment-WEB-APPS
# WHUT-experiment-WEB-APPS
