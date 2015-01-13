SHOW FULL PROCESSLIST;

KILL 63;


select concat('KILL ',id,';') from information_schema.processlist where user='querystoreuser' into outfile '/tmp/killlist.txt';
source '/tmp/killlist.txt';
