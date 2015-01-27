
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


import Database.DatabaseOperations.DatabaseTools;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stefan on 04.07.14.
 */
@ManagedBean
@SessionScoped
public class DatabaseTableNameBean implements Serializable {

    private Session session = null;
    private Logger logger;

    public DatabaseTableNameBean() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Database Name Bean");
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<String> getDatabaseNames() {
        return databaseNames;
    }

    public void setDatabaseNames(List<String> databaseNames) {
        this.databaseNames = databaseNames;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    private String databaseName;
    private List<String> databaseNames;
    private String tableName;
    private List<String> tableNames;

    private DatabaseTools dbtools;

    @PostConstruct
    public void init() {
        try {

            dbtools = new DatabaseTools();
            // this only returns the database schema specified in the connection profile.
            databaseNames = dbtools.getADatabaseCatalogFromDatabaseConnection();

            

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeDatabaseName(ValueChangeEvent event) {

        this.logger.info(event.getComponent().toString() + " " + event.toString());

        String selectedDB = event.getNewValue().toString();
        this.logger.info("Databasename = " + selectedDB);
        SessionManager sm = new SessionManager();
        sm.storeSessionData("currentDatabaseName", selectedDB);

        this.tableNames = this.dbtools.getAvailableTablesFromDatabase(selectedDB);


    }

    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeTableName(ValueChangeEvent event) {
        this.logger.info(event.getComponent().toString() + " " + event.toString());

        String selectedTable = event.getNewValue().toString();
        this.logger.info("selected table name CHANGED  = " + selectedTable);
        SessionManager sm = new SessionManager();
        sm.storeSessionData("currentTableName", selectedTable);

    }





}
