---
name: ai-setup
description: 项目AI工具配套支持。管理AI技能/规则/智能体的目录结构与软链接，为AI工具提供知识库接入。
role: AI基础设施
---

# 项目 AI 工具配套支持

所有 AI 相关的规则、技能、智能体统一存放在 `docs/ai/` 目录下，通过目录链接共享给各 AI 工具。

---

## 目录结构

```
docs/ai/
├── rules/          # AI 规则 — 静态规范，约束 AI 行为
├── skills/         # AI 技能 — 可执行能力，AI 按需调用
├── agents/         # 智能体定义（可选）
└── references/     # 参考文档（模板、示例代码）
```

### rules/

约束 AI 编码行为的规范和约定。已注册的规则文件：

- `api.md` — API 使用规范
- `uts.md` — UTS 语言规则
- `uvue.md` — UVUE 页面规则
- `ucss.md` — UCSS 样式规则
- `cool.md` — CoolUI 组件规范
- `conditional-compilation.md` — 条件编译规范
- `uni-app-x-best-practices.md` — uni-app x 最佳实践

### skills/

可按需调用的 AI 技能：

| 技能 | 职责 |
|------|------|
| `aiots-dev` | AIoT 全栈开发（CRUD/IoT/移动端/插件） |
| `app` | 移动端前端开发 |
| `cool ui` | CoolUI 组件库使用 |
| `ai-setup` | 当前技能 — AI 工具配套管理 |

### agents/

专用子代理，处理特定领域的复杂任务：

| 代理 | 职责 |
|------|------|
| `deploy` | 构建发布与远程部署 |

---

## AI 工具链接

各 AI 工具通过目录链接读取 `docs/ai/` 下的知识库：

| 工具 | 链接路径 |
|------|---------|
| Cursor | `.cursor/` → `docs/ai/` |
| Claude Code | `.claude/` → `docs/ai/` |
| Trae | `.trae/` → `docs/ai/` |
| Qoder | `.qoder/` → `docs/ai/` |
| Codex | `.codex/` → `docs/ai/` |

### 初始化链接

```bash
bash docs/script/ai-link.sh cursor      # Git Bash
.\docs\script\ai-link.bat cursor        # CMD
.\docs\script\ai-link.ps1 -Tool cursor  # PowerShell
```

### 新 AI 工具接入

1. 在 `docs/script/ai-link.sh` `ai-link.bat` `ai-link.ps1` 中添加新工具类型
2. 在本页的工具链接表中添加对应行
3. 执行脚本创建链接

---

## 可用脚本

`docs/script/` 目录下的工具脚本：

| 脚本 | 用途 |
|------|------|
| `ai-link.*` | 为 AI 工具创建目录链接 |
