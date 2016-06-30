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

import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import org.relique.jdbc.csv.CsvDriver;

import java.sql.*;

/**
 * Created by stefan on 30.06.16.
 */
public class QueryCSV {
    private QueryStoreAPI queryStoreAPI;
    private String fullPath;

    public QueryCSV(String fullPath) {
        queryStoreAPI = new QueryStoreAPI();
        this.fullPath = fullPath;
    }

    public void runQuery(Query query) {
        String queryString = queryStoreAPI.generateQueryStringForGitEvaluation(query);


        try {
            // Load the driver.
            Class.forName("org.relique.jdbc.csv.CsvDriver");

            // Create a connection. The first command line parameter is
            // the directory containing the .csv files.
            // A single connection is thread-safe for use by several threads.
            Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + fullPath);

            // Create a Statement object to execute the query with.
            // A Statement is not thread-safe.
            Statement stmt = conn.createStatement();

            // Select the ID and NAME columns from sample.csv
            ResultSet results = stmt.executeQuery(queryString);

            // Dump out the results to a CSV file with the same format
            // using CsvJdbc helper function
            boolean append = true;
            CsvDriver.writeToCsv(results, System.out, append);

            // Clean up
            conn.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }


    }
}
