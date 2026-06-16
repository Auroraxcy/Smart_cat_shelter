# 智能猫舍管理系统

一个基于Spring Boot + Vue 3的智能猫舍管理系统，支持三种角色：管理员、饲养员、普通用户。

## 技术栈

### 后端
- Spring Boot 2.7.18
- Spring Security + JWT
- Spring Data JPA
- MySQL 8.0
- Lombok

### 前端
- Vue 3
- Vite 5
- Element Plus
- Pinia
- Vue Router
- Axios

## 功能模块

### 1. 用户认证模块
- 用户登录
- 用户注册
- 获取当前用户信息
- 用户登出

### 2. 猫咪管理模块
- 猫咪列表（按角色过滤）
- 添加猫咪（管理员/饲养员）
- 编辑猫咪（管理员/饲养员）
- 删除猫咪（仅管理员）

### 3. 喂食记录模块
- 喂食记录列表
- 添加喂食记录（仅饲养员）
- 今日喂食统计
- 删除记录（仅管理员）

### 4. 健康提醒模块
- 提醒列表
- 添加提醒（管理员/饲养员）
- 标记完成（仅饲养员）
- 删除提醒（仅管理员）

### 5. 环境监控模块
- 最新环境数据
- 历史数据查询
- 录入环境数据（仅饲养员）

### 6. 数据看板模块
- 猫咪总数统计
- 待办提醒数量
- 今日喂食总量

## 角色权限

| 功能          | 管理员 | 饲养员 | 普通用户 |
| ------------- | ------ | ------ | -------- |
| 查看猫咪      | ✅      | ✅      | ✅        |
| 添加/编辑猫咪 | ✅      | ✅      | ❌        |
| 删除猫咪      | ✅      | ❌      | ❌        |
| 查看喂食记录  | ✅      | ✅      | ✅        |
| 添加喂食记录  | ❌      | ✅      | ❌        |
| 删除喂食记录  | ✅      | ❌      | ❌        |
| 查看健康提醒  | ✅      | ✅      | ✅        |
| 添加健康提醒  | ✅      | ✅      | ❌        |
| 标记提醒完成  | ❌      | ✅      | ❌        |
| 删除健康提醒  | ✅      | ❌      | ❌        |
| 查看环境数据  | ✅      | ✅      | ✅        |
| 录入环境数据  | ❌      | ✅      | ❌        |
| 查看数据看板  | ✅      | ✅      | ✅        |

## 快速开始

### 1. 数据库配置

执行SQL脚本初始化数据库：

```bash
mysql -u root -p < database/init.sql
```

或手动执行 `database/init.sql` 文件中的SQL语句。

### 2. 后端启动

```bash
# 进入项目目录
cd Smart_cat_shelter

# 修改数据库配置（如需要）
# 编辑 src/main/resources/application.properties

# 编译并运行
mvn clean install
mvn spring-boot:run
```

后端服务将在 http://localhost:8088 启动

### 3. 前端启动

```bash
# 进入前端目录
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务将在 http://localhost:5173 启动

### 4. 默认账号

- 管理员: admin / admin123
- 饲养员: keeper / keeper123

## 项目结构

```
Smart_cat_shelter/
├── src/                          # 后端代码
│   └── main/
│       ├── java/com/doctor/
│       │   ├── config/           # 配置类
│       │   ├── controller/       # 控制器
│       │   ├── dto/              # 数据传输对象
│       │   ├── entity/           # 实体类
│       │   ├── enums/            # 枚举
│       │   ├── repository/       # 数据访问层
│       │   ├── service/          # 业务逻辑层
│       │   └── util/             # 工具类
│       └── resources/
│           └── application.properties
├── frontend/                     # 前端代码
│   ├── src/
│   │   ├── api/                  # API接口
│   │   ├── layouts/              # 布局组件
│   │   ├── router/               # 路由配置
│   │   ├── stores/               # 状态管理
│   │   ├── utils/                # 工具函数
│   │   └── views/                # 页面组件
│   ├── package.json
│   └── vite.config.js
├── database/                     # 数据库脚本
│   └── init.sql
└── pom.xml
```

## API接口

### 认证接口
- POST `/api/auth/login` - 用户登录
- POST `/api/auth/register` - 用户注册
- GET `/api/auth/current` - 获取当前用户
- POST `/api/auth/logout` - 用户登出

### 猫咪管理
- GET `/api/cats` - 获取猫咪列表
- GET `/api/cats/{id}` - 获取猫咪详情
- POST `/api/cats` - 添加猫咪
- PUT `/api/cats/{id}` - 更新猫咪
- DELETE `/api/cats/{id}` - 删除猫咪

### 喂食记录
- GET `/api/feeding-records` - 获取喂食记录列表
- GET `/api/feeding-records/today-total` - 今日喂食总量
- POST `/api/feeding-records` - 添加喂食记录
- DELETE `/api/feeding-records/{id}` - 删除喂食记录

### 健康提醒
- GET `/api/reminders` - 获取提醒列表
- GET `/api/reminders/pending` - 获取待办提醒
- POST `/api/reminders` - 添加提醒
- PUT `/api/reminders/{id}/complete` - 标记完成
- DELETE `/api/reminders/{id}` - 删除提醒

### 环境监控
- GET `/api/environment/latest` - 获取最新环境数据
- GET `/api/environment/history` - 获取历史数据
- POST `/api/environment` - 录入环境数据

### 数据看板
- GET `/api/dashboard/stats` - 获取看板统计数据

## 注意事项

1. 确保MySQL服务已启动
2. 修改 `application.properties` 中的数据库配置
3. JWT密钥可以在配置文件中修改
4. 前端代理配置在 `vite.config.js` 中

## 开发说明

- 后端端口: 8088
- 前端端口: 5173
- 数据库: smart_cat_shelter
- JWT有效期: 24小时

## 许可证

MIT License