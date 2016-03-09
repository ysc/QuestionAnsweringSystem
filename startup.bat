@echo off

set MAVEN_OPTS=-d64 -Xms3g -Xmx3g

call mvn clean install
cd deep-qa-web/
call mvn jetty:run