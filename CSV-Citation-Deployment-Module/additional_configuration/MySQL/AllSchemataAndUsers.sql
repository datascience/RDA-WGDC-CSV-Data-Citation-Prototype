DROP DATABASE IF EXISTS CitationDB;
CREATE DATABASE CitationDB;
DROP DATABASE IF EXISTS QueryStoreDB;
CREATE DATABASE QueryStoreDB;
DROP DATABASE IF EXISTS PersistentIdentifierDB;
CREATE DATABASE PersistentIdentifierDB;
-- DROP DATABASE IF EXISTS CitationUserDB;
-- CREATE DATABASE CitationUserDB;



CREATE USER 'querystoreuser'@'localhost' IDENTIFIED BY 'query2014';
CREATE USER 'querystoreuser'@'192.168.0.80' IDENTIFIED BY 'query2014';



GRANT ALL PRIVILEGES ON `QueryStoreDB`. * TO 'querystoreuser'@'localhost';
GRANT ALL PRIVILEGES ON `CitationUserDB`. * TO 'querystoreuser'@'localhost';
GRANT ALL PRIVILEGES ON `CitationDB`. * TO 'querystoreuser'@'localhost';
GRANT ALL PRIVILEGES ON `PersistentIdentifierDB`. * TO 'querystoreuser'@'localhost';
GRANT ALL PRIVILEGES ON `CitationUserDB`. * TO 'querystoreuser'@'localhost';
GRANT SELECT, LOCK TABLES ON `QueryStoreDB`.* TO 'querystoreuser'@'localhost';
GRANT SELECT, LOCK TABLES ON `CitationUserDB`.* TO 'querystoreuser'@'localhost';
GRANT SELECT, LOCK TABLES ON `CitationDB`.* TO 'querystoreuser'@'localhost';
GRANT SELECT, LOCK TABLES ON `PersistentIdentifierDB`.* TO 'querystoreuser'@'localhost';
GRANT SELECT, LOCK TABLES ON `CitationUserDB`.* TO 'querystoreuser'@'localhost';

FLUSH PRIVILEGES;



