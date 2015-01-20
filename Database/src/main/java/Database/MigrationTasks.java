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

package Database;

import CSVTools.CSV_API;
import CSVTools.Column;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class MigrationTasks {
    private Logger logger;
    HashMap filesList;
    private String currentTableName;
    private String currentDatabaseName;

    public MigrationTasks() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("MigrationTasks Constructor");
        this.filesList = new HashMap();

    }

    /*
* Migration
* */
    public boolean migrate(HashMap filesListInput, List<String> primaryKey) {
        this.logger.info("Doing the migration.");

        boolean calulateHashColumn = false;
        this.logger.info("Calculate Hash Columns is OFF");
        // retrieve file names
        if (filesListInput == null) {
            System.out.println("FileListInput is NULL");
        } else {
            this.logger.info("FileListInput size is " + filesListInput.size());
            this.filesList = filesListInput;

        }


        //
        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            this.logger.info("TableName = " + pairs.getKey().toString() + " Path: " + pairs.getValue().toString());

            CSV_API csv;
            csv = new CSV_API();
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
                migrate.createSimpleDBFromCSV(meta, currentTableName, primaryKey, calulateHashColumn);
                // Import CSV Data
                migrate.insertCSVDataIntoDB(currentPath, currentTableName, true, calulateHashColumn);

                // add indices
                migrate.addDatabaseIndicesToMetadataColumns(currentTableName);

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

            try {

            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }


            it.remove(); // avoids a ConcurrentModificationException

        }

        return true;

    }

    /**
     * Append new CSV Data to an existing table
     */
    public void insertNewCSVDataToExistingTable(HashMap inputFileMap, String sessionTableName, boolean
            hasHeaders, boolean calulateHashColumn) {


        System.out.println("inserting new data");

        if (sessionTableName != null) {
            this.setCurrentTableName(sessionTableName);
        }

        // retrieve file names
        this.filesList = inputFileMap;
        if (this.filesList == null) {
            this.logger.severe("File list was NULL");
        } else {
            this.logger.info("Filelist is okay. Number of files " + this.filesList.size());
            HashMap<String, String> testMap = this.filesList;
            for (Map.Entry<String, String> entry : testMap.entrySet()) {
                this.logger.info("File list loop: Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }


        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            // When uploading data for an existing table, the table name is not provided by the user but is selected
            // from the drop down menu. is is provided in the session variable
            this.logger.info("TableName = " + this.getCurrentTableName() + " Path: " + pairs.getValue().toString());


            CSV_API csv;
            csv = new CSV_API();

            // if the table name was not set in a session, read it from the file name
            if (this.currentTableName == null || this.currentTableName.equals("")) {
                this.currentTableName = csv.replaceSpaceWithDash(pairs.getKey().toString());

            }

            String currentPath = pairs.getValue().toString();


            try {
                DatabaseTools dbt = new DatabaseTools();

                Map<String, String> columnsMap = (dbt.getColumnNamesFromTableWithoutMetadataColumns(this
                        .currentTableName));


                // read CSV file

                MigrateCSV2SQL migrate = new MigrateCSV2SQL();


                // Import CSV Data
                migrate.appendingNewCSVDataIntoExistingDB(columnsMap, currentPath, this.currentTableName,
                        hasHeaders,
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
     * Update existing records. This creates an update of a record if the primary key is already contained in the
     * database. If a row from the CSV file is new, a new record is
     */
    public void updateDataInExistingTable(HashMap inputFileMap, String sessionTableName, boolean
            hasHeaders, boolean calulateHashColumn) {

        System.out.println("UPDATING data in an existing table ");

        if (sessionTableName != null) {
            this.setCurrentTableName(sessionTableName);
        }

        // retrieve file names
        this.filesList = inputFileMap;
        if (this.filesList == null) {
            this.logger.severe("File list was NULL");
        } else {
            this.logger.info("Filelist is okay. Number of files " + this.filesList.size());
            HashMap<String, String> testMap = this.filesList;
            for (Map.Entry<String, String> entry : testMap.entrySet()) {
                this.logger.info("File list loop: Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }


        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            // When uploading data for an existing table, the table name is not provided by the user but is selected
            // from the drop down menu. is is provided in the session variable
            this.logger.info("TableName = " + this.getCurrentTableName() + " Path: " + pairs.getValue().toString());

            CSV_API csv;
            csv = new CSV_API();
            String currentPath = pairs.getValue().toString();


            // there are headers
            if (hasHeaders) {
                // Read headers
                this.logger.info("There are headers");
                String[] headers = csv.getArrayOfHeadersCSV(currentPath);

            } else {
                this.logger.info("There are no headers");
            }


            try {
                DatabaseTools dbt = new DatabaseTools();

                Map<String, String> columnsMap = (dbt.getColumnNamesFromTableWithoutMetadataColumns(this
                        .currentTableName));


                // read CSV file

                MigrateCSV2SQL migrate = new MigrateCSV2SQL();

                // get primary keys

                List<String> primaryKeyList = dbt.getPrimaryKeyFromTable(this.currentTableName);

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

    /**
     *
     * */
    public void findDeletedCSVRowsAndMarkAsDeletedInDB(HashMap inputFileMap, String sessionTableName, boolean
            hasHeaders, boolean calulateHashColumn) {

        System.out.println("DELETED records ");

        if (sessionTableName != null) {
            this.setCurrentTableName(sessionTableName);
        }

        // retrieve file names
        this.filesList = inputFileMap;
        if (this.filesList == null) {
            this.logger.severe("File list was NULL");
        } else {
            this.logger.info("Filelist is okay. Number of files " + this.filesList.size());
            HashMap<String, String> testMap = this.filesList;
            for (Map.Entry<String, String> entry : testMap.entrySet()) {
                this.logger.info("File list loop: Key = " + entry.getKey() + ", Value = " + entry.getValue());
            }
        }


        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            // When uploading data for an existing table, the table name is not provided by the user but is selected
            // from the drop down menu. is is provided in the session variable
            this.logger.info("TableName = " + this.getCurrentTableName() + " Path: " + pairs.getValue().toString());

            CSV_API csv;
            csv = new CSV_API();
            String currentPath = pairs.getValue().toString();


            // there are headers
            if (hasHeaders) {
                // Read headers
                this.logger.info("There are headers");
                String[] headers = csv.getArrayOfHeadersCSV(currentPath);

            } else {
                this.logger.info("There are no headers");
            }


            try {
                DatabaseTools dbt = new DatabaseTools();

                Map<String, String> columnsMap = (dbt.getColumnNamesFromTableWithoutMetadataColumns(this
                        .currentTableName));


                // read CSV file

                MigrateCSV2SQL migrate = new MigrateCSV2SQL();

                // get primary keys

                List<String> primaryKeyList = dbt.getPrimaryKeyFromTable(this.currentTableName);

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



    public String getCurrentTableName() {
        return currentTableName;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    public String getCurrentDatabaseName() {
        return currentDatabaseName;
    }

    public void setCurrentDatabaseName(String currentDatabaseName) {
        this.currentDatabaseName = currentDatabaseName;
    }

    public HashMap getFilesList() {
        return filesList;
    }

    public void setFilesList(HashMap filesList) {
        this.filesList = filesList;
    }
}
