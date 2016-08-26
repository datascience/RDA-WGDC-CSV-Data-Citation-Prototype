#!/usr/bin/env bash
username=root
connection_username=querystoreuser

mysql -u$username -p -e 'show processlist' | grep $connection_username | awk {'print "kill "$1";"'}| mysql -u$username -p

# SQL ALternative

#   SELECT GROUP_CONCAT(CONCAT('KILL QUERY ',id,';') SEPARATOR ' ') KillQuery
#   FROM information_schema.processlist WHERE user<>'querystoresuer';