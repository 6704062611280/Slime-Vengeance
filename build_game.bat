@echo off
title Slime-Vengeance Build Script
color 0a

echo ======================================
echo      Building Slime-Vengeance Game
echo ======================================

:: ==== CONFIG ====
set SRC=src
set OUT=build
set RES=src\res
set MAINCLASS=main.Main
set JAR=SlimeVengeance.jar

:: ==== CLEAN ====
echo Cleaning old build...
if exist "%OUT%" rd /s /q "%OUT%"
mkdir "%OUT%"

:: ==== COMPILE ====
echo Compiling source code...
javac -d "%OUT%" -sourcepath "%SRC%" %SRC%\main\Main.java
if errorlevel 1 (
    echo ❌ Compilation failed!
    pause
    exit /b
)

:: ==== COPY RESOURCES ====
echo Copying resources...
if exist "%RES%" (
    xcopy "%RES%" "%OUT%\res" /E /I /Y >nul
) else (
    echo ⚠️  WARNING: Resource folder not found at "%RES%"
)

:: ==== MANIFEST ====
echo Creating manifest...
echo Main-Class: %MAINCLASS%> "%OUT%\manifest.txt"

:: ==== PACKAGE JAR ====
echo Packaging JAR file...
cd "%OUT%"
jar cfm "%JAR%" manifest.txt *
if errorlevel 1 (
    echo ❌ Failed to create JAR file!
    cd ..
    pause
    exit /b
)
cd ..

echo.
echo ✅ Build Complete!
echo ----------------------------
echo JAR created: %OUT%\%JAR%
echo ----------------------------
echo Launching game...
echo.

:: ==== RUN GAME ====
java -jar "%OUT%\%JAR%"
if errorlevel 1 (
    echo ❌ Failed to launch game.
)

echo.
echo =============================
echo  Game Closed. Build finished.
echo =============================
pause
