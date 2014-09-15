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

package Database;


import CSVTools.CSVHelper;
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
import java.util.logging.Logger;

/**
 * Created by stefan on 03.09.14.
 */
public class DatabaseTools {
    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public Logger getLogger() {
        return logger;
    }

    private String dataBaseName;
    private String tableName;
    private Logger logger;

    private final int MAXCONNECTIONS = 1;
    private final String DATABASE_SCHEMA = "CITATION_DB";
    //private final String DATABASE_URL = "jdbc:h2:file:~/Development/workspaceDevelopment/Datatable-CSV/databases/";
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/";

    private final String USER_NAME = "sa";
    private final String PASSWORD = "sa";


    private Connection connection;

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
        DataBaseConnectionPool datasource = new DataBaseConnectionPool();

        this.connection = datasource.getConnection();


    }


    /**
     * Constructor that connects with the default database schema
     * DATABASE_SCHEMA
     *
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public DatabaseTools() throws SQLException, ClassNotFoundException {
        this.logger = Logger.getLogger(this.getClass().getName());
        DataBaseConnectionPool datasource = new DataBaseConnectionPool();

        this.setConnection(datasource.getConnection());
    }

    public Connection getConnection() {
        if (this.connection != null) {
            this.logger.warning("get connection");
            try {
                if (this.connection.isClosed()) {

                    DataBaseConnectionPool datasource = new DataBaseConnectionPool();

                    this.setConnection(datasource.getConnection());
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return this.connection;
        } else

            return null;

    }

    public void importCSVtoDatabase(String csvFileName, String tableName)
            throws SQLException {
        this.tableName = tableName;
        Statement stat;
        Connection connection = this.getConnection();

        stat = connection.createStatement();

        CSVHelper csv;
        csv = new CSVHelper();
        String headersSQL = null;
        headersSQL = csv.getHeadersOfCSV(csvFileName);


        stat.execute("DROP TABLE IF EXISTS " + this.tableName);
        String sqlCREATE = "CREATE TABLE " + this.tableName + " " + headersSQL
                + "  AS SELECT * FROM CSVREAD( \'" + csvFileName + "\' );";
        System.out.println(sqlCREATE);
        stat.execute(sqlCREATE);
        stat.close();
        connection.close();
        // this.connection.close();

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

    public List<List<String>> getRowList(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        final int columnCount = meta.getColumnCount();
        List<List<String>> rowList = new LinkedList<List<String>>();
        while (rs.next()) {
            List<String> columnList = new LinkedList<String>();
            rowList.add(columnList);

            for (int column = 1; column <= columnCount; column++) {
                Object value = rs.getObject(column);
                if (value != null) {
                    columnList.add((String) value);
                } else {
                    columnList.add("null"); // you need this to keep your
                    // columns in sync....
                }
            }
        }
        rs.close();
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
/*
    *//**
     * @param rs
     * @return
     * @throws JSONException
     * @throws SQLException  Provides the table data as DatatableModel.JSON
     *//*
    public String getJSON(CachedRowSet rs, JQueryDataTableParamModel param)
            throws JSONException, SQLException {

        JSONObject jsonObject = new JSONObject();

        ResultSetConverter rsc = new ResultSetConverter();
        JSONArray aaDataJSONArray = rsc.convert(rs);

        jsonObject.put("sEcho", param.sEcho);
        jsonObject.put("iTotalDisplayRecords", param.iTotalDisplayRecords);
        jsonObject.put("iTotalRecords", param.iTotalRecords);

        jsonObject.put("aaData", aaDataJSONArray);

        String prettyJSON = jsonObject.toString(4);
        // this.logger.warning("DatatableModel.JSON: " + prettyJSON);

        // https://stackoverflow.com/questions/14258640/hash-map-array-list-to-json-array-in-android
        return prettyJSON;

    }*/

/*    public static String getJSON(CachedRowSet rs) throws JSONException, SQLException {

        JSONObject jsonObject = new JSONObject();
        ResultSetConverter rsc = new ResultSetConverter();
        JSONArray aaDataJSONArray = rsc.convert(rs);

        jsonObject.put("aaData", aaDataJSONArray);

        String prettyJSON = jsonObject.toString(4);

        rs.close();
        // https://stackoverflow.com/questions/14258640/hash-map-array-list-to-json-array-in-android
        return prettyJSON;

    }*/

	/*
     * public void close() throws SQLException { this.connection.close();
	 * this.logger.warning("DB Connection closed");
	 *
	 * }
	 */

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
     * Get a map of <ColumnName, ColumnType> but remove the automatically generated metadata columns from the map:
     * ID_SYSTEM_SEQUENCE, INSERT_DATE, LAST_UPDATE, RECORD_STATUS
     */
    public Map<String, String> getColumnNamesFromTableWithoutMetadataColumns(String tableName)
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

        // remove the sequence nu,ber, timestamps and status columns
        columnMetadataMap.remove("ID_SYSTEM_SEQUENCE");
        columnMetadataMap.remove("INSERT_DATE");
        columnMetadataMap.remove("LAST_UPDATE");
        columnMetadataMap.remove("RECORD_STATUS");


        return columnMetadataMap;

    }

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
            throws SQLException, ClassNotFoundException {
        Statement stat;
        Connection connection;
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
        connection = this.getConnection();
        stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        stat.execute("DROP TABLE IF EXISTS " + tableName);
        stat.execute(createTableString);

        stat.close();
        connection.close();

    }

    public void insertCSVDataIntoDB(String path, String tableName,
                                    boolean hasHeaders, boolean calculateHashKeyColumn) throws IOException,
            SQLException {

        Connection connection = this.getConnection();
        if (connection.getAutoCommit()) {
            //this.logger.info("AUTO COMMIT OFF");
            connection.setAutoCommit(false);
        }
        PreparedStatement preparedStatement;
        CSVHelper csvHelper = new CSVHelper();
        CsvListReader reader = null;
        int rowCount = 0;
        try {
            reader = new CsvListReader(new FileReader(path),
                    CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        ICsvListReader listReader = null;
        try {
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

                        String appendedColumns = CSVHelper
                                .convertStringListToAppendedString(row);
                        String hash = CSVHelper
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
        } finally {
            if (listReader != null) {
                listReader.close();
            }
            connection.setAutoCommit(true);
            reader.close();
            connection.close();

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
     * Get a list of database names
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

                System.out.println("   " + rs.getString("TABLE_CAT") + ", "
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

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public ResultSet getCompleteResultSetFromTable(String tableName) {
        java.sql.PreparedStatement stmt;
        ResultSet rs = null;
        try {
            stmt = connection.prepareStatement("SELECT * FROM `" + tableName + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        ResultSet rs = null;
        String sql = "SELECT " + columnsAsList + " FROM `" + tableName + "`";
        this.logger.info("SQL Statements" + sql);
        try {
            stmt = connection.prepareStatement(sql);
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);


        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
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
        CSVHelper csvHelper = new CSVHelper();
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

                    resultSetHash = csvHelper.calculateSHA1HashFromString(currentHash);
                    // this.logger.info("First Hash! Original: " + currentHash + " First new Hash " +  resultSetHash);
                } else {
			/*		// Move the cursor to the previous row and read the hash value.
					if(cached.previous()){
						previousKey = cached.getString("sha1_hash");
						if(cached.next()){
							compositeKey = currentKey + previousKey;
							resultSetHash = csvHelper.calculateSHA1HashFromString(compositeKey);
							this.logger.info("Appended Hash " + previousKey + " to hash " + currentKey + " and
							calulated " + resultSetHash);

						}
					}*/

                    compositeHash = (resultSetHash + currentHash);
                    newResultSetHash = csvHelper.calculateSHA1HashFromString(compositeHash);
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

}
