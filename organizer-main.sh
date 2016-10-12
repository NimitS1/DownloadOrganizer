#!/bin/bash

MAIN_DIR="C:\Users\snimit\Downloads\"
CONFIG="organizer-config.txt"

cat $CONFIG | while read line 
do
  extension=$(echo $line | awk '{print $1}' | sed 's/://g' )
  target_folder=$(echo $line | awk '{print $2}' )
  
  ls $MAIN_DIR | egrep $extension | while read filename
  do
    mv $MAIN_DIR"/$filename" $target_folder
    echo $?
  done
done
