@echo off
cd /d "%~dp0"
if exist out rmdir /s /q out
mkdir out
echo Compiling RentMate...
javac -encoding UTF-8 -d out src\Main.java
if errorlevel 1 (
    echo.
    echo Compilation failed. Make sure JDK is installed.
    pause
    exit /b 1
)
echo Starting RentMate...
java -cp out Main
pause
