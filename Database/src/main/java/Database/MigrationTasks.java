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

    public MigrationTasks() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("MigrationTasks Constructor");
        this.filesList = new HashMap();

    }

    /*
* Migration
* */
    public void migrate(HashMap filesListInput, String primaryKey) {
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
            }

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }


            it.remove(); // avoids a ConcurrentModificationException
        }


    }

}
