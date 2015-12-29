# It works on the Java project part of this common repository, so that they can be released as .jar files

REPONAME = commons
DOCKERIMAGENAME = benchflow/$(REPONAME)
VERSION = dev
JAVA_VERSION_FOR_COMPILATION = java-8-oracle
JAVA_HOME := `update-java-alternatives -l | cut -d' ' -f3 | grep $(JAVA_VERSION_FOR_COMPILATION)`"/jre"

.PHONY: all build_release_java

all: build_release_java

clean_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn clean

build_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn package

build_release_java: build_java

install_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn install

test_java:
	cd keyname-hash-generator/java/ && \
	JAVA_HOME=$(JAVA_HOME) mvn test