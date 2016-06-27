/*
 * Copyright [2016] [Stefan Pröll]
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
 * Copyright [2016] [Stefan Pröll]
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

package DataPreparation;


import CSVTools.CsvToolsApi;
import Database.DatabaseOperations.DatabaseTools;
import Database.DatabaseOperations.MigrationTasks;
import Helpers.HelpersCSV;
import QueryStore.BaseTable;
import QueryStore.QueryStoreAPI;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DataPreparation {
    QueryStoreAPI queryStoreAPI;
    PersistentIdentifierAPI pidAPI;
    DatabaseTools dbTools;
    Organization org;

    public DataPreparation(Organization org) {
        this.queryStoreAPI = new QueryStoreAPI();
        this.pidAPI = new PersistentIdentifierAPI();
        this.dbTools = new DatabaseTools();
        this.org = org;



    }

    public void writeBaseTableToCSVFileIncludingSequenceNumbers(String tableName, String outputPath) {
        BaseTable baseTable = this.queryStoreAPI.getBaseTableByTableNameOnly(tableName);
        Date initialDate = baseTable.getUploadDate();
        String baseTableSQL = this.queryStoreAPI.getParentUnfilteredStringFromQueryIncludingSequenceNumber(baseTable,
                initialDate);
        CachedRowSet resultSet = this.dbTools.reExecuteQuery(baseTableSQL);


        CsvToolsApi csvAPI = new CsvToolsApi();


        csvAPI.writeResultSetIntoCSVFile(resultSet, outputPath);


    }

    public String uploadNewCSVFile(String path) {

        List<String> primaryKeys = new ArrayList<String>();
        HelpersCSV csv = new HelpersCSV();
        String fileName = csv.getFileNameFromPath(path);
        String tableName = csv.trimExtension(fileName);


        primaryKeys.add("ID_SYSTEM_SEQUENCE");
        HashMap<String, File> filesList = new HashMap<String, File>();
        filesList.put(tableName, new File(path));


        MigrationTasks migrationTasks = new MigrationTasks();
        migrationTasks.migrate(filesList, primaryKeys);


        return tableName;

    }

    public String createNewBaseTableRecord(String tableName) {
        Organization org;


        if (pidAPI.checkOrganizationPrefix(this.org.getOrganization_prefix()) == false) {
            org = pidAPI.createNewOrganitation("Database SQL Example", this.org.getOrganization_prefix());
        } else {
            org = pidAPI.getOrganizationObjectByPrefix(this.org.getOrganization_prefix());
        }

        this.queryStoreAPI.deleteBaseTableByDatabaseAndTableName(tableName);


        String baseTablePID = this.queryStoreAPI.createBaseTableRecord("Stefan", "Evaluation", tableName, "Evaluation ",
                "Eval Descr", org
                        .getOrganization_prefix(), "dummy");
        return baseTablePID;
    }


}
