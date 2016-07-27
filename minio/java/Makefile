REPONAME = commons
NAME = minio
VERSION = dev
KEYNAME_HASH_GENERATOR_RELEASE_VERSION=v-dev
JAVA_VERSION_FOR_COMPILATION = (^|/)java-8-oracle($|\s)
UNAME = $(shell uname)

find_java:
ifeq ($(UNAME), Darwin)
	$(eval JAVA_HOME := $(shell /usr/libexec/java_home))
else ifeq ($(UNAME),Linux)
ifndef TRAVIS
	$(eval JAVA_HOME := $(shell update-java-alternatives -l | cut -d' ' -f3 | egrep '$(JAVA_VERSION_FOR_COMPILATION)'))
endif 
endif

.PHONY: all build_release

all: build_release

clean:
	rm -Rf lib
	mvn clean

get_external_dependencies:
	# get keyname-hash-generator as dependency
	./bin/get-dependencies.sh

build: find_java get_external_dependencies
	mvn package

build_release: find_java
	mvn package

install: find_java get_external_dependencies
	mvn validate
	mvn package

test: find_java
	mvn test -B
