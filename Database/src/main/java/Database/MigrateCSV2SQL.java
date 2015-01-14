/*
 * Copyright [2014] [Stefan Pröll]
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
 * Copyright [2014] [Stefan Pröll]
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
 * Copyright [2014] [Stefan Pröll]
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
 * Copyright [2014] [Stefan Pröll]
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
 * Copyright [2014] [Stefan Pröll]
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
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileNotFoundException;
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
    private DataBaseConnectionPool dbcp;
    private DatabaseTools dbtools;

    public MigrateCSV2SQL() {
        this.logger = Logger.getLogger(this.getClass().getName());
        try {
            this.dbtools = new DatabaseTools();
            this.dbcp = this.dbtools.getBaseConnectionPool();

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }


    /**
     * Create a new database from a CSV file. DROPs database if exists!! Appends
     * a id column for the sequential numbering and a sha1 hash column. Adds a column for the state of the
     * record: inserted, updated, deleted
     *
     * @param columnMetadata
     * @param tableName
     * @param calculateHashKeyColumn
     * @throws java.sql.SQLException
     * @throws ClassNotFoundException
     */
    public void createSimpleDBFromCSV(Column[] columnMetadata, String tableName, String primaryKeyColumnName,
                                      boolean calculateHashKeyColumn)
            throws SQLException, ClassNotFoundException {
        Statement stat;

        String createTableString = "CREATE TABLE " + tableName
                + " ( ID_SYSTEM_SEQUENCE INTEGER NOT NULL";

        for (int i = 0; i < columnMetadata.length; i++) {
            createTableString += " , " + columnMetadata[i].getColumnName()
                    + " VARCHAR(" + columnMetadata[i].getMaxContentLength()
                    + ") ";

        }
        createTableString += ", INSERT_DATE TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP, " +
                "LAST_UPDATE TIMESTAMP DEFAULT 0 ";

        // If hash key should be computed during
        if (calculateHashKeyColumn) {
            createTableString += ", SHA1_HASH CHAR(40) NOT NULL ";

        }

        // append record status column
        createTableString += ", RECORD_STATUS enum('inserted','updated','deleted') NOT NULL DEFAULT 'inserted'";
        // append primary key
        createTableString += ",PRIMARY KEY (" + primaryKeyColumnName + ",LAST_UPDATE)";
        this.logger.info("Primary key is " + primaryKeyColumnName + " and the update column!");


        // Finalize SQL String

        createTableString += ");";

        this.logger.info("CREATE String: " + createTableString);
        Connection connection = this.getConnection();
        stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        stat.execute("DROP TABLE IF EXISTS " + tableName);
        stat.execute(createTableString);

        stat.close();
        connection.close();

    }

    /**
     * Get the connection from the connection pool
     *
     * @return
     */
    private Connection getConnection() {

        Connection connection = this.dbcp.getConnection();
        return connection;

    }

    public void insertCSVDataIntoDB(String path, String tableName,
                                    boolean hasHeaders, boolean calculateHashKeyColumn) throws IOException,
            SQLException {

        Connection connection = this.getConnection();
        if (connection.getAutoCommit()) {
            //this.logger.info("AUTO COMMIT OFF");
            connection.setAutoCommit(false);
        }

        PreparedStatement preparedStatement;
        CSV_API csvAPI;
        csvAPI = new CSV_API();
        CsvListReader reader = null;
        int rowCount = 0;
        try {
            reader = new CsvListReader(new FileReader(path),
                    CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        ICsvListReader listReader = null;
        try {
            final String[] header = reader.getHeader(hasHeaders);

            // Calculate the number of place holders required by the amount of
            // columns and add four ? for the sequence, created and updated date and the
            // hash column. The id is the
            // first placeholder and then ..., created date, updated date, hash, status)

            // placeholder for sequence number
            String placeholders = "(?,";
            for (int i = 0; i < header.length; i++) {
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

            List<String> row;


            while ((row = reader.read()) != null) {

                rowCount++;

                // there are five metadata columns: sequence, inserted, time, updated time, hash,status
                for (int columnCount = 1; columnCount <= header.length + 5; columnCount++) {

                    // first column contains sequence
                    if (columnCount == 1) {
                        preparedStatement.setInt(columnCount, rowCount);

                        // column values (first column is the id)
                    } else if (columnCount > 1
                            && columnCount <= (header.length + 1)) {

                        // index starts at 0 and the counter at 1.
                        preparedStatement.setString(columnCount,
                                row.get(columnCount - 2));

                        // insert timestamps
                    } else if (columnCount == (header.length + 2)
                            || columnCount == (header.length + 3)) {

                        preparedStatement.setDate(columnCount, null);

                        // insert the hash
                    } else if (columnCount == (header.length + 4) & calculateHashKeyColumn) {

                        String appendedColumns = CSV_API.convertStringListToAppendedString(row);

                        String hash = CSV_API
                                .calculateSHA1HashFromString(appendedColumns);

                        preparedStatement.setString(columnCount, hash);

                    }
                    // if there is no hash column, then the last record state field has is at position +4
                    else if (columnCount == (header.length + 4) & calculateHashKeyColumn == false) {
                        preparedStatement.setString(columnCount, "inserted");

                    }

                    // if there is a hash column, then the last record state field has is at position +5
                    else if (columnCount == (header.length + 5) & calculateHashKeyColumn) {
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

        } finally {
            if (listReader != null) {
                listReader.close();
            }
            connection.setAutoCommit(true);
            reader.close();
            connection.close();

        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Inserted " + rowCount + " rows in " + (totalTime / 1000) + " sec");
    }


    /**
     * Get current date time
     *
     * @return
     */
    public Date getCurrentDatetime() {
        java.util.Date today = new java.util.Date();
        return new Date(today.getTime());
    }

    public void CSVmigrationMETHODFROMWebInterface() {
/*

        boolean calulateHashColumn = false;
        this.logger.info("Calculate Hash Columns is OFF");
        // retrieve file names
        this.filesList = this.getFileListFromSession();
        System.out.println("Retrieved  " + filesList.size() + " file names");

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
                migrate.createSimpleDBFromCSV(meta, currentTableName, calulateHashColumn);
                // Import CSV Data
                migrate.insertCSVDataIntoDB(currentPath, currentTableName, true, calulateHashColumn);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }


            it.remove(); // avoids a ConcurrentModificationException
        }

*/
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

            assert connection != null;
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
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
            calculateHashKeyColumn) throws SQLException, IOException {
        this.logger.info("Appending new records to an existing database");


        // get the latest sequence number from the DB.
        int currentMaxSequenceNumber = this.dbtools.getMaxSequenceNumberFromTable(tableName);

        Connection connection = this.getConnection();
        if (connection.getAutoCommit()) {
            //this.logger.info("AUTO COMMIT OFF");
            connection.setAutoCommit(false);
        }


        PreparedStatement preparedStatement;
        CSV_API csvAPI;
        csvAPI = new CSV_API();
        CsvListReader reader = null;
        int rowCount = 0;
        try {
            reader = new CsvListReader(new FileReader(path),
                    CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        ICsvListReader listReader = null;
        try {

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

                        String appendedColumns = CSV_API.convertStringListToAppendedString(row);

                        String hash = CSV_API
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

        } finally {
            if (listReader != null) {
                listReader.close();
            }
            connection.setAutoCommit(true);
            reader.close();
            connection.close();

        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Inserted " + rowCount + " rows in " + (totalTime / 1000) + " sec");
    }

    /**
     * Update existing data in a table. Checks if each record exists.
     *
     * @param columnsMap
     * @param path
     * @param tableName
     * @param hasHeaders
     * @param calculateHashKeyColumn
     * @throws SQLException
     * @throws IOException
     */
    public void updateDataInExistingDB(Map<String, String> columnsMap, List<String> primaryKeyList, String path,
                                       String tableName,
                                       boolean hasHeaders, boolean
            calculateHashKeyColumn) throws SQLException, IOException {
        this.logger.info("Appending new records to an existing database");
        Connection connection = this.getConnection();


        if (connection.getAutoCommit()) {
            //this.logger.info("AUTO COMMIT OFF");
            connection.setAutoCommit(false);
        }


        PreparedStatement preparedStatement;


        CSV_API csvAPI;
        csvAPI = new CSV_API();
        CsvListReader reader = null;
        int rowCount = 0;
        try {
            reader = new CsvListReader(new FileReader(path),
                    CsvPreference.STANDARD_PREFERENCE);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        long startTime = System.currentTimeMillis();
        ICsvListReader listReader = null;
        try {

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


            //this.logger.info("The SQL string is " + insertString);

            List<String> row;
            // get the headers
            String[] header = reader.getHeader(hasHeaders);
            // get the primary key from the database table
            String primaryKeyTableString = this.dbtools.getPrimaryKeyFromTableWithoutMetadataColumns(tableName).get(0);
            // search the corresponding header column
            int primaryKeyCSVColumnInt = -1;
            String primaryKeyCSVColumName = null;
            for (int i = 0; i < header.length; i++) {
                this.logger.info("Comparing " + header[i] + " primary key: " + primaryKeyTableString);
                if (header[i].equals(primaryKeyTableString)) {
                    primaryKeyCSVColumnInt = i;
                    primaryKeyCSVColumName = header[i];
                    this.logger.info("The column wih the primary key is: " + primaryKeyCSVColumName + " (number " +
                            primaryKeyCSVColumnInt
                            + ")");

                }
            }

            //@todo continue here


            int currentSequenceNumber = -1;
            String updatedOrNewString = "inserted";

            while ((row = reader.read()) != null) {
                int currentMaxSequenceNumber = this.dbtools.getMaxSequenceNumberFromTable(tableName);

                rowCount++;

                //check if primary key exists:
                boolean recordExists = this.dbtools.checkIfRecordExistsInTable(tableName, primaryKeyTableString,
                        row.get(primaryKeyCSVColumnInt));

                Timestamp insertDateFromRecord = null;
                // if the record exists, set the status to updated, reuse insert date and sequence number
                if (recordExists) {
                    updatedOrNewString = "updated";
                    RecordMetadata recordMetadata = this.dbtools.getMetadataFromRecord(tableName,
                            primaryKeyTableString, row.get(primaryKeyCSVColumnInt));
                    insertDateFromRecord = recordMetadata.getINSERT_DATE();
                    currentSequenceNumber = recordMetadata.getID_SYSTEM_SEQUENCE();
                } else {
                    // get the latest sequence number from the DB.
                    currentSequenceNumber = currentMaxSequenceNumber++;
                    updatedOrNewString = "inserted";
                }

                // there are five metadata columns: sequence, inserted, time, updated time, hash,status
                for (int columnCount = 1; columnCount <= numberOfColumns + 5; columnCount++) {

                    // first column contains sequence. If the record exists, keep the number, else get a new one
                    if (columnCount == 1) {

                        preparedStatement.setInt(columnCount, currentSequenceNumber);

                        // column values (first column is the id)
                    } else if (columnCount > 1
                            && columnCount <= (numberOfColumns + 1)) {

                        // index starts at 0 and the counter at 1.
                        preparedStatement.setString(columnCount, row.get(columnCount - 2));

                        // insert timestamps
                        // if the record exists, only update the updated_timestamp and use the original inserted time
                        // timestamp
                    } else if (columnCount == (numberOfColumns + 2) && recordExists) {
                        preparedStatement.setTimestamp(columnCount, insertDateFromRecord);
                        this.logger.info("The insert date was kept at " + insertDateFromRecord.toString());

                        // The record is new
                    } else if (columnCount == (numberOfColumns + 2) && !recordExists) {
                        preparedStatement.setDate(columnCount, null);

                        // update date
                    } else if (columnCount == (numberOfColumns + 3)) {
                        preparedStatement.setDate(columnCount, null);


                    }
                    // insert the hash
                    else if (columnCount == (numberOfColumns + 4) & calculateHashKeyColumn) {

                        String appendedColumns = CSV_API.convertStringListToAppendedString(row);

                        String hash = CSV_API
                                .calculateSHA1HashFromString(appendedColumns);

                        preparedStatement.setString(columnCount, hash);

                    }
                    // if there is no hash column, then the last record state field has is at position +4
                    else if (columnCount == (numberOfColumns + 4) & calculateHashKeyColumn == false) {
                        preparedStatement.setString(columnCount, updatedOrNewString);

                    }

                    // if there is a hash column, then the last record state field has is at position +5
                    else if (columnCount == (numberOfColumns + 5) & calculateHashKeyColumn) {
                        preparedStatement.setString(columnCount, updatedOrNewString);
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

        } finally {
            if (listReader != null) {
                listReader.close();
            }
            if (!connection.isClosed()) {
                connection.setAutoCommit(true);

                connection.close();
            }
            reader.close();


        }
        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Inserted " + rowCount + " rows in " + (totalTime / 1000) + " sec");
    }
}
