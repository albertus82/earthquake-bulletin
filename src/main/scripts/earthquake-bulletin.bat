@ECHO OFF
IF "%JAVA_HOME%" == "" START "" javaw.exe -Xms4m -Xmx32m -jar "%~dp0earthquake-bulletin.jar"
IF NOT "%JAVA_HOME%" == "" START "" "%JAVA_HOME%\bin\javaw.exe" -Xms4m -Xmx32m -jar "%~dp0earthquake-bulletin.jar"
