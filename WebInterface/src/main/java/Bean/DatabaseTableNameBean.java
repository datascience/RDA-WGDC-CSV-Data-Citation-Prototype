
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
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.xml.crypto.Data;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
        return this.databaseName;
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


        dbtools = new DatabaseTools();
            // this only returns the database schema specified in the connection profile.
        this.databaseNames = dbtools.getDatabaseCatalogFromDatabaseConnection();
        //this.databaseName = this.databaseNames.get(0);
        this.tableNames = this.dbtools.getAvailableTablesFromDatabase(databaseNames.get(0));
        SessionManager sm = new SessionManager();
        this.tableName = sm.getCurrentTableNameFromSession();
        if (this.tableName == null) {
            this.logger.info("Table name in init method null. using first from db");
            // check if there actually is a table in the database
            if (this.tableNames == null) {
                this.logger.severe("There are no tables yet!");
                this.tableName = this.tableNames.get(0);
            }
            
        }
        
        

        
    }

    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeDatabaseName(ValueChangeEvent event) {
        String selectedDB = null;
        SessionManager sm = new SessionManager();
        if (event != null) {
            this.logger.info("Event: " + event.getComponent().toString() + " " + event.toString());
            selectedDB = event.getNewValue().toString();

        } else {

            selectedDB = sm.getCurrentDatabaseNameFromSession();
            this.logger.info("Event was null. Try reading session. Database is now: " + selectedDB);
            sm.storeSessionData("currentDatabaseName", selectedDB);


        }




        /*
        * If there is no database selected, get the session database and chose the first available table for this
        * * database
        * * * */
        if (selectedDB == null || selectedDB.equals("")) {
            selectedDB = this.dbtools.getDatabaseCatalogFromDatabaseConnection().get(0);
            this.logger.info("Database retrieved: " + selectedDB);
            this.tableNames = this.dbtools.getAvailableTablesFromDatabase(selectedDB);


            this.tableName = this.tableNames.get(0);
            this.logger.info("Databasename was null and is now: " + selectedDB);

            sm.storeSessionData("currentDatabaseName", selectedDB);
            sm.storeSessionData("currentTableName", this.tableName);


        } else {
            this.logger.info("Databasename was set and it was:  " + selectedDB);
            this.tableNames = this.dbtools.getAvailableTablesFromDatabase(selectedDB);


        }

        this.handleChangeTableName(null);




    }

    /*Load this event when the table page is refreshed.
    * * */
    public void onLoadTable(ActionEvent event) {

        this.logger.info("Page load event: .. " + event.toString());
        SessionManager sm = new SessionManager();

        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();



        if (this.getTableName() == null) {

            this.logger.info("No session data set. ");
            this.databaseName = this.dbtools.getDatabaseCatalogFromDatabaseConnection().get(0);
            this.tableName = this.dbtools.getAvailableTablesFromDatabase(databaseName).get(0);

        }

        sm.setCurrentTableNameFromSession(this.tableName);

        List<String> selectedColumnsSessionData = sm.getColumnNamesForSelectedColumnsCheckBoxesFromDB();
        sm.storeSelectedColumnsFromTableMap(selectedColumnsSessionData);

        if (selectedColumnsSessionData == null) {
            this.logger.info("The session was not yet set. ");
            sm = new SessionManager();
            sm.initializeSelectedColumns();

        }

    }

    /*Load this event when the page is refreshed.
* * */
    public void onLoad(ActionEvent event) {
        this.logger.info("Yay-.. " + event.toString());
        //   this.handleChangeDatabaseName(null);


    }

    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeTableName(ValueChangeEvent event) {
        this.logger.info(event.getComponent().toString() + " " + event.toString());

        String selectedTable = event.getNewValue().toString();
        if (selectedTable != null) {
            this.logger.info("selected table name CHANGED  = " + selectedTable);


        } else {
            DatabaseTools dbTools = new DatabaseTools();
            dbTools.getFirstTableFromDatabase(this.getDatabaseName());


        }
        SessionManager sm = new SessionManager();
        sm.storeSessionData("currentTableName", selectedTable);


    }


}
