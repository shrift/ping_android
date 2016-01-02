#!/bin/bash
java -jar ./wire-compiler-2.0.2-jar-with-dependencies.jar --proto_path=../app/src/main/proto --java_out=../app/src/main/java protos.proto
