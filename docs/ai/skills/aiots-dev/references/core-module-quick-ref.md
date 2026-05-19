# Core模块速查

> 开发Core模块功能时优先查阅本文档。完整Controller清单见 [core-module-reference.md](./core-module-reference.md)。

---

## 一、常用Controller速查

| 功能 | Controller | REST路径 | 权限前缀 |
|------|-----------|---------|---------|
| 登录/注销 | LoginController | `/core` | - |
| 管理员管理 | AdminController | `/core/admin` | `core:admin` |
| 管理员中心 | AdminCenterController | `/core/adminCenter` | - |
| 菜单管理 | MenuController | `/core/menu` | `core:menu` |
| 角色管理 | RoleController | `/core/role` | `core:role` |
| 组织机构 | OrgController | `/core/org` | `core:org` |
| 职位管理 | PostController | `/core/post` | `core:post` |
| 区域管理 | RegionController | `/core/region` | `core:region` |
| 字典类型 | DictTypeController | `/core/dictType` | `core:dictType` |
| 字典数据 | DictDataController | `/core/dictData` | `core:dictData` |
| 系统配置 | ConfigController | `/core/config` | `core:config` |
| 通知管理 | NotifyController | `/core/notify` | `core:notify` |
| 通知用户 | NotifyUserController | `/core/notifyUser` | `core:notifyUser` |
| 在线用户 | OnlineUserController | `/core/onlineUser` | `core:onlineUser` |
| 操作日志 | OperaLogController | `/core/operaLog` | `core:operaLog` |
| 文件上传 | UploadController | `/core/upload` | - |
| 系统统计 | CoreStatisticsController | `/core/statistics` | - |

---

## 二、安全与认证

### SecurityService（注入使用）

```java
@Autowired
private SecurityService securityService;
```

| 方法 | 返回类型 | 说明 |
|------|---------|------|
| `isLogin()` | `boolean` | 是否登录 |
| `getUser()` | `Optional<Entity>` | 当前登录用户 |
| `getLoginId()` | `Optional<Serializable>` | 当前登录用户ID |
| `getRoles()` | `Optional<List<String>>` | 角色列表 |
| `hasRole(role)` | `boolean` | 是否有指定角色 |
| `hasRole(logical, roles...)` | `boolean` | 多角色判断（AND/OR） |
| `hasPermission(permission)` | `boolean` | 是否有指定权限 |
| `hasPermission(logical, permissions...)` | `boolean` | 多权限判断（AND/OR） |
| `getSessionAttr(key)` | `Optional<T>` | 获取session属性 |
| `setSessionAttr(key, value)` | `SecurityService` | 设置session属性 |
| `removeSessionAttr(key)` | `Object` | 删除session属性 |
| `getSessionKeys()` | `Collection<String>` | 获取所有session key |
| `login(token, request, response)` | void | 登录 |
| `logout(sessionId)` | void | 按sessionId注销 |
| `logout(request, response)` | void | 注销当前用户 |
| `isCurrentUser(sessionId)` | `boolean` | 是否当前用户 |
| `getRequest()` | `HttpServletRequest` | 获取当前请求 |
| `getResponse()` | `HttpServletResponse` | 获取当前响应 |

### SecurityUtil（静态工具，更便捷）

```java
import com.iteaj.framework.security.SecurityUtil;

SecurityUtil.login(token, request, response);  // 登录
SecurityUtil.logout(request, response);         // 注销
SecurityUtil.isLogin();                          // 是否登录
SecurityUtil.getUser();                          // 当前用户
```

### SecurityToken（登录凭证）

```java
// 构造登录令牌
SecurityToken token = new SecurityToken(username, password, rememberMe);
```

---

## 三、权限注解

### @CheckPermission

```java
// 单个权限
@CheckPermission("iot:device:view")
@GetMapping("/list")
public Result list() { ... }

// 多权限OR（如 saveOrUpdate 合并 add+edit）
@CheckPermission(value = {"iot:device:add", "iot:device:edit"}, logical = Logical.OR)
@PostMapping("/saveOrUpdate")
public Result saveOrUpdate(@RequestBody Device device) { ... }
```

### 权限编码格式

```
{模块}:{资源}:{操作}
```

| 操作 | 编码后缀 | saveOrUpdate合并编码 |
|------|---------|---------------------|
| 查看 | `:view` | — |
| 新增 | `:add` | `value={"x:add","x:edit"}, logical=Logical.OR` |
| 编辑 | `:edit` | 同上 |
| 删除 | `:del` | — |
| 调用 | `:invoke` | — |
| 调试 | `:debug` | — |

### @Logger（操作日志）

```java
@Logger("操作描述")                    // 普通操作
@Logger(value = "系统登录", type = LoggerType.Login)   // 登录操作
@Logger(value = "系统注销", type = LoggerType.Logout)  // 注销操作
```

> 每个接口方法必须加 `@Logger` 注解。

---

## 四、通用服务

### IExcelService（字典转换）

```java
@Autowired
private IExcelService excelService;

excelService.toDictName("dict_type", dictValue);   // 字典值→名称
excelService.toDictValue("dict_type", dictName);   // 字典名称→值
excelService.toOrgName(orgId);                      // 机构ID→名称
excelService.toOrgValue(orgName);                   // 机构名称→ID
```

### UploadService（文件上传）

```java
@Autowired
private UploadService uploadService;

uploadService.upload(inputStream, "文件名.jpg");
uploadService.upload(inputStream, "文件名.jpg", "子路径");
uploadService.upload(inputStream, "原始文件名", "自定义文件名", "子路径");
```

---

## 五、高频开发场景

### 1. 用户登录/注销

```java
// 登录
@PostMapping("login")
public Result login(@RequestBody SecurityToken token, HttpServletRequest request, HttpServletResponse response) {
    try {
        SecurityUtil.login(token, request, response);
        return success("登录成功");
    } catch (SecurityException e) {
        return fail(e.getMessage());
    }
}

// 注销
@PostMapping("logout")
public Result logout(HttpServletRequest request, HttpServletResponse response) {
    SecurityUtil.logout(request, response);
    return success("注销成功");
}
```

### 2. 获取当前登录用户信息

```java
// 注入方式
Entity user = securityService.getUser().orElseThrow(() -> new ServiceException("未登录"));
Long userId = (Long) securityService.getLoginId().orElse(null);

// 静态方式（Controller中便捷使用）
Entity user = SecurityUtil.getUser().orElse(null);
```

### 3. 权限校验

```java
// 注解方式（推荐，Controller方法上）
@CheckPermission("iot:device:view")

// 编程方式（业务逻辑中）
if (!securityService.hasPermission("iot:device:add")) {
    throw new ServiceException("无操作权限");
}
```

### 4. 字典数据使用

```java
// 值→名称（展示时）
String typeName = excelService.toDictName("device_type", device.getType());

// 名称→值（保存时）
String typeValue = excelService.toDictValue("device_type", "温湿度传感器");
```

---

## 六、常见问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| 401 Unauthorized | 未登录或Token过期 | 重新登录获取新Token |
| 403 Forbidden | 没有访问权限 | 检查权限配置或联系管理员 |
| 权限注解无效 | 注解位置错误 | 确保在方法上而非类上 |
| saveOrUpdate权限 | add+edit需合并 | 使用 `logical=Logical.OR` |
| SecurityService注入失败 | 未在登录上下文中 | 确保请求经过认证过滤器 |

> 权限编码详细规范见 [permission-spec.md](./permission-spec.md)
