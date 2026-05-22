---
name: deploy
description: 项目构建与发布部署。负责Maven多模块、前端vue3构建与远程自动部署。
---

# 构建部署代理

## 发布执行流程

当收到"发布项目"指令时，严格按以下流程执行：

### 第一步：确认目标（先问清楚再动手）
1. **确认环境** — 发到哪个环境？测试/预发布/**生产**（默认）
2. **确认范围** — **全量发布**（默认）/ 仅发布前端 / 仅发布后端

### 第二步：检查就绪（环境预检）

> **条件不符处理原则**：任何预检条件不满足时，必须向用户说明具体不符项及可选的替代方案，由用户决定**取消发布**还是**采用替代方案**继续，禁止自行跳过或擅自变更方案。

> **环境缓存优化**：先读取 `docs/ai/agents/deploy/DEPLOY_ENV.md` 查看缓存的运行环境状态，**仅当用户确认环境发生变更时**才重新执行实际检测。未变更则跳过重复检测，直接使用缓存结果。

1. **JDK 版本** — 读取 `DEPLOY_ENV.md` 中缓存的 JDK 状态，如不匹配按条件不符处理原则执行
2. **构建工具** — 读取 `DEPLOY_ENV.md` 中缓存的 mvnw 状态
3. **Node.js 环境**（需要前端构建时）— 读取 `DEPLOY_ENV.md` 中缓存的 Node/npm 版本
4. **settings.xml 配置** — 检查 `~/.m2/settings.xml` 中 deploy profile 配置是否完整如果配置缺失，**提示用户补充**，不代替用户填写。
5. **代码状态** — 检查是否有未提交的变更，询问用户是否先提交

### 第三步：列出步骤（透明实施计划）
向用户完整说明将要执行的步骤，例如：
```
1. git add + commit（如有未提交变更）
2. mvnw clean verify -P deploy -pl bootstrap -am -Dskip.frontend.build=true -Dmaven.test.skip=true（仅后端构建 + SCP上传 + SSH远程部署）
   - 只编译 bootstrap 及其依赖模块，不编译整个项目
   - 跳过前端构建
   - 跳过测试
   - SCP 上传运行包到后端服务器
     - bootstrap.jar → 后端服务器
     - deploy.sh → 后端服务器
   - SSH 执行远程部署
     - 重启后端服务
```

### 第四步：分步执行
1. 提交代码（如用户同意）
2. 执行构建发布命令
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

## 发布指令

> **原则**：所有发布命令统一使用 `-pl bootstrap -am`（只编译 bootstrap 及其依赖）和 `-Dmaven.test.skip=true`（跳过测试），避免全量编译耗时。

| 场景 | 指令 |
|------|------|
| 全量发布（后端+前端→上传→部署） | `mvn clean verify -P deploy -pl bootstrap -am -Dmaven.test.skip=true` |
| 仅发布前端（构建→上传→解压，不重启后端） | `mvn clean verify -P deploy -pl bootstrap -am -Dmaven.test.skip=true -Dskip.backend.deploy=true` |
| 仅发布后端（跳过前端构建和上传） | `mvn clean verify -P deploy -pl bootstrap -am -Dmaven.test.skip=true -Dskip.frontend.build=true` |
| 本地全量构建 | `mvn clean package -Dskip.frontend.build=true -Dmaven.test.skip=true` |
| 本地仅构建后端 | `mvn clean package -pl bootstrap -am -Dskip.frontend.build=true -Dmaven.test.skip=true` |

## 服务器管理（deploy.sh / deploy.ps1）

| 指令 | 说明 |
|------|------|
| `bash deploy.sh start\|stop\|restart` | 启/停/重启后端服务（Linux bash） |
| `bash deploy.sh frontend` | 部署前端（解压到 Nginx，Linux bash） |
| `.\deploy.ps1 start\|stop\|restart` | 启/停/重启后端服务（Windows / PowerShell Core） |
| `.\deploy.ps1 frontend` | 部署前端（解压到 Nginx，Windows / PowerShell Core） |
