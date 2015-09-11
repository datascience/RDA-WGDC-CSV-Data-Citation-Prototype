-- create ummy user to safe delete it.
GRANT USAGE ON *.* TO 'querystoreuser'@'localhost';
DROP USER 'querystoreuser'@'localhost';
-- creat the actual user 
CREATE USER 'querystoreuser'@'localhost' IDENTIFIED BY 'query2014';
GRANT INSERT, UPDATE, ALTER, CREATE, SELECT, DROP, DELETE on querystoreDB.* to 'querystoreuser'@'localhost';
FLUSH PRIVILEGES;
