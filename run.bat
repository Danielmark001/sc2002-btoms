@echo off
echo Compiling project...
javac -cp src -d bin src/main/BtomsApp.java
if %errorlevel% neq 0 (
    echo Compilation failed.
    pause
    exit /b
)
echo Running project...
java -cp bin main.BtomsApp
pause
