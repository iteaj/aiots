# 权限体系规范

## 权限框架

项目使用 SaToken 作为认证授权框架，提供以下核心功能：
- 用户认证（登录、注销、刷新Token）
- 角色权限控制
- 接口权限校验
- 会话管理

## 权限注解使用

在 Controller 方法上使用 `@CheckPermission` 注解进行权限控制：

```java
@CheckPermission("core:device:view")
@GetMapping("/list")
public Result list() {
    // 需要 core:device:view 权限才能访问
}
```
## 权限编码规范

权限编码采用 `模块:功能:操作` 的三级结构：

| 模块 | 功能 | 操作 | 完整编码 |
|------|------|------|----------|
| device | view | query | device:view:query |
| device | control | command | device:control:command |
| product | manage | add | product:manage:add |

## 权限管理流程

1. 在系统管理模块中创建菜单和权限点
2. 将权限点分配给角色
3. 将角色分配给用户
4. 用户登录后获得权限信息
5. 访问受保护接口时自动校验权限

## 在新模块中配置匿名访问

创建新模块时，需要在模块的 AutoConfiguration 类中配置匿名访问路径：

```java
@Bean
@Order(value = 20) // 优先级，数字越小优先级越高
public OrderFilterChainDefinition appFilterChainDefinition() {
    return new OrderFilterChainDefinition()
            .addAnon("/app/public/**", "/app/login", "/app/register");
}
```

## 权限验证流程

1. **请求进入**：用户发起 HTTP 请求
2. **权限拦截**：SaToken 拦截器检查请求
3. **Token验证**：验证用户登录状态
4. **权限校验**：检查用户是否拥有访问权限
5. **访问控制**：
   - 匿名路径：直接放行
   - 需认证路径：验证登录状态
   - 需权限路径：验证权限编码

## 常见权限问题及解决方案

| 问题 | 原因 | 解决方案 |
|------|------|----------|
| 401 Unauthorized | 未登录或Token过期 | 重新登录获取新Token |
| 403 Forbidden | 没有访问权限 | 检查权限配置或联系管理员分配权限 |
| 匿名路径不生效 | 配置顺序问题 | 检查 @Order 注解的优先级 |
| 权限注解无效 | 注解位置错误 | 确保注解在方法上而不是类上 |