Deploying the System
==================

Maven 
-------

Deploy with the command line:
    mvn clean install  org.apache.tomcat.maven:tomcat7-maven-plugin:2.0:deploy


Tomcat Settings
====================
Create setenv.sh file in CATALINA_HOME/bin

chmod 755 setenv.sh
stefan@stefan-Linux:~/Development/apache/bin$ cat setenv.sh
export CATALINA_OPTS="-XX:+CMSClassUnloadingEnabled -XX:+CMSPermGenSweepingEnabled "
