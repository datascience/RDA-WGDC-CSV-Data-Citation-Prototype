#!/bin/bash
#
# Tomcat 8 start/stop/status init.d script
# Initially forked from: https://gist.github.com/valotas/1000094
# @author: Miglen Evlogiev <bash@miglen.com>
#
# Release updates:
# Updated method for gathering pid of the current proccess
# Added usage of CATALINA_BASE
# Added coloring and additional status
# Added check for existence of the tomcat user
# Added termination proccess
#Location of JAVA_HOME (bin files)
export JAVA_HOME=/usr/lib/jvm/java-7-oracle/bin
#Add Java binary files to PATH
export PATH=$JAVA_HOME/bin:$PATH
#CATALINA_HOME is the location of the bin files of Tomcat
export CATALINA_HOME=/opt/tomcat7
#CATALINA_BASE is the location of the configuration files of this instance of Tomcat
export CATALINA_BASE=/opt/tomcat7
#TOMCAT_USER is the default user of tomcat
export TOMCAT_USER=tomcat
#TOMCAT_USAGE is the message if this script is called without any options
TOMCAT_USAGE="Usage: $0 {\e[00;32mstart\e[00m|\e[00;31mstop\e[00m|\e[00;31mkill\e[00m|\e[00;32mstatus\e[00m|\e[00;31mrestart\e[00m}"
#SHUTDOWN_WAIT is wait time in seconds for java proccess to stop
SHUTDOWN_WAIT=10
tomcat_pid() {
echo `ps -fe | grep $CATALINA_BASE | grep -v grep | tr -s " "|cut -d" " -f2`
}
start() {
pid=$(tomcat_pid)
if [ -n "$pid" ]
then
echo -e "\e[00;31mTomcat is already running (pid: $pid)\e[00m"
else
# Start tomcat
echo -e "\e[00;32mStarting tomcat\e[00m"
#ulimit -n 100000
#umask 007
#/bin/su -p -s /bin/sh $TOMCAT_USER
if [ `user_exists $TOMCAT_USER` = "1" ]
then
/bin/su $TOMCAT_USER -c $CATALINA_HOME/bin/startup.sh
else
sh $CATALINA_HOME/bin/startup.sh
fi
status
fi
return 0
}
status(){
pid=$(tomcat_pid)
if [ -n "$pid" ]; then echo -e "\e[00;32mTomcat is running with pid: $pid\e[00m"
else echo -e "\e[00;31mTomcat is not running\e[00m"
fi
}
 
terminate() {
echo -e "\e[00;31mTerminating Tomcat\e[00m"
kill -9 $(tomcat_pid)
}
 
stop() {
pid=$(tomcat_pid)
if [ -n "$pid" ]
then
echo -e "\e[00;31mStoping Tomcat\e[00m"
#/bin/su -p -s /bin/sh $TOMCAT_USER
sh $CATALINA_HOME/bin/shutdown.sh
let kwait=$SHUTDOWN_WAIT
count=0;
until [ `ps -p $pid | grep -c $pid` = '0' ] || [ $count -gt $kwait ]
do
echo -n -e "\n\e[00;31mwaiting for processes to exit\e[00m";
sleep 1
let count=$count+1;
done
if [ $count -gt $kwait ]; then
echo -n -e "\n\e[00;31mkilling processes didn't stop after $SHUTDOWN_WAIT seconds\e[00m"
terminate
fi
else
echo -e "\e[00;31mTomcat is not running\e[00m"
fi
return 0
}
user_exists(){
if id -u $1 >/dev/null 2>&1; then
echo "1"
else
echo "0"
fi
}
case $1 in
start)
start
;;
stop)
stop
;;
restart)
stop
start
;;
status)
status
;;
kill)
terminate
;;
*)
echo -e $TOMCAT_USAGE
;;
esac
exit 0 
