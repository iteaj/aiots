#!/bin/bash
# =============================================
# ai-link.sh — AI 工具软连接创建
# 使用: bash ai-link.sh <工具类型>
# 工具类型: qoder, cursor, trae, claude, codex
# =============================================

ROOT="$(cd "$(dirname "$0")/../.." && pwd)"
case "$(uname -s)" in MINGW*|MSYS*|CYGWIN*) OS=win;; *) OS=unix;; esac

create() {
  local target="$1" link="$2"
  [ -e "$link" ] || [ -L "$link" ] && { echo "[跳过] $link 已存在"; return 0; }
  mkdir -p "$(dirname "$link")"
  if [ "$OS" = "win" ]; then
    cmd //c "mklink /J \"$(cygpath -w "$link")\" \"$(cygpath -w "$target")\"" >/dev/null
  else
    ln -sfn "$target" "$link"
  fi
  echo "[创建] $link -> $target"
}

tool=${1:-}
case "$tool" in
  qoder)  create "$ROOT/docs/ai" "$ROOT/.qoder" ;;
  cursor) create "$ROOT/docs/ai" "$ROOT/.cursor" ;;
  trae)   create "$ROOT/docs/ai" "$ROOT/.trae" ;;
  claude) create "$ROOT/docs/ai" "$ROOT/.claude" ;;
  codex)  create "$ROOT/docs/ai" "$ROOT/.codex" ;;
  *)
    echo "用法: bash ai-link.sh <工具类型>"
    echo "工具类型支持: qoder, cursor, trae, claude, codex"
    exit 1
    ;;
esac
