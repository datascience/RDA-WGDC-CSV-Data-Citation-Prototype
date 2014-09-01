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


import JSON.JSONArray;
import JSON.JSONException;
import JSON.JSONObject;
import WebInterface.DataTableHelpers.JQueryDataTableParamModel;
import com.sun.rowset.CachedRowSetImpl;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by stefan on 20.06.14.
 */
public class TableDataOperations {


    private DataSource dataSource;
    private Logger logger;
    private String tableName;

    public TableDataOperations() {
        this.logger = Logger.getLogger(this.getClass().getName());
        try {
            Context ctx = new InitialContext();
            this.dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/citationdatabase");
            System.out.println("Datasource added");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    public CachedRowSet queryDatabase(String tableName, int sortingColumnsID,
                                      String sortingDirection, Map<String, String> filterMap,
                                      int startRow, int offset) {

        this.tableName = tableName;
        this.logger.warning("TABLE NAME in query datanase : " + this.tableName);
        Connection connection = null;
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
                tableMetadata = this.getTableColumnMetadata(tableName);
                this.logger.warning("Table metadata: " + tableMetadata.size());

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

    /**
     * Get the connection from the data source
     *
     * @return
     */
    private Connection getConnection() {

        if (dataSource == null) try {
            throw new SQLException("Can't get data source");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //get database connection
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (con == null)
            try {
                throw new SQLException("Can't get database connection");
            } catch (SQLException e) {
                e.printStackTrace();
            }


        return con;

    }

    /**
     * Get a map of <ColumnName, ColumnType>
     */
    public Map<String, String> getTableColumnMetadata(String tableName)
            throws SQLException {
//        this.logger.info("Table column metadata - table name is" + tableName);
        // ACHTUNG

        if (tableName == null) {
            System.out.println("Table war null -> setze auf MillionSongs");
            tableName = "MillionSongs";
        }
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

    private boolean hasFilters(Map<String, String> filterMap) {
        if (filterMap.size() > 0) {
            this.logger.info("There are filters");
            return true;
        } else
            return false;
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

    /*
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

    /**
     * @param rs
     * @return
     * @throws JSONException
     * @throws java.sql.SQLException Provides the table data as JSON
     */
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
        // this.logger.warning("JSON: " + prettyJSON);

        // https://stackoverflow.com/questions/14258640/hash-map-array-list-to-json-array-in-android
        return prettyJSON;

    }

    public String getJSON(CachedRowSet rs) throws JSONException, SQLException {

        JSONObject jsonObject = new JSONObject();
        ResultSetConverter rsc = new ResultSetConverter();
        JSONArray aaDataJSONArray = rsc.convert(rs);

        jsonObject.put("aaData", aaDataJSONArray);

        String prettyJSON = jsonObject.toString(4);

        rs.close();
        // https://stackoverflow.com/questions/14258640/hash-map-array-list-to-json-array-in-android
        return prettyJSON;

    }

    public List<String> getAvailableDatabases() {
        Connection connection = null;
        List<String> listOfDatabases = new ArrayList<String>();

        try {
            connection = this.getConnection();

            DatabaseMetaData meta = connection.getMetaData();
            CachedRowSetImpl cachedResultSet = new CachedRowSetImpl();
            ResultSet rs = connection.getMetaData().getCatalogs();

            while (rs.next()) {
                listOfDatabases.add(rs.getString("TABLE_CAT"));
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

    /*
    Get the name of the column by ID. Needed for building the sorting list
     */
    public String getColumnNameByID(int columnID) {
        Map<String, String> tableMetadata;
        String columnName = null;
        try {
            tableMetadata = this.getTableColumnMetadata(this.tableName);
            columnName = (new ArrayList<String>(tableMetadata.keySet())).get(columnID);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        ;

        return columnName;

    }


}
