package QueryStore;


import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;
import org.apache.commons.codec.digest.DigestUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.Iterator;
import java.util.Set;
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

    public Query createNewQuery(String userName, String queryDescription, String pidDataSourceString, String pidString){
        Query query = new Query();
        query.setUserName(userName);
        query.setQueryDescription(queryDescription);


        query.setPID(pidString);
        query.setDatasourcePID(pidDataSourceString);
        // the query is not yet persisted, hence we need a dummy hash
        query.setQueryHash("TEMPORAL HASH");
        // Store query persistently in database
        this.session = HibernateUtil.getSessionFactory().openSession();
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
     * Iterate over filters and sortings and calculate a unique hash. Sortings and Filterings are stored in LinkedHashSets.
     * Their insertion order is preserved. If a query uses a different order of insertion, then the hash will
     * be different. If this behaviour should be changed, use a TreeSet and sort it alphabetically.
     * @param query
     * @return
     */
    private String calculateQueryHash(Query query){
        this.logger.info("Calculate Hash");

        // concatenate all query details: data source pid + filters + sortings
        String queryDetails = "";
        String allFilters = "";
        String allSortings = "";

        // Append data source PID
        queryDetails = query.getDatasourcePID();

        // Iterate over all filters, concatenate their keys and values and normalize the string.
        Set<Filter> filters = query.getFilters();
        System.out.println("Filter size = "+filters.size());
        if(filters.size()>0){
            Iterator<Filter> filterIterator= filters.iterator();
            while(filterIterator.hasNext()){
                Filter filter = (Filter) (filterIterator.next());
                this.logger.info("Filter: " + filter.getFilterName() + " - " +filter.getFilterValue());

                allFilters+= filter.getFilterName()+filter.getFilterValue();
            }

            allFilters= this.normalizeString(allFilters);
            this.logger.info("Found " + filters.size() + " filters, which are " + allFilters);

        }
        // append all filters
        queryDetails+=allFilters;


        Set<Sorting> sortings = query.getSortings();
        if(sortings.size()>0){
            Iterator<Sorting> sortingIterator= sortings.iterator();
            while(sortingIterator.hasNext()){
                Sorting sorting = (Sorting) (sortingIterator.next());
                allSortings+= sorting.getSorting_column()+sorting.getDirection();
            }

            allSortings= this.normalizeString(allSortings);
            this.logger.info("Found " + sortings.size() + " filters, which are " + allSortings);

        }

        // append all sortings
        queryDetails+=allSortings;
        this.logger.info("Query details String: " + queryDetails);



        // Calculate the hash of the text
        String uniqueQueryHash = this.calculateSHA1(queryDetails);
        this.logger.info("Query details HASH: " + uniqueQueryHash);

        return uniqueQueryHash;


    }

    /**
     * Remove all spaces from a string and convert to lower case letters.
     * @param inputString
     * @return
     */
    private String normalizeString(String inputString){
        if(inputString.equals("")){
           return "";
        }else{
            return inputString.trim().toLowerCase();
        }



    }


    /**
     * Add a new filter to the query. Recomputes the query hash.
     * @param query
     * @param key
     * @param value
     */
    public void addFilter(Query query, String key, String value){
        // create new filter and persist it
        Filter filter = new Filter(query,key,value);

        this.session = HibernateUtil.getSessionFactory().openSession();
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
     * @param query
     * @param column
     * @param direction
     */
    public void addSorting(Query query, String column, String direction){
        // create new filter and persist it

        Sorting sorting = new Sorting(query, column, direction);



        this.session = HibernateUtil.getSessionFactory().openSession();
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



    /**
     * Calculate the hash of a String
     * @param input
     * @return
     */
    private String calculateSHA1(String input){
        String hash = DigestUtils.sha1Hex(input);
        return hash;
    }


    /**
     * Calculate the result set hash. This is currently a dummy method. Implement your own hashing scheme here.
     * @param query
     */
    public void calculateResultSetHash(Query query){

        //@Todo Replace this String with a real function
        // Calculates a random string for test purposes.
        String hash = this.calculateSHA1("THIS IS A DUMMY HASH" + Helpers.getRandomAlpaNumericString(5));
        query.setResultSetHash(hash);
        this.logger.info("Result set hash: " + hash);
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        this.session.saveOrUpdate(query);
        this.session.getTransaction().commit();
        this.session.close();

    }

    /**
     * Returns the hash of the query. This hash only considers query filters, sortings and the data source for its
     * computation. Not to be confused with the result set hash!
     * @param q
     * @return
     */
    public String getQueryHash(Query q){
        return q.getResultSetHash();

    }

    /**
     * Retrieve the query by specifying its PID
     * @param pid
     * @return
     */
    public Query getQueryByPID(String pid){
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();

        Query query = null;
        Criteria criteria = this.session.createCriteria(Query.class, "query");
        criteria.add(Restrictions.like("query.PID", pid));
        query = (Query) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if(query == null){
            this.logger.severe("No query found with the pid " + pid);
        }

        return query;

    }


    /**
     * Get the result set hash
     * @param q
     * @return
     */
    public String getQueryResultSetHash(Query q){
        String resultSetHash = q.getResultSetHash();
        return resultSetHash;


    }

    /**
     * Return the PID of a query
     * @param query
     * @return
     */
    public String getQueryPID(Query query){
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
     * @param query
     */
    public int finalizeQuery(Query query){
        this.session = HibernateUtil.getSessionFactory().openSession();
        this.session.beginTransaction();
        Query sameQuery = null;
        Criteria criteria = this.session.createCriteria(Query.class, "query");
        criteria.add(Restrictions.like("query.queryHash", query.getQueryHash()));
        criteria.add(Restrictions.not(Restrictions.like("query.queryId", query.getQueryId())));
        query = (Query) criteria.uniqueResult();
        this.session.getTransaction().commit();
        this.session.close();

        if(query!= null){
            this.logger.severe("There was a identical query. This could be a new version!");
            return 1;
        } else{
            this.logger.info("No version detected. OK");
            return 0;
        }


    }
}





