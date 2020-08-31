#!/bin/sh

cd qdb-docker ; docker build -t qdb/h2:latest .
cd ../
mvn clean install -Pdocker
