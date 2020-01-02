#!/bin/bash
bench=100
# declare -a sets=("wsc2008_01" "wsc2008_02" "wsc2008_03" "wsc2008_04" "wsc2008_05" "wsc2008_06" "wsc2008_07" "wsc2008_08" "wsc2009_01" "wsc2009_02" "wsc2009_03" "wsc2009_04" "wsc2009_05")
declare -a sets=("wsc2009_01" "wsc2009_02" "wsc2009_03" "wsc2009_04" "wsc2009_05")
for data in "${sets[@]}"
do
        java -jar bench-1.0.jar -t sp:hwsc -b "$bench" -d "$data"
        java -jar bench-1.0.jar -t pf -d "$data"
done