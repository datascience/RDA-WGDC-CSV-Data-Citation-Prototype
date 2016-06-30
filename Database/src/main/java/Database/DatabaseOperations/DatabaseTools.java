package Database.DatabaseOperations;


import CSVTools.CsvToolsApi;
import au.com.bytecode.opencsv.CSVWriter;
import com.google.common.base.Joiner;
import com.sun.rowset.CachedRowSetImpl;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.sql.rowset.CachedRowSet;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by stefan on 03.09.14.
 */
public class DatabaseTools {
    private String dataBaseName;
    private String tableName;
    private Logger logger;

    private ResultSetMetadata resultSetMetadata;


    /**
     * Constructor that connects with the default database schema
     * DATABASE_SCHEMA
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public DatabaseTools() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.resultSetMetadata = new ResultSetMetadata();


    }


    public String getDataBaseName() {


        if (this.dataBaseName == null || this.dataBaseName.equals("")) {
            Connection connection = this.getConnection();

            try {
                this.dataBaseName = connection.getCatalog();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (Exception e) { /* handle close exception, quite usually ignore */ }
                }
            }
            this.logger.info("Get database name from connection. It is" + this.dataBaseName);

        }


        return this.dataBaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public Logger getLogger() {
        return logger;
    }


    /*
Count the records which are not deleted..
*/
    public int getNumberOfActiveRecords(String baseTableMame) {
        String sqlActiveRecords = "SELECT COUNT(DISTINCT ID_SYSTEM_SEQUENCE) AS activeRecords FROM " + baseTableMame + " " +
                "WHERE  " +
                "(RECORD_STATUS = 'inserted' OR RECORD_STATUS = 'updated')";
        this.logger.info("Active records SQL: " + sqlActiveRecords);


        Connection connection = null;
        ResultSet maxSequenceResult = null;
        int numberOfActiveStatements = 0;
        try {
            connection = this.getConnection();


            Statement numberOfActiveRecordsStatement = connection.createStatement();

            maxSequenceResult = numberOfActiveRecordsStatement.executeQuery(sqlActiveRecords);

            if (maxSequenceResult.next()) {
                numberOfActiveStatements = maxSequenceResult.getInt("activeRecords");
                this.logger.info("Number of active records: " + numberOfActiveStatements);

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (maxSequenceResult != null) {
                    maxSequenceResult.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return numberOfActiveStatements;

    }


    public CachedRowSet queryDatabase(String tableName, int sortingColumnsID,
                                      String sortingDirection, Map<String, String> filterMap,
                                      int startRow, int offset) {

        this.tableName = tableName;
        Connection connection = null;
        ResultSet rs = null;
        CachedRowSet cachedResultSet = null;
        String[] tableHeaders = null;
        Statement stat = null;

        // result set has to be sorted
        if (sortingColumnsID >= 0) {
            this.logger.warning("sortingColumnsID == " + sortingColumnsID);

            Map<String, String> tableMetadata;

            try {

                cachedResultSet = new CachedRowSetImpl();
                // get column names
                tableMetadata = this.getTableColumnMetadata(tableName);

                String sortColumn = (new ArrayList<String>(
                        tableMetadata.keySet())).get(sortingColumnsID);

                String whereClause = "";
                if (this.hasFilters(filterMap)) {
                    whereClause = this.getWhereString(filterMap);
                }

                connection = this.getConnection();
                String selectSQL = "SELECT * FROM "
                        + this.tableName
                        // + this.getPaginationString(startRow, offset)
                        + whereClause + " ORDER BY " + sortColumn + " "
                        + sortingDirection
                        + this.getPaginationStringWithLIMIT(startRow, offset);

                stat = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);


                ResultSet sortedResultSet = stat.executeQuery(selectSQL);
                this.logger.info("NATIVE SQL STRING "
                        + connection.nativeSQL(selectSQL));
                cachedResultSet.populate(sortedResultSet);
                stat.close();
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }

                    if (stat != null) {
                        stat.close();
                    }
                } catch (SQLException sqlee) {
                    sqlee.printStackTrace();
                }
            }

        } else {
            try {
                connection = this.getConnection();

                stat = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                // String query = "SELECT * FROM  "+this.tableName;

                // stat = this.getConnection().createStatement();
                String query = "SELECT * FROM  " + this.tableName;
                System.out.println(query);
                rs = stat.executeQuery(query);
                cachedResultSet.populate(rs);
                stat.close();
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                    if (stat != null) {
                        stat.close();
                    }
                } catch (SQLException sqlee) {
                    sqlee.printStackTrace();
                }
            }
        }
        // TODO how to close connection?

        return cachedResultSet;

    }

    /*
    * Get the row count of a table
    * */
    public int getRowCount(String tableName) {
        this.logger.info("Executing row count for " + tableName);

        // TODO SQL injection
        String sql = "SELECT COUNT(*) FROM " + tableName;

        int numberOfRecords = -1;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = this.getConnection();
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                numberOfRecords = resultSet.getInt(1);
            }

            this.logger.warning("Row Set Size:  " + numberOfRecords);
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


        return numberOfRecords;
    }

    /* Get the list of rows from a result set
    * * */
    public List<List<String>> getRowList(ResultSet resultSet) {
        ResultSetMetaData meta = null;
        List<List<String>> rowList = null;
        try {
            meta = resultSet.getMetaData();
            final int columnCount = meta.getColumnCount();

            rowList = new LinkedList<List<String>>();
            while (resultSet.next()) {
                List<String> columnList = new LinkedList<String>();
                rowList.add(columnList);

                for (int column = 1; column <= columnCount; column++) {
                    Object value = resultSet.getObject(column);
                    if (value != null) {
                        columnList.add((String) value);
                    } else {
                        columnList.add("null"); // you need this to keep your
                        // columns in sync....
                    }
                }
            }
            resultSet.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {

                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return rowList;
    }

    void printList(Collection myList) {
        for (Object o : myList) {
            if (Collection.class.isAssignableFrom(o.getClass())) {
                printList((Collection) o);
            } else {
                System.out.println(o);
            }
        }
    }

    void printTable(List<List<String>> rowList) {
        for (List<String> row : rowList) {
            for (String cell : row) {
                System.out.print(cell + "\t");
            }
            System.out.println("\n");
        }

    }

    /**
     * Get a map of <ColumnName, ColumnType>
     */
    public Map<String, String> getTableColumnMetadata(String tableName) {

        if (tableName == null || tableName.equals("")) {

            tableName = this.getFirstTableFromStandardSessionDatabase();
            this.logger.info("No table name provided. Must be first call. Using default: " + tableName);
            if (tableName == null) {
                this.logger.severe("There is not a single table available! Check if it is initialized!");
            }

        }
        Map<String, String> columnMetadataMap = null;
        Connection connection = null;
        try {
            connection = this.getConnection();

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


            columnMetadataMap = new LinkedHashMap<String, String>();
            while (cachedResultSet.next()) {


                // ColumnName
                String columnName = cachedResultSet.getString(4);

                // ColumnType
                String columnType = cachedResultSet.getString(6);

                columnMetadataMap.put(columnName, columnType);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return columnMetadataMap;

    }

    /**
     * Return a sorted map of column names and data types
     *
     * @param tableName
     * @return
     */
    public TreeMap<String, String> getColumnNamesWithoutMetadataSortedAlphabetically(String tableName) {
        Map<String, String> columnNames = this.getColumnNamesFromTableWithoutMetadataColumns(tableName);
        TreeMap<String, String> sortedByColumnName = new TreeMap<String, String>(columnNames);
        return sortedByColumnName;
    }


    /**
     * Get a map of <ColumnName, ColumnType, ColumnSize> but remove the automatically generated metadata columns from the map:
     * ID_SYSTEM_SEQUENCE, INSERT_DATE, LAST_UPDATE, RECORD_STATUS
     */
    public Map<String, String> getColumnNamesFromTableWithoutMetadataColumns(String tableName) {

        if (tableName == null) {
            this.logger.severe("SESSION DATA IS NOT SET CORRECTLY");
        }


        Connection connection = null;
        ResultSet rs = null;
        Map<String, String> columnMetadataMap = null;
        try {
            connection = this.getConnection();


            columnMetadataMap = new LinkedHashMap<String, String>();
            // this query is needed to retrieve the column names from the database
            String dummySQL = "SELECT * FROM " + connection.getCatalog() + "." + tableName +
                    " WHERE ID_SYSTEM_SEQUENCE > 0 AND ID_SYSTEM_SEQUENCE < 2";
            this.logger.info("Dummy string " + dummySQL);
            PreparedStatement pt = connection.prepareStatement(dummySQL);


            rs = pt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {

                String columnName = meta.getColumnName(i);
                String columnType = meta.getColumnTypeName(i);

                columnMetadataMap.put(columnName, columnType);
                System.out.println("Key: " + columnName + " Value " + columnType);

            }


            // remove the sequence nu,ber, timestamps and status columns
            this.logger.info("There are " + columnMetadataMap.size() + " columns in the table");
            columnMetadataMap.remove("ID_SYSTEM_SEQUENCE");
            columnMetadataMap.remove("INSERT_DATE");
            columnMetadataMap.remove("LAST_UPDATE");
            columnMetadataMap.remove("RECORD_STATUS");
            columnMetadataMap.remove("SHA1_HASH");
            this.logger.info("Removed the metadata . Now there are " + columnMetadataMap.size() + " columns in the " +
                    "table");


            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return columnMetadataMap;

    }

    /*Gets the number of columns from a table
    * * */
    public int getNumberofColumnsPerTable(String tableName) {
        Connection connection = null;
        ResultSet resultSet = null;
        int columncCount = 0;
        try {
            connection = this.getConnection();

            columncCount = 0;
            DatabaseMetaData meta = connection.getMetaData();
            String catalog = null;
            String schemaPattern = null;
            String tableNamePattern = tableName;
            String columnNamePattern = null;


            resultSet = meta.getColumns(catalog, schemaPattern,
                    tableNamePattern, columnNamePattern);

            Map<String, String> columnMetadataMap = new HashMap<String, String>();
            while (resultSet.next()) {
                columncCount++;

            }

            connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return columncCount;

    }


    public void insertCSVDataIntoDB(String path, String tableName,
                                    boolean hasHeaders, boolean calculateHashKeyColumn) throws IOException {

        Connection connection = null;
        ICsvListReader listReader = null;
        CsvListReader reader = null;
        long startTime = 0;
        int rowCount = 0;
        try {
            startTime = System.currentTimeMillis();
            connection = this.getConnection();

            if (connection.getAutoCommit()) {
                //this.logger.info("AUTO COMMIT OFF");
                connection.setAutoCommit(false);
            }
            PreparedStatement preparedStatement;
            CsvToolsApi csvAPI = new CsvToolsApi();
            reader = null;
            rowCount = 0;

            reader = new CsvListReader(new FileReader(path),
                    CsvPreference.STANDARD_PREFERENCE);

            listReader = null;

            final String[] header = reader.getHeader(hasHeaders);

            // Calculate the number of place holders required by the amount of
            // columns
            // and add four ? for the sequence, created and updated date and the
            // hash column. The id is the
            // first placeholder and then ..., created date, updated date, hash)

            String placeholders = "(?,";
            for (int i = 0; i < header.length; i++) {
                placeholders += "?,";
            }

            // Adjust the amount of placeholders
            if (calculateHashKeyColumn) {
                placeholders += "?,";

            } else {
                // If there is no hash column, then only append the two time stamp cols
                placeholders += "?,?)";
            }

            String insertString = "INSERT INTO " + tableName + " VALUES "
                    + placeholders;
            preparedStatement = connection.prepareStatement(insertString);

            List<String> row;


            while ((row = reader.read()) != null) {

                rowCount++;

                for (int columnCount = 1; columnCount <= header.length + 4; columnCount++) {
                    // this.logger.info("columns Count : " + columnCount +
                    // " _ header count = " + header.length);
                    // first column contains sequence
                    if (columnCount == 1) {
                        preparedStatement.setInt(columnCount, rowCount);

                        // column values (first column is the id)
                    } else if (columnCount > 1
                            && columnCount <= (header.length + 1)) {

                        // index starts at 0 and the counter at 1.
                        preparedStatement.setString(columnCount,
                                row.get(columnCount - 2));

                        // insert timestamps
                    } else if (columnCount == (header.length + 2)
                            || columnCount == (header.length + 3)) {

                        preparedStatement.setDate(columnCount, null);

                        // insert the hash
                    } else if (columnCount == (header.length + 4) & calculateHashKeyColumn) {

                        String appendedColumns = CsvToolsApi
                                .convertStringListToAppendedString(row);
                        String hash = CsvToolsApi
                                .calculateSHA1HashFromString(appendedColumns);

                        preparedStatement.setString(columnCount, hash);

                    }
                }

                //this.logger.info("prepared statement before exec: " + preparedStatement.toString());
                preparedStatement.executeUpdate();

                if (rowCount % 1000 == 0) {
                    connection.commit();
                }

            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (listReader != null) {
                listReader.close();
            }
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);

                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
            if (reader != null) {
                reader.close();
            }


        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Inserted " + rowCount + " rows in " + (totalTime / 1000) + " sec");
    }

    /*
     * get current time stamp
     */
    public java.util.Date getCurrentDatetime() {
        java.util.Date today = new java.util.Date();
        return new java.util.Date(today.getTime());
    }

    // iterate over the filters and buils WHERE clauses
    // the pagination string has the WHERE keyword
    public String getWhereString(Map<String, String> filtersMap) {
        // String whereString = "  AND ";
        // use this string if LIMIT is active
        String whereString = " WHERE ";
        int filterCounter = -1;
        for (Map.Entry<String, String> entry : filtersMap.entrySet()) {

            filterCounter++;

            String column = entry.getKey();
            String clause = entry.getValue();

            if (filterCounter == 0) {
                whereString += "UPPER(" + column + ") LIKE UPPER(\'%" + clause
                        + "%\') ";
            }
            if (filterCounter >= 1) {
                whereString += "AND UPPER(" + column + ") LIKE UPPER(\'%"
                        + clause + "%\') ";
            }

        }

        this.logger.info("WHERE Clause: " + whereString);
        return whereString;

    }

    private boolean hasFilters(Map<String, String> filterMap) {
        if (filterMap.size() > 0) {
            this.logger.info("There are filters");
            return true;
        } else
            return false;
    }

    /**
     * All rows have a sequence number with and index. Use this sequence number
     * in the WHERE clause in order to retrieve only the required rows.
     *
     * @return
     */
    private String getPaginationString(int startRow, int offset) {
        String paginationString = null;
        if (startRow < 0 || offset < 0) {
            this.logger.warning("Pagination error!!");

        }
        // paginationString= " WHERE ID_SYSTEM_SEQUENCE BETWEEN \'" + startRow +
        // "\' AND \'" + (startRow + offset) + "\'";
        paginationString = " WHERE ID_SYSTEM_SEQUENCE BETWEEN \'" + startRow
                + "\' AND \'" + (startRow + offset) + "\'";
        return paginationString;
    }

    /**
     * All rows have a sequence number with and index. Use this sequence number
     * in the WHERE clause in order to retrieve only the required rows.
     *
     * @return
     */
    private String getPaginationStringWithLIMIT(int showRows, int offset) {
        String paginationString = null;
        if (showRows < 0 || offset < 0) {
            this.logger.warning("Pagination error!!");

        }
        // paginationString= " WHERE ID_SYSTEM_SEQUENCE BETWEEN \'" + startRow +
        // "\' AND \'" + (startRow + offset) + "\'";
        paginationString = " LIMIT " + showRows + " OFFSET " + offset;
        return paginationString;
    }

    /**
     * Get a list of database names in the system
     *
     * @return
     */
    public List<String> getAvailableDatabases() {
        Connection connection = null;
        List<String> listOfDatabases = new ArrayList<String>();

        try {
            connection = this.getConnection();

            DatabaseMetaData meta = connection.getMetaData();
            CachedRowSetImpl cachedResultSet = new CachedRowSetImpl();
            ResultSet rs = connection.getMetaData().getCatalogs();

            while (rs.next()) {
                String currentDatabaseName = rs.getString("TABLE_CAT");
                listOfDatabases.add(currentDatabaseName);
                this.logger.info("database  ");
            }
            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }
        return listOfDatabases;

    }

    /**
     * This only returns the database of the current connection. This is used by the drop down menu for only
     * * providing the current schema. Can be replaced by getAvailableDatabases which provides all databases.
     *
     * @return
     */
    public List<String> getDatabaseCatalogFromDatabaseConnection() {
        Connection connection = null;
        List<String> listOfDatabases = new ArrayList<String>();

        try {
            connection = this.getConnection();
            String databaseFromConnection = connection.getCatalog();
            listOfDatabases.add(databaseFromConnection);
            this.logger.info("Database from connection is " + databaseFromConnection);

            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }
        return listOfDatabases;

    }

    public String getDefaultDatabaseNameFromConnection() {

        List<String> databaseNames = this.getDatabaseCatalogFromDatabaseConnection();
        String databaseName = databaseNames.get(0);
        this.logger.info("The DEFAULT database name is " + databaseName);
        return databaseName;

    }

    /**
     * @param databaseName
     * @return
     */
    public List<String> getAvailableTablesFromDatabase(String databaseName) {
        Connection connection = null;
        List<String> listOfTables = new ArrayList<String>();

        try {
            connection = this.getConnection();

            if (databaseName == null || databaseName.equals("")) {
                this.logger.info("Database was null");
                databaseName = this.getDataBaseName();
            }

            DatabaseMetaData meta = connection.getMetaData();
            String[] types = {"TABLE"};

            ResultSet rs = meta.getTables(databaseName, null, null, types);

            //  this.logger.info("retrieve metadata for " + databaseName + " retrieved " + rs.getFetchSize() + " tables");
            while (rs.next()) {
                // System.out.println("getAvailableTablesFromDatabase__________________");
                String tableName = rs.getString("TABLE_NAME");

                /*
                this.logger.info("Inside resultset:    " + rs.getString("TABLE_CAT") + ", "
                        + rs.getString("TABLE_SCHEM") + ", "
                        + rs.getString("TABLE_NAME") + ", "
                        + rs.getString("TABLE_TYPE") + ", "
                        + rs.getString("REMARKS"));
                */
                listOfTables.add(tableName);
                //  this.logger.info(tableName);

            }

            connection.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }
        return listOfTables;

    }


    public ResultSet getCompleteResultSetFromTable(String tableName) {
        java.sql.PreparedStatement stmt;
        Connection connection = null;
        ResultSet rs = null;
        try {
            connection = this.getConnection();
            stmt = connection.prepareStatement("SELECT * FROM `" + tableName + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }
        return rs;


    }

    /**
     * Provide all comuns like "spalte a, spalteb"
     *
     * @param columnsAsList
     * @param tableName
     * @return
     */
    public ResultSet getResultSetFromTable(String columnsAsList, String tableName) {
        java.sql.PreparedStatement stmt;

        String sql = "SELECT " + columnsAsList + " FROM `" + tableName + "`";
        this.logger.info("SQL Statements" + sql);

        ResultSet rs = null;
        Connection connection = null;
        try {
            connection = this.getConnection();

            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }
        return rs;


    }


    /**
     * Iterate over ResultSet and calculate a Hash row by row.
     *
     * @param rs
     */
    public String calculateResultSetHash(ResultSet rs, boolean hasHashColumn) {
        String resultSetHash = "";
        String currentHash = "";
        String previousKey = "";
        String compositeHash = "";
        CachedRowSet cached = null;
        CsvToolsApi csvAPI = new CsvToolsApi();
        long startTime = System.currentTimeMillis();
        //int hashCounter =0;


        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            this.logger.info("There are " + columnsNumber + " columns in the result set");
            String newResultSetHash = null;
            while (cached.next()) {
    /*			hashCounter++;
                if (hashCounter % 1000 ==0){
					this.logger.warning("Calculated " + hashCounter + " hashes so far");
				}*/
                // if there is a hash column, retrieve the value from the table
                if (hasHashColumn) {
                    currentHash = cached.getString("sha1_hash");

                    //if there is no hash column append all columns and hash them
                } else {

                    for (int i = 1; i < columnsNumber; i++) {
                        currentHash += cached.getString(i);
                    }


                }


                if (cached.isFirst()) {

                    resultSetHash = csvAPI.calculateSHA1HashFromString(currentHash);
                    // this.logger.info("First Hash! Original: " + currentHash + " First new Hash " +  resultSetHash);
                } else {
            /*		// Move the cursor to the previous row and read the hash value.
                    if(cached.previous()){
						previousKey = cached.getString("sha1_hash");
						if(cached.next()){
							compositeKey = currentKey + previousKey;
							resultSetHash = csvAPI.calculateSHA1HashFromString(compositeKey);
							this.logger.info("Appended Hash " + previousKey + " to hash " + currentKey + " and
							calulated " + resultSetHash);

						}
					}*/

                    compositeHash = (resultSetHash + currentHash);
                    newResultSetHash = csvAPI.calculateSHA1HashFromString(compositeHash);
                    //this.logger.info("[resultSetHash] "+resultSetHash + "[currentHash] " + currentHash +" ->
                    // [newResultSetHash]" + newResultSetHash );
                    resultSetHash = newResultSetHash;


                }
                System.gc();
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        double elapsedTime = (double) (totalTime / 1000);


        //System.out.println("Calculated " + hashCounter +" hash values in "+ elapsedTime + " sec");
        this.logger.info("done");
        return resultSetHash;

    }


    /**
     * Retrieve the primary key from a table by using the metadata of the database
     *
     * @param tableName
     * @return
     */
    public List<String> getPrimaryKeyFromTable(String tableName) {
        List<String> primaryKeyList = new ArrayList<String>();
        ResultSet result = null;
        String catalog = null;
        String schema = null;
        Connection connection = null;
        try {
            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            schema = connection.getSchema();
            catalog = connection.getCatalog();


            DatabaseMetaData databaseMetaData = null;

            databaseMetaData = connection.getMetaData();

            result = databaseMetaData.getPrimaryKeys(
                    catalog, schema, tableName);
            connection.commit();
            connection.close();



            while (result.next()) {
                String columnName = result.getString(4);
                primaryKeyList.add(columnName);
                this.logger.info("Found primary key: " + columnName);
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (result != null) {
                    result.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return primaryKeyList;


    }

    /**
     * Retrieve the primary key from a table by using the metadata of the database without the standard primary key
     * LAST_UPDATE
     *
     * @param tableName
     * @return
     */
    public List<String> getPrimaryKeyFromTableWithoutLastUpdateColumns(String tableName) {

        List<String> primaryKeyList = new ArrayList<String>();

        String catalog = null;
        String schema = this.getDataBaseName();
        DatabaseMetaData databaseMetaData = null;
        Connection connection = null;
        ResultSet result = null;
        try {
            connection = this.getConnection();
            databaseMetaData = connection.getMetaData();

            result = databaseMetaData.getPrimaryKeys(
                    catalog, schema, tableName);

            while (result.next()) {
                String columnName = result.getString(4);
                if (columnName.equals("LAST_UPDATE") == false) {
                    primaryKeyList.add(columnName);
                    this.logger.info("Found primary key: " + columnName);
                } else {
                    this.logger.info("Ignored standard key LAST_UPDATE");
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return primaryKeyList;


    }

    /**
     * Retrieve the primary key from a table by using the metadata of the database without the standard primary key
     * LAST_UPDATE and
     *
     * @param tableName
     * @return
     */
    public List<String> getPrimaryKeyFromTableWithoutLastUpdateOrSystemSequenceColumns(String tableName) {

        List<String> primaryKeyList = new ArrayList<String>();

        String catalog = null;
        String schema = this.getDataBaseName();
        DatabaseMetaData databaseMetaData = null;
        Connection connection = null;
        ResultSet result = null;
        try {
            connection = this.getConnection();
            databaseMetaData = connection.getMetaData();

            result = databaseMetaData.getPrimaryKeys(
                    catalog, schema, tableName);

            while (result.next()) {
                String columnName = result.getString(4);
                if (columnName.equals("LAST_UPDATE") == false && columnName.equals("ID_SYSTEM_SEQUENCE") == false) {
                    primaryKeyList.add(columnName);
                    this.logger.info("Found primary key: " + columnName);
                } else {
                    this.logger.info("Ignored standard key LAST_UPDATE or ID_SYSTEM_SEQUENCE");
                }

            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return primaryKeyList;


    }

    /**
     * Retrieve the max Sequence number from a record in the database
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public int getMaxSequenceNumberFromTable(String tableName) {
        Connection connection = null;
        ResultSet maxSequenceResult = null;
        int maxSequenceNumber = 0;
        try {
            connection = this.getConnection();


            Statement selectLastSequenceNumber = connection.createStatement();

            maxSequenceResult = selectLastSequenceNumber.executeQuery("SELECT MAX(ID_SYSTEM_SEQUENCE) " +
                    "AS maxSequence FROM " + tableName + ";");
            maxSequenceNumber = -1;
            if (maxSequenceResult.next()) {
                maxSequenceNumber = maxSequenceResult.getInt("maxSequence");
                this.logger.info("MAX sequence number of " + tableName + " is " + maxSequenceNumber);

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (maxSequenceResult != null) {
                    maxSequenceResult.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return maxSequenceNumber;
    }

    /**
     * Get the insert date of a record
     *
     * @param tableName
     * @param primaryKeyColumn
     * @param primaryKeyValue
     * @return
     * @throws SQLException
     */
    public Date getInsertDateFromRecord(String tableName, String primaryKeyColumn,
                                        String primaryKeyValue) {
        Connection connection = null;
        ResultSet minInsertDateResultSet = null;
        Date minInsertDate = null;
        try {
            connection = this.getConnection();


            Statement selectLastSequenceNumber = connection.createStatement();

            minInsertDateResultSet = selectLastSequenceNumber.executeQuery("SELECT MIN(INSERT_DATE) " +
                    " FROM " + tableName + "WHERE + " + primaryKeyColumn + "='" + primaryKeyValue + "';");
            minInsertDate = null;
            if (minInsertDateResultSet.next()) {
                minInsertDate = minInsertDateResultSet.getDate("INSERT_DATE");
                this.logger.info("Insert Date was " + minInsertDate);

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (minInsertDateResultSet != null) {
                    minInsertDateResultSet.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return minInsertDate;
    }

    /**
     * Get the metadata of a record
     *
     * @param tableName
     * @param primaryKeyColumn
     * @param primaryKeyValue
     * @return
     * @throws SQLException
     */
    public RecordMetadata getMetadataFromRecord(String tableName, String primaryKeyColumn,
                                                String primaryKeyValue) {
        RecordMetadata recordMetadata = null;
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = this.getConnection();


            Statement selectLastSequenceNumber = connection.createStatement();
            String metadataSQL = "SELECT ID_SYSTEM_SEQUENCE, " +
                    "INSERT_DATE, MAX(LAST_UPDATE) AS LAST_UPDATE, RECORD_STATUS " +
                    " FROM " + tableName + " WHERE " + primaryKeyColumn + "='" + primaryKeyValue + "' GROUP BY  " +
                    primaryKeyColumn + ";";
            this.logger.info("Record metadata SQL: " + metadataSQL);

            resultSet = selectLastSequenceNumber.executeQuery(metadataSQL);


            if (resultSet.next()) {
                recordMetadata = new RecordMetadata(resultSet.getInt("ID_SYSTEM_SEQUENCE"),
                        resultSet.getTimestamp("INSERT_DATE"), resultSet.getTimestamp("LAST_UPDATE"),
                        resultSet.getString("RECORD_STATUS"));

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return recordMetadata;
    }

    /*
    * Retrieve the metadata from a record by comparing all columns
    * */
    public RecordMetadata getMetadataFromRecordWithFullData(Map<String, String> columnsMap, String tableName,
                                                            List<String> csvRow) {
        RecordMetadata recordMetadata = null;
        Connection connection = null;
        Statement selectLastSequenceNumber = null;
        ResultSet resultSet = null;
        try {
            connection = this.getConnection();


            selectLastSequenceNumber = connection.createStatement();
            String metadataSQL = "SELECT ID_SYSTEM_SEQUENCE, " +
                    "INSERT_DATE, MAX(LAST_UPDATE) AS LAST_UPDATE, RECORD_STATUS " +
                    " FROM " + connection.getCatalog() + "." + tableName + " " + this
                    .recordExistsWhereClause
                            (columnsMap,
                                    csvRow) + ";";
            this.logger.info("Record metadata SQL: " + metadataSQL);

            resultSet = selectLastSequenceNumber.executeQuery(metadataSQL);


            if (resultSet.next()) {
                recordMetadata = new RecordMetadata(resultSet.getInt("ID_SYSTEM_SEQUENCE"),
                        resultSet.getTimestamp("INSERT_DATE"), resultSet.getTimestamp("LAST_UPDATE"),
                        resultSet.getString("RECORD_STATUS"));

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (selectLastSequenceNumber != null) {
                    selectLastSequenceNumber.close();
                }
                if (resultSet != null) {
                    resultSet.close();

                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return recordMetadata;
    }


    /**
     * Check if a record having the primary key value already exists in the database
     *
     * @param tableName
     * @param primaryKeyColumnName
     * @param primaryKeyValue
     * @return
     * @throws SQLException
     */
    public boolean checkIfRecordExistsInTableByPrimaryKey(String tableName, String primaryKeyColumnName,
                                                          String primaryKeyValue) {
        Connection connection = null;
        Statement checkRecordExistance = null;
        int existsInteger = 0;
        try {
            connection = this.getConnection();


            checkRecordExistance = connection.createStatement();
            String checkSQL = "SELECT EXISTS(SELECT 1 FROM " + tableName + " WHERE " + primaryKeyColumnName + "= '" +
                    primaryKeyValue + "') AS recordDoesExist;";

            this.logger.info("CHECK SQL: " + checkSQL);
            ResultSet maxSequenceResult = checkRecordExistance.executeQuery(checkSQL);
            existsInteger = -1;
            if (maxSequenceResult.next()) {
                existsInteger = maxSequenceResult.getInt("recordDoesExist");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (checkRecordExistance != null) {
                    checkRecordExistance.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


        if (existsInteger == 1) {
            this.logger.info("The record exists");
            return true;
        } else {
            this.logger.info("The record does NOT exist.");
            return false;
        }


    }


    /*
    * Check for each row if the exact same row exists
    * */
    public boolean checkIfRecordExistsInTableByFullCompare(Map<String, String> columnsMap, String tableName,
                                                           List<String> csvRow) {
        Connection connection = null;
        ResultSet maxSequenceResult = null;
        Statement checkRecordExistance = null;
        int existsInteger = 0;
        try {
            connection = this.getConnection();


            checkRecordExistance = connection.createStatement();
            String checkSQL = "SELECT EXISTS(SELECT 1 FROM " + connection.getCatalog() + "." + tableName + " " + this
                    .recordExistsWhereClause
                            (columnsMap,
                                    csvRow)
                    + ") " +
                    "AS recordDoesExist;";

            this.logger.info("CHECK SQL: " + checkSQL);
            ;
            maxSequenceResult = checkRecordExistance.executeQuery(checkSQL);
            existsInteger = -1;

            if (maxSequenceResult.next()) {
                existsInteger = maxSequenceResult.getInt("recordDoesExist");

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (checkRecordExistance != null) {
                    checkRecordExistance.close();
                }
                if (maxSequenceResult != null) {
                    maxSequenceResult.close();


                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


        if (existsInteger == 1) {
            this.logger.info("The record exists via FULL compare");
            return true;
        } else {
            this.logger.info("The record does NOT exist via FULL compare.");
            return false;
        }


    }

    /*
* Needed for updates. If a record existed or is newly inserted, mark it as existing
* */
    public void markRecordAsChecked(int system_id, String tableName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;


        int existsInteger = 0;
        try {
            connection = this.getConnection();

            String insertSQL = "INSERT INTO " + tableName + "_temp VALUES (?,?)";
            preparedStatement = connection.prepareStatement(insertSQL);
            preparedStatement.setInt(1, system_id);
            preparedStatement.setInt(2, 1);

            this.logger.info("Marking Sequence Number: " + system_id + " SQL: " + insertSQL);


            int result = preparedStatement.executeUpdate();


            this.logger.info("Set the column for the record");

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


    }

    /*
    public List<Integer> findAllRecordsWhichNeedToBeDeleted(String tableName, String tempTableName) {

        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ArrayList<Integer> listOfRowsToDelete = new ArrayList<Integer>();


        int existsInteger = 0;
        try {
            connection = this.getConnection();

            String leftJoin = "SELECT orig.ID_SYSTEM_SEQUENCE FROM " + tableName + " AS orig" + " LEFT JOIN " + tempTableName + " AS temp ON orig.ID_SYSTEM_SEQUENCE=temp.id WHERE recordChecked IS NULL ORDER BY ID_SYSTEM_SEQUENCE";
            preparedStatement = connection.prepareStatement(leftJoin);


            this.logger.info(leftJoin);


            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()) {
                int rowToDelete = rs.getInt(1);
                listOfRowsToDelete.add(rowToDelete);

            }


            this.logger.info("Colums count: " + listOfRowsToDelete.size());

            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return listOfRowsToDelete;
    }


    public void deleteMarkedRecords(List<Integer> listOfRecordsToDelete, String tableName) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        int existsInteger = 0;
        try {
            connection = this.getConnection();

            for (int recordId : listOfRecordsToDelete) {
                String deleteRecord = "UPDATE " + tableName + " SET RECORD_STATUS='deleted' WHERE ID_SYSTEM_SEQUENCE=?";
                preparedStatement = connection.prepareStatement(deleteRecord);
                preparedStatement.setInt(1, recordId);
                preparedStatement.executeUpdate();

            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


    }
    */

    public void deleteMarkedRecords(List<Integer> listOfRecordsToDelete, String tableName) {
        if (listOfRecordsToDelete.size() > 0) {
            Connection connection = null;
            Statement statement = null;
            StringBuilder builder = new StringBuilder();
            for (Integer id : listOfRecordsToDelete) {
                builder.append(id);
                builder.append(",");
            }
            String excludeIDs = builder.toString();
            excludeIDs = excludeIDs.length() > 0 ? excludeIDs.substring(0, excludeIDs.length() - 1) : "";

            try {
                connection = this.getConnection();


                String deleteRecord = "UPDATE " + tableName + " SET RECORD_STATUS='deleted', LAST_UPDATE=NOW() WHERE ID_SYSTEM_SEQUENCE NOT IN(" + excludeIDs + ")";
                statement = connection.createStatement();
                statement.executeUpdate(deleteRecord);


                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }

                } catch (SQLException sqlee) {
                    sqlee.printStackTrace();
                }
            }

        }



    }


    /*
    * When a user uploads a CSV file which has deleted records, the system needs to tick off which records have been considered so far. This method adds this column
    * */
    public String createTemporaryCheckTable(String tableName) {


        Connection connection = null;
        String tempTableName = tableName + "_temp";

        try {
            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                //this.logger.info("AUTO COMMIT OFF");
                connection.setAutoCommit(false);
            }


            DatabaseMetaData dbm = connection.getMetaData();
            // check if temptable " table is there
            ResultSet tables = dbm.getTables(null, null, tempTableName, null);
            if (tables.next()) {
                Statement dropStatement = null;
                dropStatement = connection.createStatement();
                String drop = "DROP TABLE IF EXISTS " + tempTableName + ";";
                this.logger.info(drop);
                dropStatement.execute(drop);
                connection.commit();
                dropStatement.close();

            }

            connection = this.getConnection();
            Statement createStat = connection.createStatement();


            createStat = connection.createStatement();
            String createSQL = "CREATE TABLE " + tempTableName + " ( `id` int(11) NOT NULL, `recordChecked` tinyint(1) DEFAULT NULL)";
            this.logger.info(createSQL);
            createStat.execute(createSQL);
            createStat.close();


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

        return tempTableName;

    }

    /*
* When a user uploads a CSV file which has deleted records, the system needs to tick off which records have been considered so far. This method removes this column
* */
    public void dropCheckTable(String tableName) {

        Statement stat = null;
        Connection connection = null;
        try {
            connection = this.getConnection();

            stat = connection.createStatement();
            stat.execute("DROP TABLE " + tableName + "_temp");
            stat.close();

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (stat != null) {
                    stat.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


    }

    /*
    * Create the WHERE clause for a full compare
    * */
    private String recordExistsWhereClause(Map<String, String> columnsMap, List<String> csvRow) {
        int columnCounter = -1;
        CsvToolsApi csvToolsApi = new CsvToolsApi();

        String whereString = "";

        String currentCheck = " WHERE ";
        whereString += currentCheck;

        for (Map.Entry<String, String> entry : columnsMap.entrySet()) {

            columnCounter++;

            String columnNameInDB = csvToolsApi.replaceReservedKeyWords(entry.getKey());

            String columnType = entry.getValue();
            String csvRowValue = csvRow.get(columnCounter);


            whereString += columnNameInDB;

            if (csvRowValue == null || csvRowValue.equals("null")) {
                whereString += (" is null ");
                whereString += (" AND ");
            } else {
                csvRowValue = csvToolsApi.escapeQuotes(csvRowValue);
                whereString += ("= \"" + csvRowValue + "\"");
                whereString += (" AND ");
            }


        }
        if (whereString.endsWith(" AND ")) {
            this.logger.info("The WHERE string ends with AND ");
            whereString = whereString.substring(0, whereString.length() - " AND ".length());


        }
        String whereClause = whereString;
        this.logger.info("This is the WHERE string: " + whereClause);
        return whereClause;

    }


    /*
    Get the name of the column by ID. Needed for building the sorting list
     */
    public String getColumnNameByID(int columnID) {


        Map<String, String> tableMetadata;
        String columnName = null;

        tableMetadata = this.getTableColumnMetadata(this.tableName);
        columnName = (new ArrayList<String>(tableMetadata.keySet())).get(columnID);
        ;

        return columnName;

    }

    /* Execute Query
    *
    * * * */
    public CachedRowSet executeQuery(String tableName, Map<Integer, String> columnSequenceMap, int sortingColumnsID,
                                     String sortingDirection, Map<String, String> filterMap,
                                     int startRow, int offset) {


        CachedRowSet cachedRowSet = queryDatabaseMostRecent(tableName, columnSequenceMap, sortingColumnsID,
                sortingDirection, filterMap, startRow, offset);

        return cachedRowSet;
    }


    /*
    * This method retrieves the first table of the standard database used by the session. it is needed for
    * * initializing the web interfacce.
    * * * */
    public String getFirstTableFromStandardSessionDatabase() {
        String selectedDB = this.getDatabaseCatalogFromDatabaseConnection().get(0);
        Object obj = this.getAvailableTablesFromDatabase(selectedDB).get(0);
        if (obj != null) {
            String tableName = (String) obj;
        } else {
            this.logger.severe("No table found!");
            tableName = null;
        }


        return tableName;
    }

    /*
* This method retrieves the first table of the standard database used by the session. it is needed for
* * initializing the web interfacce.
* * * */
    public String getFirstTableFromDatabase(String dataBaseName) {
        String selectedDB = dataBaseName;

        String tableName = this.getAvailableTablesFromDatabase(selectedDB).get(0);

        return tableName;
    }

    /* Get the list fo columns which have been displaxyed in the Web interface and return a string where the columns
    are concatenated in the correct order.
    * * */
    public String createSELECTstringFromColumnMap(Map<Integer, String> columnSequenceMap) {
        String joinAlias = "outerGroup";
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");

        for (Map.Entry<Integer, String> entry : columnSequenceMap.entrySet()) {
            int columnSequence = entry.getKey();
            String columnName = entry.getValue();
            sb.append(joinAlias + "." + columnName + ",");
        }

        if (sb.toString().endsWith(",")) {

            sb.setLength(sb.length() - 1);

        }
        String SELECTclause = sb.toString();
        this.logger.info("The SELECT clause is: " + SELECTclause);
        return SELECTclause;


    }

    /*
* Get the colums for the Web interface from the database. Used for building the check boxes
* * * */
    public List<String> getColumnsFromDatabaseAsList(String tableName) {


        List<String> availableColumnsList = new ArrayList<String>();
        Map<String, String> availableColumnsMap = this.getTableColumnMetadata(tableName);

        for (Map.Entry<String, String> entry : availableColumnsMap.entrySet()) {


            String columnName = entry.getKey();
            availableColumnsList.add(columnName);

        }

        return availableColumnsList;
    }


    /*
    * Re-Execute a query
    * */
    public CachedRowSet reExecuteQuery(String queryString) {
        Statement stat = null;
        Connection connection = null;
        ResultSet sortedResultSet = null;
        CachedRowSet cachedResultSet = null;
        try {
            connection = this.getConnection();


            stat = connection.createStatement();

            sortedResultSet = stat.executeQuery(queryString);

            cachedResultSet = new CachedRowSetImpl();
            cachedResultSet.populate(sortedResultSet);


            this.resultSetMetadata.setRowCount(this.getResultSetRowCount(sortedResultSet));


            stat.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (stat != null) {
                    stat.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


        return cachedResultSet;


    }


    private int getResultSetRowCount(ResultSet rs) {
        int size = 0;
        try {
            rs.last();
            size = rs.getRow();
            rs.beforeFirst();
        } catch (Exception ex) {
            return 0;
        }
        return size;

    }

    public ResultSetMetadata getResultSetMetadata() {
        return resultSetMetadata;
    }

    public void setResultSetMetadata(ResultSetMetadata resultSetMetadata) {
        this.resultSetMetadata = resultSetMetadata;
    }

    public TablePojo getTableMetadataPojoFromTable(String tableName) {
        TablePojo tbPojo = new TablePojo();
        tbPojo.setTableName(tableName);
        TreeMap<String, String> columns = getColumnNamesWithoutMetadataSortedAlphabetically(tableName);
        Connection connection = null;
        try {
            connection = getConnection();
            for (Map.Entry<String, String> column : columns.entrySet()) {
                ColumnPojo columnPojo = new ColumnPojo();
                String columnName = column.getKey();
                columnPojo.setColumnName(columnName);


                DatabaseMetaData metadata = connection.getMetaData();

                ResultSet resultSet = metadata.getColumns(connection.getCatalog(), null, tableName, columnName);
                while (resultSet.next()) {
                    String name = resultSet.getString("COLUMN_NAME");
                    String type = resultSet.getString("TYPE_NAME");
                    int size = resultSet.getInt("COLUMN_SIZE");
                    columnPojo.setDataType(type);
                    columnPojo.setColumnLength(size);

                }
                tbPojo.getColumnsMap().put(columnName, columnPojo);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }

        return tbPojo;


    }

    /**
     * Check if the length of a column in the database needs to be increased
     *
     * @param input
     * @param tablePojo
     */
    public void increaseColumnLength(String input, TablePojo tablePojo) throws SQLException {
        TreeMap<String, ColumnPojo> columnPojoTreeMap = tablePojo.getColumnsMap();
        for (Map.Entry<String, ColumnPojo> column : columnPojoTreeMap.entrySet()) {
            String columnName = column.getKey();
            ColumnPojo columnPojo = column.getValue();

            if (column.getValue().getDataType().equalsIgnoreCase("VARCHAR") && input.length() > column.getValue().getColumnLength()) {
                Connection connection = null;
                try {
                    connection = getConnection();

                    String alterTable = "ALTER TABLE " +
                            tablePojo.getTableName() + " MODIFY " + columnName + " VARCHAR(" + columnPojo.getColumnLength() + 1 + ");";

                    Statement statement = connection.createStatement();
                    statement.executeUpdate(alterTable);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                } finally {

                    if (connection != null) {
                        connection.close();
                    }

                }


            }

        }


    }

    /**
     * Get the connection from the connection pool
     *
     * @return
     */
    private Connection getConnection() {
        Connection connection = null;
        Session session = HibernateUtilData.getSessionFactory().openSession();
        SessionImpl sessionImpl = (SessionImpl) session;
        connection = sessionImpl.connection();
        return connection;

    }

    /**
     * Creates the INNER JOIN part of the Query which retrieves only the latest record.
     * Example: SELECT
     * outerGroup.ID_SYSTEM_SEQUENCE,
     * outerGroup.id,
     * outerGroup.first_name,
     * outerGroup.last_name,
     * outerGroup.email,
     * outerGroup.country,
     * outerGroup.ip_address,
     * outerGroup.INSERT_DATE,
     * outerGroup.LAST_UPDATE,
     * outerGroup.RECORD_STATUS
     * FROM
     * stefan_test AS outerGroup
     * INNER JOIN
     * (SELECT
     * id, max(LAST_UPDATE) AS mostRecent
     * FROM
     * stefan_test AS innerSelect
     * WHERE
     * (innerSELECT.RECORD_STATUS = 'inserted'
     * OR innerSELECT.RECORD_STATUS = 'updated')
     * GROUP BY id) innerGroup ON outerGroup.id = innerGroup.id
     * AND outerGroup.LAST_UPDATE = innerGroup.mostRecent;
     *
     * @param primaryKey
     * @param tableName
     * @return
     */
    public String getMostRecentVersionSQLString(String primaryKey, String tableName) {
        String innerJoinSQLString = " FROM " + tableName + " AS outerGroup INNER JOIN ( SELECT " + primaryKey + ", " +
                "max(LAST_UPDATE) AS mostRecent FROM " +
                tableName + " AS innerSELECT WHERE (innerSELECT.RECORD_STATUS = 'inserted' OR innerSELECT" +
                ".RECORD_STATUS = 'updated')" +
                " GROUP BY "
                + primaryKey
                + ") innerGroup ON outerGroup." + primaryKey + " = innerGroup." + primaryKey + " AND outerGroup" +
                ".LAST_UPDATE = innerGroup.mostRecent ";
        this.logger.info("Rewritten INNER JOIN SQL: " + innerJoinSQLString);
        return innerJoinSQLString;

    }

    /**
     * Query the database and retrieve the records. Rewrite Statement for most recent version only
     *
     * @param tableName
     * @param columnSequenceMap
     * @param sortingColumnsID
     * @param sortingDirection
     * @param filterMap
     * @param startRow
     * @param offset            @return
     */
    public CachedRowSet queryDatabaseMostRecent(String tableName, Map<Integer, String> columnSequenceMap, int sortingColumnsID,
                                                String sortingDirection, Map<String, String> filterMap,
                                                int startRow, int offset) {
        Connection connection = null;

        /*If there was no table name set in the interface, pick the first one from the selected DB
        * * */
        if (tableName == null || tableName.equals("")) {
            this.logger.warning("Get the first table of the database as there was no table selected.");
            tableName = getAvailableTablesFromDatabase(this.getDataBaseName()).get(0);


        }
        this.logger.warning("TABLE NAME in query : " + tableName);

        ResultSet rs = null;
        CachedRowSet cachedResultSet = null;
        String[] tableHeaders = null;
        Statement stat = null;

        // result set has to be sorted

        if (sortingColumnsID == 0) {
            this.logger.warning("sorting colum war null!");
        }
        if (sortingColumnsID >= 0) {
            this.logger.warning("sortingColumnsID == " + sortingColumnsID);

            Map<String, String> tableMetadata;

            try {

                cachedResultSet = new CachedRowSetImpl();
                // get column names
                tableMetadata = getTableColumnMetadata(tableName);
                this.logger.warning("Table metadata: " + tableMetadata.size());

                String sortColumn = "outerGroup." + (new ArrayList<String>(
                        tableMetadata.keySet())).get(sortingColumnsID);

                String whereClause = "";
                if (this.hasFilters(filterMap)) {
                    whereClause = this.getWhereString(filterMap);
                }

                connection = this.getConnection();

                // get primary key from the table

                String primaryKey = getPrimaryKeyFromTableWithoutLastUpdateColumns(tableName).get(0);


                this.logger.info("Primary key is: " + primaryKey);


                String selectSQL = this.createSELECTstringFromColumnMap(columnSequenceMap);

                selectSQL += this.getMostRecentVersionSQLString(primaryKey, tableName);
                selectSQL += whereClause + " ORDER BY " + sortColumn + " "
                        + sortingDirection
                        + this.getPaginationStringWithLIMIT(startRow, offset);

                this.logger.info("Final SQL String: " + selectSQL);
                stat = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);


                ResultSet sortedResultSet = stat.executeQuery(selectSQL);


                this.logger.info("NATIVE SQL STRING "
                        + connection.nativeSQL(selectSQL));

                cachedResultSet.populate(sortedResultSet);
                stat.close();
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }

                    if (stat != null) {
                        stat.close();
                    }
                } catch (SQLException sqlee) {
                    sqlee.printStackTrace();
                }
            }

        } else {
            try {
                connection = this.getConnection();


                stat = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                // String query = "SELECT * FROM  "+this.tableName;

                // stat = this.getConnection().createStatement();
                String query = "SELECT * FROM  " + tableName;
                System.out.println(query);
                rs = stat.executeQuery(query);
                cachedResultSet.populate(rs);
                stat.close();
                connection.close();
            } catch (SQLException e) {

                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                    if (stat != null) {
                        stat.close();
                    }
                } catch (SQLException sqlee) {
                    sqlee.printStackTrace();
                }
            }
        }
        return cachedResultSet;
    }

    /* Needed for the evaluation
    *
    * */
    public int insertSingleRecordIntoTable(String tableName, List<String> newRow) {

        Connection connection = null;
        int amountOfColumns = newRow.size();
        int currentTableRowCount = -1;
        currentTableRowCount = this.getRowCount(tableName);
        long startTime = 0;

        try {
            connection = this.getConnection();


            if (connection.getAutoCommit()) {
                //this.logger.info("AUTO COMMIT OFF");
                connection.setAutoCommit(false);
            }
            PreparedStatement preparedStatement;


            String placeholders = "(?,";
            for (int i = 0; i < amountOfColumns; i++) {
                placeholders += "?,";
            }

            // If there is no hash column, then only append the two time stamp cols
            placeholders += "?,?)";


            String insertString = "INSERT INTO " + tableName + " VALUES "
                    + placeholders;
            preparedStatement = connection.prepareStatement(insertString);


            for (int columnCount = 1; columnCount <= amountOfColumns + 4; columnCount++) {
                // this.logger.info("columns Count : " + columnCount +
                // " _ header count = " + header.length);
                // first column contains sequence
                if (columnCount == 1) {
                    preparedStatement.setInt(columnCount, currentTableRowCount);

                    // column values (first column is the id)
                } else if (columnCount > 1
                        && columnCount <= (amountOfColumns + 1)) {

                    // index starts at 0 and the counter at 1.
                    preparedStatement.setString(columnCount,
                            newRow.get(columnCount - 2));

                    // insert timestamps
                } else if (columnCount == (amountOfColumns + 2)
                        || columnCount == (amountOfColumns + 3)) {

                    preparedStatement.setDate(columnCount, null);

                    // insert the hash
                }
            }

            //this.logger.info("prepared statement before exec: " + preparedStatement.toString());
            preparedStatement.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);

                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }


        }
        return currentTableRowCount;
    }

    /**
     * Execute the evaluation
     *
     * @param sql
     */
    public void executeQueryForEvaluation(String sql) {
        this.logger.info("Execute: " + sql);
        Connection connection = null;
        Statement statement = null;
        try {
            connection = this.getConnection();
            statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }

    }


    /**
     * Select a random date which is between the min ans max of the last update
     *
     * @param tableName
     * @return
     */
    public Date getRandomDateBetweenMinAndMax(String tableName) {

        Date randomDate = null;
        Connection connection = null;
        Statement statement = null;
        try {
            connection = this.getConnection();
            statement = connection.createStatement();
            String sql = "SELECT FROM_UNIXTIME( UNIX_TIMESTAMP(	MIN(LAST_UPDATE) )+ FLOOR( 	RAND() * (UNIX_TIMESTAMP" +
                    "(MAX(LAST_UPDATE)) - UNIX_TIMESTAMP(	MIN(LAST_UPDATE) )+1))) AS randomDate " +
                    "FROM " + tableName + " WHERE RECORD_STATUS<>\'deleted\';";
            logger.info(sql);
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                randomDate = resultSet.getTimestamp("randomDate");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (statement != null) {
                    statement.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }
        this.logger.info("Computing random Date: " + randomDate);
        return randomDate;

    }

    /**
     * Retrieves all columns for a table as result set, not including metadata columns
     *
     * @return
     */
    public CachedRowSet getAllColumnsWithoutMetadataAsResultSet(String tableName) {

        CachedRowSet cachedResultSet = null;

        String columnsSQL = "SELECT " + this.getConcatenatedColumnNamesWithoutMetadata(tableName);
        String innerSQL = getMostRecentVersionSQLString("ID_SYSTEM_SEQUENCE", tableName);
        String query = columnsSQL + " " + innerSQL;


        java.sql.PreparedStatement stmt;
        Connection connection = null;
        ResultSet rs = null;
        try {
            cachedResultSet = new CachedRowSetImpl();
            connection = this.getConnection();
            stmt = connection.prepareStatement(query);
            rs = stmt.executeQuery();
            cachedResultSet.populate(rs);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }

            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }
        return cachedResultSet;

    }

    /**
     * return a String of all colunmns which are not a metadata column
     *
     * @param tableName
     * @return
     */
    public String getConcatenatedColumnNamesWithoutMetadata(String tableName) {
        Map<String, String> columns = this.getColumnNamesFromTableWithoutMetadataColumns(tableName);
        Set<String> colNames = columns.keySet();
        return Joiner.on(",").join(colNames);

    }

    /**
     * Export a result set to a CSV file
     *
     * @param crs
     * @param exportCSVPath
     */
    public void exportResultSetAsCSV(CachedRowSet crs, String exportCSVPath) {
        CSVWriter wr = null;
        try {
            wr = new CSVWriter(new FileWriter(exportCSVPath), ',');
            wr.writeAll(crs, true);
            wr.flush();
            wr.close();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    /**
     * Get a list of columns without metadata columns
     *
     * @param tableName
     * @return
     */
    public List<String> getColumnsFromDatabaseAsListWithoutMetadata(String tableName) {


        List<String> availableColumnsList = new ArrayList<String>();
        Map<String, String> availableColumnsMap = this.getTableColumnMetadata(tableName);

        for (Map.Entry<String, String> entry : availableColumnsMap.entrySet()) {


            String columnName = entry.getKey();
            availableColumnsList.add(columnName);

        }

        availableColumnsList.remove("ID_SYSTEM_SEQUENCE");
        availableColumnsList.remove("INSERT_DATE");
        availableColumnsList.remove("LAST_UPDATE");
        availableColumnsList.remove("RECORD_STATUS");
        availableColumnsList.remove("SHA1_HASH");


        return availableColumnsList;
    }

    /**
     * Check if a record having the SEQUENCE number
     *
     * @param tableName
     * @param sequenceNumber
     * @return
     */
    public boolean checkIfRecordExistsInTableBySequenceNumber(String tableName, int sequenceNumber) {
        Connection connection = null;
        Statement checkRecordExistance = null;
        int existsInteger = 0;
        try {
            connection = this.getConnection();


            checkRecordExistance = connection.createStatement();
            String checkSQL = "SELECT EXISTS ( SELECT 1 FROM " + tableName + " WHERE ID_SYSTEM_SEQUENCE = " +
                    sequenceNumber + ") AS recordDoesExist;";

            this.logger.info("CHECK SQL: " + checkSQL);
            ResultSet maxSequenceResult = checkRecordExistance.executeQuery(checkSQL);
            existsInteger = -1;
            if (maxSequenceResult.next()) {
                existsInteger = maxSequenceResult.getInt("recordDoesExist");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
                if (checkRecordExistance != null) {
                    checkRecordExistance.close();
                }
            } catch (SQLException sqlee) {
                sqlee.printStackTrace();
            }
        }


        if (existsInteger == 1) {
            this.logger.info("The record exists");
            return true;
        } else {
            this.logger.info("The record does NOT exist.");
            return false;
        }


    }

    /**
     * Drop the primary key and add a new one
     *
     * @param tableName
     * @param primaryKeyColumn
     */
    public void updatePrimaryKey(String tableName, String primaryKeyColumn) {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            String alterTable = "ALTER TABLE " +
                    tableName + " DROP PRIMARY KEY ";
            statement = connection.createStatement();
            statement.executeUpdate(alterTable);
            alterTable = "ALTER TABLE " + tableName + " ADD PRIMARY KEY (" + primaryKeyColumn + ", LAST_UPDATE, RECORD_STATUS)";
            statement.executeUpdate(alterTable);
            connection.close();
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

        }


    }


}
