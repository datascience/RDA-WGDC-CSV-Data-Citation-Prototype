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


    public SessionManager() {
        this.logger = Logger.getLogger(this.getClass().getName());

    }

    public void printSessionVariables() {
        Map<String, Object> sessionMap = this.getSessionMap();
        this.logger.info("Stored session variables (keys)");

        if (sessionMap != null) {
            for (Map.Entry<String, Object> entry : sessionMap.entrySet()) {
                this.logger.info(entry.getKey());
            }
        }


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




        if (selectedColumnsSessionData != null && selectedColumnsSessionData.size() >= 1) {
            this.logger.info("There are " + selectedColumnsSessionData.size() + " selected columns");

            return selectedColumnsSessionData;
        } else {
            this.logger.info("There was no session data available. Setting default column");
            this.initializeSelectedColumns();
            return selectedColumnsSessionData;

        }


    }

    @PostConstruct
    public void init() {
        this.logger.info("Initializign Session Manager");
        DatabaseTools dbtools = new DatabaseTools();
        String defaultDatabase = dbtools.getDefaultDatabaseNameFromConnection();
        this.storeSessionData("currentDatabaseName", defaultDatabase);


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

    /*
    * Get the user object from the session
    * * * */
    protected User getLogedInUserObject() {
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
    protected String getLogedInUserName() {
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

        // lesen
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
//        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String currentTableName = (String) sessionMAP.get("currentTableName");

        if (currentTableName == null || currentTableName.equals("")) {
            this.logger.warning("There was no current table name in the session. ");


        }
        return currentTableName;
    }

    /**
     * Get the session tablename
     */
    public void setCurrentTableNameFromSession(String currentTableName) {


        ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
        Map<String, Object> sessionMap = externalContext.getSessionMap();
        sessionMap.put("currentTableName", currentTableName);

        this.logger.warning("The table name is stored in the session  " + currentTableName);

    }

    /**
     * Get the database name from the session
     */
    public String getCurrentDatabaseNameFromSession() {

        // lesen
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
//        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        String currentDatabaseName = (String) sessionMAP.get("currentDatabaseName");

        if (currentDatabaseName == null || currentDatabaseName.equals("")) {
            this.logger.warning("There was no  currentDatabaseName name in the session. ");


        }
        return currentDatabaseName;
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
    public Map<Integer, String> getColumnNamesFromSessionAsMap() {

        List<String> selectedColumnsList = this.getSelectedColumnsFromTableSessionAsList();

        Map<Integer, String> selectedColumnsMap = new HashMap<>();
        for (int i = 0; i < selectedColumnsList.size(); i++) {
            selectedColumnsMap.put(i, selectedColumnsList.get(i));
        }

        return selectedColumnsMap;

    }

}



