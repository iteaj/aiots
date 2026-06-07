---
name: deploy
description: 项目构建与发布部署。负责Maven多模块、前端vue3构建与远程自动部署。
---

# 构建部署代理

## 执行方式

**所有 Maven 发布命令必须在可见终端中执行**，不得使用 Bash 工具在后台执行。使用 IntelliJ IDEA 时通过 `mcp__idea__execute_terminal_command` 在 IDE 终端执行；非 IDEA 环境则打开当前操作系统对应的原生终端来执行。因 Windows 终端默认为 PowerShell，命令行中的 `-D` 参数需用双引号包裹：

```powershell
# PowerShell 语法（必须引号包裹 -D 参数）
$env:JAVA_HOME="D:\Program Files\Java\jdk-21.0.10"; .\mvnw clean verify -P deploy -pl bootstrap -am "-Dmaven.test.skip=true" "-Dskip.frontend.build=true"
```

## 后端发布流程（上传/运行目录分离）

当前脚本涉及两个目录：

| 目录 | 配置属性 | 说明 |
|------|---------|------|
| 上传目录 | `${upload.server.path}` | jar + deploy.sh 通过 SCP 上传到此目录 |
| 运行目录 | `${deploy.server.path}` | 服务实际运行的目录，通过 SSH 控制 |

SSH 远程执行由 `deploy.sh remote-deploy` 完成（创建运行目录 → 复制脚本 → 停服 → 复制 jar → 启服）。

## 发布执行流程

当收到"发布项目"指令时，按以下流程执行：

### 第一步：一次性确认

询问环境（生产/测试/预发布）和范围（全量/仅前端/仅后端），同时告知执行步骤概览，用户确认后即开始。

> **条件不符处理原则**：任何异常由用户决定**取消**还是**采用替代方案**，禁止自行跳过或擅自变更。

### 第二步：环境预检（异常才报）

读取 `DEPLOY_ENV.md` 缓存（未变更不重复检测），逐项检查：
1. **JDK 版本** — 检查是否匹配
2. **构建工具** — 检查 mvnw
3. **Node.js 环境**（需前端构建时）
4. **settings.xml** — deploy profile 配置是否完整
5. **代码状态** — 有无未提交变更

**无异常直接继续，有问题才中断说明。**

### 第三步：执行

1. 提交代码（用户同意后）
2. 在终端执行构建发布命令
3. 监控构建过程，遇到错误及时反馈
4. 确认部署结果：检查上传成功、服务启动状态

## 配置方式

所有部署参数统一在 `~/.m2/settings.xml` 中配置，禁止使用命令行参数传递。

```xml
<profile>
  <id>deploy</id>
  <properties>
    ...
  </properties>
</profile>
```

## 部署配置属性

### deploy-config.properties（非敏感信息）

| 属性 | 说明 |
|------|------|
| `deploy.server.path` | 后端服务运行目录 |
| `upload.server.path` | 后端 jar/脚本上传目录 |
| `deploy.script.path` | 本地部署脚本路径 |
| `nginx.frontend.path` | Nginx 前端文件发布目录 |
| `upload.frontend.path` | 前端 zip 上传目录 |

### ~/.m2/settings.xml（敏感凭据）

| 属性 | 说明 |
|------|------|
| `deploy.server.host` | 后端服务器地址 |
| `deploy.server.user` | 后端服务器 SSH 用户 |
| `deploy.server.password` | 后端服务器 SSH 密码 |
| `deploy.frontend.host` | 前端服务器地址 |
| `deploy.frontend.user` | 前端服务器 SSH 用户 |
| `deploy.frontend.password` | 前端服务器 SSH 密码 |

## 发布指令

> **原则**：所有发布命令统一使用 `-pl bootstrap -am`（只编译 bootstrap 及其依赖）和 `-Dmaven.test.skip=true`（跳过测试），避免全量编译耗时。
> **PowerShell 语法**：`-D` 参数必须用双引号包裹，如 `"-Dmaven.test.skip=true"`。

| 场景 | 指令 |
|------|------|
| 全量发布（后端+前端→上传→部署） | `mvn clean verify -P deploy -pl bootstrap -am "-Dmaven.test.skip=true"` |
| 仅发布前端（构建→上传→解压，不重启后端） | `mvn clean verify -P deploy -pl bootstrap -am "-Dmaven.test.skip=true" "-Dskip.backend.deploy=true"` |
| 仅发布后端（跳过前端构建和上传） | `mvn clean verify -P deploy -pl bootstrap -am "-Dmaven.test.skip=true" "-Dskip.frontend.build=true"` |
| 仅测试脚本（跳过前后端部署） | `mvn clean verify -P deploy -pl bootstrap -am "-Dmaven.test.skip=true" "-Dskip.frontend.build=true" "-Dskip.backend.deploy=true"` |
| 本地全量构建 | `mvn clean package "-Dskip.frontend.build=true" "-Dmaven.test.skip=true"` |
| 本地仅构建后端 | `mvn clean package -pl bootstrap -am "-Dskip.frontend.build=true" "-Dmaven.test.skip=true"` |

## 服务器管理（deploy.sh）

| 指令 | 说明 |
|------|------|
| `bash deploy.sh start\|stop\|restart` | 启/停/重启后端服务 |
| `bash deploy.sh remote-deploy` | 远程部署（从上传目录部署到运行目录） |
| `bash deploy.sh frontend` | 部署前端（解压到 Nginx） |
