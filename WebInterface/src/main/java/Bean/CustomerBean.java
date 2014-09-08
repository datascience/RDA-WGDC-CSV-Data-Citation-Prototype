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

package Bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 25.06.14.
 */

@ManagedBean
@SessionScoped
public class CustomerBean implements Serializable {

    private List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

    private Logger logger;
    private DataSource dataSource;

    public CustomerBean() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Constructor called");
        list = this.getListofRows();


    }

    public List<Map<String, Object>> getList() {
        return list;
    }

    public void setList(List<Map<String, Object>> list) {
        this.list = list;
    }

    private List<Map<String, Object>> getListofRows() {


        System.out.println("Get List of Rows");
        Connection connection = null;
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/citationdatabase");
            connection = dataSource.getConnection();
            System.out.println("Datasource added");
        } catch (NamingException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String selectSQL = "SELECT * FROM csvdatabase.MillionSong";
        Statement stat = null;

        try {
            stat = connection.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        ResultSet rs = null;
        try {
            rs = stat.executeQuery(selectSQL);
            this.logger.info("Execute: " + selectSQL);
            int rowCount = 0;
            if (rs.last()) {
                rowCount = rs.getRow();
                rs.beforeFirst();
                this.logger.info("Row count: " + rowCount);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        ResultSetMetaData metaData = null;
        if (rs != null) {
            try {
                metaData = rs.getMetaData();
                this.logger.info("getting metadata");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }


        int columnCount = 0;
        if (metaData != null) {
            try {
                columnCount = metaData.getColumnCount();
                this.logger.info("Column count is: " + columnCount);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        try {
            int tableRow = 0;
            while (rs.next()) {
                tableRow++;
                Map<String, Object> columns = new LinkedHashMap<String, Object>();

                for (int i = 1; i <= columnCount; i++) {
                    columns.put(metaData.getColumnLabel(i), rs.getObject(i));
                }


                rows.add(columns);
            }
            this.logger.info("added rows: " + tableRow);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rows;
    }
}
