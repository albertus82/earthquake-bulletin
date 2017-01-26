@ECHO OFF
IF "%JAVA_HOME%" == "" START "" javaw.exe -Xms8m -Xmx32m -jar "%~dp0EarthquakeBulletin.jar"
IF NOT "%JAVA_HOME%" == "" START "" "%JAVA_HOME%\bin\javaw.exe" -Xms8m -Xmx32m -jar "%~dp0EarthquakeBulletin.jar"
