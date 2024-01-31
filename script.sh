#!/bin/bash

if [[ -z $1 ]];
then
    echo "Wrong usage: ./script.sh -help"
else
    java -jar SAT_solver.jar "$1"
fi