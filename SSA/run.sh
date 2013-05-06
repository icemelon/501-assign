#!/bin/sh

if [ $# -lt 1 ]
  then
    echo "run.sh [file:*.start]"
  else
	java -jar CFG.jar $*
fi

