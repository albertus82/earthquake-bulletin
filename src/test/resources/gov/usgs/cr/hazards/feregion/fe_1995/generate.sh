#!/bin/bash
for i in `seq -180 1 180`;
do
    for j in `seq -90 1 90`;
    do
        perl feregion.pl $i $j
    done
done
