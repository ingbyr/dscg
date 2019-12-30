#!/usr/bin/env bash
echo "remove duplicated data line from $1"
sort -u $1 > $2
echo "saved to $2"