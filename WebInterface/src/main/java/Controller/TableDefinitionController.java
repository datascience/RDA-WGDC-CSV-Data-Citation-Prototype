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

package Controller;

import Bean.SessionManager;
import Bean.TableDefinitionBean;
import Database.Authentication.User;
import Database.DatabaseOperations.DatabaseTools;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stefan on 11.02.15.
 */
@ManagedBean
@SessionScoped
public class TableDefinitionController implements Serializable {
    private String dataSetAuthor;
    private String dataSetDescription;
    private Logger logger;
    private String tableNameInput;
    private String databaseName;
    private List<String> databaseNames;
    private String dataSetTitle;



    public TableDefinitionController() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public String getTableNameInput() {
        SessionManager sm = new SessionManager();
        User user = sm.getLogedInUserObject();

        String username = user.getUsername();
        String firstname = user.getFirstName();
        String lastname = user.getLastName();

        if (this.tableNameInput == null) {
            this.logger.warning("TableName Input was Null");
            this.tableNameInput = username + "_";
            return this.tableNameInput;
        } else {
            return this.tableNameInput;
        }

    }

    public void setTableNameInput(String tableName) {
        this.tableNameInput = tableName;

    }


    public String getDataSetAuthor() {
        SessionManager sm = new SessionManager();
        User user = sm.getLogedInUserObject();


        String firstname = user.getFirstName();
        String lastname = user.getLastName();

        if (this.dataSetAuthor == null) {
            this.dataSetAuthor = firstname + " " + lastname;
            return this.dataSetAuthor;
        } else {
            return this.dataSetAuthor;
        }

    }

    public void setDataSetAuthor(String dataSetAuthor) {
        this.dataSetAuthor = dataSetAuthor;
    }

    public String getDataSetDescription() {
        return dataSetDescription;
    }

    public void setDataSetDescription(String dataSetDescription) {
        this.dataSetDescription = dataSetDescription;
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

    @PostConstruct
    public void init() {
        this.dataSetAuthor = this.getDataSetAuthor();
        this.tableNameInput = this.getTableNameInput();
        this.dataSetDescription = "";
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();
        DatabaseTools dbtools = new DatabaseTools();
        this.databaseNames = dbtools.getDatabaseCatalogFromDatabaseConnection();
        this.databaseName = this.databaseNames.get(0);

        tableBean.setDatabaseName(this.databaseName);

        sm.updateTableDefinitionBean(tableBean);

    }

    public void setTableDefinitionFormdata() {
        this.logger.info("Action table form data");
        SessionManager sm = new SessionManager();
        TableDefinitionBean tDBean = sm.getTableDefinitionBean();
        tDBean.setAuthor(dataSetAuthor);

        tDBean.setOrganizationalId(sm.getLogedInUserObject().getOrganizational_id());
        tDBean.setTableName(tableNameInput);
        tDBean.setDatabaseName(databaseName);
        tDBean.setDescription(dataSetDescription);
        tDBean.setDataSetTitle(dataSetTitle);
        sm.updateTableDefinitionBean(tDBean);

        FacesMessage msg = new FacesMessage("Succesful", "Data stored");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void handleChangeDatabaseName(ValueChangeEvent event) {
        String selectedDB = null;
        SessionManager sm = new SessionManager();
        if (event != null) {
            this.logger.info("Event: " + event.getComponent().toString() + " " + event.toString());
            selectedDB = event.getNewValue().toString();
            TableDefinitionBean tableBean = sm.getTableDefinitionBean();
            tableBean.setDatabaseName(selectedDB);
            sm.updateTableDefinitionBean(tableBean);


        }

    }

    public String getDataSetTitle() {
        return dataSetTitle;
    }

    public void setDataSetTitle(String dataSetTitle) {
        this.dataSetTitle = dataSetTitle;
    }
}
