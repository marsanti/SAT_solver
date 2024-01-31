#!/bin/bash

if [[ -z $1 ]] || [[ ! -z $5 ]];
then
    echo "Wrong usage: ./script.sh -help"
else
    if [[ "$1" == *"-help"* ]];
    then
        echo "Using script you need to substitute SAT_solver.jar with ./script.sh in order to use the script."
        echo
        java -jar SAT_solver.jar "$1"
    else
        if [[ -z $2 ]];
        then 
            if [[ -z $3 ]];
            then 
                if [[ -z $4 ]];
                then 
                    java -jar SAT_solver.jar "$1" "$2" "$3" "$4"
                else
                    java -jar SAT_solver.jar "$1" "$2" "$3"
                fi
            else
                java -jar SAT_solver.jar "$1" "$2"
            fi
        else
            echo "Wrong usage: ./script.sh -help"
        fi
    fi
fi