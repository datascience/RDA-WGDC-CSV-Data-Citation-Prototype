package DatatableModel;



import Database.DatabaseOperations.DatabaseQueries;
import Database.DatabaseOperations.DatabaseTools;
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


    public TableDataOperations() {
        this.logger = Logger.getLogger(this.getClass().getName());

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
        ResultSet resultSet = null;
        try {
            connection = this.getConnection();

            DatabaseMetaData meta = connection.getMetaData();
            CachedRowSetImpl cachedResultSet = new CachedRowSetImpl();
            resultSet = connection.getMetaData().getCatalogs();

            while (resultSet.next()) {
                listOfDatabases.add(resultSet.getString("TABLE_CAT"));
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
                if (resultSet != null) {
                    resultSet.close();
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
        DatabaseTools dbTools = new DatabaseTools();

        Map<String, String> tableMetadata;
        String columnName = null;

        tableMetadata = dbTools.getTableColumnMetadata(this.tableName);
            columnName = (new ArrayList<String>(tableMetadata.keySet())).get(columnID);
        ;

        return columnName;

    }

    /* Execute Query
    *
    * * * */
    public CachedRowSet executeQuery(String tableName, int sortingColumnsID,
                                     String sortingDirection, Map<String, String> filterMap,
                                     int startRow, int offset) {
        DatabaseQueries dbQuery = new DatabaseQueries();

        CachedRowSet cachedRowSet = dbQuery.queryDatabase(tableName, sortingColumnsID,
                sortingDirection, filterMap, startRow, offset);

        return cachedRowSet;
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


}
