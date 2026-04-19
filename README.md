<<<<<<< HEAD
# 图图世界 - Neeko Map

基于地理位置的照片分享与社交平台，使用 Vue 3 + Spring Boot 构建。

## 项目简介

图图世界(Neeko Map) 是一个以地理信息系统为核心的图片社交应用，用户可以在地图上发现、分享带有地理位置的照片，与好友互动交流。

## 技术栈

### 后端
- **Spring Boot 2.7.18** - 核心框架
- **MyBatis Plus 3.5.3.1** - ORM框架
- **JWT** - 身份认证
- **MySQL 8.0** - 数据库
- **Redis** - 缓存和会话存储

### 前端
- **Vue 3** - 渐进式JavaScript框架
- **Vite** - 新一代前端构建工具
- **Element Plus** - UI组件库
- **Pinia** - 状态管理
- **Leaflet** - 地图库
- **ECharts** - 数据可视化

## 功能模块

### 核心功能
- **地图浏览** - 在地图上浏览公开照片，标记聚合显示
- **照片上传** - 上传照片并标注地理位置
- **相册管理** - 创建、管理个人相册
- **好友系统** - 添加好友，好友请求管理
- **即时通讯** - 与好友实时聊天
- **用户中心** - 个人资料管理

### 地图特色
- 天地图底图服务
- 照片标记点聚合
- 附近照片搜索
- 位置标签筛选

## 项目结构

```
neeko_map/
├── backend/                    # Spring Boot 后端
│   ├── src/main/java/com/neeko/map/
│   │   ├── controller/         # 控制器层
│   │   ├── service/           # 服务层
│   │   ├── mapper/            # 数据访问层
│   │   ├── entity/            # 实体类
│   │   └── config/            # 配置类
│   └── src/main/resources/
│       ├── application.yml    # 应用配置
│       └── mapper/            # MyBatis XML
├── frontend/                   # Vue 3 前端
│   ├── src/
│   │   ├── api/              # API接口
│   │   ├── views/            # 页面组件
│   │   ├── store/            # 状态管理
│   │   ├── router/           # 路由配置
│   │   └── components/        # 公共组件
│   └── package.json
└── docs/                       # 文档
    └── database.sql          # 数据库脚本
```

## 快速开始

### 环境要求
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Maven 3.8+

### 后端启动

1. 创建数据库并导入数据：
```bash
mysql -u root -p < docs/database.sql
```

2. 修改配置文件 `backend/src/main/resources/application.yml` 中的数据库连接信息

3. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

### 前端启动

```bash
cd frontend
npm install
npm run dev
```

### 访问应用

打开浏览器访问 http://localhost:5173

默认测试账号：
- 用户名: test
- 密码: 123456

## 数据库表结构

| 表名 | 说明 |
|------|------|
| sys_user | 用户表 |
| photo_album | 相册表 |
| photo_image | 照片表 |
| sys_tag | 标签表 |
| friend_relation | 好友关系表 |
| chat_message | 聊天消息表 |
| sys_log | 系统日志表 |

## API 接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/register` - 用户注册
- `POST /api/auth/logout` - 用户登出

### 照片接口
- `POST /api/photo/upload` - 上传照片
- `GET /api/photo/public` - 获取公开照片
- `GET /api/photo/nearby` - 附近照片搜索

### 相册接口
- `GET /api/album/list` - 相册列表
- `POST /api/album/create` - 创建相册
- `PUT /api/album/{id}` - 更新相册

### 好友接口
- `GET /api/friend/list` - 好友列表
- `POST /api/friend/add` - 添加好友
- `POST /api/friend/confirm/{id}` - 确认好友

## 开发说明

### 添加新功能

1. 后端：在 `entity` 包下创建实体类
2. 后端：在 `mapper` 包下创建 Mapper 接口
3. 后端：在 `service` 包下创建 Service 接口和实现
4. 后端：在 `controller` 包下创建 Controller
5. 前端：在 `api` 包下添加 API 方法
6. 前端：在 `views` 包下创建页面组件

### 配置说明

天地图 Token 配置（在 `MapHome.vue` 中设置）
地图瓦片地址：`https://t{s}.tianditu.gov.cn/DataServer`

## 项目亮点

1. **地理信息系统应用** - 完整实现了基于位置的照片管理功能
2. **前后端分离架构** - 现代Web应用架构，便于扩展和维护
3. **实时社交功能** - 好友系统和即时通讯
4. **地图可视化** - 使用 Leaflet + 天地图实现地图功能

## 许可证

MIT License

## 致谢

本项目重构自武汉大学 GIS 竞赛作品「图图世界」
=======
# neeko
map_gis
>>>>>>> d69a3739ed6206f6e98f4e8617abbc68f90e3d51
