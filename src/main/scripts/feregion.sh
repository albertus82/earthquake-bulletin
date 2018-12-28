#!/bin/sh
PRG="$0"
while [ -h "$PRG" ] ; do
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
  then "$JAVA_HOME/bin/java" -Xms@feregion.vm.initialHeapSize@m -Xmx@feregion.vm.maxHeapSize@m -classpath "$PRGDIR/@linux.jarFileName@:$PRGDIR/lib/*" @feregion.mainClass@ $1 $2
  else java -Xms@feregion.vm.initialHeapSize@m -Xmx@feregion.vm.maxHeapSize@m -classpath "$PRGDIR/@linux.jarFileName@:$PRGDIR/lib/*" @feregion.mainClass@ $1 $2
fi
