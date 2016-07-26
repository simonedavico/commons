REPONAME = commons
DOCKERIMAGENAME = benchflow/$(REPONAME)
VERSION = dev
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

.PHONY: all build_release_java build_release_go

# all: build_release_java build_release_go
all: build_release_java

build_go:
	$(MAKE) -C ./docker/go
	$(MAKE) -C ./kafka/go
	$(MAKE) -C ./keyname-hash-generator/go
	$(MAKE) -C ./minio/go

build_release_go: build_go

clean_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn clean
	cd minio/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn clean

build_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn package
	cd ../../minio/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn validate && \
	JAVA_HOME=$(JAVA_HOME) mvn package

build_release_java: build_java

install_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn install
	cd minio/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn install

test_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn test
	cd minio/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn test
