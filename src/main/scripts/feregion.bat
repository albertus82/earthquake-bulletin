@ECHO OFF
IF "%JAVA_HOME%" == "" java.exe -Xms4m -Xmx16m -classpath "%~dp0EarthquakeBulletin.jar;%~dp0lib\*" gov.usgs.cr.hazards.feregion.fe_1995.FERegion %1 %2
IF NOT "%JAVA_HOME%" == "" "%JAVA_HOME%\bin\java.exe" -Xms4m -Xmx16m -classpath "%~dp0EarthquakeBulletin.jar;%~dp0lib\*" gov.usgs.cr.hazards.feregion.fe_1995.FERegion %1 %2