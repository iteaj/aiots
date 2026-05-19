#!/bin/bash
# =============================================
# aiots 项目部署脚本
# 支持本地手动执行和远程 Maven 自动化调用
# 使用: bash deploy.sh {start|stop|restart|frontend}
# =============================================

# -------------------- 配置项 --------------------
JAR_NAME="bootstrap.jar"                    # 后端 jar 包名
LOG_FILE="bootstrap.log"                    # 后端日志文件
PID_FILE="bootstrap.pid"                    # 后端进程 PID 文件
FRONTEND_ZIP="admin-ui.zip"                 # 前端压缩包名
# Nginx 前端静态文件发布目录（按实际路径修改）
NGINX_WEB_ROOT="./aiots-ui"
# ------------------------------------------------

# 启动后端服务
start() {
    if [ -f "$PID_FILE" ] && kill -0 "$(cat "$PID_FILE")" >/dev/null 2>&1; then
        echo "$JAR_NAME 服务已运行中, PID: $(cat "$PID_FILE")"
        return 0
    fi
    nohup java -Xmx1g -jar "$JAR_NAME" --spring.profiles.active=prod > "$LOG_FILE" 2>&1 &
    echo $! > "$PID_FILE"
    echo "$JAR_NAME 启动成功, PID: $(cat "$PID_FILE")"
}

# 停止后端服务
stop() {
    if [ ! -f "$PID_FILE" ] || ! kill -0 "$(cat "$PID_FILE")" >/dev/null 2>&1; then
        echo "$JAR_NAME 服务未运行, 无需停用"
        return 0
    fi
    kill "$(cat "$PID_FILE")"
    rm "$PID_FILE"
    echo "$JAR_NAME 已停止"
}

# 部署前端：备份旧版本 → 解压新版本
deploy_frontend() {
    if [ ! -f "$FRONTEND_ZIP" ]; then
        echo "未找到 $FRONTEND_ZIP, 跳过前端部署"
        return 1
    fi

    # 备份现有前端（按时间戳）
    if [ -d "$NGINX_WEB_ROOT" ] && [ "$(ls -A "$NGINX_WEB_ROOT" 2>/dev/null)" ]; then
        BACKUP_DIR="backup/$(date +%Y%m%d_%H%M%S)"
        mkdir -p "$BACKUP_DIR"
        cp -r "$NGINX_WEB_ROOT"/* "$BACKUP_DIR/"
        echo "前端备份完成: $BACKUP_DIR"
    else
        echo "nginx 目录 $NGINX_WEB_ROOT 为空或无此目录, 跳过备份"
    fi

    # 解压新前端到 nginx 目录
    echo "部署新前端到 $NGINX_WEB_ROOT ..."
    mkdir -p "$NGINX_WEB_ROOT"
    rm -rf "$NGINX_WEB_ROOT"/*
    unzip -o "$FRONTEND_ZIP" -d "$NGINX_WEB_ROOT"
    rm -f "$FRONTEND_ZIP"

    echo "前端部署完成"
    return 0
}

case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        stop
        start
        ;;
    frontend)
        deploy_frontend
        ;;
    *)
        echo "用法: $0 {start|stop|restart|frontend}"
        exit 1
esac

exit 0
