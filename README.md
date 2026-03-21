# 海明火锅店点餐系统

## 项目结构

```
code/
├── database/          # 数据库脚本
│   └── init.sql       # 建表 + 初始数据
├── backend/           # Spring Boot 后端 (端口8080)
├── frontend/          # Vue 3 管理后台 (端口3000)
└── miniapp/           # uni-app 微信小程序
```

## 技术栈

- **后端**: Spring Boot 2.7 + MyBatis-Plus + MySQL + JWT
- **管理后台**: Vue 3 + Element Plus + Axios + Vue Router
- **小程序**: uni-app (Vue 语法)

## 快速启动

### 1. 数据库
```sql
-- 创建数据库并导入
CREATE DATABASE hotpot DEFAULT CHARACTER SET utf8mb4;
USE hotpot;
SOURCE database/init.sql;
```

### 2. 后端
```bash
cd backend
mvn spring-boot:run
```
后端运行在 http://localhost:8080

### 3. 管理后台
```bash
cd frontend
npm install
npm run dev
```
管理后台运行在 http://localhost:3000

### 4. 小程序
使用 HBuilderX 打开 `miniapp` 目录，运行到微信开发者工具。

## 默认账号

- **管理后台**: admin / 123456

## API 接口

### 管理端 `/api/admin`
| 接口 | 方法 | 说明 |
|------|------|------|
| /login | POST | 管理员登录 |
| /category/list | GET | 分类列表 |
| /category/save | POST | 新增/编辑分类 |
| /category/delete/{id} | DELETE | 删除分类 |
| /dish/list | GET | 菜品列表(分页) |
| /dish/save | POST | 新增/编辑菜品 |
| /dish/delete/{id} | DELETE | 删除菜品 |
| /dish/status | POST | 上架/下架 |
| /order/list | GET | 订单列表(分页) |
| /order/detail/{id} | GET | 订单详情 |
| /order/status | POST | 更新订单状态 |
| /review/list | GET | 评价列表(分页) |
| /upload | POST | 文件上传 |

### 小程序端 `/api/app`
| 接口 | 方法 | 说明 |
|------|------|------|
| /login | POST | 微信登录(模拟) |
| /category/list | GET | 分类列表 |
| /dish/list | GET | 菜品列表(仅上架) |
| /order/submit | POST | 提交订单 |
| /order/pay | POST | 模拟支付 |
| /order/list | GET | 我的订单(分页) |
| /order/detail/{id} | GET | 订单详情 |
| /review/submit | POST | 提交评价 |
