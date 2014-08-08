# QueryStore and Persistent Identification Modules


This project provides two modules: the QueryStore and the Persistent Identification mockup service. Both components
have been developed within the [SCAPE project](http://www.scape-project.eu/) in order to facilityte data citation
capabilities for existing applications. This project provides prototypes for a query store and a identification
service used by such a service.

## Data Citation

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
 such a

Many query
languanges
exist, from

