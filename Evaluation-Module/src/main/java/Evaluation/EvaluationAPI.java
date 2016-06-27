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

package Evaluation;

import DataPreparation.DataPreparation;
import Database.DatabaseOperations.DatabaseTools;
import Helpers.FileHelper;
import QueryStore.QueryStoreAPI;
import TestDataGenerator.TestDataGenerator;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import org.apache.commons.collections4.list.TreeList;

import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import static org.apache.commons.lang3.StringUtils.appendIfMissing;

/**
 * Created by stefan on 27.06.16.
 */
public class EvaluationAPI {
    private Logger logger;
    private PersistentIdentifierAPI pidAPI;
    private Organization evaluationOrganization;
    private String evaluationCsvFolder;
    private FileHelper fileHelper;
    private QueryStoreAPI queryStoreAPI;
    private DatabaseTools dbTools;
    private final String evaulationAuthor = "EvaluationAuthor";
    private final String defaultDbSchema = "EvaluationDB";
    private List<PersistentIdentifier> listOfCsvFilePersistentIdentifiers;

    public EvaluationAPI(int organizationalPrefix, String evaluationCsvFolder) {
        logger = Logger.getLogger(EvaluationAPI.class.getName());
        pidAPI = new PersistentIdentifierAPI();
        fileHelper = new FileHelper();
        queryStoreAPI = new QueryStoreAPI();
        dbTools = new DatabaseTools();


        evaluationOrganization = pidAPI.getOrganizationObjectByPrefix(organizationalPrefix);
        if (evaluationOrganization == null) {
            evaluationOrganization = pidAPI.createNewOrganitation("Evaluation Organization", organizationalPrefix);
        }

        this.evaluationCsvFolder = appendIfMissing(evaluationCsvFolder, "/");
        fileHelper.createDirectory(new File(evaluationCsvFolder));

    }

    /**
     * Create a new CSV file and store the generated PID in a list
     *
     * @param amountOfCsvFiles
     * @param amountOfRecords
     * @param amountOfColumns
     * @param averageRecordLength
     * @param variance
     * @return
     */
    public List<PersistentIdentifier> createCsvFiles(int amountOfCsvFiles, int amountOfRecords, int amountOfColumns, int averageRecordLength, double
            variance) {
        this.listOfCsvFilePersistentIdentifiers = new TreeList<PersistentIdentifier>();
        for (int i = 0; i < amountOfCsvFiles; i++) {
            PersistentIdentifier pid = this.createNewRandomCSVFile(amountOfRecords, amountOfColumns, averageRecordLength, variance);
            this.listOfCsvFilePersistentIdentifiers.add(pid);

        }

        return this.listOfCsvFilePersistentIdentifiers;

    }


    public void uploadListOfCsvFiles(List<PersistentIdentifier> listOfCsvFilePersistentIdentifiers) {
        for (PersistentIdentifier pid : listOfCsvFilePersistentIdentifiers) {
            this.uploadCsvFileIntoProrotypeSystem(pid);
        }

    }


    /**
     * @param amountOfRecords
     * @param columns
     * @param averageRecordLength
     * @param variance
     * @return
     */
    public PersistentIdentifier createNewRandomCSVFile(int amountOfRecords, int columns, int averageRecordLength, double
            variance) {

        String fileName = null;
        // Create a new PID for the
        PersistentIdentifier pid = pidAPI.getAlphaNumericPID(evaluationOrganization, "dummy");
        fileName = evaluationCsvFolder + pid.getIdentifier() + ".csv";
        pid.setURI(fileName);

        TestDataGenerator generator = new TestDataGenerator();
        try {
            fileName = generator.writeWithCsvMapWriter(fileName, columns, amountOfRecords, averageRecordLength, variance);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pid;
    }

    public String uploadCsvFileIntoProrotypeSystem(PersistentIdentifier pid) {
        //api.uploadCsvFileIntoProrotypeSystem(pid);
        DataPreparation prep = new DataPreparation(this.getEvaluationOrganization());
        String tableName = prep.uploadNewCSVFile(pid.getURI());
        DatabaseTools dbTools = new DatabaseTools();
        dbTools.updatePrimaryKey(pid.getIdentifier(), "Column_1");

        String baseTablePid = prep.createNewBaseTableRecord(tableName);
        return baseTablePid;
    }

    public void runOperations(List<PersistentIdentifier> listOfCsvFilePersistentIdentifiers) {
        Operations op = new Operations();
        for (PersistentIdentifier pid : listOfCsvFilePersistentIdentifiers) {
            op.randomInsert(pid);
            op.randomDelete(pid);
            op.randomDelete(pid);
            op.randomDelete(pid);
            op.randomDelete(pid);

            op.randomUpdate(pid);
        }

    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

    public PersistentIdentifierAPI getPidAPI() {
        return pidAPI;
    }

    public void setPidAPI(PersistentIdentifierAPI pidAPI) {
        this.pidAPI = pidAPI;
    }

    public Organization getEvaluationOrganization() {
        return evaluationOrganization;
    }

    public void setEvaluationOrganization(Organization evaluationOrganization) {
        this.evaluationOrganization = evaluationOrganization;
    }

    public String getEvaluationCsvFolder() {
        return evaluationCsvFolder;
    }

    public void setEvaluationCsvFolder(String evaluationCsvFolder) {
        this.evaluationCsvFolder = evaluationCsvFolder;
    }

    public FileHelper getFileHelper() {
        return fileHelper;
    }

    public void setFileHelper(FileHelper fileHelper) {
        this.fileHelper = fileHelper;
    }

    public QueryStoreAPI getQueryStoreAPI() {
        return queryStoreAPI;
    }

    public void setQueryStoreAPI(QueryStoreAPI queryStoreAPI) {
        this.queryStoreAPI = queryStoreAPI;
    }

    public DatabaseTools getDbTools() {
        return dbTools;
    }

    public void setDbTools(DatabaseTools dbTools) {
        this.dbTools = dbTools;
    }

    public String getEvaulationAuthor() {
        return evaulationAuthor;
    }

    public String getDefaultDbSchema() {
        return defaultDbSchema;
    }
}
