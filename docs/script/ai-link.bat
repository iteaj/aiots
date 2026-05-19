@echo off
chcp 65001 >nul
REM ai-link.bat

set "ROOT=%~dp0..\.."
set "AI_DIR=%ROOT%\docs\ai"

if "%1"=="" goto usage

if /i "%1"=="qoder" goto qoder
if /i "%1"=="cursor" goto cursor
if /i "%1"=="trae" goto trae
if /i "%1"=="claude" goto claude
if /i "%1"=="codex" goto codex
goto usage

:qoder
if exist "%ROOT%\.qoder" echo [跳过] .qoder 已存在 & goto end
mklink /J "%ROOT%\.qoder" "%AI_DIR%" >nul
echo [创建] .qoder ^-^> docs/ai
goto end

:cursor
if exist "%ROOT%\.cursor" echo [跳过] .cursor 已存在 & goto end
mklink /J "%ROOT%\.cursor" "%AI_DIR%" >nul
echo [创建] .cursor ^-^> docs/ai
goto end

:trae
if exist "%ROOT%\.trae" echo [跳过] .trae 已存在 & goto end
mklink /J "%ROOT%\.trae" "%AI_DIR%" >nul
echo [创建] .trae ^-^> docs/ai
goto end

:claude
if exist "%ROOT%\.claude" echo [跳过] .claude 已存在 & goto end
mklink /J "%ROOT%\.claude" "%AI_DIR%" >nul
echo [创建] .claude ^-^> docs/ai
goto end

:codex
if exist "%ROOT%\.codex" echo [跳过] .codex 已存在 & goto end
mklink /J "%ROOT%\.codex" "%AI_DIR%" >nul
echo [创建] .codex ^-^> docs/ai
goto end

:usage
echo 用法: ai-link.bat ^<工具类型^>
echo 工具类型支持: qoder cursor trae claude codex
exit /b 1

:end
