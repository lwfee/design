@echo off
chcp 65001
cls
echo ==============================================
echo       若依启动脚本 (修复版)
echo ==============================================

:: --- 1. 检查后端 (Port 8080) ---
netstat -ano | findstr ":8080" >nul
if %errorlevel%==0 (
    echo [√] 后端服务(8080) 正在运行中，跳过。
) else (
    echo [!] 正在启动后端...
    echo     (注意：如果没有配置mvn环境变量，这里尝试使用自带的mvnw)
    
    :: 尝试使用项目根目录下的 mvnw 命令
    if exist "mvnw.cmd" (
        start "RuoYi-Backend" cmd /k "mvnw.cmd spring-boot:run -pl ruoyi-admin"
    ) else (
        echo [X] 错误：找不到 mvnw.cmd 文件，且系统没有安装 maven。
        echo     请建议直接在 IDEA 中启动后端。
        pause
    )
)

:: --- 2. 检查前端 (Port 80) ---
netstat -ano | findstr ":80" >nul
if %errorlevel%==0 (
    echo [√] 前端服务(80)   正在运行中，跳过。
) else (
    echo [!] 正在启动前端...
    start "RuoYi-Frontend" cmd /k "cd ruoyi-ui && npm run dev"
)

echo ==============================================
echo 脚本执行完毕。
pause