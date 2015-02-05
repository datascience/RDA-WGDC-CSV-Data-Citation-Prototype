DROP DATABASE IF EXISTS QueryStoreDB;
CREATE DATABASE QueryStoreDB;

CREATE USER 'querystoreuser'@'localhost' IDENTIFIED BY 'query2014';
GRANT ALL PRIVILEGES ON `QueryStoreDB`. * TO 'querystoreuser'@'localhost';
FLUSH PRIVILEGES;