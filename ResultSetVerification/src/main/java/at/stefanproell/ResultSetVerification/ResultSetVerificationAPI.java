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

package at.stefanproell.ResultSetVerification;

import Database.DatabaseOperations.HikariConnectionPool;
import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.codec.digest.DigestUtils;

import javax.sql.rowset.CachedRowSet;
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


    /**
     * Get row count from table name
     *
     * @param tableName
     * @return
     * @throws java.sql.SQLException
     */
    public int getRowCount(String tableName) throws SQLException {
        // TODO SQL injection
        String sql = "SELECT COUNT(*) FROM " + tableName;
        int numberOfRecords = -1;
        Connection connection = this.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            numberOfRecords = rs.getInt(1);
        }

        this.logger.warning("Row Set Size:  " + numberOfRecords);
        connection.close();

        return numberOfRecords;
    }



    public ResultSetVerificationAPI() {
        this.logger = Logger.getLogger(ResultSetVerificationAPI.class.getName());

        // Initialize Crypto
        this.initCryptoModule(DEFAULT_HASH_ALGORITHM);

    }

    /**
     * Get a map of <ColumnName, ColumnType>
     */
    public Map<String, String> getTableColumnMetadata(String tableName)
            throws SQLException {
        Connection connection = this.getConnection();
        DatabaseMetaData meta = connection.getMetaData();
        CachedRowSetImpl cachedResultSet = new CachedRowSetImpl();
        String catalog = null;
        String schemaPattern = null;
        String tableNamePattern = tableName;
        this.logger.warning("Getting metadata for table: " + tableName);
        String columnNamePattern = null;

        ResultSet result = meta.getColumns(catalog, schemaPattern,
                tableNamePattern, columnNamePattern);
        System.out.println(result.getFetchSize());
        cachedResultSet.populate(result);
        connection.close();

        Map<String, String> columnMetadataMap = new LinkedHashMap<String, String>();
        while (cachedResultSet.next()) {


            // ColumnName
            String columnName = cachedResultSet.getString(4);

            // ColumnType
            String columnType = cachedResultSet.getString(6);

            columnMetadataMap.put(columnName, columnType);

        }

        return columnMetadataMap;

    }

    /**
     * Returns a list of all column names
     *
     * @param tableName
     * @return
     */
    public List<String> getListOfColumnNames(String tableName) {
        Map<String, String> columnMetadataMap = null;
        try {
            columnMetadataMap = this.getTableColumnMetadata(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<String>(columnMetadataMap.keySet());

    }

    /**
     * Get the number of columns in a table
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public int getNumberofColumnsPerTable(String tableName) throws SQLException {
        Connection connection = this.getConnection();
        int columncCount = 0;
        DatabaseMetaData meta = connection.getMetaData();
        String catalog = null;
        String schemaPattern = null;
        String tableNamePattern = tableName;
        String columnNamePattern = null;

        ResultSet result = meta.getColumns(catalog, schemaPattern,
                tableNamePattern, columnNamePattern);

        Map<String, String> columnMetadataMap = new HashMap<String, String>();
        while (result.next()) {
            columncCount++;

        }

        connection.close();
        return columncCount;

    }


    /**
     * Calculate the CRC checksum of a complete table. Return a SH1 hash of the appended CRC string
     *
     * @param tableName
     * @return
     */
    public String calculateCRCofTable(String tableName) {
        String appendedChecksum = "";
        String hashedCRC = "";

        HikariConnectionPool pool = HikariConnectionPool.getInstance();

        String currentDatabaseName = pool.getDataBaseName();
        List<String> listOfColumns = this.getListOfColumnNames(tableName);
        String sql = "SELECT ";
        String prefix = "sum(crc32(" + currentDatabaseName + "." + tableName + ".";
        String suffix = "))";
        String checkSumSQLString = Helpers.commaSeparatedStringWithPrefixAndSuffix(listOfColumns, prefix, suffix);
        sql += checkSumSQLString;
        sql += " FROM " + currentDatabaseName + "." + tableName;
        String primaryKeysString = Helpers.commaSeparatedString(this.getPrimaryKeyFromTable(tableName));
        sql += " ORDER BY " + primaryKeysString;
        this.logger.info(sql);

        Connection connection = this.getConnection();

        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    appendedChecksum += rs.getString(i);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.logger.info(appendedChecksum);

        hashedCRC = this.calculateHashFromString(appendedChecksum);

        this.logger.info("Hashed checksum " + hashedCRC);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashedCRC;
    }

    /**
     * Get the primary key from the table
     * MySQL Specific!!
     *
     * @return
     */
    public List<String> getPrimaryKeyFromTable(String tableName) {
        List<String> primaryKeys;
        String primaryKey;
        primaryKeys = new ArrayList<String>();

        HikariConnectionPool pool = HikariConnectionPool.getInstance();

        String currentDatabaseName = pool.getDataBaseName();
        String sql = "SHOW KEYS FROM " + currentDatabaseName + "." + tableName + " WHERE Key_name = 'PRIMARY'";
        int numberOfRecords = -1;
        Connection connection = this.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                primaryKey = rs.getString("Column_name");
                primaryKeys.add(primaryKey);
                this.logger.info("new primary key added to list: " + primaryKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return primaryKeys;

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
        if (this.crypto == null) {
            this.crypto = this.initCryptoModule(DEFAULT_HASH_ALGORITHM);
        }


        try {
            this.crypto.update(inputString.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String hash = DigestUtils.sha1Hex(this.crypto.digest());
            return hash;



    }

    /**
     * Check the table metadata if there is a column with the name "rowHash"
     *
     * @param tableName
     * @return
     */
    public boolean hasAppendedHashColumn(String tableName) {
        List<String> listOfColumns = this.getListOfColumnNames(tableName);
        if (listOfColumns.contains((String) "rowHash")) {
            this.logger.info("Table already has a row hash column");
            return true;
        } else {
            this.logger.info("Table already has no hash column");
            return false;
        }

    }

    /**
     * Execute a simple query
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

    /**
     * Iterate over resultset and calculate hash. Supported hash algorithms are MD5, SHA-1 and SHA-256
     *
     * @param rs
     * @param hashAlgorithm
     * @return
     */
    public String calculateResultSetHashClientSide(ResultSet rs, String hashAlgorithm) {
        String resultSetHash = "";
        String currentHash = "";
        String previousKey = "";
        String compositeHash = "";
        CachedRowSet cached = null;
        int hashCounter = 0;

        long startTime = System.currentTimeMillis();
        //int hashCounter =0;


        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            cached.setFetchSize(1000);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            this.logger.info("There are " + columnsNumber + " columns in the result set");
            String newResultSetHash = null;
            long meanTimeStart = System.currentTimeMillis();
            while (cached.next()) {
                hashCounter++;
                if (hashCounter % 1000 == 0) {
                    long meanTimeStop = System.currentTimeMillis();

                    this.logger.warning("Calculated " + hashCounter + " hashes so far. This batch took " + (double) (
                            (meanTimeStop - meanTimeStart) / 1000) + " seconds");

                    meanTimeStart = System.currentTimeMillis();
                }
                for (int i = 1; i < columnsNumber; i++) {
                    currentHash += cached.getString(i);
                }


                if (cached.isFirst()) {

                    resultSetHash = this.calculateHashFromString(currentHash);

                } else {

                    compositeHash = (resultSetHash + currentHash);
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

    /**
     * Calculate hash on DB
     *
     * @return
     */
    public String calculateResultSetHashServerSide(ResultSet rs) {



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

    /**
     * Calculate hash of a result set wich already contains concatenated rows
     *
     * @return
     */
    public String calculateResultSetHashServerSideConcatenated(ResultSet rs) {


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

                currentHash += rs.getString(1);


                if (rs.isFirst()) {

                    resultSetHash = this.calculateHashFromString(currentHash);

                } else {

                    compositeHash = (resultSetHash + currentHash);

                    // reset the variables in order to reduce overhead
                    resultSetHash = null;
                    currentHash = null;
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


    /**
     * Performa select * from table on the specified table
     *
     * @param tableName
     * @return
     */
    private ResultSet getCompleteResultSetFromTable(String tableName) {
        Connection connection = this.getConnection();
        java.sql.PreparedStatement stmt;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM `" + tableName
                    + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);

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

    /**
     * Get all columns from a table. Same as SELECT * but with all columns specified (performance and security enhanced)
     *
     * @param tableName
     * @return
     */
    private ResultSet getCompleteResultSetFromTableWithAllColumns(String tableName) {
        List<String> columnNames = this.getListOfColumnNames(tableName);
        // Prepend the table name to all columns
        String columnNamesAsString = Helpers.commaSeparatedStringWithPrefixAndSuffix(columnNames, tableName + ".", "");
        Connection connection = this.getConnection();
        java.sql.PreparedStatement stmt;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("SELECT " + columnNamesAsString + " FROM `" + tableName
                    + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);

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

    /**
     * Concatenates all cells per row. Faster variant than delivering the conplete resultset.
     *
     * @param tableName
     * @return
     */
    private ResultSet getCompleteResultSetFromTableWithAllColumnsConcatenated(String tableName) {
        List<String> columnNames = this.getListOfColumnNames(tableName);
        // Prepend the table name to all columns
        String columnNamesAsString = Helpers.commaSeparatedStringWithPrefixAndSuffix(columnNames, tableName + ".", "");
        Connection connection = this.getConnection();
        java.sql.PreparedStatement stmt;
        ResultSet rs = null;

        try {
            stmt = connection.prepareStatement("SELECT CONCAT(" + columnNamesAsString + ") AS concatenatedvalues FROM `" + tableName
                    + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);

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


    /**
     * Retrieve the complete result set from a table and all its columns. Calculate the hash of the result set. If the server concatenates the cells of one row and returns concatenated strings, set concatServerSide = true
     *
     * @param tableName
     * @return
     */

    public String calculateHashFromCompleteTableServerSide(String tableName, boolean concatServerSide) {

        // the server concatenates the cells of one row and returns concatenated strings
        if (concatServerSide) {
            ResultSet rs = this.getCompleteResultSetFromTableWithAllColumnsConcatenated(tableName);
            return this.calculateResultSetHashServerSideConcatenated(rs);

        } else {
            ResultSet rs = this.getCompleteResultSetFromTableWithAllColumns(tableName);
            return this.calculateResultSetHashServerSide(rs);
        }


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
