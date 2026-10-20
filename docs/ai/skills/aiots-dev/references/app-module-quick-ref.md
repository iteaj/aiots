# App模块(移动端后端)速查

---

## 一、模块概述

- **模块名称**:App模块(移动端后端)
- **REST前缀**:`/m`
- **接口所在包**:各业务模块的 `app/controller/` 或插件内 `app/controller/`
- **核心职责**:为移动端APP提供专用接口,接口精简、数据轻量

### 与管理端接口的区别

| 对比项 | 管理端接口 | 移动端接口 |
|--------|-----------|-----------|
| REST前缀 | `/模块/资源`(如 `/iot/device`) | `/m/资源`(如 `/m/device`) |
| Controller | `XxxController` | `AppXxxController` |
| 接口数量 | 6个(view/list/edit/detail/saveOrUpdate/del) | 4个(page/list/edit/detail) |
| 数据量 | 完整数据 | 精简数据 |

---

## 二、移动端接口规范

### 标准接口(4件套)

| 操作 | 路径 | Method | 参数 | 返回类型 |
|------|------|--------|------|---------|
| 分页查询 | `/page` | GET | Page + EntityDto | `Result<IPage<Entity>>` |
| 列表查询 | `/list` | GET | Entity | `Result<List<Entity>>` |
| 编辑记录 | `/edit` | GET | `id` | `Result<Entity>` |
| 详情(含关联) | `/detail` | GET | `id` | `Result<EntityDto>` |

> 移动端接口**没有** `saveOrUpdate` 和 `del`,修改和删除通过管理端完成。

### Controller模板

```java
@RestController
@RequestMapping("/m/资源名")
public class AppXxxController extends BaseController {

    private final IXxxService xxxService;

    public AppXxxController(IXxxService xxxService) {
        this.xxxService = xxxService;
    }

    @GetMapping("page")
    public Result<IPage<Xxx>> page(Page<Xxx> page, XxxDto entity) {
        return success(xxxService.page(page, entity));
    }

    @GetMapping("list")
    public Result<List<Xxx>> list(Xxx entity) {
        return success(xxxService.list(entity));
    }

    @GetMapping("edit")
    public Result<Xxx> edit(Long id) {
        return success(xxxService.getById(id));
    }

    @GetMapping("detail")
    public Result<XxxDto> detail(Long id) {
        return success(xxxService.detailById(id));
    }
}
```

---

## 三、代码生成位置

| 场景 | 位置 |
|------|------|
| 主项目IoT模块 | `bootstrap/module/iot/app/controller/AppXxxController.java` |
| 主项目Core模块 | `bootstrap/module/core/app/controller/AppXxxController.java` |
| 插件内 | `iboot-plugin/iboot-xxx/app/controller/AppXxxController.java` |

---

## 四、认证与安全

### 移动端登录流程

```
1. APP发起登录请求 → POST /core/login {username, password}
2. 服务端返回Token → 存储在Cookie/LocalStorage
3. 后续请求携带Token → Header: satoken
4. Token过期 → 重新登录
```

### 移动端匿名访问配置

如需为移动端接口配置匿名访问,在AutoConfiguration中注册:

```java
@Bean
@Order(18)
public OrderFilterChainDefinition appAnonFilter() {
    return new OrderFilterChainDefinition()
            .addAnon("/m/public/**")
            .addAnon("/m/device/status/**");
}
```

---

## 五、高频开发场景

### 1. 新增移动端设备查询接口

```java
@RestController
@RequestMapping("/m/device")
public class AppDeviceController extends BaseController {

    private final IDeviceService deviceService;
    private final RealtimeDataService realtimeDataService;

    public AppDeviceController(IDeviceService deviceService, RealtimeDataService realtimeDataService) {
        this.deviceService = deviceService;
        this.realtimeDataService = realtimeDataService;
    }

    /** 设备列表(分页) */
    @GetMapping("page")
    public Result<IPage<Device>> page(Page<Device> page, DeviceDto entity) {
        return success(deviceService.page(page, entity));
    }

    /** 设备详情(含实时状态) */
    @GetMapping("detail")
    public Result<DeviceDto> detail(Long id) {
        return success(deviceService.detailById(id));
    }

    /** 设备实时状态(高性能,内存缓存) */
    @GetMapping("status")
    public Result<RealtimeStatus> status(Long id) {
        return success(deviceService.getDeviceStatusFromCache(id));
    }
}
```

### 2. 调用IoT核心API

```java
// 移动端后端也可以使用IotCoreApiService静态方法
RealtimeDeviceStatus status = IotCoreApiService.getDeviceStatus(uid);
ModelAttrRealtimeData data = IotCoreApiService.getRealtimeData(uid, "temperature");
```

### 3. 为移动端定制精简数据

```java
// 移动端接口返回精简VO,不要直接返回管理端的Entity/Dto
@GetMapping("list")
public Result<List<DeviceSimpleVo>> list(String productCode) {
    // 查询设备列表
    List<DeviceDto> devices = deviceService.listByProductCode(productCode).getData();
    // 转为精简VO
    List<DeviceSimpleVo> result = devices.stream()
            .map(DeviceSimpleVo::new)
            .collect(Collectors.toList());
    return success(result);
}
```

---

## 六、常见问题

| 问题 | 原因 | 解决方案 |
|------|------|---------|
| 接口返回数据过多 | 使用了管理端DTO | 创建移动端专用精简VO |
| Token失效 | 移动端Token管理不当 | 确保每次请求携带Token |
| 跨域问题 | APP与后端不同域 | 后端配置CORS或使用代理 |
| 接口缺少移动端版本 | 只写了管理端Controller | 按模板新增AppXxxController |

> 移动端前端开发详细流程见 [app技能](../../../app/SKILL.md)
> 完整接口清单见 [app-module-reference.md](app-module-reference.md)
