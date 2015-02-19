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
import Database.Authentication.User;
import Database.DatabaseOperations.DatabaseTools;
import org.hibernate.Session;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
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

    private List<String> columns = null;




    private List<String> selectedPrimaryKeyColumns = null;
    private HashMap<String, String> filesList;
    private List<String> filesListStrings;
    private List<String> databaseNames;
    private String CSVcolumnName;
    private List<String> CSVcolumnNames;
    private String currentSessionType = "";
    private String databaseName;


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
     * Handle the file upload an store the file in the Web server directory.
     * Get all the column names and store them in the session data.
     *
     * @param event
     */
    public void handleFileUpload(FileUploadEvent event) {
        System.out.println("Upload event...");
        UploadedFile file = event.getFile();
        SessionManager sm = new SessionManager();
        String tableName = sm.getTableDefinitionBean().getTableName();


        this.filesListStrings.add(file.getFileName());


        FacesMessage msg = new FacesMessage("Succesful", event.getFile().getFileName() + " is uploaded. Textfield: "
                + tableName);
        FacesContext.getCurrentInstance().addMessage(null, msg);


        System.out.println("added to files list " + filesList + " with Summary field " + tableName);

        this.storeFiles(file);
        this.updateCSVColumnList();


        //
        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("fileListHashMap", this.filesList);
        this.logger.info("Writing file list to session...");





    }
    




    public void storeFiles(UploadedFile file) {
        System.out.println("Store event...");
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMddHHmmss");

        String path = FacesContext.getCurrentInstance().getExternalContext()
                .getRealPath("/");
        SessionManager sm = new SessionManager();
        String tableName = sm.getTableDefinitionBean().getTableName();


        String name = tableName + "_" + fmt.format(new Date())
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

        this.filesList.put(tableName, fileToStore.getAbsolutePath());


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

        //databaseNames = new ArrayList<String>();
            //databaseNames.add("Test hard coedd");



    }

    /*Load this event when the page is refreshed.
* * */
    public void onLoad(ActionEvent event) {
        this.logger.info("File Upload Controller onLoad-.. " + event.toString());
        this.reset();
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


}