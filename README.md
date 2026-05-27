# 拼豆商城

二次元拼豆手工艺品电商平台

## 技术栈

- **后端**: Spring Boot 2.7 + MyBatis-Plus + MySQL + JWT
- **前端**: Vue 3 + Vite + Element Plus + Vue Router + Pinia

## 项目结构

```
拼豆商城/
├── pindou-backend/          # Spring Boot 后端
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/pindou/
│       │   ├── PindouApplication.java
│       │   ├── config/          # 配置（JWT、跨域、MyBatis）
│       │   ├── controller/      # 控制器
│       │   ├── entity/          # 实体类
│       │   ├── mapper/          # Mapper
│       │   ├── common/          # 通用类
│       │   └── utils/           # 工具类
│       └── resources/
│           ├── application.yml
│           └── pindou_mall.sql  # 数据库初始化
└── pindou-frontend/         # Vue 3 前端
    ├── src/
    │   ├── api/             # API 接口
    │   ├── router/          # 路由
    │   ├── store/           # Pinia 状态管理
    │   ├── views/           # 前台页面
    │   └── views/admin/     # 后台管理页面
    └── vite.config.js
```

## 快速启动

### 1. 数据库

```bash
# 导入 SQL 文件创建数据库和初始数据
mysql -u root -p < pindou-backend/src/main/resources/pindou_mall.sql
```

### 2. 后端

```bash
cd pindou-backend

# 修改 application.yml 中的数据库配置（用户名、密码）
# 编译运行
mvn spring-boot:run
```

后端默认运行在 http://localhost:8080

### 3. 前端

```bash
cd pindou-frontend
npm install --legacy-peer-deps
npm run dev
```

前端默认运行在 http://localhost:5173

## 默认账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | 123456 |
| 普通用户 | user1 | 123456 |
| 普通用户 | user2 | 123456 |

## 功能模块

### 前台（用户端）
- 首页：轮播图、分类导航、热门推荐、新品上线
- 商品浏览：分类筛选、关键词搜索、分页
- 商品详情：图片展示、价格、IP/角色标签、富文本详情、评价
- 购物车：增删改查、全选、结算
- 订单：下单、支付、取消、确认收货、评价
- 个人中心：修改资料、修改密码、收货地址管理

### 后台（管理端）
- 数据概览：订单、用户、商品统计
- 商品管理：增删改查、上下架
- 分类管理：增删改查
- 订单管理：查看、发货
- 用户管理：查看、禁用/启用
- 轮播图管理：增删改查
