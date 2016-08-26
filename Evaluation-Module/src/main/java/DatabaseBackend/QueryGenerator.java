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
import Helpers.HelpersCSV;
import QueryStore.Query;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */

enum QueryComplexity {
    EASY, STANDARD, COMPLEX
}

enum QueryType {SELECT, INSERT, UPDATE, DELETE}

enum EvaluationSystem {SQL, GIT}


public class QueryGenerator {

    private double selectProportion;
    private double insertProportion;
    private double updateProportion;
    private double deleteProportion;
    private QueryComplexity complexity;
    private QueryType type;
    private DatabaseTools dbTools;

    private ArrayList<Integer> systemSequenceSimulator;
    private String tableName;
    private static final Logger logger =
            Logger.getLogger(QueryGenerator.class.getName());


    public QueryGenerator(String tableName, QueryComplexity
            complexity, double
                                  selectProportion, double
                                  insertProportion, double
                                  updateProportion, double deleteProportion) {

        this.dbTools = new DatabaseTools();

        this.tableName = tableName;


        this.complexity = complexity;
        this.selectProportion = selectProportion;
        this.insertProportion = insertProportion;
        this.updateProportion = updateProportion;
        this.deleteProportion = deleteProportion;


    }

    /**
     * Generate a random query and return a evaluation record object.
     */
    public EvaluationRecordBean generateQueryStatement() {

        EvaluationRecordBean evaluationRecordBean = new EvaluationRecordBean();

        Date executionDate = new Date();


        List<String> columns = this.dbTools.getColumnsFromDatabaseAsListWithoutMetadata(tableName);

        String[] headers = columns.toArray(new String[columns.size()]);


        Random generator = new Random();
        String queryStringSQL = null;
        String queryStringGit = null;
        int randomSequenceNumber = 0;

        Query q = null;
        type = null;

        // random number between 0 and 1
        double randomNumber = generator.nextDouble();

        /**
         * The select statement is essential for testing the systems. Here, the used timestamp is the
         * re-execution timestamp
         */
        if (randomNumber <= selectProportion) {
            type = QueryType.SELECT;

            switch (complexity) {
                case EASY:
                    String sql = this.generateSelectStarEasy(headers, complexity,
                            type);
                    queryStringSQL = sql + this.getInnerSQL(executionDate);
                    queryStringGit = sql;

                    break;
                case STANDARD:
                    String selectedColumns = HelpersCSV.pickRandomColumn(headers) + ", " + HelpersCSV
                            .pickRandomColumn(headers) +
                            ", " +
                            HelpersCSV.pickRandomColumn(headers);
                    String selectedFilterColumn = HelpersCSV.pickRandomColumn(headers);
                    String selectedFilterValue = "%" + HelpersCSV.randomString(2, 1.0) + "%";
                    queryStringSQL = this.generateSelectStandard(selectedColumns,
                            selectedFilterColumn, selectedFilterValue,
                            headers,
                            complexity, type, executionDate);
                    queryStringGit = "SELECT " + selectedColumns + " FROM " + tableName + " WHERE " +
                            "" + selectedFilterColumn + " LIKE \' " + selectedFilterValue + "\'";
                    break;
                case COMPLEX:

                    Map<String, String> filtersMap = new HashMap<String, String>();
                    filtersMap.put(HelpersCSV.pickRandomColumn(headers), ("%" + HelpersCSV.randomString(5, 2.0) +
                            "%"));
                    filtersMap.put(HelpersCSV.pickRandomColumn(headers), ("%" + HelpersCSV.randomString(5, 2.0) +
                            "%"));
                    filtersMap.put(HelpersCSV.pickRandomColumn(headers), ("%" + HelpersCSV.randomString(5, 2.0) +
                            "%"));

                    String whereSQL = this.dbTools.getWhereString(filtersMap);

                    selectedColumns = HelpersCSV.pickRandomColumn(headers) + ", " + HelpersCSV
                            .pickRandomColumn(headers) +
                            ", " +
                            HelpersCSV.pickRandomColumn(headers);

                    queryStringSQL = this.generateSelectComplex(selectedColumns, whereSQL);

                    queryStringGit = "SELECT " + selectedColumns + " FROM " + tableName + " " + whereSQL;

                    break;
            }

        }
        // INSERT
        else if (randomNumber > selectProportion && randomNumber <= selectProportion + insertProportion) {
            type = QueryType.INSERT;
            ArrayList<String> randomRecord = this.getRandomInsertValues(headers);

            queryStringSQL = this.insertSQLString(randomRecord, randomSequenceNumber, headers);
            queryStringGit = queryStringSQL;

        }
        // UPDATE
        //
        else if (randomNumber > selectProportion + insertProportion &&
                randomNumber <= selectProportion + insertProportion + updateProportion) {
            type = QueryType.UPDATE;
            ArrayList<String> randomRecord = this.getRandomInsertValues(headers);
            randomSequenceNumber = this.getRandomSequenceNumber();

            boolean exists = this.dbTools.checkIfRecordExistsInTableBySequenceNumber(tableName,
                    randomSequenceNumber);

            if (exists) {
                queryStringSQL = this.createSQLUpdateString(randomRecord, randomSequenceNumber, headers);
                queryStringGit = queryStringSQL;
            }


        }
        // DELETE
        // The DELETE Statement is actually an update where the marker is set
        else {
            type = QueryType.DELETE;
            randomSequenceNumber = this.getRandomSequenceNumber();

            queryStringSQL = "UPDATE " + tableName + " SET RECORD_STATUS='deleted' WHERE " +
                    "ID_SYSTEM_SEQUENCE=" + randomSequenceNumber;
            queryStringGit = "DELETE FROM " + tableName + " WHERE ID_SYSTEM_SEQUENCE=" + randomSequenceNumber;
        }


        evaluationRecordBean.setSqlQuery(queryStringSQL);
        evaluationRecordBean.setGitQuery(queryStringGit);
        evaluationRecordBean.setQueryType(type.toString());
        evaluationRecordBean.setQueryComplexity(complexity.toString());

        logger.info("Created eval record with SQL: " + queryStringSQL + " and Git: " + queryStringGit + " of type " +
                type.toString());

        return evaluationRecordBean;


    }


    private String getMySQLDateString(long unixDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYY-MM-dd hh:mm:ss");
        String minDateString = sdf.format(unixDate * 1000);
        return minDateString;
    }

    private String getMySQLDateString(Date javaDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("YYY-MM-dd hh:mm:ss");
        String minDateString = sdf.format(javaDate);
        return minDateString;
    }


    private String generateSelectStarEasy(String[] headers,
                                          QueryComplexity
                                                  complexity, QueryType type) {

        //String innerSQL = this.dbQueries.getMostRecentVersionSQLString("ID_SYSTEM_SEQUENCE", tableName);
        String sql = "SELECT " + HelpersCSV.joinArrayAsString(headers) + " FROM "
                + tableName;


        return sql;

    }


    private String generateSelectStandard(String selectedColumns, String selectedFilter, String
            selectedFilterValue, String[]
                                                  headers,
                                          QueryComplexity
                                                  complexity, QueryType type, Date executionDate) {

        String sql = "SELECT " + selectedColumns + " FROM " + tableName;


        Map<String, String> filtersMap = new HashMap<String, String>();
        filtersMap.put(selectedFilter, selectedFilterValue);
        String whereSQL = this.dbTools.getWhereString(filtersMap);
        sql = sql + this.getInnerSQL(executionDate) + whereSQL;
        return sql;

    }

    private String generateSelectComplex(String selectedColumns, String whereString) {


        String sql = "SELECT " + selectedColumns;

        String innerSQL = this.dbTools.getMostRecentVersionSQLString("ID_SYSTEM_SEQUENCE", tableName);


        sql = sql + innerSQL + whereString;
        return sql;
    }

    private static CellProcessor[] getProcessors(int amountOfColumns) {
        CellProcessor[] processors = new CellProcessor[amountOfColumns];

        for (int i = 0; i < amountOfColumns; i++) {
            processors[i] = new Optional();

        }
        return processors;
    }


    private ArrayList<String> getRandomInsertValues(String[] headers) {
        ArrayList<String> randomRecord = new ArrayList<String>(headers.length);
        for (int i = 0; i < headers.length; i++) {
            randomRecord.add(i, HelpersCSV.randomString(5, 2.0));
        }

        return randomRecord;

    }

    /**
     * SQL String for inserting new records including metadata
     *
     * @param randomRecord
     * @param sequenceNumber
     * @param headers
     * @return
     */
    private String insertSQLString(ArrayList<String> randomRecord, int sequenceNumber, String[]
            headers) {
        Date currentDate = new Date();
        int maxSystemSequence = dbTools.getMaxSequenceNumberFromTable(tableName);


        String headerList = HelpersCSV.joinArrayAsString(headers);
        String sql = "INSERT INTO " + tableName + " (ID_SYSTEM_SEQUENCE," + headerList + ",INSERT_DATE,LAST_UPDATE," +
                "RECORD_STATUS) " +
                "VALUES (" + maxSystemSequence + "," +
                HelpersCSV.joinArrayListAsEscapedString(randomRecord) + ",\"" + new Timestamp(currentDate.getTime())
                + "\",\"" + new Timestamp(currentDate.getTime()) +
                "\"," +
                "\"INSERTED\")";
        return sql;

    }


    /**
     * Provides a list of changed records
     *
     * @param randomRecord
     * @param sequenceNumber
     * @param headers
     * @return
     */
    private String insertGitString(ArrayList<String> randomRecord, int sequenceNumber, String[]
            headers) {

        String headerList = HelpersCSV.joinArrayAsString(headers);
        String CSV = sequenceNumber + "," + HelpersCSV.joinArrayListAsString(randomRecord);

        return CSV;

    }


    private void pickRowsToDelete() {


    }


    private String[] removeSystemHeadersFromArray(String[] headers) {
        String[] onlyCustomHeaders = new String[headers.length - 4];
        int counter = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase("ID_SYSTEM_SEQUENCE") == false && headers[i].equalsIgnoreCase
                    ("INSERT_DATE") == false && headers[i].equalsIgnoreCase("LAST_UPDATE") == false && headers[i]
                    .equalsIgnoreCase("RECORD_STATUS") == false) {
                counter++;
                onlyCustomHeaders[counter] = headers[i];
            }
        }

        return onlyCustomHeaders;

    }

    private int getRandomSequenceNumber() {
        int maxSequenceNumber = this.dbTools.getMaxSequenceNumberFromTable(this.tableName);
        Random random = new Random();
        int randomSequenceNumber = random.nextInt(maxSequenceNumber);
        return randomSequenceNumber;
    }


    private String createSQLUpdateString(ArrayList<String> randomRecord, int sequenceNumber,
                                         String[] headers) {

        //// TODO: 01.02.16 zwei statements. eines aktualisiert den alten wert, das zweite fügt neue zeile ein

        Date currentDate = new Date();

        Date insertDate = dbTools.getInsertDateFromRecord(tableName, "ID_SYSTEM_SEQUENCE", String.valueOf
                (sequenceNumber));


        String sql = "INSERT INTO " + tableName + " VALUES (";
        String colExpressions = "";
        for (int j = 0; j < randomRecord.size(); j++) {
            colExpressions += headers[j] + "='" + randomRecord.get(j) + "',";
        }

        //todo check this

        sql += "INSERT_DATE=\'" + insertDate + "\' LAST_UPDATE=\'" + new Timestamp(currentDate.getTime()) + "\'," +
                "ID_SYSTEM_SEQUENCE=" + sequenceNumber + "," +
                "RECORD_STATUS='updated'," +
                "" + HelpersCSV.trimLastComma(colExpressions) + ")";
        return sql;
    }


    /**
     * Get the inner SQL statement string
     *
     * @param executionDate
     * @return
     */
    private String getInnerSQL(Date executionDate) {
        String innerSQL = " AS outerGroup INNER JOIN ( SELECT ID_SYSTEM_SEQUENCE, max(LAST_UPDATE) AS mostRecent " +
                "FROM " + tableName + " AS innerSELECT WHERE (innerSELECT.RECORD_STATUS = 'inserted' OR innerSELECT" +
                ".RECORD_STATUS = 'updated'" + " AND innerSELECT.LAST_UPDATE<='" + this.getMySQLDateString(executionDate)
                + "'" +
                " ) GROUP BY " +
                "ID_SYSTEM_SEQUENCE) innerGroup ON outerGroup.ID_SYSTEM_SEQUENCE = innerGroup.ID_SYSTEM_SEQUENCE AND " +
                "outerGroup.LAST_UPDATE = innerGroup.mostRecent";
        return innerSQL;
    }


}
