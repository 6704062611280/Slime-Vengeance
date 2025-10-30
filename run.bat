@echo off
if not exist SlimeVengeance.jar (
  echo SlimeVengeance.jar not found. Run build.bat first.
  pause
  exit /b 1
)
java -jar SlimeVengeance.jar
pause
