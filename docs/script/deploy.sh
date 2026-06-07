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
NGINX_WEB_ROOT="@nginx.frontend.path@"
# 上传目录（jar + deploy.sh SCP 上传的目标目录）
UPLOAD_PATH="@upload.server.path@"
# 运行目录（服务实际运行的目录）
DEPLOY_PATH="@deploy.server.path@"
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

# 部署前端：解压新版本到 Nginx 目录
deploy_frontend() {
    if [ ! -f "$FRONTEND_ZIP" ]; then
        echo "未找到 $FRONTEND_ZIP, 跳过前端部署"
        return 1
    fi

    # 解压新前端到 nginx 目录
    echo "部署新前端到 $NGINX_WEB_ROOT ..."
    mkdir -p "$NGINX_WEB_ROOT"
    rm -rf "$NGINX_WEB_ROOT"/*
    unzip -o "$FRONTEND_ZIP" -d "$NGINX_WEB_ROOT"

    echo "前端部署完成"
    return 0
}

# 远程部署：从上传目录部署到运行目录（由 Maven SSH 调用）
remote_deploy() {
    # 确保运行目录存在
    mkdir -p "$DEPLOY_PATH"

    # deploy.sh 不存在时才从上传目录复制
    if [ ! -f "$DEPLOY_PATH/deploy.sh" ]; then
        cp "$UPLOAD_PATH/deploy.sh" "$DEPLOY_PATH/"
        echo "deploy.sh 已复制到 $DEPLOY_PATH"
    fi

    # 停止旧服务
    cd "$DEPLOY_PATH" && bash deploy.sh stop

    # 复制新 jar
    cp "$UPLOAD_PATH/$JAR_NAME" "$DEPLOY_PATH/"

    # 启动新服务
    bash deploy.sh start
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
    remote-deploy)
        remote_deploy
        ;;
    *)
        echo "用法: $0 {start|stop|restart|frontend|remote-deploy}"
        exit 1
esac

exit 0
