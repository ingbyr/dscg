#!/usr/bin/env bash
echo "remove duplicated data from $1, save to u_$1";
sort -u $1 > "u_$1"