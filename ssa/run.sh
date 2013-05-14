#!/bin/sh

if [ $# -lt 1 ]
  then
    echo "run.sh [file:*.start]"
  else
	java -jar opt.jar $*
fi

