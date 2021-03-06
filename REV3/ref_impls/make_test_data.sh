#!/bin/bash

#
# REV3 Project
# Copyright 2016, - All Rights Reserved
#
# Team BPWALSH
#

OLDIFS="$IFS"

for i in `find . -name sensor_data_log.txt`;
do
    echo "Processing: ${i} ..."
    IFS='/' array=($i)
    IFS="$OLDIFS"

    #echo " cp ./${array[1]}/ref_filter_impl.txt ../app/src/main/res/raw/${array[1],,}_ref_filter.csv"
    #echo $out_file
    python filter.py --record ./${array[1]}/ref_filter_impl.txt $i
    cp ./${array[1]}/ref_filter_impl.txt ../app/src/main/res/raw/${array[1],,}_ref_filter.csv
done
