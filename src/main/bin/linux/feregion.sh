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
java -Xms${feregion.vm.initialHeapSize}m -Xmx${feregion.vm.maxHeapSize}m -cp "$PRGDIR/${project.build.finalName}.${project.packaging}:$PRGDIR/lib/*" ${feregion.mainClass} $1 $2 $3
