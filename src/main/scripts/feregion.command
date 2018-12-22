#!/bin/sh
if [ "$JAVA_HOME" != "" ]
  then "$JAVA_HOME/bin/java" -Xms@feregion.vm.initialHeapSize@m -Xmx@feregion.vm.maxHeapSize@m -classpath "`dirname $0`/@project.artifactId@.jar:`dirname $0`/lib/*" @feregion.mainClass@ $1 $2
  else java -Xms@feregion.vm.initialHeapSize@m -Xmx@feregion.vm.maxHeapSize@m -classpath "`dirname $0`/@project.artifactId@.jar:`dirname $0`/lib/*" @feregion.mainClass@ $1 $2
fi
