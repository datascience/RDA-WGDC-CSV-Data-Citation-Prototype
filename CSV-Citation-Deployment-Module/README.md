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
export CATALINA_OPTS="-XX:+CMSClassUnloadingEnabled"

Remote debugging:
=================

add this line to setenv.sh:

export JAVA_OPTS="-Xmx512m -XX:MaxPermSize=256m  -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"

Create a debug configuration and 


Github
---------

Private code repository (upstream): https://github.com/stefanproell/CSV-DataCitation
Public code repository: https://github.com/datascience/RDA-WGDC-CSV-Data-Citation-Prototype

Initialise DataScience Repository

1) create new repo
2) git clone https://github.com/stefanproell/CSV-DataCitation.git CSV-DataCitation-TU
3) git remote rename origin upstream
4) cd CSV-DataCitation-TU/
5) git remote rename origin upstream
6) git remote add origin https://github.com/datascience/RDA-WGDC-CSV-Data-Citation-Prototype.git
7) git pull https://github.com/datascience/RDA-WGDC-CSV-Data-Citation-Prototype.git
8) git commit -am "init push to data science repo"
9) git push https://github.com/datascience/RDA-WGDC-CSV-Data-Citation-Prototype.git

Publish new changes:
1) git pull  https://github.com/stefanproell/CSV-DataCitation.git
2) git push https://github.com/datascience/RDA-WGDC-CSV-Data-Citation-Prototype.git

