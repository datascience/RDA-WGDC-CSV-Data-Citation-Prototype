
package Bean;


import Database.Helpers.StringHelpers;
import Database.DatabaseOperations.MigrationTasks;
import QueryStore.BaseTable;
import QueryStore.QueryStoreAPI;


import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.HashMap;
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
    private List<String> primaryKeys;
    private boolean successStatus = false;
    public List<String> getPrimaryKey() {
        this.primaryKeys = this.getPrimaryKeyListFromSession();


        return this.primaryKeys;
    }


    /*
    * Read the current primary keys from session
    * * * */
    private List<String> getPrimaryKeyListFromSession() {
        System.out.println("Store primary key list in session");

        // schreiben
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        List<String> primaryKeys = (List<String>) session.get("selectedPrimaryKeyList");
        if (primaryKeys == null) {
            this.logger.warning("There was no primary key session data");
        } else {
            this.logger.info("Found " + primaryKeys.size() + "keys in session");

        }

        return primaryKeys;

    }

    public void setPrimaryKey(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
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

    public boolean getSuccessStatus() {
        return successStatus;
    }

    public void setSuccessStatus(boolean successStatus) {
        this.successStatus = successStatus;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    public void setCurrentDatabaseName(String currentDatabaseName) {
        this.currentDatabaseName = currentDatabaseName;
    }


    /*
    * Method called from the Web interface with no parameters
    * */
    public void migrationController() {
        StringHelpers stringHelpers = new StringHelpers();

        List<String> primaryKeys = this.getPrimaryKey();
        MigrationTasks migrationTasks = new MigrationTasks();
        this.logger.info("Called Migration Controller. Primary keys are " + stringHelpers
                .getCommaSeperatedListofPrimaryKeys
                (primaryKeys));

        boolean migrationSuccess = false;
        migrationSuccess = migrationTasks.migrate(this.getFileListFromSession(), primaryKeys);
        
        
        this.setSuccessStatus(migrationSuccess);

        SessionManager sm = new SessionManager();

        QueryStoreAPI qApi = new QueryStoreAPI();


        TableDefinitionBean tableDefinitionBean = sm.getTableDefinitionBean();
        tableDefinitionBean.setOrganizationalId(sm.getLogedInUserObject().getOrganizational_id());


        String baseTablePIDstring = qApi.createBaseTableRecord(tableDefinitionBean.getAuthor(), tableDefinitionBean.getDatabaseName(), tableDefinitionBean.getTableName(), tableDefinitionBean.getDescription(), tableDefinitionBean.getOrganizationalId());
        tableDefinitionBean.setBaseTablePID(baseTablePIDstring);

        sm.updateTableDefinitionBean(tableDefinitionBean);



        this.displayMigrationMessage();
        
        
        

    }

    private void displayMigrationMessage() {


        String msgText = "";

        FacesContext context = FacesContext.getCurrentInstance();

        if (this.getSuccessStatus()) {
            msgText = "You successfully imported the data into the system! Click on View existing data to proceed or " +
                    "upload a new file.";
        } else {
            msgText = "There was an error during uploading. Please consult the logs!";
        }

        this.logger.info("Displaying message  " + msgText);

        FacesMessage msg = new FacesMessage(msgText);
        context.addMessage("migrateForm:migrateButton", msg);


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
