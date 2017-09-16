#!/bin/sh
if [ "$JAVA_HOME" != "" ]
then "$JAVA_HOME/bin/java" -Xms4m -Xmx16m -classpath "`dirname $0`/earthquake-bulletin.jar:`dirname $0`/lib/*" gov.usgs.cr.hazards.feregion.fe_1995.FERegion $1 $2
else java -Xms4m -Xmx16m -classpath "`dirname $0`/earthquake-bulletin.jar:`dirname $0`/lib/*" gov.usgs.cr.hazards.feregion.fe_1995.FERegion $1 $2
fi
