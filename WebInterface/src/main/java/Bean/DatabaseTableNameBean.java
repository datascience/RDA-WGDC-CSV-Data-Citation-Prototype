
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

package Bean;


import Database.DatabaseOperations.DatabaseTools;
import org.hibernate.Session;
import org.hibernate.type.Type;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.servlet.http.HttpServletRequest;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
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
    private String databaseName;
    private List<String> databaseNames;
    private String tableName;
    private List<String> tableNames;
    private DatabaseTools dbtools;

    public DatabaseTableNameBean() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Database Name Bean");
    }

    public String getDatabaseName() {
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();
        this.databaseName = tableBean.getDatabaseName();
        return this.databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();
        tableBean.setDatabaseName(this.databaseName);
        sm.updateTableDefinitionBean(tableBean);

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
        this.tableNames = dbtools.getAvailableTablesFromDatabase(this.databaseName);
        return this.tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }


    /*Refresh the originating page
* * */
    protected void redirectToErrorPage() {
        ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
        try {
            ec.redirect("/error.xhtml");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @PostConstruct
    public void init() {
        this.logger.info("Initializing DatabaseTableNameBean");
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();
        dbtools = new DatabaseTools();
        this.databaseNames = dbtools.getDatabaseCatalogFromDatabaseConnection();
        this.databaseName = this.databaseNames.get(0);
        tableBean.setDatabaseName(this.databaseName);

        if (this.dbtools.getAvailableTablesFromDatabase(this.databaseName).size() > 0) {
            this.tableNames = this.dbtools.getAvailableTablesFromDatabase(databaseNames.get(0));

            if (this.tableNames == null || this.tableNames.size() == 0) {
                this.logger.warning("There are no tables there yet!");
                this.tableName = "No tables Available";
                this.redirectToErrorPage();

            } else {
                this.tableNames = dbtools.getAvailableTablesFromDatabase(this.databaseName);
                this.tableName = this.tableNames.get(0);
                tableBean.setTableName(this.tableName);
                sm.updateTableDefinitionBean(tableBean);

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

        this.logger.info("Event: " + event.getComponent().toString() + " " + event.toString());
            selectedDB = event.getNewValue().toString();
            TableDefinitionBean tableBean = sm.getTableDefinitionBean();
            tableBean.setDatabaseName(selectedDB);
            this.tableNames = this.dbtools.getAvailableTablesFromDatabase(selectedDB);
            this.tableName = this.tableNames.get(0);
            tableBean.setTableName(this.tableName);
            sm.updateTableDefinitionBean(tableBean);
            sm.storeSessionData("currentDatabaseName", selectedDB);
            sm.storeSessionData("currentTableName", this.tableName);


        this.handleChangeTableName(null);


    }


    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeTableName(ValueChangeEvent event) {
        String selectedTable = null;
        if (event == null) {
            DatabaseTools dbTools = new DatabaseTools();
            String dataBaseName = this.getDatabaseName();
            if (dataBaseName != null) {
                selectedTable = dbTools.getFirstTableFromDatabase(dataBaseName);
            }


        } else {
            Object selectedObject = event.getNewValue();
            if (selectedObject != null) {
                selectedTable = selectedObject.toString();

            }


        }
        if (selectedTable != null) {
            SessionManager sm = new SessionManager();
            TableDefinitionBean tableBean = sm.getTableDefinitionBean();
            tableBean.setTableName(selectedTable);
            sm.updateTableDefinitionBean(tableBean);
            this.logger.info("Updated table definition bean: " + selectedTable);
        }

        this.logger.info("Table changed... is now" + selectedTable);


    }

    public void onLoad(ActionEvent event) {
        this.logger.info("Database Table Name Bean onLoad-.. " + event.toString());

    }


}
