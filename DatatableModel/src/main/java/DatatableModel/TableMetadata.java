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
        System.out.println(htmlTableHeaders);

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
        System.out.println(dataTablesMDataProp);

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


    public static String getTableFooterAsHTML(String tableName) throws SQLException {
        DatabaseTools dbTools = new DatabaseTools();
        int columnCount = dbTools.getNumberofColumnsPerTable(tableName);
        Map<String, String> tableMetadata = dbTools.getTableColumnMetadata(tableName);


        String emptyTableRows = "<tfoot><tr>\n";


        for (Map.Entry<String, String> row : tableMetadata.entrySet()) {
            emptyTableRows += " <th>" + row.getKey() + "</th>\n";
        }


        emptyTableRows += "</tr></tfoot>\n";
        System.out.println(emptyTableRows);

        return emptyTableRows;
    }

    public static String getEmptyTableHeaders(String tableName) throws SQLException {
        DatabaseTools dbTools = new DatabaseTools();
        int columnCount = dbTools.getNumberofColumnsPerTable(tableName);

        String emptyTableRows = "<tr>\n";
        for (int i = 0; i < columnCount; i++) {
            emptyTableRows += " <th></th>\n";

        }

        emptyTableRows += "</tr>\n";
        System.out.println(emptyTableRows);

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
