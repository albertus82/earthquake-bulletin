#!/bin/sh
java -Xms@feregion.vm.initialHeapSize@m -Xmx@feregion.vm.maxHeapSize@m -classpath "`dirname $0`/@macos.jarFileName@:`dirname $0`/lib/*.jar" @feregion.mainClass@ $1 $2 $3
