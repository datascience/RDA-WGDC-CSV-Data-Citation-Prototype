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

import Database.DatabaseOperations.MigrationTasks;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.*;
import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by stefan on 14.02.15.
 */

@ManagedBean
@SessionScoped
public class UpdateFileController {

    private static final boolean calulateHashColumn = false;
    private HashMap<String, String> filesList;
    private Logger logger;
    private List<String> primaryKeys;
    private boolean successStatus = false;
    private boolean headerRow = false;

    private String currentTableName = null;
    private String currentDatabaseName = null;
    private boolean isNewDataOnly = false;
    private ArrayList<String> filesListStrings;


    public UpdateFileController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.filesList = new HashMap<String, String>();
        this.filesListStrings = new ArrayList<String>();
        SessionManager sm = new SessionManager();

    }

    public static boolean isCalulateHashColumn() {
        return calulateHashColumn;
    }

    /**
     * Called from Web interface
     */
    public void updateTableData() {
        this.logger.info("Update button clicked");
        this.logger.info("Currently the selected table is " + this.getCurrentTableName());

        if (this.isNewDataOnly) {
            this.logger.info("Only new data will be inserted");
            this.displayMessage("Insert new data", "Only new data will be inserted");

            this.insertNewCSVDataToExistingTableController(this.getFileListFromSession(), this.getCurrentTableName(),
                    this
                            .getCurrentDatabaseName(), this.isHeaderRow(), calulateHashColumn);

        } else {
            this.logger.info("Existing rows will be updated");
            this.displayMessage("Update existing data", "Performing updates");
            this.updateDataInExistingTableController(this.getFileListFromSession(), this.getCurrentTableName(),
                    this
                            .getCurrentDatabaseName(), this.isHeaderRow(), calulateHashColumn);

            FacesContext context = FacesContext.getCurrentInstance();


            FacesMessage msg = new FacesMessage("uploadCommandForm:messages", "Update done");


        }

    }

    /**
     * Read the name of the table to be created from the session variables
     *
     * @return
     */
    private HashMap<String, String> getFileListFromSession() {
        //System.out.println("Read session data");
        // lesen
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        if (sessionMAP == null) {
            this.logger.severe("No files selected!");
        }
        return (HashMap<String, String>) sessionMAP.get("fileListHashMap");
    }

    private void updateDataInExistingTableController(HashMap inputFileMap, String tableName, String
            databaseName, boolean
                                                             hasHeaders, boolean calulateHashColumn) {

        MigrationTasks migrationTasks = new MigrationTasks();
        migrationTasks.updateDataInExistingTable(inputFileMap, tableName, hasHeaders,
                calulateHashColumn);


    }

    private void insertNewCSVDataToExistingTableController(HashMap inputFileMap, String tableName, String
            databaseName, boolean
                                                                   hasHeaders, boolean calulateHashColumn) {

        MigrationTasks migrationTasks = new MigrationTasks();
        migrationTasks.insertNewCSVDataToExistingTable(inputFileMap, tableName, hasHeaders,
                calulateHashColumn);


    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    public boolean isSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(boolean successStatus) {
        this.successStatus = successStatus;
    }


    private String getCurrentDatabaseName() {
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();
        this.currentDatabaseName = tableBean.getDatabaseName();

        return this.currentDatabaseName;

    }

    public void setCurrentDatabaseName(String currentDatabaseName) {
        this.currentDatabaseName = currentDatabaseName;
    }

    private String getCurrentTableName() {
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();
        this.currentTableName = tableBean.getTableName();

        return this.currentTableName;

    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    private void displayMessage(String msgText) {


        FacesContext context = FacesContext.getCurrentInstance();

        this.logger.info("Displaying message  " + msgText);

        FacesMessage msg = new FacesMessage(msgText);
        context.addMessage("uploadCommandForm:messages", msg);

    }

    private void displayMessage(String text, String details) {
        FacesContext.getCurrentInstance().addMessage("messages", new FacesMessage(details));

    }

    public List<String> getFilesListStrings() {
        return filesListStrings;
    }

    public void setFilesListStrings(ArrayList<String> filesListStrings) {
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


        //
        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("fileListHashMap", this.filesList);
        this.logger.info("Writing file list to session...");

        //reset
        this.filesList = new HashMap<String, String>();
        this.filesListStrings = new ArrayList<String>();


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

    public void checkBoxListener() {
        FacesContext context = FacesContext.getCurrentInstance();
        String msgText = "";


        if (this.isNewDataOnly()) {
            msgText = "New data only";


            FacesMessage msg = new FacesMessage(msgText);
            context.addMessage("uploadCommandForm:messages", msg);


        }
    }

    public boolean isHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }

    public boolean isNewDataOnly() {
        return isNewDataOnly;
    }

    public void setNewDataOnly(boolean isNewDataOnly) {
        this.isNewDataOnly = isNewDataOnly;
    }
}
