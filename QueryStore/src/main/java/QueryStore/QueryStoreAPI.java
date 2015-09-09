/*
 * Copyright [2015] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * Copyright [2015] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * Copyright [2014] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

/*
 * Copyright [2014] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package QueryStore;


import Database.DatabaseOperations.DatabaseTools;
import at.stefanproell.PersistentIdentifierMockup.*;
import at.stefanproell.ResultSetVerification.ResultSetVerificationAPI;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.*;
import java.util.logging.Logger;

/**
 * The API for the Query Store Mockup
 */
public class QueryStoreAPI {
    private Logger logger;
    private Session session;

    public QueryStoreAPI() {
        this.logger = Logger.getLogger(QueryStoreAPI.class.getName());
    }

    /*
    * Create query with parameters
    * * * */
    public Query createNewQuery(String userName, String pidString) {
        Query query = new Query();
        query.setUserName(userName);
        query.setPID(pidString);

        // the query is not yet persisted, hence we need a dummy hash
        query.setQueryHash("TEMPORAL HASH");
        // Store query persistently in database
        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        this.session.save(query);

        // calculate the query hash of the empty query. It only contains the data source hash.
        query.setQueryHash(this.calculateQueryHash(query));
        this.session.save(query);
        this.session.getTransaction().commit();


        this.session.close();
        return query;


    }


    /**
     * Iterate over filters and sortings and calculate a unique hash. Sortings and Filterings are stored in
     * LinkedHashSets.
     * Their insertion order is preserved. If a query uses a different order of insertion, then the hash will
     * be different. If this behaviour should be changed, use a TreeSet and sort it alphabetically.
     *
     * @param query
     * @return
     */
    private String calculateQueryHash(Query query) {
        this.logger.info("Calculate Hash");

        // concatenate all query details: data source pid + filters + sortings
        String queryDetails = "";
        String allFilters = "";
        String allSortings = "";

        // Append data source PID
        queryDetails = query.getDatasourcePID();

        // Iterate over all filters, concatenate their keys and values and normalize the string.
        List<Filter> filters = query.getFilters();
        if (filters != null) {
            if (filters.size() > 0) {
                Iterator<Filter> filterIterator = filters.iterator();
                while (filterIterator.hasNext()) {
                    Filter filter = (Filter) (filterIterator.next());
                    this.logger.info("Filter: " + filter.getFilterName() + " - " + filter.getFilterValue());

                    allFilters += filter.getFilterName() + filter.getFilterValue();
                }

                allFilters = this.normalizeString(allFilters);
                this.logger.info("Found " + filters.size() + " filters, which are " + allFilters);

            }
            // append all filters
            queryDetails += allFilters;

        } else {
            this.logger.info("No filters set");
        }


        List<Sorting> sortings = query.getSortings();
        if (sortings != null) {
            if (sortings.size() > 0) {
                Iterator<Sorting> sortingIterator = sortings.iterator();
                while (sortingIterator.hasNext()) {
                    Sorting sorting = (Sorting) (sortingIterator.next());
                    allSortings += sorting.getSortingColumn() + sorting.getDirection();
                }

                allSortings = this.normalizeString(allSortings);
                this.logger.info("Found " + sortings.size() + " filters, which are " + allSortings);

            }

            // append all sortings
            queryDetails += allSortings;
        } else {
            this.logger.info("No sortings");
        }

        this.logger.info("Query details String: " + queryDetails);


        // Calculate the hash of the text
        String uniqueQueryHash = this.calculateSHA1(queryDetails);
        this.logger.info("Query details HASH: " + uniqueQueryHash);

        return uniqueQueryHash;


    }

    /**
     * Remove all spaces from a string and convert to lower case letters.
     *
     * @param inputString
     * @return
     */
    private String normalizeString(String inputString) {
        if (inputString.equals("")) {
            return "";
        } else {
            return inputString.trim().toLowerCase();
        }


    }


    /**
     * Add a new filter to the query. Recomputes the query hash.
     *
     * @param query
     * @param key
     * @param value
     */
    public void addFilter(Query query, String key, String value) {
        // create new filter and persist it
        Filter filter = new Filter(query, key, value);

        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        this.session.saveOrUpdate(filter);
        query.getFilters().add(filter);
        this.session.saveOrUpdate(query);
        this.session.getTransaction().commit();

        // recalculate query hash
        String newQueryHash = this.calculateQueryHash(query);
        query.setQueryHash(newQueryHash);

        this.logger.info("new Hash: " + newQueryHash);

        this.session.beginTransaction();
        this.session.saveOrUpdate(query);
        this.session.getTransaction().commit();
        this.session.close();


    }

    /**
     * Add a sorting to the query
     *
     * @param query
     * @param column
     * @param direction
     */
    public void addSorting(Query query, String column, String direction) {
        // create new filter and persist it

        Sorting sorting = new Sorting(query, column, direction);


        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        this.session.saveOrUpdate(sorting);
        query.getSortings().add(sorting);
        this.session.saveOrUpdate(query);
        this.session.getTransaction().commit();

        // recalculate query hash
        String newQueryHash = this.calculateQueryHash(query);
        query.setQueryHash(newQueryHash);

        this.logger.info("new Hash: " + newQueryHash);

        this.session.beginTransaction();
        this.session.saveOrUpdate(query);
        this.session.getTransaction().commit();
        this.session.close();


    }
    
    /*
    * Store query persistently n database.
    * * * */
    public void persistQuery(Query query){
        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        this.session.saveOrUpdate(query);
        this.session.getTransaction().commit();
        this.session.close();
    }

    /*Add filter map to the query
    * * */
    public void addFilters(Query query, Map<String, String> filterMap){
        // Iterate over Filters
        // TODO: externalize in own method
        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        session.beginTransaction();
        int currentFilterSequence = -1;
        Long qID = query.getQueryId();
        this.logger.info("Query id = " + qID);
        // Get the max sequence number for the filters of query 
        Criteria cr = session.createCriteria(Filter.class);
        cr.setProjection(Projections.projectionList()
                //.add(Projections.groupProperty("query"))
                .add(Projections.max("filterSequence")));
        cr.add(Restrictions.eq("query.queryId", new Long(query.getQueryId())));
        Object unique = (Object) cr.uniqueResult();
        session.getTransaction().commit();
        session.close();

        if (unique == null) {
            this.logger.warning("No previous filter exists");
            currentFilterSequence = 0;
        } else {
            this.logger.info("Filter exists. Setting filter sequence");
            currentFilterSequence = (Integer) unique;

        }


        this.logger.info("The filter sequence number is currently: " + currentFilterSequence);

        int currentFilterCounter = 0;
        for (Map.Entry<String, String> entry : filterMap.entrySet()) {

            String filterName = entry.getKey();
            String filterValue = entry.getValue();
            Filter filter = new Filter(query, filterName, filterValue);

            if (this.checkIfFilterExists(query, filter)) {
                this.logger.info("Filter exists");
                filter = null;
            } else {
                currentFilterCounter++;
                filter.setFilterSequence(currentFilterSequence + currentFilterCounter);
                this.logger.info("new Filter persisted");
                query.getFilters().add(filter);
            }



        }

        // recalculate query hash
        String newQueryHash = this.calculateQueryHash(query);
        query.setQueryHash(newQueryHash);
        this.persistQuery(query);


        
    }

    /*add sorting map to the query 
    * * */
    public void addSortings(Query query, Map<String, String> sortingMap) {

        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        session.beginTransaction();
        int currentSortingSequence = -1;
        Long qID = query.getQueryId();
        this.logger.info("Query id = " + qID);

        // Get the max sequence number for the sortings of query
        Criteria cr = session.createCriteria(Sorting.class);
        cr.setProjection(Projections.projectionList()
                //.add(Projections.groupProperty("query"))
                .add(Projections.max("sortingSequence")));
        cr.add(Restrictions.eq("query.queryId", new Long(query.getQueryId())));
        Object unique = (Object) cr.uniqueResult();
        session.getTransaction().commit();
        session.close();

        if (unique == null) {
            this.logger.warning("No previous sorting exists");
            currentSortingSequence = 0;
        } else {
            this.logger.info("Sorting exists. Setting sorting sequence");
            currentSortingSequence = (Integer) unique;

        }


        this.logger.info("The sorting sequence number is currently: " + currentSortingSequence);

        int currentSortingCounter = 0;
        for (Map.Entry<String, String> entry : sortingMap.entrySet()) {
            currentSortingCounter++;
            String sortingName = entry.getKey();
            String sortingDir = entry.getValue();
            this.logger.info("Sorting direction: " + entry.getValue());
            Sorting sorting = new Sorting(query, sortingName, sortingDir);

            if (this.checkIfSortingExists(query, sorting)) {
                this.logger.info("Filter exists");
                sorting = null;
            } else {
                currentSortingCounter++;
                sorting.setSortingSequence(currentSortingSequence + currentSortingCounter);
                this.logger.info("new sorting persisted");
                query.getSortings().add(sorting);

            }


        }
        // recalculate query hash
        String newQueryHash = this.calculateQueryHash(query);
        query.setQueryHash(newQueryHash);
        this.persistQuery(query);


    }

    /* Check if a filter is already set.
    * * */
    private boolean checkIfFilterExists(Query query, Filter filter) {
        Session filterSession = HibernateUtilQueryStore.getSessionFactory().openSession();
        filterSession.beginTransaction();

        Long qID = query.getQueryId();


        // Get the max sequence number for the sortings of query
        Criteria cr = filterSession.createCriteria(Filter.class);

        cr.setProjection(Projections.projectionList()
                //.add(Projections.groupProperty("query"))
                .add(Projections.property("filterId")));
        cr.add(Restrictions.eq("query.queryId", new Long(query.getQueryId())));
        cr.add(Restrictions.eq("filterName", filter.getFilterName()));
        cr.add(Restrictions.eq("filterValue", filter.getFilterValue()));

        Object filterRecord = (Object) cr.uniqueResult();
        filterSession.getTransaction().commit();
        filterSession.close();
        if (filterRecord == null) {

            return false;
        } else {
            return true;
        }


    }

    /* Check if a filter is already set.
* * */
    private boolean checkIfSortingExists(Query query, Sorting sorting) {
        Session sortingSession = HibernateUtilQueryStore.getSessionFactory().openSession();
        sortingSession.beginTransaction();

        Long qID = query.getQueryId();


        // Get the max sequence number for the sortings of query
        Criteria cr = sortingSession.createCriteria(Sorting.class);

        cr.setProjection(Projections.projectionList()
                //.add(Projections.groupProperty("query"))
                .add(Projections.property("sortingId")));
        cr.add(Restrictions.eq("query.queryId", new Long(query.getQueryId())));
        cr.add(Restrictions.eq("sortingColumn", sorting.getSortingColumn()));
        cr.add(Restrictions.eq("direction", sorting.getDirection()));

        Object sortingResult = (Object) cr.uniqueResult();
        sortingSession.getTransaction().commit();
        sortingSession.close();
        if (sortingResult == null) {

            return false;
        } else {
            return true;
        }


    }





    /**
     * Calculate the hash of a String
     *
     * @param input
     * @return
     */
    private String calculateSHA1(String input) {
        if (input != null) {
            String hash = DigestUtils.sha1Hex(input);
            return hash;
        } else {
            return null;
        }
    }


    /**
     * Calculate the result set hash. This is currently a dummy method. Implement your own hashing scheme here.
     *
     * @param query
     */
    public String calculateResultSetHashFull(Query query) {

        //@Todo Replace this String with a real function
        // Calculates a random string for test purposes.

        ResultSetVerificationAPI resultSetVerification = new ResultSetVerificationAPI();
        String resultSetHash = resultSetVerification.calculateFullHashOfTheQuery(query.getQueryString());


        return resultSetHash;
    }

    /**
     * Calculate the result set hash. This is currently a dummy method. Implement your own hashing scheme here.
     *
     * @param query
     */
    public String calculateResultSetHashShort(Query query) {

        //@Todo Replace this String with a real function
        // Calculates a random string for test purposes.

        ResultSetVerificationAPI resultSetVerification = new ResultSetVerificationAPI();

        Map<Integer, String> selectedColumns = query.getSelectedColumns();

        String listOfConcatenatedColumns = "";
        for (Map.Entry<Integer, String> entry : selectedColumns.entrySet()) {
            listOfConcatenatedColumns += entry.getValue();

        }

        this.logger.info("The concatenated columns are: " + listOfConcatenatedColumns);

        String resultSetHash = resultSetVerification.calculateQuickHashOfTheQuery(this.generateQueryStringForShortHash(query), listOfConcatenatedColumns);


        return resultSetHash;
    }




    /*Check if the result set hash is not already stored
    * */
    public boolean persistResultSetHash(Query query, String resultSetHash) {
        Query resultSetQuery = this.getQueryByResultSetHash(resultSetHash);
        if (resultSetQuery == null) {
            session = HibernateUtilQueryStore.getSessionFactory().openSession();
            query.setResultSetHash(resultSetHash);
            this.logger.info("Result set hash: " + resultSetHash);

            session.beginTransaction();
            session.saveOrUpdate(query);
            session.getTransaction().commit();
            session.close();

            return true;
        } else {
            this.logger.severe("The same resultset hash already exists. The PID of this query is " + resultSetQuery.getPID());

            return false;
        }


    }

    /*Return a query with a given result set hash.
    * */
    public Query getQueryByResultSetHash(String resultSetHash) {
        Session session = HibernateUtilQueryStore.getSessionFactory().openSession();
        session.beginTransaction();
        // Get the max sequence number for the sortings of query
        Criteria cr = session.createCriteria(Query.class);


        cr.add(Restrictions.eq("resultSetHash", resultSetHash));
        Query resultSetQuery = (Query) cr.uniqueResult();
        session.getTransaction().commit();
        session.close();
        return resultSetQuery;
    }

    /**
     * Returns the hash of the query. This hash only considers query filters, sortings and the data source for its
     * computation. Not to be confused with the result set hash!
     *
     * @param q
     * @return
     */
    public String getQueryHash(Query q) {
        return q.getQueryHash();

    }

    /*Delete query by hash.
    * */
    public boolean deleteQuery(Query q) {
        Session session = HibernateUtilQueryStore.getSessionFactory().openSession();
        session.beginTransaction();
        session.delete(q);
        session.getTransaction().commit();
        session.close();

        if (this.getQueryByResultSetHash(q.getResultSetHash()) == null) {
            this.logger.info("Query deleted");
            return true;
        } else {
            this.logger.severe("Query NOT deleted");
            return false;
        }


    }


    /**
     * Retrieve the query by specifying its PID
     *
     * @param pid
     * @return
     */
    public Query getQueryByPID(String pid) {
        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();

        Query query = null;
        Criteria criteria = this.session.createCriteria(Query.class, "query");
        criteria.add(Restrictions.like("query.PID", pid));
        query = (Query) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if (query == null) {
            this.logger.severe("No query found with the pid " + pid);
        } else {
            this.logger.info("Found query with pid: " + pid);
        }

        return query;

    }


    /**
     * Get the result set hash
     *
     * @param q
     * @return
     */
    public String getQueryResultSetHash(Query q) {


        String resultSetHash = q.getResultSetHash();
        return resultSetHash;


    }

    /**
     * Return the PID of a query
     *
     * @param query
     * @return
     */
    public String getQueryPID(Query query) {
        String pid = query.getPID();
        this.logger.info("PID = " + pid);
        return pid;
    }


    /**
     * Finalizes the query. If no identical query was found, the query receives a new PID.
     * Otherweise a warning is shown, upon which the user may react.
     * The method uses the following int codes as return value to indicate the status:
     * 0 -- no identical query detected
     * 1 -- identical query detected
     *
     * @param query
     */
    public int finalizeQuery(Query query) {


        
        String querString = this.generateQueryString(query);
        query.setQueryString(querString);
        this.persistQuery(query);

        boolean queryIsUnique = this.checkQueryUniqueness(query);


        if (queryIsUnique == false) {
            this.logger.severe("There was a identical query. This could be a new version!");
            return 1;
        } else {
            this.logger.info("No version detected. OK");
            return 0;
        }


    }

    private boolean checkQueryUniqueness(Query query) {
        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        Query sameQuery = null;
        Criteria criteria = this.session.createCriteria(Query.class, "query");
        criteria.add(Restrictions.like("query.queryHash", query.getQueryHash()));
        criteria.add(Restrictions.not(Restrictions.like("query.queryId", query.getQueryId())));
        sameQuery = (Query) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if (sameQuery == null) {
            return true;
        } else {
            return false;
        }

    }

    /*
    * Generate the string from the persisted query.
    * *
    * * * */
    public String generateQueryString(Query query) {

        List<Filter> filterSet = query.getFilters();
        List<Sorting> sortingsSet = query.getSortings();
        DatabaseTools dbTools = new DatabaseTools();



        String fromString = query.getBaseTable().getBaseTableName();

        List<String> primaryKeyList = dbTools.getPrimaryKeyFromTable(fromString);
        this.logger.info("Primary key list size: " + primaryKeyList.size());
        String primaryKey = primaryKeyList.get(0);


        String sqlString = "SELECT ";
        Map<Integer, String> selectedColumns = query.getSelectedColumns();

        for (Map.Entry<Integer, String> entry : selectedColumns.entrySet()) {
            String columnName = entry.getValue();
            sqlString += "`outerGroup`.`" + columnName + "`,";
        }

        // remove last comma from string
        if (sqlString.endsWith(",")) {
            sqlString = sqlString.substring(0, sqlString.length() - 1);
        }

        sqlString += " FROM " + fromString;

        // inner join 

        sqlString += "  AS outerGroup INNER JOIN " +
                "    (SELECT " + primaryKey + ", max(LAST_UPDATE) AS mostRecent " +
                "    FROM " +
                query.getBaseTable().getBaseTableName() +
                " AS innerSELECT " +
                "    WHERE " +
                "        (innerSELECT.RECORD_STATUS = 'inserted' " +
                "            OR innerSELECT.RECORD_STATUS = 'updated'" + " AND innerSELECT.LAST_UPDATE<=\""
                + this.convertJavaDateToMySQLTimeStamp(query.getExecution_timestamp()) + "\") GROUP BY " + primaryKey + ") innerGroup ON outerGroup." + primaryKey + " = innerGroup." + primaryKey + " " +
                "        AND outerGroup.LAST_UPDATE = innerGroup.mostRecent ";

        if (filterSet.size() > 0) {
            String whereString = " WHERE ";
            int filterCounter = 0;
            for (Filter currentFilter : filterSet) {
                filterCounter++;
                if (filterCounter == 1) {
                    whereString += "UPPER(`outerGroup`.`" + currentFilter.getFilterName() + "`) LIKE UPPER('%" +
                            currentFilter.getFilterValue() + "%') ";
                } else {
                    whereString += " AND UPPER(`outerGroup`.`" + currentFilter.getFilterName() + "`) LIKE UPPER('%" +
                            currentFilter.getFilterValue() + "%') ";

                }
                
            }

            sqlString += whereString;
        }
        if (sortingsSet.size() > 0) {
            String sortingString = " ORDER BY ";
            for (Sorting currentSorting : sortingsSet) {

                sortingString += "`outerGroup`.`" + currentSorting.getSortingColumn() + "` " + currentSorting
                        .getDirection() + ",";

            }
            if (sortingString.endsWith(",")) {
                sortingString = sortingString.substring(0, sortingString.length() - 1);

            }

            sqlString += sortingString;
        }


        this.logger.info(sqlString);

        return sqlString;


    }

    /**
     * only retrieve the internal counter column
     */
    public String generateQueryStringForShortHash(Query query) {

        List<Filter> filterSet = query.getFilters();
        List<Sorting> sortingsSet = query.getSortings();
        DatabaseTools dbTools = new DatabaseTools();


        String fromString = query.getBaseTable().getBaseTableName();

        List<String> primaryKeyList = dbTools.getPrimaryKeyFromTable(fromString);
        this.logger.info("Primary key list size: " + primaryKeyList.size());
        String primaryKey = primaryKeyList.get(0);


        String sqlString = "SELECT ";
        sqlString += sqlString = "`outerGroup`.`ID_SYSTEM_SEQUENCE`";


        sqlString += " FROM " + fromString;

        // inner join

        sqlString += "  AS outerGroup INNER JOIN " +
                "    (SELECT " + primaryKey + ", max(LAST_UPDATE) AS mostRecent " +
                "    FROM " +
                query.getBaseTable().getBaseTableName() +
                " AS innerSELECT " +
                "    WHERE " +
                "        (innerSELECT.RECORD_STATUS = 'inserted' " +
                "            OR innerSELECT.RECORD_STATUS = 'updated'" + " AND innerSELECT.LAST_UPDATE<=\""
                + this.convertJavaDateToMySQLTimeStamp(query.getExecution_timestamp()) + "\") GROUP BY " + primaryKey + ") innerGroup ON outerGroup." + primaryKey + " = innerGroup." + primaryKey + " " +
                "        AND outerGroup.LAST_UPDATE = innerGroup.mostRecent ";

        if (filterSet.size() > 0) {
            String whereString = " WHERE ";
            int filterCounter = 0;
            for (Filter currentFilter : filterSet) {
                filterCounter++;
                if (filterCounter == 1) {
                    whereString += "UPPER(`outerGroup`.`" + currentFilter.getFilterName() + "`) LIKE UPPER('%" +
                            currentFilter.getFilterValue() + "%') ";
                } else {
                    whereString += " AND UPPER(`outerGroup`.`" + currentFilter.getFilterName() + "`) LIKE UPPER('%" +
                            currentFilter.getFilterValue() + "%') ";

                }

            }

            sqlString += whereString;
        }
        if (sortingsSet.size() > 0) {
            String sortingString = " ORDER BY ";
            for (Sorting currentSorting : sortingsSet) {

                sortingString += "`outerGroup`.`" + currentSorting.getSortingColumn() + "` " + currentSorting
                        .getDirection() + ",";

            }
            if (sortingString.endsWith(",")) {
                sortingString = sortingString.substring(0, sortingString.length() - 1);

            }

            sqlString += sortingString;
        }


        this.logger.info(sqlString);

        return sqlString;


    }

    public Date updateExecutiontime(Query query) {
        Date currentDate = new Date();
        query.setExecution_timestamp(currentDate);
        this.logger.info("Updated execition timestamp: " + currentDate);
        this.persistQuery(query);
        return currentDate;

    }

    protected java.sql.Timestamp convertJavaDateToMySQLTimeStamp(java.util.Date utilDate) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(utilDate);
        cal.set(Calendar.MILLISECOND, 0);
        return new java.sql.Timestamp(utilDate.getTime());
    }
    
    /* Store the table metadata
    * * */
    public String createBaseTableRecord(String author, String baseSchema, String tableName, String title, String description, int
            prefix, String pidURL) {

 
        
        PersistentIdentifierAPI pidApi= new PersistentIdentifierAPI();
        Organization org = pidApi.getOrganizationObjectByPrefix(prefix);


        PersistentIdentifierAlphaNumeric pid = pidApi.getAlphaNumericPID(org, pidURL);



        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();


        BaseTable baseTable = new BaseTable();
        baseTable.setBaseTableName(tableName);
        baseTable.setBaseTablePID(pid.getFQNidentifier());

        String newURL = pidURL + baseTable.getBaseTablePID();
        this.logger.info("The new url is: " + newURL);
        pidApi.updateURI(pid.getIdentifier(), newURL);


        baseTable.setAuthor(author);
        baseTable.setBaseDatabase(baseSchema);
        baseTable.setDescription(description);
        baseTable.setOrganizationalId(prefix);
        baseTable.setDataSetTitle(title);
        Date currentDate = new Date();

        baseTable.setUploadDate(currentDate);
        baseTable.setLastUpdate(currentDate);
        
        DatabaseTools dbTools = new DatabaseTools();
        int numberOfActiveRecords = dbTools.getNumberOfActiveRecords(tableName);
        baseTable.setNumberOfActiveRecords(numberOfActiveRecords);

        this.session.saveOrUpdate(baseTable);
        this.session.getTransaction().commit();
        this.session.close();

        this.logger.info("Base table persisted");

        return baseTable.getBaseTablePID();
        
        
        
        
        
    }
    


    /*
    * Get base table by PID
    * **/
    public BaseTable getBaseTableByPID(String pid) {
        BaseTable baseTable = null;


        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();

        this.logger.info("Searching for base Table Pid: " + pid);
        Criteria cr = this.session.createCriteria(BaseTable.class);
        cr.add(Restrictions.like("baseTablePID", new String(pid)));
        baseTable = (BaseTable) cr.uniqueResult();

        this.session.getTransaction().commit();
        this.session.close();


        if (baseTable != null) {
            this.logger.info("Base Table found " + pid);
            return baseTable;
        } else {
            this.logger.severe("BaseTable NOT found " + pid);
            return null;
        }


    }

    /*
   * Get base table by PID
   * **/
    public BaseTable getBaseTableByDatabaseAndTableName(String databaseName, String tableName) {
        BaseTable baseTable = null;
        this.logger.info("Looking for base table: " + tableName + " in database " + databaseName);


        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        // Get the max sequence number for the sortings of query
        Criteria cr = this.session.createCriteria(BaseTable.class);


        cr.add(Restrictions.eq("baseTableName", tableName));
        cr.add(Restrictions.eq("baseDatabase", databaseName));
        baseTable = (BaseTable) cr.uniqueResult();

        this.session.getTransaction().commit();
        this.session.close();


        if (baseTable != null) {
            this.logger.info("Base Table found " + baseTable.getBaseTablePID());
            return baseTable;
        } else {
            this.logger.severe("BaseTable NOT found");
            return null;
        }


    }

    public BaseTable getBaseTableByTableNameOnly(String tableName) {
        BaseTable baseTable = null;


        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        // Get the max sequence number for the sortings of query
        Criteria cr = this.session.createCriteria(BaseTable.class);


        cr.add(Restrictions.eq("baseTableName", tableName));
        baseTable = (BaseTable) cr.uniqueResult();

        this.session.getTransaction().commit();
        this.session.close();


        if (baseTable != null) {
            this.logger.info("Base Table found " + baseTable.getBaseTablePID());
            return baseTable;
        } else {
            this.logger.severe("BaseTable NOT found");
            return null;
        }


    }

    /*
    * Update the the last update date field of the base table in order to detect changed data
    * */
    public void updateBaseTableLastUpdateDate(String tableName) {
        BaseTable baseTable = this.getBaseTableByTableNameOnly(tableName);

        if (baseTable != null) {

            this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
            this.session.beginTransaction();

            baseTable.setLastUpdate(new Date());

            this.session.saveOrUpdate(baseTable);
            this.session.getTransaction().commit();
            this.session.close();
        }


    }

    /*
    * The base table can be updated. When the base table was updated after a query was executed, this method returns true.
    * */
    public boolean checkIfBaseTableWasUpdatedMeanwhile(Query query, BaseTable baseTable) {
        if (query.getExecution_timestamp().before(baseTable.getLastUpdate())) {
            this.logger.info("The base table record is newer than the query");
            return true;

        } else {
            return false;
        }


    }


    /*
    * Query the database for all base tables
    * */
    public Map<String, String> getAvailableBaseTables() {

        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        // Get the max sequence number for the sortings of query
        Criteria cr = this.session.createCriteria(BaseTable.class);

        ProjectionList proList = Projections.projectionList();
        proList.add(Projections.property("baseTableName"));
        proList.add(Projections.property("baseTablePID"));
        cr.setProjection(proList);
        List baseTableObjects = cr.list();


        this.session.getTransaction().commit();
        this.session.close();


        Map<String, String> availableBaseTables = new HashMap<String, String>();

        for (Iterator it = baseTableObjects.iterator(); it.hasNext(); ) {
            Object[] row = (Object[]) it.next();

            for (int i = 0; i < row.length; i++) {
                availableBaseTables.put(row[0].toString(), row[1].toString());
            }

        }

        return availableBaseTables;


    }

    /*
    * Query the database for all base tables
    * */
    public Map<String, String> getAvailableSubsetsFromBase(String baseTableName) {
        BaseTable baseTable = null;
        Map<String, String> availableSubsets = new HashMap<String, String>();

        this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
        this.session.beginTransaction();
        // Get the max sequence number for the sortings of query
        Criteria cr = this.session.createCriteria(BaseTable.class);
        cr.add(Restrictions.eq("baseTableName", baseTableName));

        baseTable = (BaseTable) cr.uniqueResult();

        this.session.getTransaction().commit();
        this.session.close();

        if (baseTable == null) {
            this.logger.warning("Base table NOT found: " + baseTableName);

        } else {

            this.session = HibernateUtilQueryStore.getSessionFactory().openSession();
            this.session.beginTransaction();
            // get all queries with the given base table


            this.logger.info("Base table id : " + baseTable.getBaseTableId());

            Criteria criteria = this.session.createCriteria(Query.class, "q");
            criteria.add(Restrictions.eq("q.baseTable", baseTable));
            List<Object[]> list = (List<Object[]>) criteria.list();





            this.session.getTransaction().commit();
            this.session.close();


            for (Object queryObj : list) {
                Query query = (Query) queryObj;
                availableSubsets.put(query.getPID(), query.getExecution_timestamp().toString());
            }


            this.logger.info("Found " + availableSubsets.size() + " subsets");


        }


        return availableSubsets;


    }

    /*
* Method returns all rows from one table for specific date based om query
* */
    public String getParentUnfilteredStringFromQuery(BaseTable baseTable, Date queryDate) {


        DatabaseTools dbTools = new DatabaseTools();


        String baseTableName = baseTable.getBaseTableName();

        List<String> primaryKeyList = dbTools.getPrimaryKeyFromTable(baseTableName);
        this.logger.info("Primary key list size: " + primaryKeyList.size());
        String primaryKey = primaryKeyList.get(0);

        Map<String, String> columnsMap = dbTools.getColumnNamesFromTableWithoutMetadataColumns(baseTableName);


        String sqlString = "SELECT ";


        for (Map.Entry<String, String> entry : columnsMap.entrySet()) {
            String columnName = entry.getKey();
            sqlString += "`outerGroup`.`" + columnName + "`,";
        }

        // remove last comma from string
        if (sqlString.endsWith(",")) {
            sqlString = sqlString.substring(0, sqlString.length() - 1);
        }

        sqlString += " FROM " + baseTableName;

        // inner join

        sqlString += "  AS outerGroup INNER JOIN " +
                "    (SELECT " + primaryKey + ", max(LAST_UPDATE) AS mostRecent " +
                "    FROM " +
                baseTable.getBaseTableName() +
                " AS innerSELECT " +
                "    WHERE " +
                "        (innerSELECT.RECORD_STATUS = 'inserted' " +
                "            OR innerSELECT.RECORD_STATUS = 'updated'" + " AND innerSELECT.LAST_UPDATE<=\""
                + this.convertJavaDateToMySQLTimeStamp(queryDate) + "\") GROUP BY " + primaryKey + ") innerGroup ON outerGroup." + primaryKey + " = innerGroup." + primaryKey + " " +
                "        AND outerGroup.LAST_UPDATE = innerGroup.mostRecent ORDER BY outerGroup.ID_SYSTEM_SEQUENCE";


        this.logger.info(sqlString);

        return sqlString;

    }




}






