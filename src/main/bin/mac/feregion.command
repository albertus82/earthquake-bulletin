#!/bin/sh
java -Xms${feregion.vm.initialHeapSize}m -Xmx${feregion.vm.maxHeapSize}m -cp "`dirname $0`/../Resources/Java/${mac.build.finalName}.${project.packaging}:`dirname $0`/../Resources/Java/lib/*.jar" ${feregion.mainClass} $1 $2 $3
