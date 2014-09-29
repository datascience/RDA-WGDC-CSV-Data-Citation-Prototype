
package Bean;


import CSVTools.CSVHelper;

import CSVTools.Column;
import Database.DatabaseTools;
import Database.MigrateCSV2SQL;

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

    public void migrate() {
        System.out.println("Doing the migration");
        boolean calulateHashColumn = false;

        this.logger.info("Calculate Hash Columns is OFF");
        // retrieve file names
        this.filesList = this.getFileListFromSession();

        System.out.println("Retrieved  " + filesList.size() + " file names");

        //
        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            this.logger.info("TableName = " + pairs.getKey().toString() + " Path: " + pairs.getValue().toString());

            CSVHelper csv;
            csv = new CSVHelper();
            String currentTableName = csv.replaceSpaceWithDash(pairs.getKey().toString());
            String currentPath = pairs.getValue().toString();
            // Read headers
            String[] headers = csv.getArrayOfHeadersCSV(currentPath);
            try {
                csv.readWithCsvListReaderAsStrings(currentPath);
                // get column metadata
                Column[] meta = csv.analyseColumns(true, currentPath);

                // read CSV file
                csv.readWithCsvListReaderAsStrings(currentPath);
                MigrateCSV2SQL migrate = new MigrateCSV2SQL();


                // Create DB schema
                migrate.createSimpleDBFromCSV(meta, currentTableName, this.getPrimaryKey(), calulateHashColumn);
                // Import CSV Data
                migrate.insertCSVDataIntoDB(currentPath, currentTableName, true, calulateHashColumn);

                // add indices
                migrate.addDatabaseIndicesToMetadataColumns(currentTableName);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }


            it.remove(); // avoids a ConcurrentModificationException
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

    public void updateTableData() {
        this.logger.info("Update button clicked");
        this.logger.info("Currently the selected table is " + this.getCurrentTableName());
        if (this.isNewDataOnly) {
            this.logger.info("Only new data will be inserted");
            this.displayMessage("Insert new data", "Only new data will be inserted");

            this.insertNewCSVData();

        } else {
            this.logger.info("Existing rows will be updated");


        }
    }

    private void displayMessage(String text, String details) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(details));

    }

    /**
     * Append new CSV Data to an existing table
     */
    private void insertNewCSVData() {
        System.out.println("inserting new data");

        // retrieve file names
        this.filesList = this.getFileListFromSession();
        if (this.filesList == null) {
            this.logger.severe("File list was NULL");
        } else {
            this.logger.info("Filelist is okay. Number of files " + this.filesList.size());
            HashMap<String, String> testMap = this.filesList;
            for (Map.Entry<String, String> entry : testMap.entrySet()) {
                this.logger.info("File list loop: Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }

        if (this.currentTableName == null) {
            this.logger.warning("Current table name was null... reading from session");

        }

        this.logger.info("Selected database: " + this.getCurrentDatabaseName() + " Table: " + this
                .getCurrentTableName());


        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            // When uploading data for an existing table, the table name is not provided by the user but is selected
            // from the drop down menu. is is provided in the session variable
            this.logger.info("TableName = " + this.getCurrentTableName() + " Path: " + pairs.getValue().toString());

            CSVHelper csv;
            csv = new CSVHelper();
            String currentPath = pairs.getValue().toString();


            // there are headers
            if (this.isHeaderRow()) {
                // Read headers
                this.logger.info("There are headers");
                String[] headers = csv.getArrayOfHeadersCSV(currentPath);

            } else {
                this.logger.info("There are no headers");
            }


            try {
                DatabaseTools dbt = new DatabaseTools();
                this.logger.info("Table: " + this.currentTableName + " DB: " + this.getCurrentDatabaseName());
                Map<String, String> columnsMap = (dbt.getColumnNamesFromTableWithoutMetadataColumns(this
                        .currentTableName, this.currentDatabaseName));


                // read CSV file

                MigrateCSV2SQL migrate = new MigrateCSV2SQL();


                // Import CSV Data
                migrate.appendingNewCSVDataIntoExistingDB(columnsMap, currentPath, this.currentTableName, true,
                        calulateHashColumn);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }


            it.remove(); // avoids a ConcurrentModificationException

        }


    }

    /**
     * Update existing records
     */
    private void updateDataInExistingTable() {

        System.out.println("UPDATING data in an existing table ");

        // retrieve file names
        this.filesList = this.getFileListFromSession();
        if (this.filesList == null) {
            this.logger.severe("File list was NULL");
        } else {
            this.logger.info("Filelist is okay. Number of files " + this.filesList.size());
            HashMap<String, String> testMap = this.filesList;
            for (Map.Entry<String, String> entry : testMap.entrySet()) {
                this.logger.info("File list loop: Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }

        if (this.currentTableName == null) {
            this.logger.warning("Current table name was null... reading from session");

        }

        this.logger.info("Selected database: " + this.getCurrentDatabaseName() + " Table: " + this
                .getCurrentTableName());


        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            // When uploading data for an existing table, the table name is not provided by the user but is selected
            // from the drop down menu. is is provided in the session variable
            this.logger.info("TableName = " + this.getCurrentTableName() + " Path: " + pairs.getValue().toString());

            CSVHelper csv;
            csv = new CSVHelper();
            String currentPath = pairs.getValue().toString();


            // there are headers
            if (this.isHeaderRow()) {
                // Read headers
                this.logger.info("There are headers");
                String[] headers = csv.getArrayOfHeadersCSV(currentPath);

            } else {
                this.logger.info("There are no headers");
            }


            try {
                DatabaseTools dbt = new DatabaseTools();
                this.logger.info("Table: " + this.currentTableName + " DB: " + this.getCurrentDatabaseName());
                Map<String, String> columnsMap = (dbt.getColumnNamesFromTableWithoutMetadataColumns(this
                        .currentTableName, this.currentDatabaseName));


                // read CSV file

                MigrateCSV2SQL migrate = new MigrateCSV2SQL();

                // get primary keys

                List<String> primaryKeyList = dbt.getPrimaryKeyFromTable(this.currentTableName,
                        this.currentDatabaseName);

                // Import CSV Data
                //@todo move primary key as a new attribute into the column list

                migrate.updateDataInExistingDB(columnsMap, primaryKeyList, currentPath,
                        this.currentTableName, true,
                        calulateHashColumn);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }


            it.remove(); // avoids a ConcurrentModificationException

        }



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
