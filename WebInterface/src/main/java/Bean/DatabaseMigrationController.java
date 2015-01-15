
package Bean;


import CSVTools.CSV_API;

import CSVTools.Column;
import Database.DatabaseTools;
import Database.MigrateCSV2SQL;
import Database.MigrationTasks;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 18.06.14.
 */

@ManagedBean
@SessionScoped
public class DatabaseMigrationController implements Serializable {


    private HashMap<String, String> filesList;
    private Logger logger;
    private static final boolean calulateHashColumn = false;
    private String primaryKey;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public boolean isNewDataOnly() {
        return isNewDataOnly;
    }

    public void setNewDataOnly(boolean isNewDataOnly) {
        this.isNewDataOnly = isNewDataOnly;

    }

    private boolean isNewDataOnly = false;

    public boolean isHeaderRow() {
        return headerRow;
    }

    public void setHeaderRow(boolean headerRow) {
        this.headerRow = headerRow;
    }

    private boolean headerRow = false;

    private String currentTableName = null;
    private String currentDatabaseName = null;
    public DatabaseMigrationController() {


        this.logger = Logger.getLogger(this.getClass().getName());
        System.out.println("DB controller");

        // Init file list
        this.filesList = new HashMap<String, String>();


    }

    public String viewTable() {

        this.logger.info("Opening table view");
        return "table.xhtml?faces-redirect=true";
    }

    /**
     * @TODO needs to be rewritten!!
     */

    /*
    * Method called from the Web interface with no parameters
    * */
    public void migrationController() {

        String primaryKey = this.getPrimaryKey();
        MigrationTasks migrationTasks = new MigrationTasks();
        this.logger.info("Called Migration Controller. Primary key is " + primaryKey + "Filelist size from session: "
                + this.getFileListFromSession().size());
        migrationTasks.migrate(this.getFileListFromSession(), primaryKey);

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



    /**
     * Action button
     */
    public void setPrimarKeyAction() {
        this.logger.info("Primary key is " + this.getPrimaryKey());
        //FacesContext.getCurrentInstance().addMessage("primaryKeyform:primaryKeyButton", new FacesMessage("yayyayyay"));
        FacesMessage msg = new FacesMessage("The primary key is " + this.getPrimaryKey(), "The primary key must be unique");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

    public void addMessage() {
        String summary = this.isNewDataOnly ? "Checked" : "Unchecked";
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(summary));
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


        }
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

    private void displayMessage(String text, String details) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(details));

    }




    private String getCurrentDatabaseName() {
        // read data from session
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        this.currentDatabaseName = (String) sessionMAP.get("currentDatabaseName");
        this.logger.info("Read current database name from session: " + this.currentDatabaseName);
        return this.currentDatabaseName;

    }

    private String getCurrentTableName() {
        // read data from session
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        this.currentTableName = (String) sessionMAP.get("currentTableName");
        if (this.currentTableName == null) {
            this.logger.warning("THERE IS NO DATA FOR TABLENAME IN THE SESSION");
        }
        this.logger.info("Read current table name from session: " + this.currentTableName);
        return this.currentTableName;

    }

}
