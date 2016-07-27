#!/usr/bin/env bash

RELEASE_VERSION=v-dev

mkdir lib
wget -q --no-check-certificate -O ./lib/keyname-hash-generator.jar \
    http://github.com/benchflow/commons/releases/download/${RELEASE_VERSION}/keyname-hash-generator.jar
