#!/usr/bin/env bash

javac -d build $(find src -name "*.java")

jar cf Fedja-JSON.jar -C build .
