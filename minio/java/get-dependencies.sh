#!/usr/bin/env bash

RELEASE_VERSION=v-dev

pwd
echo "HELLO, THIS IS WORKING"

mkdir lib
wget -q --no-check-certificate -O ./lib/benchflow-keyname-hash-generator.jar \
    http://github.com/benchflow/commons/releases/download/${RELEASE_VERSION}/benchflow-keyname-hash-generator.jar