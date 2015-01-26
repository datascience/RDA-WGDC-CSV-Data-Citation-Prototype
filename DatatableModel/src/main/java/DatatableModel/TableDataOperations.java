package DatatableModel;



import Database.DatabaseOperations.DatabaseQueries;
import Database.DatabaseOperations.HikariConnectionPool;
import JSON.JSONArray;
import JSON.JSONException;
import JSON.JSONObject;
import com.sun.rowset.CachedRowSetImpl;


import javax.sql.rowset.CachedRowSet;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by stefan on 20.06.14.
 */
public class TableDataOperations {


    private Logger logger;
    private String tableName;
    private HikariConnectionPool dbcp;

    public TableDataOperations() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.dbcp = new HikariConnectionPool();



    }




    /**
     * Get a map of <ColumnName, ColumnType>
     */
    public Map<String, String> getTableColumnMetadata(String tableName)
            throws SQLException {
//        this.logger.info("Table column metadata - table name is" + tableName);
        // ACHTUNG

        if (tableName == null) {
            System.out.println("Table war null -..................> setze auf MillionSongs");
            // tableName = "MillionSongs";
        }
        Connection connection = this.dbcp.getConnection();
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
        Connection connection = this.dbcp.getConnection();
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





    public int getRowCount(String tableName) throws SQLException {
        // TODO SQL injection
        String sql = "SELECT COUNT(*) FROM " + tableName;
        int numberOfRecords = -1;
        Connection connection = this.dbcp.getConnection();
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
     * @throws java.sql.SQLException Provides the table data as DatatableModel.JSON
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
        // this.logger.warning("DatatableModel.JSON: " + prettyJSON);

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

    /**
     * Get list of databases
     * @return
     */
    public List<String> getAvailableDatabases() {
        Connection connection = null;
        List<String> listOfDatabases = new ArrayList<String>();

        try {
            connection = this.dbcp.getConnection();

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

    public CachedRowSet executeQuery(String tableName, int sortingColumnsID,
                                     String sortingDirection, Map<String, String> filterMap,
                                     int startRow, int offset) {
        DatabaseQueries dbQuery = new DatabaseQueries();

        CachedRowSet cachedRowSet = dbQuery.queryDatabase(tableName, sortingColumnsID,
                sortingDirection, filterMap, startRow, offset);

        return cachedRowSet;
    }


}
