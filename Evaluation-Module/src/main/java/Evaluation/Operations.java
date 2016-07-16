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

import CSVTools.CsvToolsApi;
import Database.DatabaseOperations.DatabaseTools;
import Database.DatabaseOperations.MigrateCSV2SQL;
import DatabaseBackend.EvaluationRecordBean;
import GitBackend.GitAPI;
import GitBackend.QueryCSV;
import Helpers.FileHelper;
import Helpers.HelpersCSV;
import QueryStore.BaseTable;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import at.stefanproell.CSV_Tools.CSV_Analyser;
import at.stefanproell.DataGenerator.DataGenerator;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import com.sun.rowset.CachedRowSetImpl;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.*;
import org.supercsv.prefs.CsvPreference;

import javax.sql.rowset.CachedRowSet;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.ResultSetMetaData;
import java.util.*;
import java.util.logging.Logger;

import static Helpers.HelpersCSV.randomString;

/**
 * Created by stefan on 27.06.16.
 */


enum QueryComplexity {
    EASY, STANDARD, COMPLEX
}

enum QueryType {SELECT, INSERT, UPDATE, DELETE}

enum EvaluationSystem {SQL, GIT}


public class Operations {
    private final DataGenerator csvDataWriter;
    private final CSV_Analyser csvAnalyzer;
    private final Logger logger;
    private final QueryStoreAPI queryAPI;
    private final PersistentIdentifierAPI pidAPI;

    private double selectProportion;
    private double insertProportion;
    private double updateProportion;
    private double deleteProportion;
    private QueryComplexity complexity;
    private QueryType type;
    private MigrateCSV2SQL migrate;
    private CSV_Analyser csv_analyser;
    private DatabaseTools dbtools;
    private GitAPI gitApi;
    private FileHelper fileHelper;



    public Operations() {
        csvAnalyzer = new CSV_Analyser();
        csvDataWriter = new DataGenerator();
        logger = Logger.getLogger(Operations.class.getName());
        migrate = new MigrateCSV2SQL();
        csv_analyser = new CSV_Analyser();
        queryAPI = new QueryStoreAPI();
        pidAPI = new PersistentIdentifierAPI();
        dbtools = new DatabaseTools();
        fileHelper = new FileHelper();





    }

    public void randomInsert(PersistentIdentifier pid) {


        CsvToolsApi csvToolsApi = new CsvToolsApi();
        String currentPath = pid.getURI();

        String[] headers = csvToolsApi.getArrayOfHeadersCSV(currentPath);
        int amountOfColumns = headers.length;


        Map<String, Object> newRecord = new HashMap<String, Object>();

        for (int i = 0; i < amountOfColumns; i++) {
            newRecord.put(headers[i], randomString(10, 2));
        }

        ICsvMapWriter mapWriter = null;

        try {
            mapWriter = new CsvMapWriter(new FileWriter(currentPath, true),
                    CsvPreference.STANDARD_PREFERENCE);


            final CellProcessor[] processors = csvDataWriter.getProcessors(amountOfColumns);
            mapWriter.write(newRecord, headers, processors);

            // write the header
       /*
            mapWriter.writeHeader(headers);

            for(int i=0; i < amountOfRecordsInFile;i++){
                Map<String, Object> record = csvMap.get(i);
                mapWriter.write(record,headers, processors);
            }
           */


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mapWriter != null) {
                try {
                    mapWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public void randomDelete(PersistentIdentifier pid) {
        ICsvMapReader mapReader = null;
        CsvToolsApi csvToolsApi = new CsvToolsApi();
        String currentPath = pid.getURI();

        String[] headers = csvToolsApi.getArrayOfHeadersCSV(currentPath);
        int amountOfColumns = headers.length;


        ICsvMapWriter mapWriter = null;
        try {

            // read existing file
            Map<Integer, Map<String, Object>> csvMap = csvAnalyzer.readCSV(new File(currentPath));
            int amountOfRecords = csvMap.size();

            Random rand = new Random();
            int randomRecord = rand.nextInt((amountOfRecords) + 1);
            final CellProcessor[] processors = csvDataWriter.getProcessors(amountOfColumns);


            mapWriter = new CsvMapWriter(new FileWriter(currentPath),
                    CsvPreference.STANDARD_PREFERENCE);
            mapWriter.writeHeader(headers);


            int counter = 0;
            for (Map.Entry<Integer, Map<String, Object>> csvRowMap : csvMap.entrySet()) {

                Map<String, Object> csvRow = csvRowMap.getValue();
                if (randomRecord != counter) {
                    mapWriter.write(csvRow, headers, processors);
                } else {
                    logger.info("Deleting record: " + counter);

                }

                counter++;


            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mapWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public void randomUpdate(PersistentIdentifier pid) {

        CsvToolsApi csvToolsApi = new CsvToolsApi();
        String currentPath = pid.getURI();

        String[] headers = csvToolsApi.getArrayOfHeadersCSV(currentPath);
        int amountOfColumns = headers.length;


        ICsvMapWriter mapWriter = null;
        try {

            // read existing file
            Map<Integer, Map<String, Object>> csvMap = csvAnalyzer.readCSV(new File(currentPath));
            int amountOfRecords = csvMap.size();

            Random rand = new Random();
            int randomRecord = rand.nextInt((amountOfRecords) + 1);
            final CellProcessor[] processors = csvDataWriter.getProcessors(amountOfColumns);


            mapWriter = new CsvMapWriter(new FileWriter(currentPath),
                    CsvPreference.STANDARD_PREFERENCE);
            mapWriter.writeHeader(headers);


            int counter = 0;
            for (Map.Entry<Integer, Map<String, Object>> csvRowMap : csvMap.entrySet()) {

                Map<String, Object> csvRow = csvRowMap.getValue();
                Object columnOne = csvRow.get("COLUMN_1");
                if (columnOne == null) {
                    logger.info("Null");
                }
                String primaryKey = columnOne.toString();
                if (randomRecord != counter) {

                    mapWriter.write(csvRow, headers, processors);
                } else {
                    logger.info("Updating record: " + primaryKey);
                    Map<String, Object> newRecord = new HashMap<String, Object>();

                    // we start from 1 as this is the primary key column
                    newRecord.put("COLUMN_1", primaryKey);

                    for (int i = 1; i < amountOfColumns; i++) {
                        newRecord.put(headers[i], randomString(10, 2));
                    }

                    mapWriter.write(newRecord, headers, processors);

                }

                counter++;


            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mapWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }

    public EvaluationRecordBean executeRandomOperationBasedOnDistribution(PersistentIdentifier tablePid, String exportPath, GitAPI gitAPI, double selectProportion,
                                                                          double insertProportion, double updateProportion, double deleteProportion,double qEasyProbability, double qStandardProbability, double qComplexProbability) {
        Random generator = new Random();
        EvaluationRecordBean recordBean = new EvaluationRecordBean();


        type = null;

        // random number between 0 and 1
        double randomOperation = generator.nextDouble();
        double randomComplexity = generator.nextDouble();


        /**
         * The select statement is essential for testing the systems. Here, the used timestamp is the
         * re-execution timestamp
         */
        if (randomOperation <= selectProportion) {
            type = QueryType.SELECT;


            // Create a query
            PersistentIdentifier queryPid = pidAPI.getAlphaPID(tablePid.getOrganization(), "dummy");
            Query query = queryAPI.createNewQuery("evaluation user", queryPid.getIdentifier());


            BaseTable bt = queryAPI.getBaseTableByTableNameOnly(tablePid.getIdentifier());
            query.setBaseTable(bt);
            query.setQueryDescription("Evaluation query");
            query.setSubSetTitle("Evaluation Subset");
            query.setResultSetRowCount(0);


            queryAPI.updateExecutiontime(query);
            queryAPI.finalizeQuery(query);
            HashMap<Integer, String> selectedColumns;
            String randomFilterString;
            int sequence=0;
            int randomColumn=0;
            int amountOfColumns=0;
            Random random = new Random();

            if(randomComplexity<=qEasyProbability){
                complexity=QueryComplexity.EASY;
            }else if (randomComplexity>qEasyProbability && randomComplexity<=qEasyProbability+qStandardProbability){
                complexity=QueryComplexity.STANDARD;
            }else{
                complexity=QueryComplexity.COMPLEX;
            }


            switch (complexity) {
                case EASY:

                    // Select one column
                    selectedColumns = new HashMap<Integer, String>();
                    selectedColumns.put(0,"COLUMN_1");
                    query.setSelectedColumns(selectedColumns);
                    // add one filter
                    randomFilterString = HelpersCSV.randomString(2, 1);
                    queryAPI.addFilter(query, "COLUMN_1", randomFilterString);
                    queryAPI.persistQuery(query);
                    break;
                case STANDARD:
                    // Select three columns
                    selectedColumns = new HashMap<Integer, String>();
                    TreeMap<String, String> allColumns = dbtools.getColumnNamesWithoutMetadataSortedAlphabetically(tablePid.getIdentifier());
                    // add three filters
                    amountOfColumns = allColumns.size();



                    for(int i =1;i<=3;i++){
                        randomColumn= random.nextInt(amountOfColumns)+1;
                        selectedColumns.put(i, "COLUMN_"+randomColumn);
                    }


                    query.setSelectedColumns(selectedColumns);


                    // add three filters
                    for(int i = 1; i <= 3;i++){

                        randomColumn= random.nextInt(amountOfColumns)+1;
                        randomFilterString = HelpersCSV.randomString(2, 1);
                        queryAPI.addFilter(query, "COLUMN_"+randomColumn, randomFilterString);
                        queryAPI.persistQuery(query);
                    }



                    break;
                case COMPLEX:
                    // Select all columns
                    selectedColumns = new HashMap<Integer, String>();
                    allColumns = dbtools.getColumnNamesWithoutMetadataSortedAlphabetically(tablePid.getIdentifier());
                    sequence = 0;
                    for (Map.Entry<String, String> entry : allColumns.entrySet()) {
                        selectedColumns.put(sequence, entry.getKey());
                        sequence++;
                    }
                    query.setSelectedColumns(selectedColumns);


                    // add three filters
                    amountOfColumns = allColumns.size();

                    for(int i = 1; i <= 3;i++){

                        randomColumn= random.nextInt(amountOfColumns )+1;
                        randomFilterString = HelpersCSV.randomString(2, 1);
                        queryAPI.addFilter(query, "COLUMN_"+randomColumn, randomFilterString);
                        queryAPI.persistQuery(query);
                    }

                    for(int i = 1; i <= 3;i++){

                        randomColumn= random.nextInt(amountOfColumns + 1);
                        int randomSorting = random.nextInt(2);
                        String direction="ASC";
                        if(randomSorting==0){
                            direction="ASC";
                        }else{
                            direction="DESC";
                        }

                        queryAPI.addSorting(query,"COLIMN_"+randomColumn,direction);
                        queryAPI.persistQuery(query);
                    }



                    break;
            }

            // set metadata
            Date date = new Date();
            query.setCreatedDate(date);

            query.setDatasourcePID(tablePid.getIdentifier());


            // Get a random date for the re-execution

            Date tableCreationDate = tablePid.getCreatedDate();
            Date gitFirstCommit = gitAPI.getFirstCommitDate(tablePid.getIdentifier() + ".csv");

            Date startDate = null;
            if (tableCreationDate.after(gitFirstCommit)) {
                startDate = tableCreationDate;
            } else {
                startDate = gitFirstCommit;
            }
            Date nowdate = new Date();
            Date randomDate = gitAPI.getRandomdateBetweentwoDates(startDate, nowdate);



            query.setExecution_timestamp(randomDate);
            recordBean.setReExecutionDate(randomDate);
            // persist
            queryAPI.finalizeQueryEvaluation(query);



            recordBean.setStartTimestampSQL(this.getCurrentTimeStamp());

            CachedRowSetImpl result = dbtools.reExecuteQueryEvaluation(query.getQueryString());
            String fullExportPathSQL = exportPath + query.getDatasourcePID() + "_export_sql.csv";
            amountOfColumns = query.getSelectedColumns().size();
            try {
                writeWithResultSetWriter(result,amountOfColumns, fullExportPathSQL);
            } catch (Exception e) {
                e.printStackTrace();
            }

            recordBean.setEndTimestampSQL(this.getCurrentTimeStamp());
            recordBean.setSqlQuery(query.getQueryString());


            recordBean.setStartTimestampGit(this.getCurrentTimeStamp());

            RevCommit commit = gitAPI.getMostRecentCommit(randomDate, tablePid.getIdentifier() + ".csv");
            String fullExportPathGit = exportPath + query.getDatasourcePID() + "_export_git.csv";

            gitAPI.retrieveFileFromCommit(tablePid.getIdentifier() + ".csv", commit, fullExportPathGit);

            QueryCSV queryCSV = new QueryCSV();
            result = queryCSV.runQuery(query, exportPath, fullExportPathGit);
            try {
                writeWithResultSetWriter(result, amountOfColumns, fullExportPathGit);
            } catch (Exception e) {
                e.printStackTrace();
            }
            String gitQuery =  this.queryAPI.generateQueryStringForGitEvaluation(query);
            recordBean.setGitQuery(gitQuery);
            recordBean.setQueryComplexity(complexity.toString());

            recordBean.setEndTimestampGit(this.getCurrentTimeStamp());


            try {
                String gitHash = gitAPI.createSha1(new File(fullExportPathGit));
                String sqlHash = gitAPI.createSha1(new File(fullExportPathSQL));
                if (gitHash.equals(sqlHash) == false) {

                    logger.severe("Files not identical on: "+query.getQueryString());
                }else{
                    logger.severe("Identical");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
        // INSERT
        else if (randomOperation > selectProportion && randomOperation <= selectProportion + insertProportion) {
            type = QueryType.INSERT;
            this.randomInsert(tablePid);
            this.commitChanges(recordBean, tablePid);


        }
        // UPDATE
        //
        else if (randomOperation > selectProportion + insertProportion &&
                randomOperation <= selectProportion + insertProportion + updateProportion) {
            type = QueryType.UPDATE;

            this.randomUpdate(tablePid);
            this.commitChanges(recordBean, tablePid);


        }
        // DELETE
        // The DELETE Statement is actually an update where the marker is set
        else {
            type = QueryType.DELETE;

            this.randomDelete(tablePid);
            this.commitChanges(recordBean, tablePid);


        }
        recordBean.setQueryType(type.toString());
        int gitSize = fileHelper.getFileFolderSize(gitAPI.getRepoPath());
        recordBean.setGitFolderSizeInBytes(gitSize);
        int sqlSize = dbtools.getDatabaseSizeInBytes();
        recordBean.setSqlDBSizeInBytes(sqlSize);



        return recordBean;
    }

    private void commitChanges(EvaluationRecordBean recordBean, PersistentIdentifier tablePid) {
        

        recordBean.setStartTimestampGit(new Date());
        java.sql.Timestamp updateDate = this.commitChangesToGitSystem(tablePid);
        recordBean.setEndTimestampGit(new Date());


        recordBean.setStartTimestampSQL(new Date());
        this.commitChangesToPrototypeSystem(tablePid,updateDate);
        recordBean.setEndTimestampSQL(new Date());


    }

    private void commitChangesToPrototypeSystem(PersistentIdentifier pid, Date updateDate) {
        Map<Integer, Map<String, Object>> csvMap = csv_analyser.parseCSV(new File(pid.getURI()));
        migrate.updateDataInExistingDBEvaluation(pid.getIdentifier(), csvMap,updateDate);
    }


    private java.sql.Timestamp commitChangesToGitSystem(PersistentIdentifier pid) {
        java.sql.Timestamp commitDate = null;
        try {

            commitDate = gitApi.addAndCommit(new File(pid.getURI()), "Evaluation commit: " + pid.getIdentifier());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return commitDate;
    }

    public GitAPI getGitApi() {
        return gitApi;
    }

    public void setGitApi(GitAPI gitApi) {
        this.gitApi = gitApi;
    }

    /**
     * An example of writing using CsvResultSetWriter
     */
    private static void writeWithResultSetWriter(CachedRowSet resultSet, int amountOfColumns, String outputPath) throws Exception {


        ICsvResultSetWriter resultSetWriter = null;
        try {
            resultSetWriter = new CsvResultSetWriter(new FileWriter(outputPath),
                    CsvPreference.STANDARD_PREFERENCE);
            ResultSetMetaData rsmd = resultSet.getMetaData();



            final CellProcessor[] processors = DataGenerator.getProcessors(amountOfColumns);

            // writer csv file from ResultSet
            resultSetWriter.write(resultSet, processors);

        } finally {
            if (resultSetWriter != null) {
                resultSetWriter.close();
            }
        }
    }

    private java.sql.Timestamp getCurrentTimeStamp(){
        java.util.Date date = new java.util.Date();
        java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
        return timestamp;


    }
}
