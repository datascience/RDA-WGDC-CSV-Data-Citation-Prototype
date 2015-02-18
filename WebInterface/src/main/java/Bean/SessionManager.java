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

package Bean;

import Database.Authentication.User;
import Database.DatabaseOperations.DatabaseTools;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.crypto.Data;
import java.util.*;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */

@ManagedBean
@SessionScoped
public class SessionManager {
    private Logger logger;
    private Map<String, Object> sessionMap = null;
    private TableDefinitionBean tableDefinitionBean = null;
    private int rowCount;
    private String landingPageSelectedSubset;



    public SessionManager() {
        this.logger = Logger.getLogger(this.getClass().getName());


    }

    public void printSessionVariables() {
        this.init();


        if (this.getSessionMap() != null) {
            for (Map.Entry<String, Object> entry : this.sessionMap.entrySet()) {
                this.logger.info(entry.getKey());
            }
        }


    }

    /**
     * Store details in session
     */
    public void storeSelectedColumnsFromTableMap(List<String> columnList) {
        this.logger.info("++++++++++++++++++++++++ Store ... First item: " + columnList.get(0));

        if (FacesContext.getCurrentInstance() != null) {
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

            // schreiben

            session.put("selectedColumnsFromTableMap", columnList);


            this.logger.info("Wrote the key selectedColumnsFromTableMap  a list of size " + columnList.size());

        }


    }

    /*
    * Get the selected columns from the session
    * * * */
    public List<String> getSelectedColumnsFromTableSessionAsList() {

        List<String> selectedColumnsSessionData = null;
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (sessionMAP == null) {
            this.logger.severe("Session map is null in the session manager");
            this.initializeSelectedColumns();

        } else {
            selectedColumnsSessionData = (List<String>) sessionMAP.get("selectedColumnsFromTableMap");
        }


        return selectedColumnsSessionData;


    }

    @PostConstruct
    public void init() {
        this.logger.info("Initializign Session Manager");
        DatabaseTools dbtools = new DatabaseTools();
        String defaultDatabase = dbtools.getDefaultDatabaseNameFromConnection();
        TableDefinitionBean tableBean = this.getTableDefinitionBean();
        tableBean.setDatabaseName(defaultDatabase);
        this.updateTableDefinitionBean(tableBean);


        this.sessionMap = this.getSessionMap();


    }

    /*
    * Get session data
    * * * */
    protected Map<String, Object> getSessionMap() {
        Map<String, Object> sessionMAP = null;
        if (FacesContext.getCurrentInstance() != null) {
            sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            if (sessionMAP == null) {
                this.logger.severe("No session data");
                return sessionMAP;
            }
        }
        return sessionMAP;


    }

    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }

    public void storeFileListInSession(HashMap<String, String> fileList) {
        Map<String, Object> sessionMAP = this.getSessionMap();
        sessionMap.put("fileListHashMap", fileList);

    }

    /*
    * Get the user object from the session
    * * * */
    public User getLogedInUserObject() {
        // Get the loginBean from session attribute
        Map<String, Object> sessionMAP = this.getSessionMap();

        LoginBean loginBean = (LoginBean) sessionMAP.get("loginBean");
        User user = loginBean.getCurrentUser();
        this.logger.info("Login Bean retrieved for user " + user.getUsername());
        return user;


    }

    /*
 * Get the user object from the session
 * * * */
    public String getLogedInUserName() {
        // Get the loginBean from session attribute
        Map<String, Object> sessionMAP = this.getSessionMap();

        LoginBean loginBean = (LoginBean) sessionMAP.get("loginBean");
        User user = loginBean.getCurrentUser();
        this.logger.info("Login Bean retrieved for user " + user.getUsername());
        return user.getUsername();


    }

    /**
     * Get the type of the upload. Available types are: newCSV, updateExistingCSV, appendNewRowsToExistingCSV.
     * The information is stored in the session variable uploadSessionType
     */
    protected String getUploadTypeFromSession() {

        // lesen
//        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String uploadSessionType = params.get("uploadSessionType");

        this.logger.info("request data");
        for (Map.Entry<String, String> entry : params.entrySet()) {
            this.logger.info("Key: " + entry.getKey() + "  Value: " + entry.getValue().toString());
        }


        return uploadSessionType;
    }

    /**
     * Get the session tablename
     */
    public String getCurrentTableNameFromSession() {
        TableDefinitionBean tableBean = this.getTableDefinitionBean();
        String tableName = tableBean.getTableName();
        return tableName;
    }

    /**
     * Get the session tablename
     */
    public void setCurrentTableNameFromSession(String currentTableName) {
        TableDefinitionBean tableBean = this.getTableDefinitionBean();
        tableBean.setTableName(currentTableName);
        this.setTableDefinitionBean(tableBean);


    }

    /**
     * Get the database name from the session
     */
    public String getCurrentDatabaseNameFromSession() {

        TableDefinitionBean tableBean = this.getTableDefinitionBean();
        return tableBean.getDatabaseName();
    }


    public void initializeSelectedColumns() {
        String currentTableName = this.getCurrentTableNameFromSession();
        DatabaseTools dbTools = new DatabaseTools();

        if (currentTableName == null || currentTableName.equals("")) {

            String selectedDB = dbTools.getDatabaseCatalogFromDatabaseConnection().get(0);
            this.logger.info("Database retrieved: " + selectedDB);
            List<String> tableNames = dbTools.getAvailableTablesFromDatabase(selectedDB);
            if (tableNames.size() >= 1) {
                currentTableName = tableNames.get(0);
                List<String> initializeSelectedColumns = dbTools.getColumnsFromDatabaseAsList(currentTableName);
                SessionManager sm = new SessionManager();
                sm.storeSelectedColumnsFromTableMap(initializeSelectedColumns);
            }


        }


    }

    /*The user may have rearranged the columns in the interface
    * * */
    public void updateSortingOfSelectedColumnsInSession(Map<Integer, String> sortedColumns) {
        this.logger.info("-------------------------------------------------------UPDATE Sorted");
        List<String> sortedColumnList = new LinkedList<>();

        for (Map.Entry<Integer, String> entry : sortedColumns.entrySet()) {
            this.logger.info("Column Sequence : " + entry.getKey() + " has Name  " + entry.getValue().toString());
            sortedColumnList.add(entry.getValue());

        }

        this.storeSelectedColumnsFromTableMap(sortedColumnList);


    }

    /*
    * Retrieve the columns from the database
    * * * */
    public List<String> getColumnNamesForSelectedColumnsCheckBoxesFromDB() {


        String tableName = this.getCurrentTableNameFromSession();
        DatabaseTools dbtools = new DatabaseTools();

        List<String> availableColumnsList = new ArrayList<String>();
        Map<String, String> availableColumnsMap = dbtools.getTableColumnMetadata(tableName);

        for (Map.Entry<String, String> entry : availableColumnsMap.entrySet()) {


            String columnName = entry.getKey();
            availableColumnsList.add(columnName);

        }

        return availableColumnsList;
    }

    /*Create a sorted list of columns
    * * */


    //@todo holt falschen wert
    public Map<Integer, String> getColumnNamesFromSessionAsMap() {

        List<String> selectedColumnsList = this.getSelectedColumnsFromTableSessionAsList();

        Map<Integer, String> selectedColumnsMap = new HashMap<>();
        for (int i = 0; i < selectedColumnsList.size(); i++) {
            selectedColumnsMap.put(i, selectedColumnsList.get(i));
        }

        return selectedColumnsMap;

    }


    public Map<Integer, String> getColumnNamesFromDataTablesSession() {


        return (Map<Integer, String>) this.getSessionMap().get("selectedColumnsFromDataTables");

    }


    public TableDefinitionBean getTableDefinitionBean() {
        Map<String, Object> sessionMAP = this.getSessionMap();

        tableDefinitionBean = (TableDefinitionBean) sessionMAP.get("tableDefinitionBean");
        if (tableDefinitionBean == null) {
            this.logger.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            tableDefinitionBean = new TableDefinitionBean();
            this.setTableDefinitionBean(tableDefinitionBean);

        }

        return tableDefinitionBean;
    }

    public void setTableDefinitionBean(TableDefinitionBean tableDefinitionBean) {
        this.tableDefinitionBean = tableDefinitionBean;
        Map<String, Object> sessionMAP = this.getSessionMap();
        sessionMAP.put("tableDefinitionBean", tableDefinitionBean);

    }


    public void updateTableDefinitionBean(String dataSetAuthor, String databaseName, String tableName, String dataSetDescription, int
            orgId) {

        TableDefinitionBean tB = new TableDefinitionBean();
        tB.setAuthor(dataSetAuthor);
        tB.setDatabaseName(databaseName);
        tB.setDescription(dataSetDescription);
        tB.setOrganizationalId(orgId);
        tB.setTableName(tableName);
        this.setTableDefinitionBean(tB);

        this.logger.info("Table bean updated: " + tB.getAuthor() + " " + tB.getDatabaseName() + " " + tB.getDescription());

    }

    public void updateTableDefinitionBean(TableDefinitionBean updatedTableBean) {
        this.logger.info("Table bean updated: " + updatedTableBean.getAuthor() + " "
                + updatedTableBean.getDatabaseName() + " " + updatedTableBean.getTableName() + " " + updatedTableBean.getDescription());

        this.setTableDefinitionBean(updatedTableBean);

    }

    /**
     * Store details in session
     */
    public void storeSessionData(String key, String value) {
        System.out.println("Writing data into session: Key " + key + "  Value:  " + value);

        if (FacesContext.getCurrentInstance() != null) {
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

            // schreiben

            session.put(key, value);

        }


    }

    public int getRowCount() {
        TableDefinitionBean tableBean = this.getTableDefinitionBean();
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String rowCountString = params.get("currentRowCount");
        int rowCount = 0;
        if (rowCountString != null) {
            rowCount = Integer.parseInt(rowCountString);
        }

        this.rowCount = rowCount;
        this.logger.info("Row count set: " + rowCount);
        tableBean.setRowCount(rowCount);

        return rowCount;
    }

    public String getLandingPageSelectedSubset() {
        Map<String, Object> sessionMAP = this.getSessionMap();

        String landingPageSelectedSubset = (String) sessionMAP.get("landingPageSelectedSubset");
        return landingPageSelectedSubset;
    }

    public void setLandingPageSelectedSubset(String landingPageSelectedSubset) {
        Map<String, Object> sessionMAP = this.getSessionMap();
        sessionMAP.put("landingPageSelectedSubset", landingPageSelectedSubset);
        this.landingPageSelectedSubset = landingPageSelectedSubset;
    }
}



