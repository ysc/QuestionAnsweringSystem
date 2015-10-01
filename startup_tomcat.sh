#!/bin/bash

export MAVEN_OPTS="-Xms3g -Xmx3g"

if [ -f ~/tomcat-8.0.21.zip ] ; then
    echo tomcat-8.0.21 prepared
else
    echo downloading tomcat8.0.21...
    wget http://archive.apache.org/dist/tomcat/tomcat-8/v8.0.21/bin/apache-tomcat-8.0.21.zip
    echo tomcat has been downloaded
    mv apache-tomcat-8.0.21.zip ~/tomcat-8.0.21.zip
    echo copy finished
    unzip -d ~/tomcat-8.0.21 ~/tomcat-8.0.21.zip
    echo unzip finished
fi
mvn clean install
echo clean install finished
rm -rf ~/tomcat-8.0.21/apache-tomcat-8.0.21/webapps/deep-qa-web-1.2
echo old webapps directory deleted
cp deep-qa-web/target/deep-qa-web-1.2.war ~/tomcat-8.0.21/apache-tomcat-8.0.21/webapps/
echo copy war finished
rm -rf ~/tomcat-8.0.21/apache-tomcat-8.0.21/logs
echo old logs directory deleted
mkdir ~/tomcat-8.0.21/apache-tomcat-8.0.21/logs
echo mkdir logs directory
chmod +x ~/tomcat-8.0.21/apache-tomcat-8.0.21/bin/*.sh
~/tomcat-8.0.21/apache-tomcat-8.0.21/bin/catalina.sh start
echo deep-qa-web-1.2 has been started
tail -f ~/tomcat-8.0.21/apache-tomcat-8.0.21/logs/*