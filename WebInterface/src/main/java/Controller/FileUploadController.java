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

package Controller;

import Bean.SessionManager;
import Bean.TableDefinitionBean;
import CSVTools.CSV_API;
import Database.Authentication.User;
import Database.DatabaseOperations.DatabaseTools;
import org.apache.commons.io.FilenameUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by stefan on 17.06.14.
 */

@ManagedBean
@SessionScoped
public class FileUploadController implements Serializable {
    private Logger logger;

    private List<String> columns = null;




    private List<String> selectedPrimaryKeyColumns = null;
    private HashMap<String, String> filesList;
    private List<String> filesListStrings;
    private List<String> databaseNames;
    private String CSVcolumnName;
    private List<String> CSVcolumnNames;
    private String currentSessionType = "";
    private String databaseName;

    private String dataSetAuthor;
    private String dataSetDescription;

    private String tableNameInput;
    //   private String databaseName;
    //   private List<String> databaseNames;
    private String dataSetTitle;
    
    // the uploaded file form the event
    private UploadedFile uploadedFile;

    


    public FileUploadController() {

        this.reset();
        this.logger = Logger.getLogger(this.getClass().getName());
        this.filesList = new HashMap<String, String>();
        this.filesListStrings = new ArrayList<String>();
        SessionManager sm = new SessionManager();
        this.currentSessionType = sm.getUploadTypeFromSession();
        this.columns = new ArrayList<String>() {
        };

        this.columns.add("Use insert sequence number");


    }

    private void reset() {
        //reset
        this.filesList = new HashMap<String, String>();
        this.filesListStrings = new ArrayList<String>();
    }



    public List<String> getSelectedPrimaryKeyColumns() {
        return selectedPrimaryKeyColumns;
    }

    public void setSelectedPrimaryKeyColumns(List<String> selectedPrimaryKeyColumns) {
        this.logger.info("Set primary key check boxes. Size is " + selectedPrimaryKeyColumns.size());
        this.selectedPrimaryKeyColumns = selectedPrimaryKeyColumns;

    }

    public String getCSVcolumnName() {
        return CSVcolumnName;
    }

    public void setCSVcolumnName(String CSVcolumnName) {
        this.CSVcolumnName = CSVcolumnName;
    }

    public List<String> getCSVcolumnNames() {
        return CSVcolumnNames;
    }

    public void setCSVcolumnNames(List<String> CSVcolumnNames) {
        this.CSVcolumnNames = CSVcolumnNames;
    }

    public List<String> getDatabaseNames() {
        return databaseNames;
    }

    public void setDatabaseNames(List<String> databaseNames) {
        this.databaseNames = databaseNames;
    }

    public String getCurrentSessionType() {
        return currentSessionType;
    }

    public void setCurrentSessionType(String currentSessionType) {
        this.currentSessionType = currentSessionType;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /*
    *If there is no table name, return the user name prefix as a suggestion for the form.
     *  *  * */



    
    public List<String> getFilesListStrings() {
        return filesListStrings;
    }

    public void setFilesListStrings(List<String> filesListStrings) {
        this.filesListStrings = filesListStrings;
    }

    public HashMap<String, String> getFilesList() {
        return filesList;
    }

    public void setFilesList(HashMap<String, String> filesList) {
        this.filesList = filesList;
    }

    /**
     * Handle the file upload 
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("Upload event...");
        this.uploadedFile = event.getFile();

        
     

    }
    




    public File storeFiles(UploadedFile uploadedFile) {
        File fileToStore = null;
        System.out.println("Store event...");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

        String path = FacesContext.getCurrentInstance().getExternalContext()
                .getRealPath("/");
        SessionManager sm = new SessionManager();
        String tableName = sm.getTableDefinitionBean().getTableName();


        String name = tableName + "_" + fmt.format(new Date())
                + uploadedFile.getFileName().substring(
                uploadedFile.getFileName().lastIndexOf('.')) + ".csv";

        
        try {
            InputStream input = null;
            input = uploadedFile.getInputstream();
            CSV_API csvApi = new CSV_API();
            File folder = new File(csvApi.getDIRECTORY());
            String filename = name;
            String extension ="csv";
            fileToStore = File.createTempFile(filename + "-", "." + extension, folder);
            Files.copy(input, fileToStore.toPath());
            
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileToStore;
    }


    private void storePrimaryKeyListInSession(List<String> selectedPrimaryKeyColumns) {
        System.out.println("Store primary key list in session");

        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("selectedPrimaryKeyList", selectedPrimaryKeyColumns);

    }
    



    @PostConstruct
    public void init() {
        this.logger.info("Initializign databasenames");


        DatabaseTools dbtools = new DatabaseTools();
        databaseNames = dbtools.getAvailableDatabases();
        this.filesList = new HashMap<String,String>();
        // reset
        this.selectedPrimaryKeyColumns = new ArrayList<>();

        this.dataSetAuthor = this.getDataSetAuthor();
        this.tableNameInput = this.getTableNameInput();
        this.dataSetDescription = "";
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();



        List<String> databaseNames=dbtools.getDatabaseCatalogFromDatabaseConnection();
        String databaseName = databaseNames.get(0);
        tableBean.setDatabaseName(databaseName);

        sm.updateTableDefinitionBean(tableBean);




    }

    /*Load this event when the page is refreshed.
* * */
    public void onLoad(ActionEvent event) {
        this.logger.info("File Upload Controller onLoad-.. " + event.toString());
//        this.reset();
        this.selectedPrimaryKeyColumns = new ArrayList<String>();
        //   this.handleChangeDatabaseName(null);
        SessionManager sm = new SessionManager();
        sm.printSessionVariables();

    }

    public void updateCSVColumnList() {

        // reset columns dropdown

        this.columns = new ArrayList<String>();
        this.columns.add("ID_SYSTEM_SEQUENCE");

        String path = "";
        CSV_API csvAPI = new CSV_API();


        List pathList = new ArrayList(this.filesList.values());

        for (int i = 0; i < pathList.size(); i++) {
            path = pathList.get(i).toString();
            this.logger.info("Found file paths: " + path);
            // do stuff here
        }

        this.logger.info("Path of CSV file: " + path);

        // Append all headers to the default (which is the sequence number)

        columns.addAll(csvAPI.getListOfHeadersCSV(path));


        this.logger.info("Updating columns.... found " + columns.size() + " cols");
        this.CSVcolumnNames = columns;

    }


    /*
    * Get the colums for the Web interface. Used for building the check boxes
    * * * */
    public List<String> getColumnsValue() {
        //this.updateCSVColumnList();
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    /**
     * Action button
     */
    public String setPrimarKeyAction() {

        this.storePrimaryKeyListInSession(this.getSelectedPrimaryKeyColumns());

        FacesContext context = FacesContext.getCurrentInstance();


        FacesMessage msg = new FacesMessage("You selected " + this.getSelectedPrimaryKeyColumns().size() + " colums " +
                "as a " +
                "compund primary key", "Primary key is set. Please ensure that the primary key is unique within the complete file.");
        context.addMessage(
                "primaryKeyForm:primaryKeyButton", msg
        );
        return null;


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



    public void finalizeFileUpload() {
        this.logger.info("Action table form data");
        SessionManager sm = new SessionManager();
        TableDefinitionBean tDBean = sm.getTableDefinitionBean();
        tDBean.setAuthor(dataSetAuthor);

        tDBean.setOrganizationalId(sm.getLogedInUserObject().getOrganizational_id());
        tDBean.setTableName(tableNameInput);
        DatabaseTools dbtools = new DatabaseTools();
        List<String> databaseNames=dbtools.getDatabaseCatalogFromDatabaseConnection();
        String databaseName = databaseNames.get(0);
        tDBean.setDatabaseName(databaseName);
        tDBean.setDatabaseName(databaseName);
        tDBean.setDescription(dataSetDescription);
        tDBean.setDataSetTitle(dataSetTitle);
        sm.updateTableDefinitionBean(tDBean);


        String tableName = sm.getTableDefinitionBean().getTableName();
        File storedFile = this.storeFiles(this.uploadedFile);


        this.filesListStrings.add(storedFile.getName());
        this.filesList.put(tableName, storedFile.getAbsolutePath());

        this.updateCSVColumnList();


        //
        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("fileListHashMap", this.filesList);
        this.logger.info("Writing file list to session...");


        FacesMessage msg = new FacesMessage("Data stored", "Successfully");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    /*
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
    
    */

    public String getDataSetTitle() {
        return dataSetTitle;
    }

    public void setDataSetTitle(String dataSetTitle) {
        this.dataSetTitle = dataSetTitle;
    }

    public UploadedFile getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(UploadedFile uploadedFile) {
        this.uploadedFile = uploadedFile;
    }
}