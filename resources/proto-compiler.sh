#!/bin/bash
java -jar ./wire-compiler-1.7.0-jar-with-dependencies.jar --proto_path=../app/proto --java_out=../app/src/main/java protos.proto
