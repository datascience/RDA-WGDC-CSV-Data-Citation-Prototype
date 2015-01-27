
package Database.DatabaseOperations;


import CSVTools.CSV_API;
import CSVTools.Column;
import com.sun.rowset.CachedRowSetImpl;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.sql.rowset.CachedRowSet;
import java.io.FileNotFoundException;
import java.io.FileReader;
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
    private HikariConnectionPool pool;
    public DatabaseTools(String dataBaseName) {
        this.logger = Logger.getLogger(this.getClass().getName());
        /*
         * // start the TCP Server try {
		 *
		 * Server server = Server.createTcpServer().start(); } catch
		 * (SQLException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); }
		 */
        this.dataBaseName = dataBaseName;


    }
    /**
     * Constructor that connects with the default database schema
     * DATABASE_SCHEMA
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public DatabaseTools() throws ClassNotFoundException {
        this.logger = Logger.getLogger(this.getClass().getName());


    }

    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public Logger getLogger() {
        return logger;
    }

    /**
     * Get the connection from the connection pool
     *
     * @return
     */
    private Connection getConnection() throws SQLException {
        HikariConnectionPool pool = HikariConnectionPool.getInstance();
        Connection connection = null;


        connection = pool.getConnection();

        return connection;

    }

    public void importCSVtoDatabase(String csvFileName, String tableName) {
        this.tableName = tableName;
        Statement stat = null;
        Connection connection = null;
        try {
            connection = this.getConnection();

            stat = connection.createStatement();

            CSV_API csv;
            csv = new CSV_API();
            String headersSQL = null;
            headersSQL = csv.getHeadersOfCSV(csvFileName);


            stat.execute("DROP TABLE IF EXISTS " + this.tableName);
            String sqlCREATE = "CREATE TABLE " + this.tableName + " " + headersSQL
                    + "  AS SELECT * FROM CSVREAD( \'" + csvFileName + "\' );";
            System.out.println(sqlCREATE);
            stat.execute(sqlCREATE);
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
                System.out.println("TEEEEEE: " + selectSQL);

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
     * Get a map of <ColumnName, ColumnType> but remove the automatically generated metadata columns from the map:
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


    /**
     * Create a new database from a CSV file. DROPs database if exists!! Appends
     * a id column for the sequential numbering and a sha1 hash column
     *
     * @param columnMetadata
     * @param tableName
     * @param calculateHashKeyColumn
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public void createSimpleDBFromCSV(Column[] columnMetadata, String tableName, boolean calculateHashKeyColumn)
            throws ClassNotFoundException {
        Statement stat = null;
        Connection connection = null;
        String createTableString = "CREATE TABLE " + tableName
                + " ( ID_SYSTEM_SEQUENCE INTEGER PRIMARY KEY AUTO_INCREMENT";

        for (int i = 0; i < columnMetadata.length; i++) {
            createTableString += " , " + columnMetadata[i].getColumnName()
                    + " VARCHAR(" + columnMetadata[i].getMaxContentLength()
                    + ") ";

        }
        createTableString += ", INSERT_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "LAST_UPDATE TIMESTAMP DEFAULT 0 ";

        // If hash key should be computed during
        if (calculateHashKeyColumn) {
            createTableString += ", sha1_hash CHAR(40) NOT NULL ";

        }

        // Finalize SQL String

        createTableString += ");";

        this.logger.info("CREATE String: " + createTableString);
        try {
            connection = this.getConnection();
            stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            stat.execute("DROP TABLE IF EXISTS " + tableName);
            stat.execute(createTableString);

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
            CSV_API csvAPI = new CSV_API();
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

                        String appendedColumns = CSV_API
                                .convertStringListToAppendedString(row);
                        String hash = CSV_API
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
    private String getWhereString(Map<String, String> filtersMap) {
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
    public List<String> getADatabaseCatalogFromDatabaseConnection() {
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


    /**
     * @param databaseName
     * @return
     */
    public List<String> getAvailableTablesFromDatabase(String databaseName) {
        Connection connection = null;
        List<String> listOfTables = new ArrayList<String>();

        try {
            connection = this.getConnection();

            DatabaseMetaData meta = connection.getMetaData();
            String[] types = {"TABLE"};
            this.logger.info("retrieve metadata for " + databaseName);
            ResultSet rs = meta.getTables(databaseName, null, null, types);
            while (rs.next()) {
                // System.out.println("getAvailableTablesFromDatabase__________________");
                String tableName = rs.getString("TABLE_NAME");

                this.logger.info("   " + rs.getString("TABLE_CAT") + ", "
                        + rs.getString("TABLE_SCHEM") + ", "
                        + rs.getString("TABLE_NAME") + ", "
                        + rs.getString("TABLE_TYPE") + ", "
                        + rs.getString("REMARKS"));

                listOfTables.add(tableName);
                this.logger.info(tableName);

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
        CSV_API csvAPI = new CSV_API();
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

            schema = connection.getSchema();
            catalog = connection.getCatalog();


            DatabaseMetaData databaseMetaData = null;

            databaseMetaData = this.getConnection().getMetaData();

            result = databaseMetaData.getPrimaryKeys(
                    catalog, schema, tableName);

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
    public List<String> getPrimaryKeyFromTableWithoutMetadataColumns(String tableName) {
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
    * Create the WHERE clause for a full compare
    * */
    private String recordExistsWhereClause(Map<String, String> columnsMap, List<String> csvRow) {
        int columnCounter = -1;
        StringBuilder sb = new StringBuilder();
        String currentCheck = " WHERE ";
        sb.append(currentCheck);

        for (Map.Entry<String, String> entry : columnsMap.entrySet()) {

            columnCounter++;

            String columnNameInDB = entry.getKey();
            String columnType = entry.getValue();
            String csvRowValue = csvRow.get(columnCounter);
            this.logger.info("Compare -------------- : " + columnNameInDB + " " + columnType + " " + csvRowValue);

            sb.append(columnNameInDB);
            sb.append("= \"" + csvRowValue + "\"");
            sb.append(" AND ");

        }
        if (sb.toString().endsWith(" AND ")) {
            this.logger.info("The WHERE string ends with AND ");
            sb.setLength(sb.length() - " AND ".length());

        }
        String whereClause = sb.toString();
        this.logger.info("This is the WHERE string: " + whereClause);
        return whereClause;

    }
}
