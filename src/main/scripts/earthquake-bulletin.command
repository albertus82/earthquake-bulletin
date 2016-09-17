#!/bin/sh
if [ "$JAVA_HOME" != "" ]
  then "$JAVA_HOME/bin/java" -XstartOnFirstThread -Xms4m -Xmx32m -jar `dirname $0`/earthquake-bulletin.jar >/dev/null 2>&1 &
  else java -XstartOnFirstThread -Xms4m -Xmx32m -jar `dirname $0`/earthquake-bulletin.jar >/dev/null 2>&1 &
  fi
osascript -e 'tell application "Terminal" to quit' &
exit
