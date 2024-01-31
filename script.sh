#!/bin/bash

if [[ -z $1 ]] || [[ $# -gt 4 ]] || [[ ( $1 != "-help" ) && ( $# -lt 2 ) ]];
then
    echo "Wrong usage: ./script.sh -help"
else
    if [[ "$1" == *"-help"* ]];
    then
        echo "Using script you need to substitute SAT_solver.jar with ./script.sh in order to use the script."
        echo
        java -jar SAT_solver.jar "$@"
    else
        java -jar SAT_solver.jar "$@"
    fi
fi