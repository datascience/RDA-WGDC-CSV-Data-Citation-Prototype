/*
 * Copyright [2015] [Stefan Pröll]
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
 * Copyright [2015] [Stefan Pröll]
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

package Database.DatabaseOperations;


import GenericTools.PropertyHelpers;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Logger;

/**
 * Hibernate session management
 */
public class HikariConnectionPool {
    private HikariDataSource dataSource;
    private Logger logger;
    private Connection connection;


    public HikariConnectionPool() {
        this.connection = null;
        this.logger = Logger.getLogger(this.getClass().getName());
        HikariConfig config = new HikariConfig();

        String filename = "db.properties";
        Properties prop = null;

        prop = PropertyHelpers.readPropertyFile(filename);

        String dbhost = prop.getProperty("dbhost");
        String dbport = prop.getProperty("dbport");
        String dbname = prop.getProperty("dbname");
        String dbuser = prop.getProperty("dbuser");
        String dbpw = prop.getProperty("dbpassword");

        String mysqlString = "jdbc:mysql://" + dbhost + ":" + dbport + "/" + dbname;
        System.out.println("db string_ " + mysqlString);
        Properties extraProperties = new Properties();


        config.setJdbcUrl(mysqlString);
        config.setUsername(dbuser);
        config.setPassword(dbpw);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        this.dataSource = new HikariDataSource(config);

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
        } else if (this.connection == null) {
            try {
                this.connection = this.dataSource.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        return this.connection;

    }

    public String getDataBaseName() {
        Connection con = this.getConnection();
        String databaseName = null;
        try {
            databaseName = con.getCatalog();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return databaseName;
    }
}