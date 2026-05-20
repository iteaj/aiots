---
name: ai-setup
description: 为ai开发工具提供技能、规则、智能体支持时使用。用于管理AI技能/规则/智能体的目录结构与软链接，为AI工具提供知识库接入。
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
```

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
