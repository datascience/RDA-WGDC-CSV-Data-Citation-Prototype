package DatatableModel;



import Database.DatabaseOperations.DatabaseTools;
import Database.DatabaseOperations.HikariConnectionPool;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TableMetadata {

    private DatabaseTools dbtools;

    private Logger logger;

    public TableMetadata() {
        this.logger = Logger.getLogger(this.getClass().getName());
        HikariConnectionPool pool = HikariConnectionPool.getInstance();

        this.dbtools = new DatabaseTools();
        


    }


    /**
     * Provides the HTML Code for building the table headers
     *
     * @return
     * @throws java.sql.SQLException
     */
    public static String getTableHeadersAsHTML(String tableName) throws SQLException {
        DatabaseTools dbTools = new DatabaseTools();
        Map<String, String> columnMap = dbTools.getTableColumnMetadata(tableName);
        String htmlTableHeaders = "<thead>\n\t<tr>\n";
        Iterator it = columnMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            htmlTableHeaders += " <th><u>" + pairs.getKey() + "</u></th>\n";
            it.remove(); // avoids a ConcurrentModificationException
        }
        htmlTableHeaders += "</tr>\n</thead>\n";


        return htmlTableHeaders;
    }


    /**
     * Provides the HTML Code for building the table headers
     *
     * @return
     * @throws java.sql.SQLException
     */
    public static String getTableHeadersAsHTML(List<String> selectedColumns) throws SQLException {

        String htmlTableHeaders = "<thead>\n\t<tr>\n";
        for (int i = 0; i < selectedColumns.size(); i++) {

            htmlTableHeaders += " <th><u>" + selectedColumns.get(i) + "</u></th>\n";

        }
        htmlTableHeaders += "</tr>\n</thead>\n";


        return htmlTableHeaders;
    }

    public static String getDataTablesMDataProp(String tableName) throws SQLException {
        DatabaseTools dbTools = new DatabaseTools();
        Map<String, String> columnMap = dbTools.getTableColumnMetadata(tableName);

        // If the value of a column is null, datatables shows an error
        // This string is shown as a default value
        String defaultContent = ",\"sDefaultContent\":\"(Data n/a)\"";

        String dataTablesMDataProp = " [ ";

        Iterator it = columnMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>) it.next();
            dataTablesMDataProp += "{ \"mDataProp\": \"" + pairs.getKey() + "\"" + defaultContent
                    + "},\n";

            it.remove(); // avoids a ConcurrentModificationException
        }

        dataTablesMDataProp += " ] ";


        return dataTablesMDataProp;
    }

    public static String getDataTablesMDataProp(List<String> selectedColumns) throws SQLException {


        // If the value of a column is null, datatables shows an error
        // This string is shown as a default value
        String defaultContent = ",\"sDefaultContent\":\"(Data n/a)\"";

        String dataTablesMDataProp = " [ ";


        for (int i = 0; i < selectedColumns.size(); i++) {
            dataTablesMDataProp += "{ \"mDataProp\": \"" + selectedColumns.get(i) + "\"" + defaultContent
                    + "},\n";


        }

        dataTablesMDataProp += " ] ";


        return dataTablesMDataProp;
    }

    

    // returns the tags required for the filter input fields
    public static String getColumnFilterColumnns(String tableName) throws SQLException {
        DatabaseTools dbTools = new DatabaseTools();
        Map<String, String> columnMap = dbTools.getTableColumnMetadata(tableName);
        String columnsList = "";
        for (int i = 0; i < columnMap.values().size(); i++) {
            columnsList += "{ type: \"text\"}, \n";

        }


        if (columnsList.endsWith(",")) {
            columnsList = columnsList.substring(0, columnsList.length() - 1);
        }

        columnsList += "";
        return columnsList;
    }

    // returns the tags required for the filter input fields for selected columns
    public static String getColumnFilterColumnns(List<String> selectedColumns) throws SQLException {

        String columnsList = "";
        for (int i = 0; i < selectedColumns.size(); i++) {
            columnsList += "{ type: \"text\"}, \n";

        }


        if (columnsList.endsWith(",")) {
            columnsList = columnsList.substring(0, columnsList.length() - 1);
        }

        columnsList += "";
        return columnsList;
    }


    /* Render HTML for all table columns
    * * */
    public static String getTableFooterAsHTML(String tableName) throws SQLException {
        DatabaseTools dbTools = new DatabaseTools();
        int columnCount = dbTools.getNumberofColumnsPerTable(tableName);
        Map<String, String> tableMetadata = dbTools.getTableColumnMetadata(tableName);


        String emptyTableRows = "<tfoot><tr>\n";


        for (Map.Entry<String, String> row : tableMetadata.entrySet()) {
            emptyTableRows += " <th>" + row.getKey() + "</th>\n";
        }


        emptyTableRows += "</tr></tfoot>\n";


        return emptyTableRows;
    }

    /*Selected columns only
    * * */
    public static String getTableFooterAsHTML(List<String> selectedColumnsFromInterface) throws SQLException {

        int columnCount = selectedColumnsFromInterface.size();


        String emptyTableRows = "<tfoot><tr>\n";


        for (String row : selectedColumnsFromInterface) {
            emptyTableRows += " <th>" + row + "</th>\n";
        }


        emptyTableRows += "</tr></tfoot>\n";


        return emptyTableRows;
    }


    /*get all headers
    * * */
    public static String getEmptyTableHeaders(String tableName) throws SQLException {
        DatabaseTools dbTools = new DatabaseTools();
        int columnCount = dbTools.getNumberofColumnsPerTable(tableName);

        String emptyTableRows = "<tr>\n";
        for (int i = 0; i < columnCount; i++) {
            emptyTableRows += " <th></th>\n";

        }

        emptyTableRows += "</tr>\n";


        return emptyTableRows;
    }

    /* Get table headers for selected columns
    * * */
    public static String getEmptyTableHeaders(List<String> selectedColumnsFromInterface) throws SQLException {

        int columnCount = selectedColumnsFromInterface.size();

        String emptyTableRows = "<tr>\n";
        for (int i = 0; i < columnCount; i++) {
            emptyTableRows += " <th></th>\n";

        }

        emptyTableRows += "</tr>\n";


        return emptyTableRows;
    }


    public static List<String> getAvailableDatabasesAsList() {
        DatabaseTools dbTools = new DatabaseTools();

        List<String> listOfDatabases = null;
        listOfDatabases = dbTools.getAvailableDatabases();


        return listOfDatabases;

    }


    /**
     * This method is used for generating the drop down box of tables
     *
     * @return
     */
    public Map<String, String> getAvailableTablesAsMap(String databaseName) {

        Map<String, String> tableMap = null;

        HikariConnectionPool pool = HikariConnectionPool.getInstance();
        List<String> listOfTables = this.dbtools.getAvailableTablesFromDatabase(pool.getDataBaseName());
        tableMap = new HashMap<String, String>();
        for (String tableName : listOfTables) {
            tableMap.put(tableName, tableName);
        }


        return tableMap;

    }





}
