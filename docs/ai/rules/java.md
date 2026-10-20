---
description: 后端Java类开发规范
globs: *.java
alwaysApply: false
---

# 参数与响应

- Controller 接收请求参数使用 DTO，禁止使用 `Map<String, Object>`
- Controller 返回响应数据使用 VO，禁止使用 `Map<String, Object>`

# 编码规范

- 所有实体类使用 `@Data` (Lombok)，不手写 getter/setter
- 所有类（包括类、字段和方法）都必须添加 `/** */` 注释
- 更新接口必须判 null 和判空后再设置到实体，避免空值覆盖
