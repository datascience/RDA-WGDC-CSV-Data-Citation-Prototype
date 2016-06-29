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
import Database.DatabaseOperations.MigrationTasks;
import Helpers.HelpersCSV;
import QueryStore.BaseTable;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import at.stefanproell.CSV_Tools.CSV_Analyser;
import at.stefanproell.DataGenerator.DataGenerator;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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


    public Operations() {
        csvAnalyzer = new CSV_Analyser();
        csvDataWriter = new DataGenerator();
        logger = Logger.getLogger(Operations.class.getName());
        migrate = new MigrateCSV2SQL();
        csv_analyser = new CSV_Analyser();
        queryAPI = new QueryStoreAPI();
        pidAPI = new PersistentIdentifierAPI();
        dbtools = new DatabaseTools();
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
                Object columnOne = csvRow.get("Column_1");
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
                    newRecord.put("Column_1", primaryKey);

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

    public void executeRandomOperationBasedOnDistribution(PersistentIdentifier tablePid, QueryComplexity complexity, double selectProportion,
                                                          double insertProportion, double updateProportion, double deleteProportion) {
        Random generator = new Random();


        type = null;

        // random number between 0 and 1
        double randomNumber = generator.nextDouble();


        /**
         * The select statement is essential for testing the systems. Here, the used timestamp is the
         * re-execution timestamp
         */
        if (randomNumber <= selectProportion) {
            type = QueryType.SELECT;


            // Create a query
            PersistentIdentifier queryPid = pidAPI.getAlphaNumericPID(tablePid.getOrganization(), "dummy");
            Query query = queryAPI.createNewQuery("evaluation user", queryPid.getIdentifier());


            BaseTable bt = queryAPI.getBaseTableByTableNameOnly(tablePid.getIdentifier());
            query.setBaseTable(bt);
            query.setQueryDescription("Evaluation query");
            query.setSubSetTitle("Evaluation Subset");
            query.setResultSetRowCount(0);


            queryAPI.updateExecutiontime(query);
            queryAPI.finalizeQuery(query);
/*
            // some filters
            queryAPI.addFilter(query, "Filter1", "Value1");
            queryAPI.addFilter(query, "Filter2", "Value2");
            queryAPI.addFilter(query, "Filter3", "Value3");
            queryAPI.addFilter(query, "Filter4", "Value4");
            queryAPI.addFilter(query, "Filter5", "Value5");

            // some sortings
            queryAPI.addSorting(query, "ColumnA", "DESC");
            queryAPI.addSorting(query, "ColumnB", "ASC");
            queryAPI.addSorting(query, "ColumnC", "ASC");

*/


            switch (complexity) {
                case EASY:

                    // Select all columns
                    HashMap<Integer, String> selectedColumns = new HashMap<Integer, String>();
                    TreeMap<String, String> allColumns = dbtools.getColumnNamesWithoutMetadataSortedAlphabetically(tablePid.getIdentifier());
                    int sequence = 0;
                    for (Map.Entry<String, String> entry : allColumns.entrySet()) {
                        selectedColumns.put(sequence, entry.getKey());
                        sequence++;
                    }
                    query.setSelectedColumns(selectedColumns);


                    // add one filter

                    String randomFilterString = HelpersCSV.randomString(4, 2) + "%";
                    queryAPI.addFilter(query, "Column_1", randomFilterString);

                    queryAPI.persistQuery(query);

                    // set metadata
                    Date date = new Date();
                    query.setCreatedDate(date);
                    query.setExecution_timestamp(date);
                    query.setDatasourcePID(tablePid.getIdentifier());

                    // persist
                    queryAPI.finalizeQuery(query);




                    break;
                case STANDARD:

                    break;
                case COMPLEX:


                    break;
            }

        }
        // INSERT
        else if (randomNumber > selectProportion && randomNumber <= selectProportion + insertProportion) {
            type = QueryType.INSERT;
            this.randomInsert(tablePid);
            this.commitChangesToPrototypeSystem(tablePid);


        }
        // UPDATE
        //
        else if (randomNumber > selectProportion + insertProportion &&
                randomNumber <= selectProportion + insertProportion + updateProportion) {
            type = QueryType.UPDATE;
            this.randomUpdate(tablePid);
            this.commitChangesToPrototypeSystem(tablePid);


        }
        // DELETE
        // The DELETE Statement is actually an update where the marker is set
        else {
            type = QueryType.DELETE;
            this.randomDelete(tablePid);
            this.commitChangesToPrototypeSystem(tablePid);

        }
    }

    private void commitChangesToPrototypeSystem(PersistentIdentifier pid) {


        Map<Integer, Map<String, Object>> csvMap = csv_analyser.parseCSV(new File(pid.getURI()));
        migrate.updateDataInExistingDB(pid.getIdentifier(), csvMap);




    }
}
