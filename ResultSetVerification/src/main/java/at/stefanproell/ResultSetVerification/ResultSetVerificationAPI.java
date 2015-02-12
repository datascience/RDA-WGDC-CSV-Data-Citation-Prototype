package at.stefanproell.ResultSetVerification;

import Database.DatabaseOperations.HikariConnectionPool;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class ResultSetVerificationAPI {
    private Logger logger;
    private static final String DEFAULT_HASH_ALGORITHM = "SHA-1";
    private MessageDigest crypto = null;





    public ResultSetVerificationAPI() {
        this.logger = Logger.getLogger(ResultSetVerificationAPI.class.getName());

        // Initialize Crypto
        this.initCryptoModule(DEFAULT_HASH_ALGORITHM);

    }





    private MessageDigest initCryptoModule(String algorithm) {

        HashSet<String> algorithms = new HashSet<String>();
        algorithms.add("SHA-1");
        algorithms.add("MD5");
        algorithms.add("SHA-256");
        if (algorithms.contains(algorithm)) {
            this.crypto = null;


            try {
                this.crypto = MessageDigest.getInstance(algorithm);
                this.crypto.reset();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }


        }
        return this.crypto;
    }



    /**
     * Calculate SHA1 hash from input
     *
     * @param inputString
     * @return
     * @throws NoSuchAlgorithmException
     */
    public String calculateHashFromString(String inputString) {
        try {
            this.crypto.update(inputString.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String hash = DigestUtils.sha1Hex(this.crypto.digest());
            return hash;



    }


    /**
     * Execute the query provided from the querstore.
     *
     * @param sqlString
     * @return
     */
    public ResultSet executeQuery(String sqlString) {
        this.logger.info("Trying to execute: " + sqlString);

        Connection connection = this.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(sqlString);
            preparedStatement.setFetchSize(10000);


            rs = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            this.logger.info("Resulset row count: " + this.getResultSetRowCount(rs));

            return rs;

        }

    }

    /*
    * Rexecute query and retrieve a list of the sorted sequence numbers
    * */
    public List<Integer> getSortedSequenceListFromQuery(String sqlString) {
        this.logger.info("Trying to execute: " + sqlString);
        List<Integer> sortedIntegerList = new LinkedList<Integer>();

        Connection connection = this.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(sqlString);
            preparedStatement.setFetchSize(10000);
            rs = preparedStatement.executeQuery();


            while (rs.next()) {
                int seq = rs.getInt("ID_SYSTEM_SEQUENCE");
                sortedIntegerList.add(seq);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }


            return sortedIntegerList;

        }

    }

    public int getResultSetRowCount(ResultSet rs) {
        int rows = 0;
        try {
            if (rs.last()) {
                rows = rs.getRow();
                // Move to beginning
                rs.beforeFirst();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.logger.info("Returned rows: " + rows);
        return rows;

    }


    /*Calculate the full hash

    * */
    public String calculateFullHashOfTheQuery(String sqlQuery) {
        ResultSet rs = this.executeQuery(sqlQuery);
        String resultSetHash = this.retrieveFullResultSet(rs);
        return resultSetHash;

    }

    /*
    * Only calculate the the shorter hashing version
    *
    * */
    public String calculateQuickHashOfTheQuery(String sqlQuery, String concatenatedColumns) {
        this.logger.info("SQL String for Hashing: " + sqlQuery);

        //ResultSet rs = this.executeQuery(sqlQuery);
        List<Integer> sortedSequenceList = this.getSortedSequenceListFromQuery(sqlQuery);

        //@todo here weiter


        String resultSetHash = this.calculateResultSetHashFromSortedSequenceList(sortedSequenceList, concatenatedColumns);
        return resultSetHash;

    }


    /**
     * Calculate hash on DB
     *
     * @return
     */
    public String retrieveFullResultSet(ResultSet rs) {



            this.logger.info("Resulset row count: " + this.getResultSetRowCount(rs));


            String resultSetHash = "";
            String currentHash = "";
            String previousKey = "";
            String compositeHash = "";
            int hashCounter = 0;

            long startTime = System.currentTimeMillis();
            //int hashCounter =0;


            try {

                ResultSetMetaData rsmd = rs.getMetaData();
                int columnsNumber = rsmd.getColumnCount();
                this.logger.info("There are " + columnsNumber + " columns in the result set");
                String newResultSetHash = null;
                long meanTimeStart = System.currentTimeMillis();

                rs.setFetchSize(1000);


                while (rs.next()) {
                    hashCounter++;
                    if (hashCounter % 1000 == 0) {
                        long meanTimeStop = System.currentTimeMillis();

                        this.logger.warning("Calculated " + hashCounter + " hashes so far. This batch took " +
                                (double) (
                                (meanTimeStop - meanTimeStart) / 1000) + " seconds");

                        meanTimeStart = System.currentTimeMillis();
                    }
                    for (int i = 1; i < columnsNumber; i++) {
                        currentHash += rs.getString(i);
                    }


                    if (rs.isFirst()) {

                        resultSetHash = this.calculateHashFromString(currentHash);

                    } else {

                        compositeHash = (resultSetHash + currentHash);

                        // reset the variables in order to reduce overhead
                        resultSetHash = null;
                        currentHash= null;
                        newResultSetHash = this.calculateHashFromString(compositeHash);
                        //this.logger.info("[resultSetHash] "+resultSetHash + "[currentHash] " + currentHash +" ->
                        // [newResultSetHash]" + newResultSetHash );
                        resultSetHash = newResultSetHash;


                    }
                    System.gc();
                }

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            double elapsedTime = (double) (totalTime / 1000);


            this.logger.info("Calculated " + hashCounter + " hash values in " + elapsedTime + " sec");
            this.logger.info("Hash is " + resultSetHash);
            return resultSetHash;

    }

    /*
     concat selected columns and append sequence if ids. calculate hash thereof. Slow method
    *
    * */
    public String calculateResultSetHashShort(ResultSet rs, String concatenatedColumns) {

        //@todo hashing


        this.logger.info("Resulset row count: " + this.getResultSetRowCount(rs));


        String resultSetHash = concatenatedColumns;
        String concatenatedIdentifiers = "";


        String currentHash = "";
        String previousKey = "";
        String compositeHash = "";
        int hashCounter = 0;

        long startTime = System.currentTimeMillis();
        //int hashCounter =0;


        try {

            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            this.logger.info("There are " + columnsNumber + " columns in the result set");

            long meanTimeStart = System.currentTimeMillis();

            rs.setFetchSize(10000);


            while (rs.next()) {
                hashCounter++;
                if (hashCounter % 1000 == 0) {
                    long meanTimeStop = System.currentTimeMillis();

                    this.logger.warning("Calculated " + hashCounter + " hashes so far. This batch took " +
                            (double) (
                                    (meanTimeStop - meanTimeStart) / 1000) + " seconds");

                    meanTimeStart = System.currentTimeMillis();
                }


                concatenatedIdentifiers += rs.getString(1);
                //this.logger.info("At row " + hashCounter +" the hash currently has the length of: " + concatenatedIdentifiers.length());

                System.gc();
            }

            resultSetHash += concatenatedIdentifiers;
            this.logger.info("The hash has the length of: " + concatenatedIdentifiers.length());
            resultSetHash = this.calculateHashFromString(resultSetHash);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double elapsedTime = (double) (totalTime / 1000);


        this.logger.info("Calculated " + hashCounter + " hash values in " + elapsedTime + " sec");
        this.logger.info("Hash is " + resultSetHash);
        return resultSetHash;

    }


    /*
    * Iterates over the sorted list of strings and calculates a hash.
    * */
    public String calculateResultSetHashFromSortedSequenceList(List<Integer> sortedSequenceList, String concatenatedColumns) {

        //@todo hashing


        String resultSetHash = concatenatedColumns;
        String concatenatedIdentifiers = "";
        int hashCounter = 0;
        int rowCount = sortedSequenceList.size();

        long startTime = System.currentTimeMillis();
        //int hashCounter =0;

        for (int i = 0; i < rowCount; i++) {
            concatenatedIdentifiers += sortedSequenceList.get(i);

        }

        resultSetHash += concatenatedIdentifiers;
        this.logger.info("The hash has the length of: " + concatenatedIdentifiers.length());
        resultSetHash = this.calculateHashFromString(resultSetHash);


        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double elapsedTime = (double) (totalTime / 1000);


        this.logger.info("Calculated " + hashCounter + " hash values in " + elapsedTime + " sec");
        this.logger.info("Hash is " + resultSetHash);
        return resultSetHash;

    }





    /**
     * Get the connection from the connection pool
     *
     * @return
     */
    private Connection getConnection() {
        HikariConnectionPool pool = HikariConnectionPool.getInstance();
        Connection connection = null;

        try {
            connection = pool.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;

    }
}
