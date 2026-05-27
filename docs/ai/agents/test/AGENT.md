---
name: test
description: 自动化测试流程代理。接收变更内容，自动执行测试、发现问题、修复问题、重验的闭环流程。
---

# 自动化测试代理

## 核心流程

```
接收变更 → 分析影响范围 → 编译构建 → 分端测试 → 发现问题 → 修复 → 重验 → 报告
```

## 第一步：分析变更影响范围

| 变更类型 | 端 | 生效方式 |
|---------|----|---------|
| `*.java` | 后端 | 需重启 |
| `admin-ui/` | 管理前端 | HMR |
| `app/` | 移动端 H5 | HMR |
| 多端同时 | 跨端 | 各端分别验证 |

## 第二步：编译构建

```bash
mcp__idea__build_project                           # 后端变更时编译
# 前端由框架自动 HMR，无需手动构建
```

## 第三步：启动/重启服务

| 服务 | 端口 | 运行配置名 | 检查方法 |
|------|------|-----------|---------|
| 管理前端 | 5173 | admin | `curl localhost:5173` |
| 移动端 H5 | 9900 | app | `curl localhost:9900` |
| 后端 | 8085 | IBootApplication | `xdebug_get_debugger_status()` |

### 前端（管理端 & 移动端 H5）

> 已运行跳过，重复运行会启动新实例而非重启。

```bash
curl -s -o /dev/null -w "%{http_code}" http://localhost:{PORT} 2>/dev/null || echo "未启动"
mcp__idea__execute_run_configuration(configurationName="{admin|app}", waitForExit=false)
```

### 后端

```bash
mcp__idea__xdebug_get_debugger_status()            # 检测是否运行中
mcp__idea__build_project                            # 编译
mcp__idea__xdebug_control_session(action=STOP)      # 停止
mcp__idea__xdebug_start_debugger_session(configurationName="IBootApplication")  # Debug 启动
```

纯前端变更跳过后端重启。

## 第四步：执行测试

### 自动化规范

1. **判断页面** — `eval "window.location.href"`，不用截图
2. **获取元素** — `snapshot` 输出直接看终端，不生成文件
3. **操作元素** — 通过 snapshot ref 编号（`fill`/`click` 等）
4. **测试范围** — 测试增删改查，不只看加载
5. **清理** — 测试数据测试完删除

### 前置条件

```bash
playwright-cli --version || npm install -g @playwright/cli@latest

# 检测浏览器，优先级 Edge > Chrome > Chromium
test -f "/c/Program Files (x86)/Microsoft/Edge/Application/msedge.exe" && echo "edge" \
  || test -f "/c/Program Files/Google/Chrome/Application/chrome.exe" && echo "chrome" \
  || echo "chromium"
```

浏览器参数：Edge → `--browser=msedge`，Chrome → `--browser=chrome`，均不可用 → 安装后使用 Chromium。

### 管理端测试

```bash
# 1. 打开浏览器
playwright-cli open http://localhost:5173/#/login --headed --browser=msedge

# 2. 判断登录状态
playwright-cli eval "window.location.href"
# 已跳到首页 → 已登录；仍是登录页 → 执行登录

# 3. 登录
playwright-cli snapshot
playwright-cli fill e3 "admin"
playwright-cli fill e5 "admin123456"
playwright-cli click e7
playwright-cli eval "window.location.href"          # 确认跳转

# 4. 保存登录态
playwright-cli state-save admin-auth.json

# 5. 导航到目标页面
playwright-cli goto http://localhost:5173/#/{模块}/{功能}
playwright-cli eval "document.body.innerText.includes('页面标题')"

# 6. CRUD
playwright-cli fill e10 "关键词"                      # 查：填充搜索
playwright-cli click e15; playwright-cli requests     # 查看请求参数
playwright-cli click e20                              # 增：新增
playwright-cli snapshot; playwright-cli fill e25 "test"; playwright-cli click e30
playwright-cli click e35                              # 改：编辑
playwright-cli fill e40 "修改后"; playwright-cli click e45
playwright-cli click e50; playwright-cli click e55    # 删：删除并确认
```

### 移动端 H5 测试

```bash
# 1. 直接打开目标页面（未登录自动跳登录页）
playwright-cli open http://localhost:9900/#/pages/{路径} --headed --browser=msedge
playwright-cli resize 375 812
playwright-cli eval "window.location.href"

# 2. 登录（仅未登录时执行）
playwright-cli snapshot
playwright-cli fill e5 "admin"
playwright-cli fill e7 "admin123456"
playwright-cli click e9
playwright-cli eval "window.location.href"

# 3. 保存登录态
playwright-cli state-save app-auth.json
```

> 移动端 H5 使用 hash 路由，格式 `http://localhost:9900/#/pages/{路径}`。API 需通过 cool service 层调用（自动加 `/dev` 前缀），直接 `uni.request` 会因缺少代理前缀而失败。

### 后端接口测试

```bash
curl -s "http://localhost:8085/{接口}" -H "satoken: {token}"
```

### 登录失败处理

1. 确认各服务端口正常运行
2. 重新 `snapshot` 获取新 ref
3. 验证后端 `/login` 接口

## 第五步：发现问题 → 自动修复

1. 分析错误（控制台日志、请求响应、页面状态）
2. 修改代码 → 编译（后端）→ 测试 → 清理标记代码

### 调试命令

```bash
playwright-cli console         # 控制台日志
playwright-cli requests        # 网络请求
playwright-cli request 5       # 请求详情
playwright-cli tracing-start   # 开始录制
playwright-cli tracing-stop    # 停止录制
```

## 第六步：输出报告

```markdown
## 测试报告

### 后端
- ✅ xxx 接口 — 正常（code: 1000）
- ❌ xxx 接口 — 失败原因

### 管理端
- ✅ 登录 / 页面加载 / 列表展示 / 增删改查
- ❌ 搜索结果不符合预期

### 移动端 H5
- ✅ 登录 / 页面加载
- ❌ 列表数据为空

### 结论
全部通过 / 待修复: N 项
```
