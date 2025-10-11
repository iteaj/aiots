#!/bin/bash

# SpringBoot 项目启动脚本
# 使用方法: ./startup.sh [start|stop|restart|status]

# 配置信息
APP_NAME="bootstrap"                  # 应用名称
APP_JAR="${APP_NAME}.jar"  # JAR包路径
LOG_FILE="${APP_NAME}.log"            # 日志文件
PID_FILE="${APP_NAME}.pid"             # PID文件
JVM_OPTS="-Xms512m -Xmx1024m -XX:MetaspaceSize=128m"  # JVM参数

# 检查应用状态
check_status() {
    if [ -f "$PID_FILE" ]; then
        PID=$(cat "$PID_FILE")
        if ps -p "$PID" > /dev/null; then
            return 0  # 应用正在运行
        else
            return 1  # PID文件存在但进程不存在
        fi
    else
        return 2  # PID文件不存在
    fi
}

# 启动应用
start() {
    check_status
    status=$?

    if [ $status -eq 0 ]; then
        echo "应用已在运行，PID: $(cat "$PID_FILE")"
        return
    fi

    echo "正在启动应用..."
    nohup java $JVM_OPTS -jar $APP_JAR > $LOG_FILE 2>&1 &
    echo $! > "$PID_FILE"

    sleep 2
    check_status
    if [ $? -eq 0 ]; then
        echo "应用启动成功，PID: $(cat "$PID_FILE")"
        echo "日志文件: $LOG_FILE"
    else
        echo "应用启动失败，请检查日志"
    fi
}

# 停止应用
stop() {
    check_status
    status=$?

    if [ $status -ne 0 ]; then
        echo "应用未运行"
        return
    fi

    PID=$(cat "$PID_FILE")
    echo "正在停止应用，PID: $PID..."

    # 尝试优雅停止
    kill $PID
    sleep 5

    # 检查是否已停止
    if ps -p "$PID" > /dev/null; then
        echo "应用未响应，强制终止..."
        kill -9 $PID
    fi

    rm -f "$PID_FILE"
    echo "应用已停止"
}

# 重启应用
restart() {
    stop
    start
}

# 查看状态
status() {
    check_status
    status=$?

    if [ $status -eq 0 ]; then
        echo "应用正在运行，PID: $(cat "$PID_FILE")"
    elif [ $status -eq 1 ]; then
        echo "PID文件存在，但应用未运行"
        rm -f "$PID_FILE"
    else
        echo "应用未运行"
    fi
}

# 查看日志
tail_logs() {
    if [ -f "$LOG_FILE" ]; then
        tail -f $LOG_FILE
    else
        echo "日志文件不存在: $LOG_FILE"
    fi
}

# 执行命令
case "$1" in
    start)
        start
        ;;
    stop)
        stop
        ;;
    restart)
        restart
        ;;
    status)
        status
        ;;
    logs)
        tail_logs
        ;;
    *)
        echo "用法: $0 [start|stop|restart|status|logs]"
        exit 1
        ;;
esac

exit 0
