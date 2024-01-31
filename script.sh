#!/bin/bash

if [[ -z $1 ]];
then
    echo "Wrong usage: ./script.sh -help"
else
    echo "Parameter passed = $1"
fi