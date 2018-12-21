#!/bin/sh
PRG="$0"
while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done
PRGDIR=`dirname "$PRG"`
if [ "$JAVA_HOME" != "" ]
  then "$JAVA_HOME/bin/java" -Xms@vm.initialHeapSize@m -Xmx@vm.maxHeapSize@m -classpath "$PRGDIR/@artifactId@.jar:$PRGDIR/lib/*" @mainClass@
  else java -Xms@vm.initialHeapSize@m -Xmx@vm.maxHeapSize@m -classpath "$PRGDIR/@artifactId@.jar:$PRGDIR/lib/*" @mainClass@
fi
