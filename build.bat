@echo off
set JAVA8_HOME="C:\Program Files\Java\jdk1.8.0_351"
set PATH=%JAVA8_HOME%\bin;%PATH%

echo Cleaning...
rmdir /s /q bin 2>nul
mkdir bin

echo Compiling with Java 8...
javac --release 8 -d bin -cp "lib/json-20210307.jar" src\*.java

if errorlevel 1 (
    echo Compilation failed.
    pause
    exit /b
)

echo Creating JAR...
jar --create --file app.jar --main-class=GetItems -C bin . -C src itemlist.txt

echo Done.
pause
