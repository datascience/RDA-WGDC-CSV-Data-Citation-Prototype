# QueryStore and Persistent Identification Modules


This project provides two modules: the QueryStore and the Persistent Identification mockup service. Both components
have been developed within the [SCAPE project](http://www.scape-project.eu/) in order to facilityte data citation
capabilities for existing applications. This project provides prototypes for a query store and a identification
service used by such a service.

## Background: Data Citation

Sharing research data is becoming increasingly important as it enables peers to validate and reproduce data driven experiments.
As researchers work with different data sets and highly specific individual subsets, the knowledge how subsets have
been created is worth preserving. The most common way of creating a subset of a potentially large data set is to
make a selection based on filtering out those records which are not needed and only include data which fulfils a
given criteria.

## Enter the QueryStore

The generic approach to perform such filter operations is to use either use a query language which allows to describe
which data to be included and which records should be omitted. SQL is such a general purpose query language. For
end users, interfaces exist which can hide the complexity of query languages by providing forms where users can
make their selections visually or by entering appropriate values. The QueryStore stored these queries (thus the name)
and makes them available for later reuse. Whenever a reseacher uses a query (via an interface) in order to create a
subset, the parameters, their order and the sortings applied can be stored. Based on temporal metadata which is
collected automatically, the query can be re-executed.

### Query Store Features:

The Query store is implemented in Java and provides an API for the most common Tasks. It uses Hibernate to store the
entities (details below).

* Create new queries
* Add metadata about users
* Add descriptive text
* Attach the persistent identifier of the data source (details below)
* Generate a persistent identifier for the query itself (details below)
* Append a hash of the result set
* Calculate a hash of the query to detect duplicate queries
* Add arbitrary filters in a key value fashion (e.g. 'instrumentName';'tempreatureSensor'). This is used to mapp the
interface input fields and their respective values which have been entered.
* Add arbitrary many sortings either in ascending or desceinding order
* Detect query duplicates
* Create timestamps automatically for each insert and update.
* Full audit log.

You can initialize the QueryStore by using its API. The QueryStore uses Hibernate to persist the data.

```java
        // Initialize Query Store
        QueryStoreAPI queryAPI= new QueryStoreAPI();
```
The package Examples contains usage samples. Read the JavaDocs for more details.



## Persistent Identifaction Service

Identifying datasets is essential for future reuse. Storing exported datasets on a Web server and providing the URL
for its retrieval is not enough. A simle change in the directory structure of the server renders a URL obsolete and
the dataset can not be retrieved again. Hence a more sophisticated way of referencing datasets is needed. The concept
 of [persistent identifiers (PID)](http://en.wikipedia.org/wiki/Persistent_identifier) deals with this problem.
 Several different PID systems [exist](http://metadaten-twr.org/2010/10/13/persistent-identifiers-an-overview/) and
 they all follow similar principles. A resolver service is introduced which can resolve an identifier to its URL. The
  actual URL may change during the course of time, but the identifier remains always the same (it is persistent).
  Thus whenever a digital object has to be moved to a different location (e.g.. server filesystem updates or similar
  events), its location may be updated and the identifier can then refer to its new location.

### Persistent Identification Service Features

The Query store is implemented in Java and provides an API for the most common Tasks. It uses Hibernate to store the
entities (details below). The following features are currently implemented in the API:

* Create PIDs of the form *prefix/identifier*.
* Create alphabetical identifiers of arbitrary length (e.g. zjqpU).
* Create alphanumeric identifiers of arbitrary length (YouTube style, eg. qsn4zPVRA7a0).
* Create numeric identifiers of arbitrary length (e.g. 08652).
* Store one URI with each identifier
* Update URIs (the identifier can neither be deleted nor updated via the API)
* Create organizations and prefixes (prefixes are unique).
* One organization can mit arbitrary many identifiers per prefix
* Identifiers are uniqie within one prefix and therefore the complete PID is unique.
* Resolve PIDs to URLs
* Retrieve latest PID pre organization
* Retrieve latest PID in the database.
* Print details
* Full audit log

You can initialize the QueryStore by using its API, refer to the examples for getting started. The QueryStore uses
Hibernate to persist the data. Details below.

```java
       PersistentIdentifierAPI api = new PersistentIdentifierAPI();


              // create a dummy organization and provide a prefix
              Organization evilOrganization = api.createNewOrganitation("Evil Corp",2345);
              Organization goodOrganization = api.createNewOrganitation("Good Company",6789);
              // set the length for alphanumeric identifiers
              evilOrganization.setAlphanumericPIDlength(20);
              goodOrganization.setAlphanumericPIDlength(12);

              // create identifiers
              api.getAlphaNumericPID(evilOrganization, "www.repository.org/collections/datasets/ResearchData.csv");
              api.getNumericPID(evilOrganization, "www.repository.org/collections/datasets/QuerySet");
              api.getAlphaPID(evilOrganization, "www.repository.org/documentation/manual.pdf");
```
The package Examples contains usage samples. Read the JavaDocs for more details.



## Initializing the database

In order to use the modules you need to create a database scheme, create a user and add the corresponding permissions
. This shows an example for MySQL. The permissions are not intended to be used in productive environments.

```sql

DROP DATABASE IF EXISTS `QueryStore`;
CREATE DATABASE `QueryStore`;

GRANT ALL PRIVILEGES ON QueryStore.* To 'QUERYSTOREUSER'@'localhost' IDENTIFIED BY 'PASSWORD';
FLUSH PRIVILEGES;

```

After you have set up and tested the database access, you need to enter the username,
the password and the database into the Hibernate configuration files called hibernate.cfg.xml in thr folder resources
 in each module.


 ```xml
<property name="hibernate.connection.username">QUERYSTOREUSER</property>
<property name="hibernate.connection.password">PASSWORD</property>
<property name="hibernate.connection.url">jdbc:mysql://localhost:3306/QueryStore</property>
 ```

# CSV Data Citation

Users should be able to upload CSV files which they want to make citable. The CSV files are previously unkown to the
server, therefore the files are analyzed and a new table schema is created automatically.

## Workfows
The following secenarios are considered.

### A new file
The user uploads a new file to the server. The user needs to specify a primary key. If no primary key can be
specified, updates can not be detected automatically. The server then server creates a new table schema and adds
metadata.

### Appending data
The user appends data to an existing file. The file needs to have the same structure as the original file and only
new records may be contained in the file. If a record is already there, an error is thrown.

### Updating data
A user provides a file which contains updated records or new records. Existing records are identified via their
primary key. When a primary key already exists in the database, the records gets updated and a new timestamp gets
inserted.

### Deleting records

The user uploads a file where rows have been deleted.