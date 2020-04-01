#!/usr/bin/env bash

RED='\033[0;31m'
NC='\033[0m' # No Color

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null 2>&1 && pwd )"
PROJECT_DIR="$SCRIPT_DIR/.."

# go to project dir
cd "$PROJECT_DIR" || exit

mkdir -p ./classes

CLASSPATH="$(echo lib/*.jar | tr ' ' ':')"

printf "Compile classpath: ${RED}$CLASSPATH${NC}\n"

echo "Compiling project..."
# compile project
javac -cp "$CLASSPATH" src/Main.java -d ./classes

echo "Running project..."
# run program
java -cp "$CLASSPATH:./classes" Main

echo "Done!"