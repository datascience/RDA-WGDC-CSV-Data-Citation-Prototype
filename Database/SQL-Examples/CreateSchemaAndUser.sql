DROP DATABASE IF EXISTS CITATION_DB;
CREATE DATABASE CITATION_DB;

CREATE USER 'querystoreuser'@'localhost' IDENTIFIED BY 'query2014';
GRANT ALL PRIVILEGES ON `CITATION_DB`. * TO 'querystoreuser'@'localhost';
FLUSH PRIVILEGES;