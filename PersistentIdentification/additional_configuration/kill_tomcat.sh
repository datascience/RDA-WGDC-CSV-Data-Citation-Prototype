TOMCAT="/home/stefan/Development/apache"

$TOMCAT/bin/shutdown.sh
sleep 2
ps -ef | grep tomcat | awk '{ print $2 }' | xargs kill -9
sleep 2
rm $TOMCAT/logs/catalina.out
$TOMCAT/bin/startup.sh
sleep 2
tail -f $TOMCAT/logs/catalina.out
