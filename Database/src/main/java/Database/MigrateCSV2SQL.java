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


import CSVTools.CSVHelper;
import CSVTools.Column;
import org.supercsv.io.CsvListReader;
import org.supercsv.io.ICsvListReader;
import org.supercsv.prefs.CsvPreference;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 18.06.14.
 */
public class MigrateCSV2SQL {
    private Logger logger;
    private DataBaseConnectionPool dbcp;

    public MigrateCSV2SQL() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.dbcp = new DataBaseConnectionPool();


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
    public void createSimpleDBFromCSV(Column[] columnMetadata, String tableName, String primaryKeyColumnName, boolean calculateHashKeyColumn)
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
            createTableString += ", sha1_hash CHAR(40) NOT NULL ";

        }

        // append record status column
        createTableString += ", RECORD_STATUS enum('inserted','updated','deleted') NOT NULL DEFAULT 'inserted'";
        // append primary key
        createTableString += ",PRIMARY KEY (" + primaryKeyColumnName + ")";
        this.logger.info("Primary key is " + primaryKeyColumnName);


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
        CSVHelper csvHelper;
        csvHelper = new CSVHelper();
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

                        String appendedColumns = CSVHelper.convertStringListToAppendedString(row);

                        String hash = CSVHelper
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

            CSVHelper csv;
            csv = new CSVHelper();
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
                createIndexStmt = connection.prepareStatement("CREATE INDEX  `" + tableName + "_" + columnName + "` ON " + tableName + " (`" + columnName + "`);");
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


}
