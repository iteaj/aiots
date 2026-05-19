---
name: deploy
description: 项目构建与发布部署代理。负责Maven多模块构建与远程自动部署。
role: 构建部署工程师
---

# 构建部署代理

## 配置方式（二选一）

**方式一：命令行参数**
```bash
-Ddeploy.server.host=<IP> -Ddeploy.server.user=<用户> -Ddeploy.server.password=<密码> -Ddeploy.server.path=<路径>
```

**方式二：`~/.m2/settings.xml`**
```xml
<profile>
  <id>deploy</id>
  <properties>
    <deploy.server.host>IP</deploy.server.host>
    <deploy.server.user>root</deploy.server.user>
    <deploy.server.password>密码</deploy.server.password>
    <deploy.server.path>/opt/app</deploy.server.path>
  </properties>
</profile>
```

## 发布指令

| 场景 | 指令 |
|------|------|
| 全量发布（后端+前端→上传→部署） | `mvn clean verify -P deploy -Ddeploy.server.host=...` |
| 配置后简化发布 | `mvn clean verify -P deploy` |
| 仅发布后端（跳过前端） | `mvn clean verify -P deploy -Dskip.frontend.build=true` |
| 本地全量构建 | `mvn clean package -Dskip.frontend.build=true -Dmaven.test.skip=true` |
| 本地仅构建后端 | `mvn clean package -pl bootstrap -am -Dskip.frontend.build=true -Dmaven.test.skip=true` |

## 服务器管理（deploy.sh）

| 指令 | 说明 |
|------|------|
| `bash deploy.sh start\|stop\|restart` | 启/停/重启后端服务 |
| `bash deploy.sh frontend` | 部署前端（解压到 Nginx） |
