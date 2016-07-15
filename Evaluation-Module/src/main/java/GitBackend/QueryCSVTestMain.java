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

package GitBackend;


import org.xbib.jdbc.csv.CsvDriver;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.sql.*;

/**
 * Created by stefan on 30.06.16.
 */
public class QueryCSVTestMain {
    public static void main(String[] args) {
        try {
            // Load the driver.
            Class.forName("org.xbib.jdbc.csv.CsvDriver");

            // Create a connection. The first command line parameter is
            // the directory containing the .csv files.
            // A single connection is thread-safe for use by several threads.
            Connection conn = DriverManager.getConnection("jdbc:xbib:csv:" + "/tmp/Evaluation_Git_Repo/");

            // Create a Statement object to execute the query with.
            // A Statement is not thread-safe.
            Statement stmt = conn.createStatement();

            // Select the ID and NAME columns from sample.csv
            ResultSet results = stmt.executeQuery("SELECT COLUMN_1,COLUMN_2,COLUMN_3,COLUMN_4,COLUMN_5 FROM checkout WHERE  UPPER(COLUMN_1) LIKE UPPER('%G%')  ORDER BY COLUMN_1 ASC");

            // Dump out the results to a CSV file with the same format
            // using CsvJdbc helper function
            boolean append = true;
            PrintStream outputStream = new PrintStream(new File("/tmp/out.csv"));
            CsvDriver.writeToCsv(results, outputStream, false);

            // Clean up
            conn.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


    }

}
