#!/bin/bash

legacy=legacy
files=files
dest=schemas

function fail() {
    echo $*
    echo
    echo "Usage:"
    echo "$0: <name of session cookie> <value of session cookie>"
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

for url in $(cat ${files}); do
    file=${url##*/}
    curl -b "${cookie}" $url -o ${dest}/$file
done

# legacy
for url in $(cat ${legacy}); do
    file=${url##*/}
    curl -b "${cookie}" $url -o ${dest}/legacy/$file
done
