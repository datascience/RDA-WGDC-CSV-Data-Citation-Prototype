DROP DATABASE IF EXISTS PersistentIdentifierDB;
CREATE DATABASE PersistentIdentifierDB;

CREATE USER 'querystoreuser'@'localhost' IDENTIFIED BY 'query2014';
GRANT ALL PRIVILEGES ON `PersistentIdentifierDB`. * TO 'querystoreuser'@'localhost';
