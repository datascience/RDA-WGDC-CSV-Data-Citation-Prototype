# copy to bin directory of apache
export CATALINA_OPTS="-XX:+CMSClassUnloadingEnabled"
export JAVA_OPTS="-Xmx2048m -XX:-UseGCOverheadLimit -Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
export JRE_HOME="/opt/java/java8"
export JAVA_HOME="/opt/java/java8"
