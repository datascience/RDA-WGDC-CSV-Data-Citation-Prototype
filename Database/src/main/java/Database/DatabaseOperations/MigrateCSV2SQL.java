package Database.DatabaseOperations;


import CSVTools.CsvToolsApi;
import Database.Helpers.StringHelpers;
import at.stefanproell.DataTypeDetector.ColumnMetadata;
import at.stefanproell.DataTypeDetector.DatatypeStatistics;
import at.stefanproell.SQL_Tools.CreateTableStatement;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Created by stefan on 18.06.14.
 */
public class MigrateCSV2SQL {
    private Logger logger;

    private DatabaseTools dbtools;

    public MigrateCSV2SQL() {
        this.logger = Logger.getLogger(this.getClass().getName());

        this.dbtools = new DatabaseTools();


    }


    /**
     * Create a new database from a CSV file. DROPs database if exists!! Appends
     * a id column for the sequential numbering and a sha1 hash column. Adds a column for the state of the
     * record: inserted, updated, deleted
     * <p>
     * Version 2016 with data type detector
     */
    public void createSimpleDBFromCSV(String tableName, List<String> primaryKeyColumns, DatatypeStatistics datatypeStatistics) {

        //todo ersetze die meta funktion.

        StringHelpers stringHelpers = new StringHelpers();
        CreateTableStatement createTableStatement = new CreateTableStatement();
        CsvToolsApi csvAPI = new CsvToolsApi();

        Statement stat;

        String createTableString = "CREATE TABLE " + tableName
                + " ( ID_SYSTEM_SEQUENCE INTEGER NOT NULL";


        Map<String, ColumnMetadata> columnMap = datatypeStatistics.getColumnMap();
        for (Map.Entry<String, ColumnMetadata> column : columnMap.entrySet()) {
            ColumnMetadata columnMetadata = column.getValue();
            String mySQLDataType = createTableStatement.getMySQLColumn(columnMetadata);
            String normalizedColumnName = csvAPI.replaceReservedKeyWords(columnMetadata.getColumnName());

            createTableString += ",  " + normalizedColumnName + " " + mySQLDataType;

        }
        //@// TODO: 25.05.16 MySQL 5.7 strict mode does not allow zero timestamps any more.
        createTableString += ", INSERT_DATE TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6), " +
                "LAST_UPDATE TIMESTAMP(6) ";

        // append record status column
        createTableString += ", RECORD_STATUS enum('inserted','updated','deleted') NOT NULL DEFAULT 'inserted'";

        String primaryKeysString = stringHelpers.getCommaSeperatedListofPrimaryKeys(primaryKeyColumns);
        // append primary key
        createTableString += ",PRIMARY KEY (" + primaryKeysString + ",LAST_UPDATE)";
        this.logger.info("Primary key is " + primaryKeysString + " and the update column!");


        // Finalize SQL String

        createTableString += ");";

        this.logger.info("CREATE String: " + createTableString);
        Connection connection = null;

        try {
            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            this.logger.info("The current DATABASE is " + connection.getCatalog());
            stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_READ_ONLY);
            // Required to use the correct database as the evaluation module drops the database
            stat.execute("USE " +connection.getCatalog() );
            stat.execute("DROP TABLE IF EXISTS " + tableName);
            stat.execute(createTableString);
            connection.commit();
            stat.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }

    }


    public void insertCSVDataIntoDB(String currentTableName, Map<Integer, Map<String, Object>> csvMap) throws SQLException {

        Statement stat = null;
        String insertSQL = null;
        Connection connection = null;
        long startTime = System.currentTimeMillis();
        try {
            connection = this.getConnection();
            CsvToolsApi csvToolsApi = new CsvToolsApi();
            if (connection.getAutoCommit()) {
                //this.logger.info("AUTO COMMIT OFF");
                connection.setAutoCommit(false);
            }


            for (Map.Entry<Integer, Map<String, Object>> entry : csvMap.entrySet()) {
                int currentRow = entry.getKey();
                insertSQL = "INSERT INTO " + currentTableName + " ";
                String valuesSpecification = "(ID_SYSTEM_SEQUENCE,";
                // Insert the System Sequence
                String valuesString = " VALUES(\"" + currentRow + "\",";
                Map<String, Object> data = entry.getValue();
                TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);


                for (Map.Entry<String, Object> record : sortedByColumnName.entrySet()) {


                    String columnName = record.getKey();
                    String normalizedColumnName = csvToolsApi.replaceReservedKeyWords(columnName);
                    valuesSpecification += normalizedColumnName + ",";
                    String columnValue;
                    if (record.getValue() != null) {
                        columnValue = record.getValue().toString();
                        columnValue = csvToolsApi.escapeQuotes(columnValue);
                        valuesString += "\"" + columnValue + "\"" + ",";
                    } else {
                        columnValue = "NULL";
                        valuesString += columnValue + ",";
                    }

                }

                Timestamp currentTimestamp = new Timestamp(Calendar.getInstance().getTime().getTime());

                valuesSpecification = StringHelpers.removeLastComma(valuesSpecification) + ",INSERT_DATE,LAST_UPDATE,RECORD_STATUS)";
                valuesString = StringHelpers.removeLastComma(valuesString) + ",\"" + currentTimestamp + "\",\"" + currentTimestamp + "\"," + "\"inserted\");";
                insertSQL += valuesSpecification + valuesString;
                logger.info("SQL INSERT: " + insertSQL);

                stat = connection.createStatement();
                stat.execute(insertSQL);
                connection.commit();
            }
            stat.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;


    }

    /**
     * Steps:
     * Step 1: Copy table based on the structure of the existing one as temp table
     * Step 2: Insert new uploaded file into the temp table
     * Step 3: Find all records to be inserted
     * Step 4: Find all records to be updated
     * Step 5: Find all records to be deleted
     * <p>
     * There are some points to consider.
     * The system can only detect if a record has been updated, if there exists a primary key which is different than
     * the ID_SYSTEM_SEQUENCE The reason is that the updated CSV file does not contain the system sequence number column,
     * therefore a real primary key is needed which allows to detect updates.
     *
     * @param currentTableName
     * @param csvMap
     * @throws SQLException
     */
    public void updateDataInExistingDB(String currentTableName, Map<Integer, Map<String, Object>> csvMap) {


        Statement stat = null;
        CsvToolsApi csvToolsApi = new CsvToolsApi();

        // We store all IDs which exist in the CSV file
        List<Integer> validIDs = new ArrayList<Integer>();
        int existsIdSystemSequenceInteger = -5;


        // Boolean to check if the primary key is not a standard metadata key.
        boolean hasUserDefinedPrimaryKey = false;
        Map<String, String> columnNamesWithoutMetadataSortedAlphabetically = dbtools.getColumnNamesWithoutMetadataSortedAlphabetically(currentTableName);


        // Neue idee:
        // map direkt vergleichen und inserten.

        // check if there is a primary key which was defined by the user.
        hasUserDefinedPrimaryKey = checkIfUserDevinedPrimaryKeyAvailable(currentTableName);


        if (hasUserDefinedPrimaryKey) {

            // Iterate over the CSV Map
            for (Map.Entry<Integer, Map<String, Object>> entry : csvMap.entrySet()) {
                existsIdSystemSequenceInteger = -5;

                Map<String, Object> data = entry.getValue();
                TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
                boolean recordExists = false;

                // Check if record exists.
                existsIdSystemSequenceInteger = checkIfRecordExists(data, currentTableName);

                // The record exists
                if (existsIdSystemSequenceInteger > -1) {
                    this.logger.info("The record exists. It has the SYSTEM_SEQUENCE_NUMER: " + existsIdSystemSequenceInteger + " Check if it changed");
                    recordExists = true;


                    int changedRecordSequence = checkIfRecordHasChanged(existsIdSystemSequenceInteger, data, currentTableName);

                    if (changedRecordSequence == -1) {
                        logger.info("The record has changed. UPDATE Record");
                        // The record has changed.
                        // Update the old record

                        updateOldRecord(existsIdSystemSequenceInteger, currentTableName);
                        insertNewRecordVersionOfExistingRecord(existsIdSystemSequenceInteger, data, currentTableName);


                    }
                    validIDs.add(existsIdSystemSequenceInteger);
                } else {
                    existsIdSystemSequenceInteger = insertNewRecord(currentTableName, data);
                    validIDs.add(existsIdSystemSequenceInteger);

                }


            }


        }

        dbtools.deleteMarkedRecords(validIDs, currentTableName);


    }

    /**
     * Steps:
     * Step 1: Copy table based on the structure of the existing one as temp table
     * Step 2: Insert new uploaded file into the temp table
     * Step 3: Find all records to be inserted
     * Step 4: Find all records to be updated
     * Step 5: Find all records to be deleted
     * <p>
     * There are some points to consider.
     * The system can only detect if a record has been updated, if there exists a primary key which is different than
     * the ID_SYSTEM_SEQUENCE The reason is that the updated CSV file does not contain the system sequence number column,
     * therefore a real primary key is needed which allows to detect updates.
     *
     * @param currentTableName
     * @param csvMap
     * @throws SQLException
     */
    public void updateDataInExistingDBEvaluation(String currentTableName, Map<Integer, Map<String, Object>> csvMap, Date updateDate) {


        Statement stat = null;
        CsvToolsApi csvToolsApi = new CsvToolsApi();

        // We store all IDs which exist in the CSV file
        List<Integer> validIDs = new ArrayList<Integer>();
        int existsIdSystemSequenceInteger = -5;


        // Boolean to check if the primary key is not a standard metadata key.
        boolean hasUserDefinedPrimaryKey = false;
        Map<String, String> columnNamesWithoutMetadataSortedAlphabetically = dbtools.getColumnNamesWithoutMetadataSortedAlphabetically(currentTableName);


        // Neue idee:
        // map direkt vergleichen und inserten.

        // check if there is a primary key which was defined by the user.
        hasUserDefinedPrimaryKey = checkIfUserDevinedPrimaryKeyAvailable(currentTableName);


        if (hasUserDefinedPrimaryKey) {

            // Iterate over the CSV Map
            for (Map.Entry<Integer, Map<String, Object>> entry : csvMap.entrySet()) {
                existsIdSystemSequenceInteger = -5;

                Map<String, Object> data = entry.getValue();
                TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
                boolean recordExists = false;

                // Check if record exists.
                existsIdSystemSequenceInteger = checkIfRecordExists(data, currentTableName);

                // The record exists
                if (existsIdSystemSequenceInteger > -1) {
                    this.logger.info("The record exists. It has the SYSTEM_SEQUENCE_NUMER: " + existsIdSystemSequenceInteger + " Check if it changed");
                    recordExists = true;


                    int changedRecordSequence = checkIfRecordHasChanged(existsIdSystemSequenceInteger, data, currentTableName);

                    if (changedRecordSequence == -1) {
                        logger.info("The record has changed. UPDATE Record");
                        // The record has changed.
                        // Update the old record, by marking it as updated
                        // Insert the new record and mark it as inserted

                        updateOldRecordEvaluation(existsIdSystemSequenceInteger, currentTableName, updateDate);
                        insertNewRecordVersionOfExistingRecordEvaluation(existsIdSystemSequenceInteger, data, currentTableName,updateDate);


                    }
                    validIDs.add(existsIdSystemSequenceInteger);
                } else {
                    existsIdSystemSequenceInteger = insertNewRecordEvaluation(currentTableName, data,updateDate);
                    validIDs.add(existsIdSystemSequenceInteger);

                }


            }


        }

        dbtools.deleteMarkedRecords(validIDs, currentTableName);


    }

    /**
     *
     * @param existsIdSystemSequenceInteger
     * @param data
     * @param currentTableName
     */
    private void insertNewRecordVersionOfExistingRecord(int existsIdSystemSequenceInteger, Map<String, Object> data, String currentTableName) {

        TablePojo tablePojo = dbtools.getTableMetadataPojoFromTable(currentTableName);
        TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
        CsvToolsApi csvToolsApi = new CsvToolsApi();


        String insertSQL = "INSERT INTO " + currentTableName + " (ID_SYSTEM_SEQUENCE,";

        String columnValuesString = ",LAST_UPDATE,RECORD_STATUS) VALUES(" + (existsIdSystemSequenceInteger) + ",";
        for (Map.Entry<String, Object> record : sortedByColumnName.entrySet()) {
            String columnValue;
            String columnName = record.getKey();
            String normalizedColumnName = csvToolsApi.replaceReservedKeyWords(columnName);
            insertSQL += normalizedColumnName + ",";
            if (record.getValue() != null) {
                columnValue = record.getValue().toString();
                try {
                    dbtools.increaseColumnLength(columnValue, tablePojo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                columnValue = csvToolsApi.escapeQuotes(columnValue);
                columnValue = "\"" + columnValue + "\"" + ",";

            } else {
                columnValue = " NULL,";
            }


            columnValuesString += columnValue;
        }

        insertSQL = StringUtils.removeEndIgnoreCase(insertSQL, ",");
        columnValuesString = StringUtils.removeEndIgnoreCase(columnValuesString, ",");

        insertSQL += columnValuesString + ",NOW(),\"inserted\");";
        logger.info(insertSQL);
        // insert the new record
        Connection connection = null;
        Statement statement;
        try {

            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            statement = connection.createStatement();
            statement.executeUpdate(insertSQL);
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }

    }

    private void insertNewRecordVersionOfExistingRecordEvaluation(int existsIdSystemSequenceInteger, Map<String, Object> data, String currentTableName, Date updateDate) {

        TablePojo tablePojo = dbtools.getTableMetadataPojoFromTable(currentTableName);
        TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
        CsvToolsApi csvToolsApi = new CsvToolsApi();


        String insertSQL = "INSERT INTO " + currentTableName + " (ID_SYSTEM_SEQUENCE,";

        String columnValuesString = ",INSERT_DATE,LAST_UPDATE,RECORD_STATUS) VALUES(" + (existsIdSystemSequenceInteger) + ",";
        for (Map.Entry<String, Object> record : sortedByColumnName.entrySet()) {
            String columnValue;
            String columnName = record.getKey();
            String normalizedColumnName = csvToolsApi.replaceReservedKeyWords(columnName);
            insertSQL += normalizedColumnName + ",";
            if (record.getValue() != null) {
                columnValue = record.getValue().toString();
                try {
                    dbtools.increaseColumnLength(columnValue, tablePojo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                columnValue = csvToolsApi.escapeQuotes(columnValue);
                columnValue = "\"" + columnValue + "\"" + ",";

            } else {
                columnValue = " NULL,";
            }


            columnValuesString += columnValue;
        }

        insertSQL = StringUtils.removeEndIgnoreCase(insertSQL, ",");
        columnValuesString = StringUtils.removeEndIgnoreCase(columnValuesString, ",");
        java.sql.Timestamp updateDateSQL=new java.sql.Timestamp(updateDate.getTime());

        insertSQL += columnValuesString + ",'"+updateDateSQL+"','"+updateDateSQL+"',\"inserted\");";
        logger.info(insertSQL);
        // insert the new record
        Connection connection = null;
        Statement statement;
        try {

            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            statement = connection.createStatement();
            statement.executeUpdate(insertSQL);
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }

    }

    /**
     * Update the existing record in the database
     *  @param idSystemSequence
     * @param currentTableName
     *
     */
    private void updateOldRecord(int idSystemSequence, String currentTableName) {
        Connection connection = null;


        Statement latestRecordStatement = null;
        try {
            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            latestRecordStatement = connection.createStatement();

            String latestRecordSQL = "SELECT ID_SYSTEM_SEQUENCE, INSERT_DATE,MAX(LAST_UPDATE) AS LAST_UPDATE, RECORD_STATUS FROM "
                    + currentTableName + "  WHERE ID_SYSTEM_SEQUENCE = " + idSystemSequence
                    + " AND RECORD_STATUS <> \"deleted\" GROUP BY ID_SYSTEM_SEQUENCE, INSERT_DATE,RECORD_STATUS;";
            ResultSet latestRecordRS = latestRecordStatement.executeQuery(latestRecordSQL);

            connection.commit();

            Timestamp lastUpdateDate = null;
            String status;
            if (latestRecordRS.next()) {

                lastUpdateDate = latestRecordRS.getTimestamp("LAST_UPDATE");
                status = latestRecordRS.getString("RECORD_STATUS");
                String updateOldRecord = "UPDATE " + currentTableName + " SET LAST_UPDATE=NOW(),RECORD_STATUS=\"updated\" WHERE ID_SYSTEM_SEQUENCE=" + idSystemSequence + " AND LAST_UPDATE=\"" + lastUpdateDate.toString() + "\"";
                logger.info("Update String: " + updateOldRecord);
                latestRecordStatement = connection.createStatement();
                int update = latestRecordStatement.executeUpdate(updateOldRecord);
                connection.commit();

            }


            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
    }

    /**
     * Update the existing record in the database
     *  @param idSystemSequence
     * @param currentTableName
     *
     */
    private void updateOldRecordEvaluation(int idSystemSequence, String currentTableName, Date updateDate) {
        Connection connection = null;


        Statement latestRecordStatement = null;
        try {
            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            latestRecordStatement = connection.createStatement();

            String latestRecordSQL = "SELECT ID_SYSTEM_SEQUENCE, INSERT_DATE,MAX(LAST_UPDATE) AS LAST_UPDATE, RECORD_STATUS FROM "
                    + currentTableName + "  WHERE ID_SYSTEM_SEQUENCE = " + idSystemSequence
                    + " AND RECORD_STATUS <> \"deleted\" GROUP BY ID_SYSTEM_SEQUENCE, INSERT_DATE,RECORD_STATUS;";
            ResultSet latestRecordRS = latestRecordStatement.executeQuery(latestRecordSQL);

            connection.commit();

            Timestamp lastUpdateDate = null;
            String status;
            if (latestRecordRS.next()) {

                lastUpdateDate = latestRecordRS.getTimestamp("LAST_UPDATE");
                java.sql.Timestamp updateDateSQL=new java.sql.Timestamp(updateDate.getTime());
                status = latestRecordRS.getString("RECORD_STATUS");

                Calendar calendar = Calendar.getInstance();
                calendar.setTime(updateDateSQL);
                updateDateSQL = new java.sql.Timestamp(calendar.getTimeInMillis());

                String updateOldRecord = "UPDATE " + currentTableName + " SET LAST_UPDATE='"+updateDateSQL+"',RECORD_STATUS=\"updated\" WHERE ID_SYSTEM_SEQUENCE=" + idSystemSequence + " AND LAST_UPDATE=\"" + lastUpdateDate.toString() + "\"";
                logger.info("Update String: " + updateOldRecord);
                latestRecordStatement = connection.createStatement();
                int update = latestRecordStatement.executeUpdate(updateOldRecord);
                connection.commit();

            }


            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
    }

    private int checkIfRecordHasChanged(int idSystemSequenceOfExistingRecord, Map<String, Object> data, String currentTableName) {
        // check if it has changed
        Statement checkRecordChangedQuery = null;
        CsvToolsApi csvToolsApi = new CsvToolsApi();
        TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
        int changedRecordSequence = -1;
        Connection connection = null;

        try {
            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            Statement checkRecordExistanceQuery = connection.createStatement();
            String hasChangedWHEREString = " WHERE ID_SYSTEM_SEQUENCE=" + idSystemSequenceOfExistingRecord + " AND ";
            for (Map.Entry<String, Object> record : sortedByColumnName.entrySet()) {
                String columnValue;
                String columnName = record.getKey();
                String normalizedColumnName = csvToolsApi.replaceReservedKeyWords(columnName);

                if (record.getValue() != null) {
                    columnValue = record.getValue().toString();
                    columnValue = csvToolsApi.escapeQuotes(columnValue);
                    columnValue = "\"" + columnValue + "\"";
                    hasChangedWHEREString += normalizedColumnName + "=" + columnValue + " AND ";
                } else {
                    hasChangedWHEREString += normalizedColumnName + " IS NULL AND ";
                }

            }
            hasChangedWHEREString = StringUtils.removeEndIgnoreCase(hasChangedWHEREString, " AND ");


            String checkSQL = "SELECT ID_SYSTEM_SEQUENCE AS recordHasNotChanged FROM " + currentTableName + hasChangedWHEREString + ";";

            this.logger.info("CHECK SQL: " + checkSQL);

            ResultSet hasChangedRS = checkRecordExistanceQuery.executeQuery(checkSQL);
            connection.commit();


            if (hasChangedRS.next()) {
                changedRecordSequence = hasChangedRS.getInt("recordHasNotChanged");
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
        return changedRecordSequence;

    }

    private boolean checkIfUserDevinedPrimaryKeyAvailable(String currentTableName) {
        List<String> primaryKey = dbtools.getPrimaryKeyFromTableWithoutLastUpdateOrSystemSequenceColumns(currentTableName);
        // check if there is a primary key which is not ID_SYSTEM_SEQUENCE or LAST_UPDATE
        // If there is no other key available, we need to find all records one by one.
        // If there is a real primary key, then we can use it for checking if a record exists or not.
        String subselectWherePrimaryKey = "";
        if (primaryKey.size() != 0) {
            return true;
        } else {
            return false;


        }
    }

    /**
     * Check if the record exists already in the database and return the system sequence id if exists.
     * If it does not exist, return -1
     *
     * @param data
     * @param currentTableName
     * @return
     */
    private int checkIfRecordExists(Map<String, Object> data, String currentTableName) {

        TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
        CsvToolsApi csvToolsApi = new CsvToolsApi();
        List<String> primaryKey = dbtools.getPrimaryKeyFromTableWithoutLastUpdateOrSystemSequenceColumns(currentTableName);

        String primaryWhereString = " WHERE (RECORD_STATUS = \'inserted\' OR RECORD_STATUS = 'updated') AND ";
        for (Map.Entry<String, Object> record : sortedByColumnName.entrySet()) {
            String columnValue;
            String columnName = record.getKey();
            String normalizedColumnName = csvToolsApi.replaceReservedKeyWords(columnName);
            for (String pKey : primaryKey) {
                if (pKey.equals(normalizedColumnName)) {
                    columnValue = record.getValue().toString();
                    columnValue = csvToolsApi.escapeQuotes(columnValue);
                    columnValue = "\"" + columnValue + "\"" + " AND";
                    primaryWhereString += normalizedColumnName + "=" + columnValue;
                }
            }
        }
        primaryWhereString = StringUtils.removeEndIgnoreCase(primaryWhereString, "AND");

        Statement checkRecordExistanceQuery = null;
        int existsIdSystemSequenceInteger = -1;


        Connection connection = null;
        try {
            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }

            checkRecordExistanceQuery = connection.createStatement();

            String checkSQL = "SELECT ID_SYSTEM_SEQUENCE AS recordDoesExist FROM " + currentTableName + primaryWhereString + ";";

            this.logger.info("CHECK SQL: " + checkSQL);
            ResultSet maxSequenceResult = checkRecordExistanceQuery.executeQuery(checkSQL);

            if (maxSequenceResult.next()) {
                existsIdSystemSequenceInteger = maxSequenceResult.getInt("recordDoesExist");
            }
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
        return existsIdSystemSequenceInteger;

    }


    /**
     * Insert a new record based on a row of the CSV file
     *
     * @param currentTableName
     * @param data
     */
    private int insertNewRecord(String currentTableName, Map<String, Object> data) {


        int maxSystemSequence = dbtools.getMaxSequenceNumberFromTable(currentTableName);
        TablePojo tablePojo = dbtools.getTableMetadataPojoFromTable(currentTableName);
        TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
        CsvToolsApi csvToolsApi = new CsvToolsApi();
        int newSystemSequenceID = maxSystemSequence + 1;

        String insertSQL = "INSERT INTO " + currentTableName + " (ID_SYSTEM_SEQUENCE,";

        String columnValuesString = ",LAST_UPDATE) VALUES(" + (newSystemSequenceID) + ",";
        for (Map.Entry<String, Object> record : sortedByColumnName.entrySet()) {
            String columnValue;
            String columnName = record.getKey();
            String normalizedColumnName = csvToolsApi.replaceReservedKeyWords(columnName);
            insertSQL += normalizedColumnName + ",";
            if (record.getValue() != null) {
                columnValue = record.getValue().toString();
                try {
                    dbtools.increaseColumnLength(columnValue, tablePojo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                columnValue = csvToolsApi.escapeQuotes(columnValue);
                columnValue = "\"" + columnValue + "\"" + ",";
            } else {
                columnValue = "NULL,";
            }


            columnValuesString += columnValue;
        }

        insertSQL = StringUtils.removeEndIgnoreCase(insertSQL, ",");
        columnValuesString = StringUtils.removeEndIgnoreCase(columnValuesString, ",");

        insertSQL += columnValuesString + ",NOW());";
        logger.info(insertSQL);
        // insert the new record
        Connection connection = null;
        Statement statement;
        try {

            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            statement = connection.createStatement();
            statement.executeUpdate(insertSQL);
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }

        return newSystemSequenceID;
    }

    /**
     * Insert a new record based on a row of the CSV file
     *  @param currentTableName
     * @param data
     * @param updateDate
     */
    private int insertNewRecordEvaluation(String currentTableName, Map<String, Object> data, Date updateDate) {


        int maxSystemSequence = dbtools.getMaxSequenceNumberFromTable(currentTableName);
        java.sql.Timestamp updateDateSQL=new java.sql.Timestamp(updateDate.getTime());
        TablePojo tablePojo = dbtools.getTableMetadataPojoFromTable(currentTableName);
        TreeMap<String, Object> sortedByColumnName = new TreeMap<String, Object>(data);
        CsvToolsApi csvToolsApi = new CsvToolsApi();
        int newSystemSequenceID = maxSystemSequence + 1;

        String insertSQL = "INSERT INTO " + currentTableName + " (ID_SYSTEM_SEQUENCE,";

        String columnValuesString = ",INSERT_DATE,LAST_UPDATE) VALUES(" + (newSystemSequenceID) + ",";
        for (Map.Entry<String, Object> record : sortedByColumnName.entrySet()) {
            String columnValue;
            String columnName = record.getKey();
            String normalizedColumnName = csvToolsApi.replaceReservedKeyWords(columnName);
            insertSQL += normalizedColumnName + ",";
            if (record.getValue() != null) {
                columnValue = record.getValue().toString();
                try {
                    dbtools.increaseColumnLength(columnValue, tablePojo);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                columnValue = csvToolsApi.escapeQuotes(columnValue);
                columnValue = "\"" + columnValue + "\"" + ",";
            } else {
                columnValue = "NULL,";
            }


            columnValuesString += columnValue;
        }

        insertSQL = StringUtils.removeEndIgnoreCase(insertSQL, ",");
        columnValuesString = StringUtils.removeEndIgnoreCase(columnValuesString, ",");

        insertSQL += columnValuesString + ",'"+updateDateSQL+"','"+updateDateSQL+"');";

        logger.info(insertSQL);
        // insert the new record
        Connection connection = null;
        Statement statement;
        try {

            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                connection.setAutoCommit(false);
            }
            statement = connection.createStatement();
            statement.executeUpdate(insertSQL);
            connection.commit();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }

        return newSystemSequenceID;
    }


    public void addDatabaseIndicesToMetadataColumns(String tableName) {
        Connection connection = null;
        this.logger.info("Adding indices");
        long startTime = System.currentTimeMillis();
        try {

            List<String> metadataColumns = new ArrayList<String>();
            metadataColumns.add("INSERT_DATE");
            metadataColumns.add("LAST_UPDATE");
            metadataColumns.add("RECORD_STATUS");
            metadataColumns.add("ID_SYSTEM_SEQUENCE");

            connection = this.getConnection();

            PreparedStatement createIndexStmt;
            for (String columnName : metadataColumns) {
                this.logger.info("Adding index on " + columnName);
                createIndexStmt = connection.prepareStatement("CREATE INDEX  `" + tableName + "_" + columnName + "` " +
                        "ON " + tableName + " (`" + columnName + "`);");
                int statuscode = createIndexStmt.executeUpdate();
            }


        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        this.logger.info("Adding indices took " + (totalTime / 1000) + " sec");


    }


    /**
     * Appends new records to an existing database. If the file contains a header,
     * the header will be skipped. The sequence number is retrieved from the database
     *
     * @param columnsMap
     * @param path
     * @param tableName
     * @param hasHeaders
     * @param calculateHashKeyColumn
     * @throws SQLException
     * @throws IOException
     */
    public void appendingNewCSVDataIntoExistingDB(Map<String, String> columnsMap, String path, String tableName,
                                                  boolean hasHeaders, boolean
                                                          calculateHashKeyColumn) {
        this.logger.info("Appending new records to an existing database");
        long startTime = System.currentTimeMillis();


        // get the latest sequence number from the DB.
        int currentMaxSequenceNumber = this.dbtools.getMaxSequenceNumberFromTable(tableName);


        PreparedStatement preparedStatement;
        CsvToolsApi csvAPI;
        csvAPI = new CsvToolsApi();
        CsvListReader reader = null;
        int rowCount = 0;
        Connection connection = null;
        try {

            connection = this.getConnection();
            if (connection.getAutoCommit()) {
                //this.logger.info("AUTO COMMIT OFF");
                connection.setAutoCommit(false);
            }

            reader = new CsvListReader(new FileReader(path),
                    CsvPreference.STANDARD_PREFERENCE);


            ICsvListReader listReader = null;


            int numberOfColumns = columnsMap.size();
            this.logger.info("Original Table has " + numberOfColumns + " columns (without the metadata)");


            // Calculate the number of place holders required by the amount of
            // columns and add four ? for the sequence, created and updated date and the
            // hash column. The id is the
            // first placeholder and then ..., created date, updated date, hash, status)

            // placeholder for sequence number
            String placeholders = "(?,";
            for (int i = 0; i < numberOfColumns; i++) {
                placeholders += "?,";
            }

            // Adjust the amount of placeholders
            if (calculateHashKeyColumn) {
                placeholders += "?,";

            }

            // If there is no hash column, then only append the two time stamp cols
            placeholders += "?,?";


            // record status column
            placeholders += ",?";
            // finalize place holder
            placeholders += ")";

            String insertString = "INSERT INTO " + tableName + " VALUES "
                    + placeholders;
            preparedStatement = connection.prepareStatement(insertString);


            this.logger.info("The SQL string is " + insertString + " [new]");


            List<String> row;

            // Check if there are headers
            if (hasHeaders) {
                // Read headers
                this.logger.info("There are headers... Skipping header");
                row = reader.read();

            } else {
                this.logger.info("There are no headers");
            }


            while ((row = reader.read()) != null) {


                currentMaxSequenceNumber++;
                rowCount++;

                // there are five metadata columns: sequence, inserted, time, updated time, hash,status
                for (int columnCount = 1; columnCount <= numberOfColumns + 5; columnCount++) {

                    // first column contains sequence
                    if (columnCount == 1) {
                        preparedStatement.setInt(columnCount, currentMaxSequenceNumber);

                        // column values (first column is the id)
                    } else if (columnCount > 1
                            && columnCount <= (numberOfColumns + 1)) {

                        // index starts at 0 and the counter at 1.
                        preparedStatement.setString(columnCount, row.get(columnCount - 2));

                        // insert timestamps
                    } else if (columnCount == (numberOfColumns + 2)
                            || columnCount == (numberOfColumns + 3)) {

                        preparedStatement.setDate(columnCount, null);

                        // insert the hash
                    } else if (columnCount == (numberOfColumns + 4) & calculateHashKeyColumn) {

                        String appendedColumns = CsvToolsApi.convertStringListToAppendedString(row);

                        String hash = CsvToolsApi
                                .calculateSHA1HashFromString(appendedColumns);

                        preparedStatement.setString(columnCount, hash);

                    }
                    // if there is no hash column, then the last record state field has is at position +4
                    else if (columnCount == (numberOfColumns + 4) & calculateHashKeyColumn == false) {
                        preparedStatement.setString(columnCount, "inserted");

                    }

                    // if there is a hash column, then the last record state field has is at position +5
                    else if (columnCount == (numberOfColumns + 5) & calculateHashKeyColumn) {
                        preparedStatement.setString(columnCount, "inserted");
                    }
                }

                // this.logger.info("prepared statement before exec: " + preparedStatement.toString());
                int statuscode = preparedStatement.executeUpdate();


                if (rowCount % 1000 == 0) {
                    connection.commit();
                }

            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SQLIntegrityConstraintViolationException m) {
            this.logger.severe("duplicate key detected!: " + m.getSQLState() + " " + m.getLocalizedMessage());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) { /* handle close exception, quite usually ignore */ }
            }
        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Inserted " + rowCount + " rows in " + (totalTime / 1000) + " sec");
    }




    /**
     * Get the connection from the connection pool
     *
     * @return
     */
    private Connection getConnection() {
        Connection connection = null;
        Session session = HibernateUtilData.getSessionFactory().openSession();
        SessionImpl sessionImpl = (SessionImpl) session;
        connection = sessionImpl.connection();
        return connection;

    }


}
