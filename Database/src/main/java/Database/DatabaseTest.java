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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DatabaseTest {
    public static void main(String[] args) {

        DataBaseConnectionPool datasource = new DataBaseConnectionPool();

        Connection connection = datasource.getConnection();

        DataBaseConnectionPool dbcp = new DataBaseConnectionPool();
        Connection conn = dbcp.getConnection();
        String catalog = null;
        String schemaPattern = "CITATION_DB";
        String tableNamePattern = "Addressen";
        String columnNamePattern = null;


        ResultSet rsColumns = null;
        DatabaseMetaData meta = null;
        try {
            meta = conn.getMetaData();
            rsColumns = meta.getColumns(null, schemaPattern, tableNamePattern, null);

            conn.close();

            Map<String, String> columnMetadataMap = new LinkedHashMap<String, String>();

            while (rsColumns.next()) {


                // ColumnName
                String columnName = rsColumns.getString(4);

                // ColumnType
                String columnType = rsColumns.getString(6);

                columnMetadataMap.put(columnName, columnType);
                System.out.println("Key: " + columnName + " Value " + columnType);

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
