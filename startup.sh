#!/bin/bash

export MAVEN_OPTS="-Xms3g -Xmx3g"

mvn clean install
cd deep-qa-web/
mvn jetty:run