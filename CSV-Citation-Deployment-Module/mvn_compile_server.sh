#!/usr/bin/env bash
mvn clean install org.apache.tomcat.maven:tomcat7-maven-plugin:2.0:deploy -PlocalServer -Dmaven.test.skip=true
