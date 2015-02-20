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

package at.stefanproell.ResultSetVerification;

import Database.DatabaseOperations.HikariConnectionPool;
import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by stefan on 12.02.15.
 */
public class ResultSetHashAlternatives {
    private Connection con = null;
    private Logger logger;


    public ResultSetHashAlternatives() {

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
     * Iterate over ResultSet and calculate a Hash row by row.
     *
     * @param rs
     */
    public String preCalculatedHashes(ResultSet rs) {
        String resultSetHash = "";
        String currentHash = "";
        String compositeHash = "";
        CachedRowSet cached = null;
        //  CSVHelper csvHelper = new CSVHelper();
        long startTime = System.currentTimeMillis();
        int hashCounter = 0;

        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            ResultSetMetaData metaData = cached.getMetaData();
            int count = metaData.getColumnCount(); // number of columns
            String columnName[] = new String[count];

            String includedColumns = "Columns in this query: ";
            for (int i = 1; i <= count; i++) {

                columnName[i - 1] = metaData.getColumnLabel(i);
                includedColumns += columnName[i - 1] + " , ";

            }

            System.out.println(includedColumns);

            while (cached.next()) {
                hashCounter++;
                currentHash = cached.getString("sha1_hash");

                if (cached.isFirst()) {
                    // @TODO change me here
                    //resultSetHash = csvHelper      .calculateHashFromString(currentHash);
                    // this.logger.info("First Hash! Original: " + currentHash +
                    // " First new Hash " + resultSetHash);
                } else {
                    /*
                     * // Move the cursor to the previous row and read the hash
					 * value. if(cached.previous()){ previousKey =
					 * cached.getString("sha1_hash"); if(cached.next()){
					 * compositeKey = currentKey + previousKey; resultSetHash =
					 * csvHelper.calculateHashFromString(compositeKey);
					 * this.logger.info("Appended Hash " + previousKey +
					 * " to hash " + currentKey + " and calulated " +
					 * resultSetHash);
					 *
					 * } }
					 */

                    compositeHash = (resultSetHash + currentHash);
                    // @TODO change me here
                    // String newResultSetHash = csvHelper                          .calculateHashFromString
                    // (compositeHash);
                    // this.logger.info("[resultSetHash] "+resultSetHash +
                    // "[currentHash] " + currentHash +" -> [newResultSetHash]"
                    // + newResultSetHash );
                    // resultSetHash = newResultSetHash;

                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Calculated " + hashCounter + " hash values in "
                + (totalTime) + " millisec");

        return resultSetHash;

    }

    /**
     * Iterate over ResultSet and calculate a hash over all appended strings.
     *
     * @param rs
     */
    public String appendAllRowesBeforeHashing(ResultSet rs) {
        String resultSetHash = "";

        String completeResultSet = "";
        CachedRowSet cached = null;
        //CSVHelper csvHelper = new CSVHelper();
        long startTime = System.currentTimeMillis();
        int hashCounter = 0;

        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            ResultSetMetaData metaData = cached.getMetaData();
            int count = metaData.getColumnCount(); // number of columns
            String columnName[] = new String[count];

            String includedColumns = "Columns in this query: ";
            for (int i = 1; i <= count; i++) {

                columnName[i - 1] = metaData.getColumnLabel(i);
                includedColumns += columnName[i - 1] + " , ";

            }

            System.out.println(includedColumns);

            while (cached.next()) {
                hashCounter++;
                for (int i = 1; i <= count; i++) {

                    completeResultSet += cached.getString(i);

                }
                if ((hashCounter % 500) == 0) {
                    System.out.println("Appended Nr " + hashCounter);
                }

            }
            System.out.println("Starting Hash calculation");

            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Calculated " + hashCounter
                    + " hash values of a String appended from length "
                    + completeResultSet.length() + "in " + (totalTime) + " millisec");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultSetHash;

    }


    /**
     * only get some cols
     *
     * @param tableName
     * @return
     */
    public ResultSet getSomeColumnsFromTable(String tableName,
                                             String listOfColumns) {
        java.sql.PreparedStatement stmt;
        ResultSet rs = null;
        try {
            stmt = this.con.prepareStatement("SELECT " + listOfColumns
                    + " FROM `" + tableName + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;

    }

    public String calculateHashRowByRow(ResultSet rs) {
        String resultSetHash = "";
        String currentHash = "";
        String compositeHash = "";
        CachedRowSet cached = null;
        long startTime = System.currentTimeMillis();
        int hashCounter = 0;

        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            ResultSetMetaData metaData = cached.getMetaData();
            int count = metaData.getColumnCount(); // number of columns
            String columnName[] = new String[count];

            String includedColumns = "Columns in this query: ";
            for (int i = 1; i <= count; i++) {

                columnName[i - 1] = metaData.getColumnLabel(i);
                includedColumns += columnName[i - 1] + " , ";

            }

            System.out.println(includedColumns);

            while (cached.next()) {

                hashCounter++;
                String currentrow = null;
                for (int i = 1; i <= count; i++) {

                    currentrow += cached.getString(i);

                }

                currentHash = currentrow;

                if (cached.isFirst()) {
                    // @TODO change me here


                    // resultSetHash = csvHelper           .calculateHashFromString(currentHash);
                    // this.logger.info("First Hash! Original: " + currentHash +
                    // " First new Hash " + resultSetHash);
                } else {
                    /*
                     * // Move the cursor to the previous row and read the hash
					 * value. if(cached.previous()){ previousKey =
					 * cached.getString("sha1_hash"); if(cached.next()){
					 * compositeKey = currentKey + previousKey; resultSetHash =
					 * csvHelper.calculateHashFromString(compositeKey);
					 * this.logger.info("Appended Hash " + previousKey +
					 * " to hash " + currentKey + " and calulated " +
					 * resultSetHash);
					 *
					 * } }
					 */

                    compositeHash = (resultSetHash + currentHash);

                    // @TODO change me here
                    // String newResultSetHash = csvHelper .calculateHashFromString(compositeHash);
                    // this.logger.info("[resultSetHash] "+resultSetHash +
                    // "[currentHash] " + currentHash +" -> [newResultSetHash]"
                    // + newResultSetHash );
                    //resultSetHash = newResultSetHash;

                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Calculated " + hashCounter + " hash values in "
                + (totalTime) + " millisec");

        return resultSetHash;

    }


    /**
     * Calculate hash of a result set wich already contains concatenated rows
     *
     * @return
     */
    public static String calculateResultSetHashServerSideConcatenated(ResultSetVerificationAPI resultSetVerificationAPI, ResultSet rs) {


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

            String newResultSetHash = null;
            long meanTimeStart = System.currentTimeMillis();

            rs.setFetchSize(1000);


            while (rs.next()) {
                hashCounter++;
                if (hashCounter % 1000 == 0) {
                    long meanTimeStop = System.currentTimeMillis();


                    meanTimeStart = System.currentTimeMillis();
                }

                currentHash += rs.getString(1);


                if (rs.isFirst()) {

                    resultSetHash = resultSetVerificationAPI.calculateHashFromString(currentHash);

                } else {

                    compositeHash = (resultSetHash + currentHash);

                    // reset the variables in order to reduce overhead
                    resultSetHash = null;
                    currentHash = null;
                    newResultSetHash = resultSetVerificationAPI.calculateHashFromString(compositeHash);
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


        return resultSetHash;

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

        //@todo enable if needed
        // hashedCRC = ResultSetVerificationAPI.calculateHashFromString(appendedChecksum);

        this.logger.info("Hashed checksum " + hashedCRC);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashedCRC;
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


}
