@echo on
REM Build script for Windows cmd: compile Java sources and create runnable JAR including resources

:: create bin folder if missing
if not exist bin mkdir bin
echo Created bin directory

:: list source files into sources.txt
dir /b /s src\*.java > sources.txt
echo Listed source files

:: compile all Java sources into bin (preserve package structure)
echo Compiling Java sources...
javac -d bin @sources.txt
if %errorlevel% neq 0 (
  echo Compilation failed with error %errorlevel%.
  pause
  exit /b %errorlevel%
)
echo Compilation successful

:: create manifest (Main-Class must match your main class)
echo Creating manifest...
echo Main-Class: main.Main>manifest.txt
echo.>>manifest.txt
echo Created manifest

:: create jar and include resources from src\res
echo Creating JAR file...
jar cfm SlimeVengeance.jar manifest.txt -C bin . -C src\res .
if %errorlevel% neq 0 (
  echo JAR creation failed with error %errorlevel%.
  pause
  exit /b %errorlevel%
)

echo Created SlimeVengeance.jar successfully
dir SlimeVengeance.jar
pause
