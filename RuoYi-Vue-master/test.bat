@echo off
echo 正在检查环境...

echo [1] 检查 Java:
java -version
if %errorlevel% neq 0 echo --- Java 未找到！

echo.
echo [2] 检查 Maven:
call mvn -v
if %errorlevel% neq 0 echo --- Maven 未找到！

echo.
echo [3] 检查 Node:
call node -v
if %errorlevel% neq 0 echo --- Node.js 未找到！

echo.
echo [4] 检查 NPM:
call npm -v
if %errorlevel% neq 0 echo --- NPM 未找到！

echo.
echo 检查结束，按任意键退出...
pause