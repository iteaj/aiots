# =============================================
# aiots 项目部署脚本 (PowerShell)
# 支持本地手动执行和远程 Maven 自动化调用
# 适用于 Windows Server 或安装了 pwsh 的 Linux
# 使用: .\deploy.ps1 {start|stop|restart|frontend}
# =============================================

# -------------------- 配置项 --------------------
$JAR_NAME     = "bootstrap.jar"           # 后端 jar 包名
$LOG_FILE     = "bootstrap.log"           # 后端日志文件
$PID_FILE     = "bootstrap.pid"           # 后端进程 PID 文件
$FRONTEND_ZIP = "admin-ui.zip"            # 前端压缩包名
$NGINX_WEB_ROOT = "./aiots-ui"            # 前端静态文件发布目录
# ------------------------------------------------

# 启动后端服务
function Start-Service {
    if (Test-Path $PID_FILE) {
        $pid = Get-Content $PID_FILE
        $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
        if ($proc) {
            Write-Host "$JAR_NAME 服务已运行中, PID: $pid"
            return
        }
    }

    $java = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin\java.exe" } else { "java" }
    $arg = "-Xmx1g", "-jar", $JAR_NAME, "--spring.profiles.active=prod"

    $psi = New-Object System.Diagnostics.ProcessStartInfo
    $psi.FileName = $java
    $psi.Arguments = $arg -join " "
    $psi.RedirectStandardOutput = $true
    $psi.RedirectStandardError = $true
    $psi.UseShellExecute = $false
    $psi.CreateNoWindow = $true

    $proc = New-Object System.Diagnostics.Process
    $proc.StartInfo = $psi
    $proc.Start() | Out-Null

    $proc.Id | Out-File -FilePath $PID_FILE
    Write-Host "$JAR_NAME 启动成功, PID: $($proc.Id)"

    # 将输出重定向到日志文件（异步）
    Start-Job -ScriptBlock {
        param($p, $log)
        $p.StandardOutput.ReadToEnd() | Out-File -FilePath $log
        $p.StandardError.ReadToEnd() | Out-File -FilePath $log -Append
    } -ArgumentList $proc, $LOG_FILE | Out-Null
}

# 停止后端服务
function Stop-Service {
    if (-not (Test-Path $PID_FILE)) {
        Write-Host "$JAR_NAME 服务未运行, 无需停用"
        return
    }

    $pid = Get-Content $PID_FILE
    $proc = Get-Process -Id $pid -ErrorAction SilentlyContinue
    if ($proc) {
        Stop-Process -Id $pid -Force
        Write-Host "$JAR_NAME 已停止"
    } else {
        Write-Host "$JAR_NAME 服务未运行, 无需停用"
    }
    Remove-Item $PID_FILE -Force -ErrorAction SilentlyContinue
}

# 部署前端：备份旧版本 → 解压新版本
function Deploy-Frontend {
    if (-not (Test-Path $FRONTEND_ZIP)) {
        Write-Host "未找到 $FRONTEND_ZIP, 跳过前端部署"
        return 1
    }

    # 备份现有前端（按时间戳）
    if (Test-Path $NGINX_WEB_ROOT) {
        $items = Get-ChildItem $NGINX_WEB_ROOT -ErrorAction SilentlyContinue
        if ($items) {
            $backupDir = "backup/$(Get-Date -Format 'yyyyMMdd_HHmmss')"
            New-Item -ItemType Directory -Path $backupDir -Force | Out-Null
            Copy-Item -Path "$NGINX_WEB_ROOT\*" -Destination $backupDir -Recurse -Force
            Write-Host "前端备份完成: $backupDir"
        } else {
            Write-Host "nginx 目录 $NGINX_WEB_ROOT 为空, 跳过备份"
        }
    } else {
        Write-Host "nginx 目录 $NGINX_WEB_ROOT 不存在, 跳过备份"
    }

    # 解压新前端到 nginx 目录
    Write-Host "部署新前端到 $NGINX_WEB_ROOT ..."
    New-Item -ItemType Directory -Path $NGINX_WEB_ROOT -Force | Out-Null
    Remove-Item -Path "$NGINX_WEB_ROOT\*" -Recurse -Force -ErrorAction SilentlyContinue
    Expand-Archive -Path $FRONTEND_ZIP -DestinationPath $NGINX_WEB_ROOT -Force
    Remove-Item $FRONTEND_ZIP -Force -ErrorAction SilentlyContinue

    Write-Host "前端部署完成"
    return 0
}

# -------------------- 入口 --------------------
switch ($args[0]) {
    "start"   { Start-Service }
    "stop"    { Stop-Service }
    "restart" { Stop-Service; Start-Service }
    "frontend" { Deploy-Frontend }
    default {
        Write-Host @"
用法: .\deploy.ps1 {start|stop|restart|frontend}

  命令:
    start      启动后端服务
    stop       停止后端服务
    restart    重启后端服务
    frontend   部署前端（解压到 Nginx）
"@
        exit 1
    }
}
