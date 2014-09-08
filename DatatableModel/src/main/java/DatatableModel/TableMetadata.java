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

package DatatableModel;


import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class TableMetadata {
    private DataSource dataSource;
    private Logger logger;

    public TableMetadata(DataSource dataSource, Logger logger) {
        this.logger = Logger.getLogger(this.getClass().getName());
        System.out.println("DB controller");

        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/citationdatabase");
            System.out.println("Datasource added");
        } catch (NamingException e) {
            e.printStackTrace();
        }

    }


    /**
     * Provides the HTML Code for building the table headers
     *
     * @return
     * @throws java.sql.SQLException
     */
    public static String getTableHeadersAsHTML(String tableName) throws SQLException {
        TableDataOperations tableOperations = new TableDataOperations();
        Map<String, String> columnMap = tableOperations.getTableColumnMetadata(tableName);
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
        TableDataOperations tableOperations = new TableDataOperations();
        Map<String, String> columnMap = tableOperations.getTableColumnMetadata(tableName);

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
        TableDataOperations tableOperations = new TableDataOperations();
        Map<String, String> columnMap = tableOperations.getTableColumnMetadata(tableName);
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
        TableDataOperations tableOperations = new TableDataOperations();
        int columnCount = tableOperations.getNumberofColumnsPerTable(tableName);
        Map<String, String> tableMetadata = tableOperations.getTableColumnMetadata(tableName);


        String emptyTableRows = "<tfoot><tr>\n";


        for (Map.Entry<String, String> row : tableMetadata.entrySet()) {
            emptyTableRows += " <th>" + row.getKey() + "</th>\n";
        }


        emptyTableRows += "</tr></tfoot>\n";
        System.out.println(emptyTableRows);

        return emptyTableRows;
    }

    public static String getEmptyTableHeaders(String tableName) throws SQLException {
        TableDataOperations tableOperations = new TableDataOperations();
        int columnCount = tableOperations.getNumberofColumnsPerTable(tableName);

        String emptyTableRows = "<tr>\n";
        for (int i = 0; i < columnCount; i++) {
            emptyTableRows += " <th></th>\n";

        }

        emptyTableRows += "</tr>\n";
        System.out.println(emptyTableRows);

        return emptyTableRows;
    }

    public static List<String> getAvailableDatabasesAsList() {
        TableDataOperations tableOperations = new TableDataOperations();

        List<String> listOfDatabases = null;
        listOfDatabases = tableOperations.getAvailableDatabases();


        return listOfDatabases;

    }


    /**
     * This method is used for generating the drop down box of tables
     *
     * @return
     */
    public static Map<String, String> getAvailableTablesAsMap(String databaseName) {
        TableDataOperations tableOperations = new TableDataOperations();
        Map<String, String> tableMap = null;

        List<String> listOfTables = tableOperations.getAvailableTablesFromDatabase(databaseName);
        tableMap = new HashMap<String, String>();
        for (String tableName : listOfTables) {
            tableMap.put(tableName, tableName);
        }


        return tableMap;

    }


    public static List<String> getAvailableTablesFromDatabaseAsList(String databaseName) {
        TableDataOperations tableOperations = new TableDataOperations();
        List<String> listOfTables = null;

        listOfTables = tableOperations.getAvailableTablesFromDatabase(databaseName);


        return listOfTables;

    }

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


}
