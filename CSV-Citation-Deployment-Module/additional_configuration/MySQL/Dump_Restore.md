MySQL
========

MySQLdump does not include USE statements. As several databases are used, either the USE statement needs to be 
inserted manually, or the databases need to be imported seperately.
  
Databases
----------------------------




mysqldump -u root -p CitationUserDB > CitationUserDB.sql
mysqldump -u root -p PersistentIdentifierDB > PersistentIdentifierDB.sql
mysqldump -u root -p QueryStoreDB > QueryStoreDB.sql
mysqldump -u root -p CitationDB > CitationDB.sql

echo "USE CitationUserDB;" > CSV-CitationBaseSystem.sql
cat CitationUserDB.sql >>  CSV-CitationBaseSystem.sql
echo "USE PersistentIdentifierDB;" >> CSV-CitationBaseSystem.sql
cat PersistentIdentifierDB.sql >> CSV-CitationBaseSystem.sql
echo "USE QueryStoreDB;" >> CSV-CitationBaseSystem.sql
cat QueryStoreDB.sql >> CSV-CitationBaseSystem.sql
echo "USE CitationDB;" >> CSV-CitationBaseSystem.sql
cat CitationDB.sql >> CSV-CitationBaseSystem.sql



Insert the databases in this sequence: 

CitationUserDB
PersistentIdentifierDB
QueryStoreDB
CitationDB


