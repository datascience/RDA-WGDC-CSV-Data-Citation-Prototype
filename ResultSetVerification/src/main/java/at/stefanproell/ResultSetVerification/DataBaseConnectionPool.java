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

package at.stefanproell.ResultSetVerification;

import com.jolbox.bonecp.BoneCPDataSource;

import javax.sql.DataSource;
import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DataBaseConnectionPool {
    private final String DATABASE_URL = "jdbc:mysql://localhost:3306/";
    private final String DATABASE_SCHEMA = "CITATION_DB";
    private final String DATABASE_CONNECTION_STRING = DATABASE_URL + DATABASE_SCHEMA;
    private final String USER_NAME = "querystoreuser";
    private final String PASSWORD = "query2014";

    private BoneCPDataSource dataSource = null;
    private Connection connection;
    private String dataBaseName;
    private String tableName;
    private Logger logger;


    /**
     * Constructor
     */
    public DataBaseConnectionPool() {
        this.logger = Logger.getLogger(this.getClass().getName());

        this.dataBaseName = DATABASE_SCHEMA;
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }    // load the DB driver
        this.setDataSource(new BoneCPDataSource());  // create a new datasource object
        this.dataSource.setJdbcUrl(DATABASE_URL + this.dataBaseName);        // set the JDBC url

        this.dataSource.setUser(USER_NAME);
        this.dataSource.setPassword(PASSWORD);

        try {
            this.setConnection(dataSource.getConnection());
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }


    /**
     * Get connection.
     *
     * @return
     */
    public Connection getConnection() {
        if (this.connection != null) {
            this.logger.info("Connection retrieved");
            try {
                if (this.connection.isClosed()) {

                    this.connection = this.dataSource.getConnection();
                }
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return this.connection;
        } else

            return null;

    }


    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public BoneCPDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(BoneCPDataSource dataSource) {
        this.dataSource = dataSource;
    }


    public String getDataBaseName() {
        return dataBaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public Logger getLogger() {
        return logger;
    }


}
