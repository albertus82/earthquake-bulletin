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
  then "$JAVA_HOME/bin/java" -Xms4m -Xmx16m -classpath "$PRGDIR/earthquake-bulletin.jar:$PRGDIR/lib/*" gov.usgs.cr.hazards.feregion.fe_1995.FERegion $1 $2
  else java -Xms4m -Xmx16m -classpath "$PRGDIR/earthquake-bulletin.jar:$PRGDIR/lib/*" gov.usgs.cr.hazards.feregion.fe_1995.FERegion $1 $2
fi
