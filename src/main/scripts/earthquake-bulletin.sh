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
  then "$JAVA_HOME/bin/java" -DSWT_GTK3=0 -Xms8m -Xmx48m -classpath "$PRGDIR/earthquake-bulletin.jar:$PRGDIR/lib/*" it.albertus.eqbulletin.EarthquakeBulletin
  else java -DSWT_GTK3=0 -Xms8m -Xmx48m -classpath "$PRGDIR/earthquake-bulletin.jar:$PRGDIR/lib/*" it.albertus.eqbulletin.EarthquakeBulletin
fi
