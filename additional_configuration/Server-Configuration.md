Tomcat 7
=======================

Java
--------------
sudo apt-get install python-software-properties
sudo apt-get install oracle-java7-installer
sudo add-apt-repository ppa:webupd8team/java
sudo apt-get update
sudo apt-get install oracle-java7-installer
sudo ln -s /usr/lib/jvm/java-7-oracle /usr/lib/jvm/java

java -version

Apache
---------
sudo apt-get install apache2 
sudo apt-get install  libapache2-mod-jk


Tomcat
---------------
wget http://tweedo.com/mirror/apache/tomcat/tomcat-7/v7.0.59/bin/apache-tomcat-7.0.59.tar.gz
tar xvfz apache-tomcat-7.0.59.tar.gz 
sudo mv apache-tomcat-7.0.59 /opt/
cd /opt/
sudo ln -s apache-tomcat-7.0.59/ tomcat7

groupadd tomcat
useradd tomcat -g tomcat -m -s /bin/bash

sudo chown -R tomcat:tomcat /opt/apache-tomcat-7.0.59/
sudo chown -R tomcat:tomcat /opt/tomcat7

#Set init.d script: See additional_configuration directory. Copy script to /etc/init.d(tomca7 and make executable
sudo chmod +x /etc/init.d/tomcat7

#Edit /etc/environment and add the following: 
JAVA_HOME="/usr/lib/jvm/java-7-oracle"
JRE_HOME="/usr/lib/jvm/java-7-oracle/jre"


# Launch at startup
sudo update-rc.d tomcat7 defaults


Mod_JK
--------------------------------------------

# workers.properties -
#
# This file is a simplified version of the workers.properties supplied
# with the upstream sources. The jni inprocess worker (not build in the
# debian package) section and the ajp12 (deprecated) section are removed.
#
# As a general note, the characters $( and ) are used internally to define
# macros. Do not use them in your own configuration!!!
#
# Whenever you see a set of lines such as:
# x=value
# y=$(x)\something
#
# the final value for y will be value\something
#
# Normaly all you will need to do is un-comment and modify the first three
# properties, i.e. workers.tomcat_home, workers.java_home and ps.
# Most of the configuration is derived from these.
#
# When you are done updating workers.tomcat_home, workers.java_home and ps
# you should have 3 workers configured:
#
# - An ajp13 worker that connects to localhost:8009
# - A load balancer worker
#
#

# OPTIONS ( very important for jni mode ) 

#
# workers.tomcat_home should point to the location where you
# installed tomcat. This is where you have your conf, webapps and lib
# directories.
#
workers.tomcat_home=/opt/tomcat7

#
# workers.java_home should point to your Java installation. Normally
# you should have a bin and lib directories beneath it.
#
workers.java_home=/usr/lib/jvm/java

#
# You should configure your environment slash... ps=\ on NT and / on UNIX
# and maybe something different elsewhere.
#
ps=/

#
#------ ADVANCED MODE ------------------------------------------------
#---------------------------------------------------------------------
#

#
#------ worker list ------------------------------------------
#---------------------------------------------------------------------
#
#
# The workers that your plugins should create and work with
# 
worker.list=ajp13_worker

#
#------ ajp13_worker WORKER DEFINITION ------------------------------
#---------------------------------------------------------------------
#

#
# Defining a worker named ajp13_worker and of type ajp13
# Note that the name and the type do not have to match.
#
worker.ajp13_worker.port=8009
worker.ajp13_worker.host=localhost
worker.ajp13_worker.type=ajp13
#
# Specifies the load balance factor when used with
# a load balancing worker.
# Note:
#  ----> lbfactor must be > 0
#  ----> Low lbfactor means less work done by the worker.
worker.ajp13_worker.lbfactor=1

#
# Specify the size of the open connection cache.
#worker.ajp13_worker.cachesize

#
#------ DEFAULT LOAD BALANCER WORKER DEFINITION ----------------------
#---------------------------------------------------------------------
#

#
# The loadbalancer (type lb) workers perform wighted round-robin
# load balancing with sticky sessions.
# Note:
#  ----> If a worker dies, the load balancer will check its state
#        once in a while. Until then all work is redirected to peer
#        workers.
worker.loadbalancer.type=lb
worker.loadbalancer.balance_workers=ajp13_worker


####################################################################################################################


Apache 
--------------------------

<Virtualhost *:80>
    JkMount /cite* ajp13_worker
    ServerName ate.ifs.tuwien.ac.at
    DocumentRoot /opt/tomcat7/webapps
    ErrorLog /opt/tomcat7/logs/error.log
    CustomLog /opt/tomcat7/logs/access.log common
    <Directory /opt/tomcat7/webapps>
        Options -Indexes
        Order Allow,Deny

    </Directory>

  #<Directory  /opt/tomcat7/webapps/cite>
#	  Order allow,deny
#	  Allow from all
#   </Directory>
</Virtualhost>


########################################################################

Security

----------

proell@ate:/opt/tomcat7/conf$ sudo cat context.xml 
<?xml version='1.0' encoding='utf-8'?>
<!--
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
-->
<!-- The contents of this file will be loaded for each web application -->
<Context>

    <!-- Default set of monitored resources -->
    <WatchedResource>WEB-INF/web.xml</WatchedResource>

    <!-- Uncomment this to disable session persistence across Tomcat restarts -->
    <!--
    <Manager pathname="" />
    -->

    <!-- Uncomment this to enable Comet connection tacking (provides events
         on session expiration as well as webapp lifecycle) -->
    <!--
    <Valve className="org.apache.catalina.valves.CometConnectionManagerValve" />
    -->

	<!-- block unwanted IPs -->
<Valve className="org.apache.catalina.valves.RemoteAddrValve"
	allow="188.21.236.102|128.131.237.75|127.0.0.1"/>

</Context>


#########################################

MySQL

sudo apt-get install mysql-server-5.6

Connect via SSH Tunnel (ate is a shortcut):
ssh ate -L 3307:127.0.0.1:3306 -N

Then use Workbench







