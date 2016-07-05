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

import org.relique.jdbc.csv.CsvDriver;

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
            Class.forName("org.relique.jdbc.csv.CsvDriver");

            // Create a connection. The first command line parameter is
            // the directory containing the .csv files.
            // A single connection is thread-safe for use by several threads.
            Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + "/tmp/Evaluation_Results/");

            // Create a Statement object to execute the query with.
            // A Statement is not thread-safe.
            Statement stmt = conn.createStatement();

            // Select the ID and NAME columns from sample.csv
            ResultSet results = stmt.executeQuery("SELECT COLUMN_1 FROM rAuLD7ZfzgqR_export_git WHERE COLUMN_1 LIKE '%7%'");

            // Dump out the results to a CSV file with the same format
            // using CsvJdbc helper function
            boolean append = true;
            PrintStream outputStream = new PrintStream(new File("/tmp/Evaluation_Results/out.csv"));
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
