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

The Query store is implemented in Java and provides an API for the most common Tasks.

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

```java
        // Initialize Query Store
        QueryStoreAPI queryAPI= new QueryStoreAPI();

```



## Persistent Identifaction

Identifying datasets is essential for future reuse. Storing exported datasets on a Web server and providing the URL
for its retrieval is not enough. A simle change in the directory structure of the server renders a URL obsolete and
the dataset can not be retrieved again. Hence a more sophisticated way of referencing datasets is needed. The concept
 of [persistent identifiers (PID)](http://en.wikipedia.org/wiki/Persistent_identifier) deals with this problem.
 Several different PID systems [exist](http://metadaten-twr.org/2010/10/13/persistent-identifiers-an-overview/) and
 they all follow similar principles. A resolver service is introduced which can resolve an identifier to its URL. The
  actual URL may change during the course of time, but the identifier remains always the same (it is persistent).
  Thus whenever a digital object has to be moved to a different location (e.g.. server filesystem updates or similar
  events), its location may be updated and the identifier can then refer to its new location.


