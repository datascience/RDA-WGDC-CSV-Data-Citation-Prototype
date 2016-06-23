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

package QueryGenerator_alt;

import Database.DatabaseOperations.DatabaseQueries;
import Database.DatabaseOperations.DatabaseTools;
import Helpers.HelpersCSV;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.io.IOException;
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
    private final DatabaseQueries dbQueries;
    private String[] evaluationHeader = {"QueryID", "SQL-System-Query", "Git-System-Query", "Query-Type",
            "Execution-Time",
            "Affected-Id-System-Sequence",
            "SQL-System-Query-Start", "SQL-System-Query-Stop", "Git-System-Query-Start", "Git-System-Query-Stop"};
    private String inputCSV;
    private String outputCSV;
    private int amountOfQueries;
    private double selectProportion;
    private double insertProportion;
    private double updateProportion;
    private double deleteProportion;
    private QueryComplexity complexity;
    private QueryType type;
    QueryStoreAPI queryStoreAPI;
    private DatabaseTools dbTools;
    private long minReExecutionDate;
    private long currentQueryTimestamp;

    public long getCurrentQueryTimestamp() {
        return currentQueryTimestamp;
    }

    public void setCurrentQueryTimestamp(long currentQueryTimestamp) {
        this.currentQueryTimestamp = currentQueryTimestamp;
    }


    private ArrayList<Integer> systemSequenceSimulator;
    private String tableName;


    private static final Logger logger =
            Logger.getLogger(QueryGenerator.class.getName());


    public QueryGenerator(String tableName) {

        this.dbTools = new DatabaseTools();
        this.dbQueries = new DatabaseQueries();
        this.tableName = tableName;
        this.systemSequenceSimulator = this.dbTools.getAllSequenceNumbersAsArrayList(tableName);





    }

    /**
     * Generate random queries based on a provided distribution.
     * @param outputCSV
     * @param amountOfQueries
     * @param complexity
     * @param selectProportion
     * @param insertProportion
     * @param updateProportion
     * @param deleteProportion
     * @param evalSystem
     */
    public void generateQueryStatements(String outputCSV, int amountOfQueries, QueryComplexity
            complexity, double
                                                selectProportion, double
                                                insertProportion, double
                                                updateProportion, double deleteProportion, EvaluationSystem
                                                evalSystem) {
        this.outputCSV = outputCSV;
        this.amountOfQueries = amountOfQueries;
        this.complexity = complexity;
        this.selectProportion = selectProportion;
        this.insertProportion = insertProportion;
        this.updateProportion = updateProportion;
        this.deleteProportion = deleteProportion;

        this.minReExecutionDate = computeUnixTimeIntervals();
        currentQueryTimestamp=minReExecutionDate;



        ICsvMapWriter mapWriter = null;
        try {

            long timestampUsed=-1;
            // increment simulation time


            mapWriter = new CsvMapWriter(new FileWriter(this.outputCSV),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = getProcessors(this.evaluationHeader.length);
            // write the header
            mapWriter.writeHeader(this.evaluationHeader);


            List<String> columns = this.dbTools.getColumnsFromDatabaseAsList(tableName);
            String[] headers = columns.toArray(new String[columns.size()]);


            Random generator = new Random();
            String queryStringSQL;
            String queryStringGit;
            int randomSequenceNumber = 0;

            Query q = null;
            for (int i = 0; i < amountOfQueries; i++) {
                type = null;
                queryStringSQL = null;
                queryStringGit = null;
                Map<String, String> recordMap = new HashMap<String, String>();


                // random number between 0 and 1
                double randomNumber = generator.nextDouble();
                // set the simulation time
                currentQueryTimestamp++;
                timestampUsed=currentQueryTimestamp;

                /**
                 * The select statement is essential for testing the systems. Here, the used timestamp is the
                 * re-execution timestamp
                 */
                if (randomNumber <= selectProportion) {
                    type = QueryType.SELECT;

                   switch (complexity) {
                        case EASY:
                            queryStringSQL = this.generateSelectStarEasy( headers, evalSystem, complexity,
                                    type);
                            queryStringGit = "SELECT " + HelpersCSV.joinArrayAsString(this
                                    .removeSystemHeadersFromArray(headers)) + " FROM " + tableName;

                            break;
                        case STANDARD:
                            String selectedColumns = HelpersCSV.pickRandomColumn(headers) + ", " + HelpersCSV
                                    .pickRandomColumn(headers) +
                                    ", " +
                                    HelpersCSV.pickRandomColumn(headers);
                            String selectedFilterColumn = HelpersCSV.pickRandomColumn(headers);
                            String selectedFilterValue = "%" + HelpersCSV.randomString(2, 1.0) + "%";
                            queryStringSQL = this.generateSelectStandard( selectedColumns,
                                    selectedFilterColumn, selectedFilterValue,
                                    headers,
                                    evalSystem, complexity, type);
                            queryStringGit = "SELECT " + selectedColumns + " FROM " + tableName + " WHERE " +
                                    "" + selectedFilterColumn + " LIKE " + selectedFilterValue;
                            break;
                        case COMPLEX:

                            Map<String, String> filtersMap = new HashMap<String, String>();
                            filtersMap.put(HelpersCSV.pickRandomColumn(headers), ("%" + HelpersCSV.randomString(5, 2.0) + "%"));
                            filtersMap.put(HelpersCSV.pickRandomColumn(headers), ("%" + HelpersCSV.randomString(5, 2.0) + "%"));
                            filtersMap.put(HelpersCSV.pickRandomColumn(headers), ("%" + HelpersCSV.randomString(5, 2.0) + "%"));

                            String whereSQL = this.dbQueries.getWhereString(filtersMap);

                            selectedColumns = HelpersCSV.pickRandomColumn(headers) + ", " + HelpersCSV
                                    .pickRandomColumn(headers) +
                                    ", " +
                                    HelpersCSV.pickRandomColumn(headers);

                            queryStringSQL = this.generateSelectComplex(selectedColumns,whereSQL);

                            queryStringGit = "SELECT " + selectedColumns + " FROM " + tableName + " " + whereSQL;

                            break;
                    }

                }
                // INSERT
                else if (randomNumber > selectProportion && randomNumber <= selectProportion + insertProportion) {
                    type = QueryType.INSERT;
                    ArrayList<String> randomRecord = this.getRandomInsertValues( this
                            .removeSystemHeadersFromArray(headers));
                    randomSequenceNumber = this.getNextSequenceNumber();
                    queryStringSQL = this.insertSQLString( randomRecord, randomSequenceNumber, headers);
                    queryStringGit = this.insertGitString( randomRecord, randomSequenceNumber, headers);

                }
                // UPDATE
                else if (randomNumber > selectProportion + insertProportion &&
                        randomNumber <= selectProportion + insertProportion + updateProportion) {
                    type = QueryType.UPDATE;
                    ArrayList<String> randomRecord = this.getRandomInsertValues( this
                            .removeSystemHeadersFromArray(headers));
                    randomSequenceNumber = this.getRandomSequenceNumber();

                    boolean exists = this.checkIfRecordExistsInSimulationSequenceNumber(randomSequenceNumber);

                    if(exists){
                        queryStringSQL = this.createSQLUpdateString(randomRecord,randomSequenceNumber,headers);

                    }



                }
                // DELETE
                else {
                    type = QueryType.DELETE;
                    randomSequenceNumber = this.getRandomSequenceNumber();
                    this.deleteSimulationSequenceNumber(randomSequenceNumber);
                    queryStringSQL = "DELETE FROM " + tableName + " WHERE ID_SYSTEM_SEQUENCE="+randomNumber;
                    queryStringGit = queryStringSQL;
                }




                recordMap.put("QueryID", String.valueOf(i));
                recordMap.put("SQL-System-Query", queryStringSQL);
                recordMap.put("Git-System-Query", queryStringGit);
                recordMap.put("Query-Type", type.toString());
                recordMap.put("Execution-Time",String.valueOf(timestampUsed));
                recordMap.put("Affected-Id-System-Sequence", String.valueOf(randomSequenceNumber));
                recordMap.put("SQL-System-Query-Start", "");
                recordMap.put("SQL-System-Query-Stop", "");
                recordMap.put("Git-System-Query-Start", "");
                recordMap.put("Git-System-Query-Stop", "");
                mapWriter.write(recordMap, this.evaluationHeader, processors);


            }


        } catch (Exception e) {
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


    private String getMySQLDateString(long unixDate){
        SimpleDateFormat sdf = new SimpleDateFormat("YYY-MM-dd hh:mm:ss");
        String minDateString = sdf.format(unixDate*1000);
        return minDateString;
    }





    private String generateSelectStarEasy(String[] headers, EvaluationSystem evaluationSystem,
                                          QueryComplexity
                                                  complexity, QueryType type) {

        //String innerSQL = this.dbQueries.getMostRecentVersionSQLString("ID_SYSTEM_SEQUENCE", tableName);
        String sql = "SELECT " + HelpersCSV.joinArrayAsString(this.removeSystemHeadersFromArray(headers)) + " FROM "
                + tableName;

        sql+=this.getInnerSQL();

        return sql;

    }

    private String generateSelectStandard(String selectedColumns, String selectedFilter, String
            selectedFilterValue, String[]
                                                  headers, EvaluationSystem
                                                  evaluationSystem,
                                          QueryComplexity
                                                  complexity, QueryType type) {

        String sql = "SELECT " + selectedColumns;


        Map<String, String> filtersMap = new HashMap<String, String>();
        filtersMap.put(selectedFilter, selectedFilterValue);
        String whereSQL = this.dbQueries.getWhereString(filtersMap);
        sql = sql + this.getInnerSQL() + whereSQL;
        return sql;

    }

    private String generateSelectComplex(            String selectedColumns, String whereString) {




        String sql = "SELECT " + selectedColumns;

        String innerSQL = this.dbQueries.getMostRecentVersionSQLString("ID_SYSTEM_SEQUENCE", tableName);


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


    /**
     * create a new base table for the evaluation
     */
    private String generateNewBaseTableRecord(Organization org) {


        String baseTablePID = this.queryStoreAPI.createBaseTableRecord("Stefan", "Evaluation", tableName, "Evaluation ",
                "Eval Descr", org
                        .getOrganization_prefix(), "dummy");
        return baseTablePID;
    }


    private ArrayList<String> getRandomInsertValues(String[] headers) {
        ArrayList<String> randomRecord = new ArrayList<String>(headers.length);
        for (int i = 0; i < headers.length; i++) {
            randomRecord.add(i, HelpersCSV.randomString(5, 2.0));
        }

        return randomRecord;

    }

    private String insertSQLString(ArrayList<String> randomRecord, int sequenceNumber, String[]
            headers) {

        // mit query store
        //this.dbTools.insertSingleRecordIntoTable(tableName, randomRecord);
        String headerList = HelpersCSV.joinArrayAsString(this.removeSystemHeadersFromArray(headers));
        String sql = "INSERT INTO " + tableName + " (ID_SYSTEM_SEQUENCE," + headerList + ",RECORD_STATUS) " +
                "VALUES (" + sequenceNumber + "," +
                HelpersCSV.joinArrayListAsEscapedString(randomRecord) + ",\"INSERTED\")";
        return sql;

    }

    private String insertGitString(ArrayList<String> randomRecord, int sequenceNumber, String[]
            headers) {

        String headerList = HelpersCSV.joinArrayAsString(this.removeSystemHeadersFromArray(headers));
        String CSV = sequenceNumber + "," + HelpersCSV.joinArrayListAsString(randomRecord);

        return CSV;

    }



    /**
     * Get the latest sequence number from the table
     * @return
     */
    private int getNextSequenceNumber() {
        return this.systemSequenceSimulator.size() + 1;

    }

    /**
     * Get the latest sequence number from the table
     *
     * @return
     */
    private int getSequenceNumber() {
        return this.getMaxEvaluationSequenceNumber();

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
        int maxSequenceNumber = this.getSequenceNumber();
        int randomSequenceNumber = 1 + (int) (Math.random() * ((maxSequenceNumber - 1) + 1));
        return randomSequenceNumber;
    }



    private String createSQLUpdateString(ArrayList<String> randomRecord, int sequenceNumber,
                                         String[] headers){
        String sql = "UPDATE "+tableName+ " SET ";
        String colExpressions = "";
        for(int j  = 1; j < randomRecord.size();j++){
            colExpressions+=headers[j]+"="+randomRecord.get(j)+",";
        }
        return sql+HelpersCSV.trimLastComma(colExpressions)+ " WHERE ID_SYSTEM_SEQUENCE=" +sequenceNumber;
    }

    private void deleteSimulationSequenceNumber(int seq){
        Iterator<Integer> it = this.systemSequenceSimulator.iterator();
        while (it.hasNext()) {
            Integer searchInt = it.next();
            if (searchInt.equals(seq)) {
                it.remove();
                System.out.println("remove " + seq+ " new size " + this.systemSequenceSimulator.size());
            }
        }

    }

    private int getMaxEvaluationSequenceNumber(){
        Iterator<Integer> it = this.systemSequenceSimulator.iterator();
        int max = -1;
        while (it.hasNext()) {
            Integer searchInt = it.next();
            if (searchInt>max) {
                max = searchInt;

            }

        }

        return max;

    }

    private boolean checkIfRecordExistsInSimulationSequenceNumber(int seq){
        Iterator<Integer> it = this.systemSequenceSimulator.iterator();
        while (it.hasNext()) {
            Integer searchInt = it.next();
            if (searchInt.equals(seq)) {
               return true;
            }else{
                return false;
            }
        }
        return false;
    }

    private long getUnixTimeFromDate(Date dateVal){
        return dateVal.getTime()/1000;

    }

    /**
     * This methods provides the starting time for the query evaluation
     * @return
     */
    private long computeUnixTimeIntervals(){
        Date currentDate = new Date();
        long currentUnixTime = currentDate.getTime() / 1000;
        long startUnixTime = currentUnixTime-amountOfQueries;
        logger.info("The current time was" + new Date(currentUnixTime*1000)+". The starting date for "
                +amountOfQueries+ " queries is " + new
                Date
                (startUnixTime*1000)
                .toString());
        return startUnixTime;


    }

    private String getInnerSQL(){
        String innerSQL =  " AS outerGroup INNER JOIN ( SELECT ID_SYSTEM_SEQUENCE, max(LAST_UPDATE) AS mostRecent " +
                "FROM " + tableName+ " AS innerSELECT WHERE (innerSELECT.RECORD_STATUS = 'inserted' OR innerSELECT" +
                ".RECORD_STATUS = 'updated'" + " AND innerSELECT.LAST_UPDATE<='"+this.getMySQLDateString(this
                .getCurrentQueryTimestamp())
                +"'" +
                " ) GROUP BY " +
                "ID_SYSTEM_SEQUENCE) innerGroup ON outerGroup.ID_SYSTEM_SEQUENCE = innerGroup.ID_SYSTEM_SEQUENCE AND outerGroup.LAST_UPDATE = innerGroup.mostRecent";
        return innerSQL;
    }


}
