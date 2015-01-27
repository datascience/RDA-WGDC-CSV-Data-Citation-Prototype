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

package Bean;

import CSVTools.CSV_API;
import Database.DatabaseOperations.DatabaseTools;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.io.*;
import java.sql.SQLException;
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
    private String tableName;
    private List<String> columns = null;

    public List<String> getSelectedPrimaryKeyColumns() {
        return selectedPrimaryKeyColumns;
    }

    public void setSelectedPrimaryKeyColumns(List<String> selectedPrimaryKeyColumns) {
        this.logger.info("Set primary key check boxes. Size is " + selectedPrimaryKeyColumns.size());
        this.selectedPrimaryKeyColumns = selectedPrimaryKeyColumns;
    }

    private List<String> selectedPrimaryKeyColumns = null;
    
    

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

    private HashMap<String, String> filesList;
    private List<String> filesListStrings;
    private List<String> databaseNames;

    private String CSVcolumnName;
    private List<String> CSVcolumnNames;
    private String currentSessionType = "";


    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    private String databaseName;

    public FileUploadController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.filesList = new HashMap<String, String>();
        this.filesListStrings = new ArrayList<String>();
        this.currentSessionType = getUploadTypeFromSession();
        this.columns = new ArrayList<String>() {
        };

        this.columns.add("Use insert sequence number");


    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;

    }

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
     * Handle the file upload an store the file in the Web server directory.
     * Get all the column names and store them in the session data.
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("Upload event...");
        UploadedFile file = event.getFile();


        this.filesListStrings.add(file.getFileName());


        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded. Textfield: "
                + tableName);
        FacesContext.getCurrentInstance().addMessage(null, msg);


        System.out.println("added to files list " + filesList + " with Summary field " + tableName);

        this.storeFiles(file);
        this.storeSessionData();
        this.updateCSVColumnList();
    }


    public void storeFiles(UploadedFile file) {
        System.out.println("Store event...");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

        String path = FacesContext.getCurrentInstance().getExternalContext()
                .getRealPath("/");


        String name = this.tableName + "_" + fmt.format(new Date())
                + file.getFileName().substring(
                file.getFileName().lastIndexOf('.')) + ".csv";

        File fileToStore = new File(path + "/" + name);
        System.out.println("Path set to: " + fileToStore.getAbsolutePath());

        InputStream is = null;
        try {
            is = file.getInputstream();
            OutputStream out = new FileOutputStream(fileToStore);
            byte buf[] = new byte[1024];
            int len;
            while ((len = is.read(buf)) > 0)
                out.write(buf, 0, len);
            is.close();
            out.close();
        } catch (IOException e) {
            System.out.println("error");
            e.printStackTrace();

        }

        this.filesList.put(this.tableName, fileToStore.getAbsolutePath());


    }

    private void storeSessionData() {
        System.out.println("Session data function");

        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("fileListHashMap", this.filesList);
        this.logger.info("Writing file list to session...");
        // schreiben


        // @todo review this, there is a problem with the table because the uploda uses a input text field and one
        // time it uses a drop down.
        if (this.tableName != null) {
            this.logger.info("current table name was set to null... using session data.");
            session.put("currentTableName", this.tableName);
        }


        // lesen
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        HashMap<String, String> filesList = (HashMap<String, String>) sessionMAP.get("fileListHashMap");

    }

    private void storePrimaryKeyListInSession(List<String> selectedPrimaryKeyColumns) {
        System.out.println("Store primary key list in session");

        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("selectedPrimaryKeyList", selectedPrimaryKeyColumns);

    }
    

    /**
     * Get the type of the upload. Available types are: newCSV, updateExistingCSV, appendNewRowsToExistingCSV.
     * The information is stored in the session variable uploadSessionType
     */
    private String getUploadTypeFromSession() {

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

    @PostConstruct
    public void init() {
        this.logger.info("Initializign databasenames");
        try {

            DatabaseTools dbtools = new DatabaseTools();
            databaseNames = dbtools.getAvailableDatabases();
            //databaseNames = new ArrayList<String>();
            //databaseNames.add("Test hard coedd");

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeTableName(ValueChangeEvent event) {
        this.logger.info(event.getComponent().toString() + " " + event.toString());

        String selectedTable = event.getNewValue().toString();
        this.logger.info("Changed Table Name = " + selectedTable);


    }



    public void updateCSVColumnList() {

        // reset columns dropdown

        this.columns = new ArrayList<String>();
        this.columns.add("ID_SYSTEM_SEQUENCE");

        String path = "";
        CSV_API csvAPI = new CSV_API();
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        HashMap<String, String> filesList = (HashMap<String, String>) sessionMAP.get("fileListHashMap");
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
    public void setPrimarKeyAction() {

        this.storePrimaryKeyListInSession(this.getSelectedPrimaryKeyColumns());

        FacesContext context = FacesContext.getCurrentInstance();


        FacesMessage msg = new FacesMessage("You selected " + this.getSelectedPrimaryKeyColumns().size() + " colums " +
                "as a " +
                "compund primary key", "Please ensure that the primary key (bei it a single column or a compound key)" +
                " must be unique within the complete file.");
        context.addMessage(
                "primaryKeyForm:primaryKeyButton", msg
        );


    }


    
}