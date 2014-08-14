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

package at.stefanproell.ResultSetVerification;

import com.sun.rowset.CachedRowSetImpl;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.sql.rowset.CachedRowSet;

import com.sun.rowset.CachedRowSetImpl;


/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class ResultSetHash {
    private Connection con = null;

    public ResultSetHash() {
    }

    /**
     * Iterate over ResultSet and calculate a Hash row by row. This computes a
     * new hash for ecery row
     *
     * @param rs
     */


    public String calculateHashRowByRow(ResultSet rs) {
        String resultSetHash = "";
        String currentHash = "";
        String compositeHash = "";
        CachedRowSet cached = null;
        long startTime = System.currentTimeMillis();
        int hashCounter = 0;

        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            ResultSetMetaData metaData = cached.getMetaData();
            int count = metaData.getColumnCount(); // number of columns
            String columnName[] = new String[count];

            String includedColumns = "Columns in this query: ";
            for (int i = 1; i <= count; i++) {

                columnName[i - 1] = metaData.getColumnLabel(i);
                includedColumns += columnName[i - 1] + " , ";

            }

            System.out.println(includedColumns);

            while (cached.next()) {

                hashCounter++;
                String currentrow = null;
                for (int i = 1; i <= count; i++) {

                    currentrow += cached.getString(i);

                }

                currentHash = currentrow;

                if (cached.isFirst()) {
                    // @TODO change me here


                    // resultSetHash = csvHelper           .calculateSHA1HashFromString(currentHash);
                    // this.logger.info("First Hash! Original: " + currentHash +
                    // " First new Hash " + resultSetHash);
                } else {
                    /*
					 * // Move the cursor to the previous row and read the hash
					 * value. if(cached.previous()){ previousKey =
					 * cached.getString("sha1_hash"); if(cached.next()){
					 * compositeKey = currentKey + previousKey; resultSetHash =
					 * csvHelper.calculateSHA1HashFromString(compositeKey);
					 * this.logger.info("Appended Hash " + previousKey +
					 * " to hash " + currentKey + " and calulated " +
					 * resultSetHash);
					 *
					 * } }
					 */

                    compositeHash = (resultSetHash + currentHash);

                    // @TODO change me here
                    // String newResultSetHash = csvHelper .calculateSHA1HashFromString(compositeHash);
                    // this.logger.info("[resultSetHash] "+resultSetHash +
                    // "[currentHash] " + currentHash +" -> [newResultSetHash]"
                    // + newResultSetHash );
                    //resultSetHash = newResultSetHash;

                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Calculated " + hashCounter + " hash values in "
                + (totalTime) + " millisec");

        return resultSetHash;

    }

    public ResultSet getCompleteResultSetFromTable(String tableName) {
        java.sql.PreparedStatement stmt;
        ResultSet rs = null;
        try {
            stmt = this.con.prepareStatement("SELECT * FROM `" + tableName
                    + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;

    }

    /**
     * only get some cols
     *
     * @param tableName
     * @return
     */
    public ResultSet getSomeColumnsFromTable(String tableName,
                                             String listOfColumns) {
        java.sql.PreparedStatement stmt;
        ResultSet rs = null;
        try {
            stmt = this.con.prepareStatement("SELECT " + listOfColumns
                    + " FROM `" + tableName + "`");
            rs = stmt.executeQuery();
            System.out.println("Statement was: " + stmt);

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return rs;

    }

    /**
     * Iterate over ResultSet and calculate a Hash row by row.
     *
     * @param rs
     */
    public String preCalculatedHashes(ResultSet rs) {
        String resultSetHash = "";
        String currentHash = "";
        String compositeHash = "";
        CachedRowSet cached = null;
        //  CSVHelper csvHelper = new CSVHelper();
        long startTime = System.currentTimeMillis();
        int hashCounter = 0;

        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            ResultSetMetaData metaData = cached.getMetaData();
            int count = metaData.getColumnCount(); // number of columns
            String columnName[] = new String[count];

            String includedColumns = "Columns in this query: ";
            for (int i = 1; i <= count; i++) {

                columnName[i - 1] = metaData.getColumnLabel(i);
                includedColumns += columnName[i - 1] + " , ";

            }

            System.out.println(includedColumns);

            while (cached.next()) {
                hashCounter++;
                currentHash = cached.getString("sha1_hash");

                if (cached.isFirst()) {
                    // @TODO change me here
                    //resultSetHash = csvHelper      .calculateSHA1HashFromString(currentHash);
                    // this.logger.info("First Hash! Original: " + currentHash +
                    // " First new Hash " + resultSetHash);
                } else {
					/*
					 * // Move the cursor to the previous row and read the hash
					 * value. if(cached.previous()){ previousKey =
					 * cached.getString("sha1_hash"); if(cached.next()){
					 * compositeKey = currentKey + previousKey; resultSetHash =
					 * csvHelper.calculateSHA1HashFromString(compositeKey);
					 * this.logger.info("Appended Hash " + previousKey +
					 * " to hash " + currentKey + " and calulated " +
					 * resultSetHash);
					 *
					 * } }
					 */

                    compositeHash = (resultSetHash + currentHash);
                    // @TODO change me here
                    // String newResultSetHash = csvHelper                          .calculateSHA1HashFromString
                    // (compositeHash);
                    // this.logger.info("[resultSetHash] "+resultSetHash +
                    // "[currentHash] " + currentHash +" -> [newResultSetHash]"
                    // + newResultSetHash );
                    // resultSetHash = newResultSetHash;

                }
            }

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Calculated " + hashCounter + " hash values in "
                + (totalTime) + " millisec");

        return resultSetHash;

    }

    /**
     * Iterate over ResultSet and calculate a hash over all appended strings.
     *
     * @param rs
     */
    public String appendAllRowesBeforeHashing(ResultSet rs) {
        String resultSetHash = "";

        String completeResultSet = "";
        CachedRowSet cached = null;
        //CSVHelper csvHelper = new CSVHelper();
        long startTime = System.currentTimeMillis();
        int hashCounter = 0;

        try {
            cached = new CachedRowSetImpl();
            cached.populate(rs);
            ResultSetMetaData metaData = cached.getMetaData();
            int count = metaData.getColumnCount(); // number of columns
            String columnName[] = new String[count];

            String includedColumns = "Columns in this query: ";
            for (int i = 1; i <= count; i++) {

                columnName[i - 1] = metaData.getColumnLabel(i);
                includedColumns += columnName[i - 1] + " , ";

            }

            System.out.println(includedColumns);

            while (cached.next()) {
                hashCounter++;
                for (int i = 1; i <= count; i++) {

                    completeResultSet += cached.getString(i);

                }
                if ((hashCounter % 500) == 0) {
                    System.out.println("Appended Nr " + hashCounter);
                }

            }
            System.out.println("Starting Hash calculation");
            //resultSetHash = csvHelper                     .calculateSHA1HashFromString(completeResultSet);
            long endTime = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("Calculated " + hashCounter
                    + " hash values of a String appended from length "
                    + completeResultSet.length() + "in " + (totalTime) + " millisec");

        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return resultSetHash;

    }
}
