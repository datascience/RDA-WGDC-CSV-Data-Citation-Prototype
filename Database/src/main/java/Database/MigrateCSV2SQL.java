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


import CSV.CSVHelper;
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
import java.util.List;
import java.util.logging.Logger;

/**
 * Created by stefan on 18.06.14.
 */
public class MigrateCSV2SQL {
    private Logger logger;
    private DataSource dataSource;

    public MigrateCSV2SQL() {
        this.logger = Logger.getLogger(this.getClass().getName());
        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/citationdatabase");
            System.out.println("Datasource added");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Create a new database from a CSV file. DROPs database if exists!! Appends
     * a id column for the sequential numbering and a sha1 hash column
     *
     * @param columnMetadata
     * @param tableName
     * @param calculateHashKeyColumn
     * @throws java.sql.SQLException
     * @throws ClassNotFoundException
     */
    public void createSimpleDBFromCSV(Column[] columnMetadata, String tableName, boolean calculateHashKeyColumn)
            throws SQLException, ClassNotFoundException {
        Statement stat;
        Connection connection;
        String createTableString = "CREATE TABLE " + tableName
                + " ( ID_SYSTEM_SEQUENCE INTEGER PRIMARY KEY AUTO_INCREMENT";

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

        // Finalize SQL String

        createTableString += ");";

        this.logger.info("CREATE String: " + createTableString);
        connection = this.getConnection();
        stat = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,
                ResultSet.CONCUR_READ_ONLY);
        stat.execute("DROP TABLE IF EXISTS " + tableName);
        stat.execute(createTableString);

        stat.close();
        connection.close();

    }

    /**
     * Get the connection from the data source
     *
     * @return
     */
    private Connection getConnection() {

        if (dataSource == null) try {
            throw new SQLException("Can't get data source");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //get database connection
        Connection con = null;
        try {
            con = dataSource.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (con == null)
            try {
                throw new SQLException("Can't get database connection");
            } catch (SQLException e) {
                e.printStackTrace();
            }


        return con;

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
        CSVHelper csvHelper = new CSVHelper();
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
            // columns
            // and add four ? for the sequence, created and updated date and the
            // hash column. The id is the
            // first placeholder and then ..., created date, updated date, hash)

            String placeholders = "(?,";
            for (int i = 0; i < header.length; i++) {
                placeholders += "?,";
            }

            // Adjust the amount of placeholders
            if (calculateHashKeyColumn) {
                placeholders += "?,";

            } else {
                // If there is no hash column, then only append the two time stamp cols
                placeholders += "?,?)";
            }

            String insertString = "INSERT INTO " + tableName + " VALUES "
                    + placeholders;
            preparedStatement = connection.prepareStatement(insertString);

            List<String> row;


            while ((row = reader.read()) != null) {

                rowCount++;

                for (int columnCount = 1; columnCount <= header.length + 4; columnCount++) {
                    // this.logger.info("columns Count : " + columnCount +
                    // " _ header count = " + header.length);
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
                }

                //this.logger.info("prepared statement before exec: " + preparedStatement.toString());
                preparedStatement.executeUpdate();

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

}
