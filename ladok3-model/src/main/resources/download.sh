#!/bin/bash

files=legacy

function fail() {
    echo $*
    echo
    echo "Usage:"
    echo "$0: <name of session cookie> <value of session cookie> [destination]"
    echo
    echo "  destination: a destination directory, i.e. schemas, the current directory by default"
    echo
}

if [ -z "$1" ]; then
    fail "No cookie name"
    exit 1
fi

if [ -z "$2" ]; then
    fail "No cookie value"
    exit 1
fi

cookie="$1=$2"

if [ ! -z "$3" ]; then
    mkdir -p "$3"
fi

for url in $(cat ${files}); do
    file=${url##*/}
    echo "-> $url -> ${3:-.}"
    curl -b "${cookie}" $url -o ${3:-.}/$file
    echo
done
