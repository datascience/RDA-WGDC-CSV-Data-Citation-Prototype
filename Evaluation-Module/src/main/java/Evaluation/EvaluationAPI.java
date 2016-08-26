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
import Database.DatabaseOperations.MigrateCSV2SQL;
import DatabaseBackend.EvaluationRecordBean;
import DatabaseBackend.EvaluationRunBean;
import GitBackend.GitAPI;
import Helpers.FileHelper;
import QueryStore.QueryStoreAPI;
import TestDataGenerator.TestDataGenerator;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import org.apache.commons.collections4.list.TreeList;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.GC;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static org.apache.commons.lang3.StringUtils.appendIfMissing;


/**
 * Created by stefan on 27.06.16.
 */
public class EvaluationAPI {

    private int amountOfOperations;
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
    private GitAPI gitAPI;
    private int organizationalPrefix;
    private String repositoryPath;
    private String exportPath;
    private double selectProportion;
    private double insertProportion;
    private double updateProportion;
    private double deleteProportion;
    double qEasyProbability=0.6;
    double qStandardProbability=0.3;
    double qComplexProbability=0.1;
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    private EvaluationRunBean runBean;



    public EvaluationAPI(int organizationalPrefix, String evaluationCsvFolder, String repositoryPath, String exportPath, double selectProportion, double insertProportion,
                         double updateProportion, double deleteProportion, int amountOfOperations, double qEasyProbability, double qStandardProbability, double qComplexProbability) {
        try {
            setUpBackend();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.organizationalPrefix = organizationalPrefix;
        this.evaluationCsvFolder = evaluationCsvFolder;
        this.repositoryPath = repositoryPath;
        this.selectProportion = selectProportion;
        this.insertProportion = insertProportion;
        this.updateProportion = updateProportion;
        this.deleteProportion = deleteProportion;
        this.qEasyProbability=qEasyProbability;
        this.qStandardProbability = qStandardProbability;
        this.qComplexProbability = qComplexProbability;

        this.amountOfOperations = amountOfOperations;
        this.exportPath = exportPath;

        logger = Logger.getLogger(EvaluationAPI.class.getName());
        pidAPI = new PersistentIdentifierAPI();
        fileHelper = new FileHelper();
        queryStoreAPI = new QueryStoreAPI();
        dbTools = new DatabaseTools();
        runBean = new EvaluationRunBean();
        runBean.setStartDate(new Date());





        try {

            // Sleep for 1 second
            try {
                logger.info("Going to sleep...");
                TimeUnit.MILLISECONDS.sleep(1000);
                logger.info("Wakeing up...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            initEvaluationSystem();

            persistRunBean(runBean);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void initEvaluationSystem() {
        gitAPI = new GitAPI();
        try {
            gitAPI.initRepository(repositoryPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
            Date originalInsertDate = this.uploadCsvFileIntoGitSystem(pid);
            pid.setCreatedDate(originalInsertDate);
            this.uploadCsvFileIntoProrotypeSystem(pid);
            this.dbTools.fakeInsertdataEvaluation(pid);
            MigrateCSV2SQL csvMigrate = new MigrateCSV2SQL();
            csvMigrate.addDatabaseIndexOnColumn(pid.getIdentifier(),"COLUMN_1");

            try {
                logger.info("Going to sleep...");
                TimeUnit.MILLISECONDS.sleep(1000);
                logger.info("Wakeing up...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    private java.sql.Timestamp uploadCsvFileIntoGitSystem(PersistentIdentifier pid) {
        java.sql.Timestamp insertDate = null;
        try {
            insertDate= gitAPI.addAndCommit(new File(pid.getURI()), "initial commit");
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return  insertDate;

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
        PersistentIdentifier pid = pidAPI.getAlphaPID(evaluationOrganization, "dummy");
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

    /**
     * @param amountOfRecords
     * @param columns
     * @param averageRecordLength
     * @param variance
     * @return
     */
    public PersistentIdentifier createNewRandomCSVFileWithDataTypes(int amountOfRecords, int columns, int averageRecordLength, double
            variance) {
        //todo korrektur

        String fileName = null;
        // Create a new PID for the
        PersistentIdentifier pid = pidAPI.getAlphaPID(evaluationOrganization, "dummy");
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
        dbTools.updatePrimaryKey(pid.getIdentifier(), "COLUMN_1");

        String baseTablePid = prep.createNewBaseTableRecord(tableName);
        return baseTablePid;
    }

    public void runOperations(List<PersistentIdentifier> listOfCsvFilePersistentIdentifiers) {
        Operations op = new Operations();
        op.setGitApi(gitAPI);



        int counter =0;
        int operationCount=0;
        for (PersistentIdentifier pid : listOfCsvFilePersistentIdentifiers) {
            counter++;
            for (int i = 0; i < amountOfOperations; i++) {
                System.out.println("Processing CSV file number: " + counter);
                operationCount++;

                EvaluationRecordBean recordBean = op.executeRandomOperationBasedOnDistribution(pid, exportPath, gitAPI,  selectProportion, insertProportion, updateProportion, deleteProportion,qEasyProbability,qStandardProbability,qComplexProbability);
                recordBean.setOperationCount(operationCount);
                // Sleep for 1 second
                try {
                    logger.info("Going to sleep...");
                    TimeUnit.MILLISECONDS.sleep(1000);
                    logger.info("Wakeing up...");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                recordBean.setEvaluationRunBean(runBean);
                persistRecordBean(recordBean);
                runBean.getEvaluationRecordSet().add(recordBean);





            }

        }

    }

    /**
     * Initialize new Git repository
     */
    public void gitRepositoryInit() {

        try {
            gitAPI.initRepository(this.getRepositoryPath());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Export the most recent version of the data to a CSV file
     */
    public void exportMostRecentDataVersionsAsCSV(String tableName) {
        CachedRowSet crs = dbTools.getAllColumnsWithoutMetadataAsResultSet(tableName);
        dbTools.exportResultSetAsCSV(crs, this.getRepositoryPath() + "/" + this.getRepositoryPath());
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

    public List<PersistentIdentifier> getListOfCsvFilePersistentIdentifiers() {
        return listOfCsvFilePersistentIdentifiers;
    }

    public void setListOfCsvFilePersistentIdentifiers(List<PersistentIdentifier> listOfCsvFilePersistentIdentifiers) {
        this.listOfCsvFilePersistentIdentifiers = listOfCsvFilePersistentIdentifiers;
    }

    public GitAPI getGitAPI() {
        return gitAPI;
    }

    public void setGitAPI(GitAPI gitAPI) {
        this.gitAPI = gitAPI;
    }

    public int getOrganizationalPrefix() {
        return organizationalPrefix;
    }

    public void setOrganizationalPrefix(int organizationalPrefix) {
        this.organizationalPrefix = organizationalPrefix;
    }

    public String getRepositoryPath() {
        return repositoryPath;
    }

    public void setRepositoryPath(String repositoryPath) {
        this.repositoryPath = repositoryPath;
    }

    /**
     * Setup the session factory with hibernate native api
     *
     * @throws Exception
     */
    protected void setUpBackend() throws Exception {


        try {

            Configuration configuration = new Configuration();
            configuration.configure("hibernate.evaluation.cfg.xml");


            configuration.addAnnotatedClass(EvaluationRunBean.class);
            configuration.addAnnotatedClass(EvaluationRecordBean.class);

            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);


        } catch (HibernateException he) {
            System.err.println("Error creating Session: " + he);
            throw new ExceptionInInitializerError(he);
        }

    }

    private void persistRecordBean(EvaluationRecordBean record) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(record);
            session.getTransaction().commit();
        } catch (RuntimeException e) {

            throw e; // or display error message
        } finally {
            session.close();
        }

    }

    private void persistRunBean(EvaluationRunBean run) {
        Session session = sessionFactory.openSession();
        try {
            session.beginTransaction();
            session.saveOrUpdate(run);
            session.getTransaction().commit();
        } catch (RuntimeException e) {

            throw e; // or display error message
        } finally {
            session.close();
        }

    }

    public void setRunBeanStopTime(){
        this.runBean.setEndDateDate(new Date());
        persistRunBean(runBean);

    }

    public void setRunDetails(String runName, String evaluationMachine, int amountOfColumns, int amountOfCsvFiles, int amountOfOperations, int amountOfRecords, double selectProportion, double deleteProportion, double updateProportion, double insertProportion) {
        this.runBean.setAmountOfColumns(amountOfColumns);
        this.runBean.setAmountOfCsvFiles(amountOfCsvFiles);
        this.runBean.setAmountOfOperations(amountOfOperations);
        this.runBean.setAmountOfRecords(amountOfRecords);
        this.runBean.setDeleteProportion(deleteProportion);
        this.runBean.setInsertProportion(insertProportion);
        this.runBean.setUpdateProportion(updateProportion);
        this.runBean.setSelectProportion(selectProportion);
        this.runBean.setEvaluationMachineName(evaluationMachine);
        this.runBean.setRunName(runName);
        persistRunBean(this.runBean);
    }


}
