#!/usr/bin/env bash

# 5/4/14
# afm -> values

inDir=~/Downloads/afm\ 2
cd "$inDir"

outDir=~/Downloads/afm_k
mkdir -p $outDir

ls | while read f; do
    newFile=$outDir/"$f"
    grep "^KPX" "$f" | sed -E s'|KPX(\ )*||' > "$newFile"
    if [ $(wc -l < "$newFile") -eq "0" ]; then
        rm -f "$newFile"
    fi
done
