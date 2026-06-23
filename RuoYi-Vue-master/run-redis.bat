@echo off
echo 正在启动 Redis...
:: -------------------------------
:: 下面这一行改成你真实的 Redis 安装目录
cd /d F:\Others\Redis\Redis-x64-5.0.14.1
:: -------------------------------
redis-server.exe redis.windows.conf