REPONAME = commons
NAME = keyname-hash-generator
VERSION = dev
GOPATH_SAVE_RESTORE:=$(shell pwd):${GOPATH}

.PHONY: all build 

all: build

save_dependencies:
	# TODO: figure out how to get the following work, currently we add the dependencies to the Godeps.json manually
	# - refer to the following issue: https://github.com/benchflow/benchflow/issues/33
	# cd src/cloud/benchflow/$(REPONAME)/ && \
	# GOPATH=$(GOPATH_SAVE_RESTORE) godep save ./... && \
	# rm -rf ../../../../Godeps/*.* && \
	# rm -rf ../../../../Godeps && \
	# mv Godeps/ ../../../.. && \
	# cd ../../../..

restore_dependencies:
	rm -rf Godeps/_workspace/src/github.com/*
	GOPATH=$(GOPATH_SAVE_RESTORE) godep restore ./...
	# TODO: make it working with all but cloud folder
	mv src/github.com/* Godeps/_workspace/src/github.com
	rm -rf src/github.com

clean:
	go clean -i ./...
	rm -rf Godeps/_workspace/pkg

build:
	GOPATH=$(GOPATH_SAVE_RESTORE) godep go build -o bin/$(NAME) -v ./...

test:
	godep go test ./...