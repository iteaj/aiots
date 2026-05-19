# 系统架构速查

> 开发前速查:系统提供了哪些能力、哪些可直接使用、哪些扩展点可以接入。

---

## 1. 系统启动流程

启动入口:`com.iteaj.iboot.boot.IBootApplication` → 通过 `@ImportAutoConfiguration` 导入 `CoreAutoConfiguration`(必须) 和 `IotAutoConfiguration`(Profile=iot时启用)

### 应用就绪回调

实现 `ApplicationReadyListener` 接口并注册为 Spring Bean,在应用启动完成后执行:

```java
@FunctionalInterface
public interface ApplicationReadyListener extends EventListener, Ordered {
    void started(ApplicationContext context);
    default int getOrder() { return Integer.MIN_VALUE + 100000; } // 越小越先执行
}
```

#### 系统已实现的监听器(按执行顺序)

| 顺序 | order 值 | 实现类 | 所属模块 | 启动时做的事 |
|------|---------|--------|---------|------------|
| 1 | `Integer.MIN_VALUE` | `IotCoreApiService` | framework/spi/iot | 获取4个核心Bean引用(`ProductModelService`/`ModelAttrMetaService`/`DataCollectPublishService`/`IModelAttrRealtimeDataService`),使静态API可用 |
| 2 | `Integer.MIN_VALUE + 10000` | `IotRestartListener` | bootstrap/module/iot | 注册IoT事件监听器 → 缓存设备信息 → 启用网关 → 启用产品及设备 → 发布产品启用事件 |
| 3 | `Integer.MIN_VALUE + 100000`(默认) | `LocalLinkageCacheService` | bootstrap/module/iot | 加载已启用的联动场景,注册联动项缓存 |
| 4 | `Integer.MIN_VALUE + 100000`(默认) | `EventSourceCollectService` | bootstrap/module/iot | 启动已启用的事件源,创建定时采集任务 |
| 5 | `Integer.MIN_VALUE + 100000`(默认) | `IotViewAutoConfiguration` | iboot-plugin/iboot-iotview | 扫描并注册所有 `ViewApi` 实现到大屏视图管理器 |

#### 顺序重要性说明

**1. `IotCoreApiService` 必须最先执行(order=MIN_VALUE)**
   - 它负责将4个核心Service Bean赋值给静态变量,之后 `IotCoreApiService` 的所有静态方法才能正常工作
   - 后续所有监听器(如 `IotRestartListener`)在处理设备、发布数据时都依赖此API
   - 如果顺序不对,调用 `IotCoreApiService.publish()` 等方法会抛NPE

**2. `IotRestartListener` 必须第二执行(order=MIN_VALUE+10000)**
   - 它需要先注册 `IotListener` 到 `IotEvenPublisher`,后续事件才能被监听
   - 缓存设备信息 → 启用网关 → 启用产品 是严格的依赖链:产品下的设备需要先有网关才能连接
   - 在启用产品时会发布 `ProductSwitch` 事件,此事件需要已有的监听器才能被处理

**3. 新增监听器的 order 选择原则**
   - 依赖 `IotCoreApiService` 静态API → order ≤ `MIN_VALUE + 10000`
   - 依赖设备缓存/网关已启动 → order ≥ `MIN_VALUE + 10000`
   - 依赖联动缓存已就绪 → order ≥ `MIN_VALUE + 100000`
   - 无特殊依赖 → 使用默认值 `MIN_VALUE + 100000`

### Profile 机制

**方式一:配置文件**

| Profile | 配置方式 | 说明 |
|---------|---------|------|
| `iot` | `spring.profiles.include=iot` | 启用物联网模块 |
| `mcp` | `spring.profiles.include=mcp` | 启用AI-MCP模块 |
| `dev/test/prod` | `spring.profiles.active=dev` | 环境切换 |

**方式二:`@ProfilesInclude` 注解(推荐插件使用)**

框架提供的注解,标注在AutoConfiguration类上,功能等同于 `spring.profiles.include=xx`。
启动时 `FrameworkEnvironmentListener` 会扫描所有插件AutoConfiguration类上的 `@ProfilesInclude`,自动激活对应的profile。

```java
// 在插件的AutoConfiguration上声明
@ProfilesInclude("satoken")  // 等同于 spring.profiles.include=satoken
public class SaTokenAutoConfiguration { ... }
```

**开发新插件时**:在AutoConfiguration类上添加 `@ProfilesInclude("yourprofile")` 即可自动激活,无需手动修改配置文件。

---

## 2. SPI扩展点体系

```
com.iteaj.framework.spi
├── admin/          # 管理端SPI
│   ├── Module           # 模块注册 ← 注册新模块
│   └── event/           # 在线事件
│       └── OnlinePayload     # 在线状态负载
│
├── iot/            # 物联网SPI ← 核心扩展点
│   ├── DeviceProtocolSupplier   # 协议驱动顶层接口
│   ├── ProtocolSupplierManager  # 协议注册管理
│   ├── IotCoreApiService        # IoT核心API(静态工具)
│   ├── DataValueResolverFactory # 数据解析器工厂
│   ├── protocol/       # 协议模型定义
│   ├── listener/       # 事件监听
│   ├── data/           # 业务数据服务
│   ├── view/           # 大屏视图接口
│   └── consts/         # 常量/枚举
│
├── event/          # 事件SPI
│   ├── ApplicationReadyListener  # 应用就绪监听 ← 扩展点
│   ├── EventUtils               # 事件发布工具
│   ├── PayloadEvent             # 通用事件载体
│   ├── AsyncPublisher           # 异步发布者
│   └── FrameworkListener        # 框架监听器
│
├── file/           # 文件SPI
│   ├── UploadService       # 文件上传接口 ← 可替换实现
│   └── LocalUploadService  # 默认本地实现
│
├── excel/          # Excel SPI
│   ├── IExcelService       # Excel字典服务
│   └── ExcelDictHandle     # 字典转换处理
│
├── message/        # 消息SPI
│   ├── MessageService      # 消息服务接口 ← 扩展点
│   ├── MessageManager      # 消息管理器
│   └── MessageType         # 消息类型
│
├── amqp/           # AMQP SPI
│   └── SenderService       # 消息发送接口
│
├── app/            # 移动端SPI
│   └── NativeAuthAction    # 原生认证动作
│
├── gbs/            # 国标GB28181 SPI
│   └── GbsRealtimeTree     # 国标实时树
│
└── open/           # 开放平台SPI
    └── OpenApiUtil         # 开放API工具
```

---

## 3. 核心服务API速查

> 详细用法见各模块速查文档。此处仅列出服务名称和核心方法,便于定位。

### 安全服务 — SecurityService

```java
securityService.isLogin() / getUser() / getLoginId() / hasRole() / hasPermission()
securityService.login() / logout() / getSessionAttr() / setSessionAttr()
```

> 详细API见 [core-module-quick-ref.md](./core-module-quick-ref.md)

### IoT核心API — IotCoreApiService(静态方法)

```java
IotCoreApiService.getDeviceStatus(uid)                    // 获取设备状态
IotCoreApiService.getRealtimeData(uid, signalOrField)      // 获取实时数据
IotCoreApiService.listByProductCode(productCode)           // 产品下设备列表
IotCoreApiService.listByGroupId(groupId)                   // 设备组下设备列表
IotCoreApiService.getProductByCache(productId)             // 获取产品信息
IotCoreApiService.getProductModel(productCode)             // 获取产品模型
IotCoreApiService.publish(dataSupplier, device)            // 发布采集数据
```

> 详细API见 [iot-module-quick-ref.md](./iot-module-quick-ref.md)

### 事件发布 — EventUtils

```java
EventUtils.publish(new PayloadEvent(this, payload));
```

### IoT事件发布 — IotEvenPublisher

```java
IotEvenPublisher.publish(IotEventType.Realtime, realtimePayload);
IotEvenPublisher.publish(IotEventType.CmdInvoke, payload, callback);
```

> 详细事件监听见 [iot-module-quick-ref.md](./iot-module-quick-ref.md#三iot事件监听速查)

### 文件上传 — UploadService

```java
uploadService.upload(inputStream, "文件名.jpg");
uploadService.upload(inputStream, "文件名.jpg", "子路径");
```

### Excel字典 — IExcelService

```java
excelService.toDictName("dict_type", dictValue);   // 字典值→名称
excelService.toDictValue("dict_type", dictName);   // 字典名称→值
```

### 大屏视图 — ViewApiManager

```java
ViewApiManager.initAndRegister(viewApi);
ViewApiManager.fetch(chartParams) / fetch(tableParams) / invoke(funcParams);
```

---

## 4. 模块注册机制

### 注册新模块

在 `AutoConfiguration` 类中注册 `Module` Bean:

```java
@Bean
public Module myModule() {
    // msn: 模块编号(不重复), name: 模块名称, order: 排序, packageName: 包名
    return Module.module("mymod", "我的模块", 100, "mymod");
}
```

**模块编号规则**:msn 用于菜单的 msn 字段,同一模块下的菜单共享此编号。

---

## 5. 插件扩展机制

### 插件接口

```java
public interface Plugin {
    String getPluginName();  // 插件名称
    String getPluginDesc();  // 插件说明
    default void banner() {} // 启动banner
}
```

实现 `Plugin` 接口并注册为 Spring Bean,系统启动时会自动收集并打印已加载插件。

### 插件自动配置扫描

`PluginAutoConfiguration` 会自动扫描 `com.iteaj.iboot.plugin.*.autoconfig` 包下的配置类。

**插件目录结构**:

```
iboot-plugin/iboot-xxx/
└── src/main/java/com/iteaj/iboot/plugin/xxx/
    ├── autoconfig/
    │   └── XxxAutoConfiguration.java  # 自动配置类(被框架扫描)
    ├── XxxPlugin.java                  # 实现Plugin接口
    ├── controller/
    ├── service/
    └── entity/
```

### 协议驱动插件SPI加载

`ProtocolSupplierManager` 支持两种加载方式:

| 方式 | ProtocolImplMode | 加载时机 | 说明 |
|------|-----------------|---------|------|
| **内置** | `Internal` | Spring启动时 | 协议类在项目内,`@Component`注册 |
| **Jar** | `Jar` | 启动时+动态导入 | 外部Jar,使用Java SPI从`protocol/`目录加载 |

Jar协议的SPI配置文件:`META-INF/services/com.iteaj.framework.spi.iot.DeviceProtocolSupplier`

---

## 6. 匿名访问配置

任何模块都可能需要配置匿名访问URL,通过注册 `OrderFilterChainDefinition` Bean实现:

```java
@Bean
@Order(18)  // 数值越小优先级越高
public OrderFilterChainDefinition myAnonFilterChain() {
    return new OrderFilterChainDefinition()
            .addAnon("/mymod/public/**");    // 匿名访问路径
}

@Bean
@Order(9999)  // 认证过滤器放在最后
public OrderFilterChainDefinition authFilterChain() {
    return new OrderFilterChainDefinition()
            .addInclude("/**");               // 其他路径需认证
}
```
**编写原则**: 匿名的url应该放在对应的模块上 

---

## 7. 条件注解(控制Bean加载)

| 注解 | 条件 | 用法 |
|------|------|------|
| `@ConditionalOnCluster` | `framework.cluster=true` | 集群模式下加载 |
| `@ConditionalOnRedis` | Redis可用时 | Redis可用时加载 |
| `@ConditionalOnMissingBean` | 容器中无指定Bean | 替换默认实现 |
| `@Profile("iot")` | `spring.profiles.include=iot` | 仅IoT模块启用时加载 |

---

## 8. 框架工具类

| 工具 | 位置 | 说明 |
|------|------|------|
| `SpringUtils` | `com.iteaj.framework.spring` | Spring上下文工具 |
| `EventUtils` | `com.iteaj.framework.spi.event` | Spring事件发布 |
| `IBootThreadManger` | `com.iteaj.framework.thread` | 线程池管理, `execute(Runnable)` |
| `IotCoreApiService` | `com.iteaj.framework.spi.iot` | IoT核心API(静态方法) |
| `IotEvenPublisher` | `com.iteaj.framework.spi.iot.listener` | IoT事件发布 |
| `ProtocolSupplierManager` | `com.iteaj.framework.spi.iot` | 协议注册管理 |
| `DataValueResolverFactory` | `com.iteaj.framework.spi.iot` | 数据解析器工厂 |
| `ViewApiManager` | `com.iteaj.framework.spi.iot.view` | 大屏视图管理 |
| `SecurityUtil` | `com.iteaj.framework.security` | 安全工具(快捷获取当前用户等) |

---

## 9. 开发速查决策树

```
需要开发新功能?
├── 是CRUD业务?
│   → 继承 BaseController + BaseServiceImpl
│   → 参考 crud-quick-ref.md
│
├── 是设备驱动?
│   → 继承 StaticServerProtocolSupplier 或 StaticClientProtocolSupplier
│   → 参考 driver-dev-flow.md
│
├── 是插件模块?
│   → 实现 Plugin 接口 + 创建 autoconfig 包
│   → 参考 plugin-quick-ref.md
│
├── 需要监听IoT事件?
│   → 实现 RealtimeInfoListener / StatusSwitchListener
│   → 参考 iot-module-quick-ref.md
│
├── 需要获取设备实时数据?
│   → 使用 IotCoreApiService 静态方法
│   → 参考 iot-module-quick-ref.md
│
├── 需要安全认证/权限?
│   → 使用 SecurityService / @CheckPermission
│   → 参考 core-module-quick-ref.md
│
├── 需要开发移动端后端?
│   → AppXxxController + /m/ 前缀
│   → 参考 app-module-quick-ref.md
│
├── 需要配置匿名访问?
│   → 注册 OrderFilterChainDefinition Bean
│   → 参见上方第6节
│
├── 需要自定义数据解析?
│   → 实现 DataValueResolver + DataValueResolverFactory.register()
│
├── 需要扩展大屏视图?
│   → 实现 ViewApi + ViewApiManager.initAndRegister()
│
├── 需要消息通知?
│   → 实现 MessageService 接口
│
├── 需要替换文件上传?
│   → 实现 UploadService + @ConditionalOnMissingBean
│
└── 需要启动时初始化?
    → 实现 ApplicationReadyListener + 注册Bean
```
