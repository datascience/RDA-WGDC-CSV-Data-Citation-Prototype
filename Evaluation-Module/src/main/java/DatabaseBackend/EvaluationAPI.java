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

package DatabaseBackend;


import Database.DatabaseOperations.DatabaseTools;
import GitBackend.GitAPI;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class EvaluationAPI {
    private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    private EvaluationRunBean evaluationRunBean;
    private DatabaseTools dbTools;
    private Logger logger;
    private String repositoryPaTH;
    private String exportCsvFileName;
    private GitAPI gitAPI;
    private String tableName;
    private int currentIteration;


    /**
     * Constructor, creates the session factory
     */
    public EvaluationAPI() {
        try {
            setUp();
        } catch (Exception e) {
            e.printStackTrace();
        }

        logger = Logger.getLogger(this.getClass().getName());
        dbTools = new DatabaseTools();
        gitAPI = new GitAPI();


    }


    /**
     * Setup the session factory with hibernate native api
     *
     * @throws Exception
     */
    protected void setUp() throws Exception {


        try {

            Configuration configuration = new Configuration();
            configuration.configure("hibernate.evaluation.cfg.xml");

            serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);


        } catch (HibernateException he) {
            System.err.println("Error creating Session: " + he);
            throw new ExceptionInInitializerError(he);
        }

    }

    public EvaluationRunBean createNewEvaluationRun() {
        if (evaluationRunBean == null) {
            evaluationRunBean = new EvaluationRunBean();
        }


        Session session = sessionFactory.openSession();
        try {


            session.beginTransaction();

            evaluationRunBean.setStartDate(new Date());
            session.save(evaluationRunBean);
            session.getTransaction().commit();
        } catch (RuntimeException e) {

            throw e; // or display error message
        } finally {
            session.close();
        }


        return evaluationRunBean;


    }


    /**
     * Run x iterations
     *
     * @param tableName
     * @param amountOfQueries
     * @param complexity
     * @param selectProportion
     * @param insertProportion
     * @param updateProportion
     * @param deleteProportion
     */
    public void runEvaluation(String tableName, int amountOfQueries, QueryComplexity
            complexity, double selectProportion, double insertProportion, double
                                      updateProportion, double deleteProportion) {
        this.tableName = tableName;

        for (currentIteration = 0; currentIteration < amountOfQueries; currentIteration++) {
            QueryGenerator qG = new QueryGenerator(tableName, complexity, selectProportion, insertProportion,
                    updateProportion, deleteProportion);

            EvaluationRecordBean evalRecord = qG.generateQueryStatement();


            evalRecord = execute(evalRecord);


        }


    }

    /**
     * @return
     */
    public EvaluationRunBean getEvaluationRunBean() {
       return evaluationRunBean;
    }

    public void setEvaluationRunBean(EvaluationRunBean evaluationRunBean) {
        this.evaluationRunBean = evaluationRunBean;
    }

    /**
     * Set the start time of the evaluation to the current time stamp
     */
    public void setEvaluationStartTime() {
        evaluationRunBean.setStartDate(new Date());
    }

    /**
     * Set the end time of the evaluation
     */
    public void setEvaluationStoppTime() {
        evaluationRunBean.setEndDateDate(new Date());
        logger.info("Time: " + (computeTimedifference(evaluationRunBean.getStartDate(), evaluationRunBean
                .getEndDateDate())) / 1000 + " seconds");
    }

    /**
     * Get time difference in millis
     *
     * @param start
     * @param stop
     * @return
     */
    public long computeTimedifference(Date start, Date stop) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");

        long difference = stop.getTime() - start.getTime();
        return difference;

    }

    /**
     * Execute queries
     *
     * @param evalRecord
     * @return
     */
    private EvaluationRecordBean execute(EvaluationRecordBean evalRecord) {

        // create a random re-execution date
        Date randomReExecutionDate = this.computeRandomReExecutiondate();

        // persist

        Session session = sessionFactory.openSession();
        try {


            session.beginTransaction();
            evalRecord.setReExecutionDate(randomReExecutionDate);
            evalRecord.setStartTimestampSQL(new Date());
            evalRecord.setEvaluationRunBean(evaluationRunBean);
            dbTools.executeQueryForEvaluation(evalRecord.getSqlQuery());

            evalRecord.setEndTimestampSQL(new Date());

            session.save(evalRecord);
            session.getTransaction().commit();
        } catch (RuntimeException e) {

            throw e; // or display error message
        } finally {
            session.close();
        }


        // Git part
        evalRecord.setStartTimestampGit(new Date());
        exportMostRecentDataVersionsAsCSV();
        addAndCommit();
        evalRecord.setEndTimestampGit(new Date());
        return evalRecord;

    }


    /**
     * Initialize new Git repository
     */
    public void gitRepositoryInit() {

        try {
            gitAPI.initRepository(this.getRepositoryPaTH());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Export the most recent version of the data to a CSV file
     */
    public void exportMostRecentDataVersionsAsCSV() {
        CachedRowSet crs = dbTools.getAllColumnsWithoutMetadataAsResultSet(tableName);
        dbTools.exportResultSetAsCSV(crs, this.getRepositoryPaTH() + "/" + this.getExportCsvFileName());
    }

    private void addAndCommit() {
        try {
            gitAPI.openRepository(new File(this.getRepositoryPaTH()));
            gitAPI.setCommitMessage("This was iteration " + currentIteration);
            gitAPI.addAndCommit(new File(this.getRepositoryPaTH() + "/" + this.getExportCsvFileName()), "This was iteration " + currentIteration);
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieve a random date between the earliest and the latest change to the table.
     *
     * @return
     */
    public Date computeRandomReExecutiondate() {
        return this.dbTools.getRandomDateBetweenMinAndMax(tableName);
    }


    public String getRepositoryPaTH() {
        return repositoryPaTH;
    }

    public void setRepositoryPaTH(String repositoryPaTH) {
        this.repositoryPaTH = repositoryPaTH;
    }

    public String getExportCsvFileName() {
        return exportCsvFileName;
    }

    public void setExportCsvFileName(String exportCsvFileName) {
        this.exportCsvFileName = exportCsvFileName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public GitAPI getGitAPI() {
        return gitAPI;
    }

    public void setGitAPI(GitAPI gitAPI) {
        this.gitAPI = gitAPI;
    }
}
