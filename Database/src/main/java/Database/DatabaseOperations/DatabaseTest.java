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

package Database.DatabaseOperations;

import java.sql.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DatabaseTest {
    public static void main(String[] args) throws SQLException {
        String tableName = "Addressen";
        String dataBaseName = "CITATION_DB";


        HikariConnectionPool pool = HikariConnectionPool.getInstance();

        Connection conn = pool.getConnection();


        String catalog = null;
        String schemaPattern = tableName;
        String tableNamePattern = dataBaseName;
        String columnNamePattern = null;

        Map<String, String> columnMetadataMap = new LinkedHashMap<String, String>();
        String dummySQL = "SELECT * FROM " + dataBaseName + "." + tableName +
                " WHERE ID_SYSTEM_SEQUENCE > 0 AND ID_SYSTEM_SEQUENCE < 2";

        PreparedStatement pt = null;
        try {
            pt = conn.prepareStatement(dummySQL);
            ResultSet rs = pt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {

                String columnName = meta.getColumnName(i);
                String columnType = meta.getColumnTypeName(i);

                columnMetadataMap.put(columnName, columnType);
                System.out.println("Key: " + columnName + " Value " + columnType);

            }


        } catch (SQLException e) {
            e.printStackTrace();
        }


        // remove the sequence nu,ber, timestamps and status columns
        System.out.println("There are " + columnMetadataMap.size() + " columns in the table");
        columnMetadataMap.remove("ID_SYSTEM_SEQUENCE");
        columnMetadataMap.remove("INSERT_DATE");
        columnMetadataMap.remove("LAST_UPDATE");
        columnMetadataMap.remove("RECORD_STATUS");
        columnMetadataMap.remove("SHA1_HASH");
        System.out.println("Removed the metadata . Now there are " + columnMetadataMap.size() + " columns in the " +
                "table");


        DatabaseTools dbt = null;
        try {
            dbt = new DatabaseTools();
            List<String> primaryKeyList = dbt.getPrimaryKeyFromTable(tableName);


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }



    }
}
