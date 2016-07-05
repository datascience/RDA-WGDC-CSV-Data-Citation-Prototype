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
import at.stefanproell.DataGenerator.DataGenerator;
import org.relique.jdbc.csv.CsvDriver;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvResultSetWriter;
import org.supercsv.io.ICsvResultSetWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintStream;
import java.sql.*;

/**
 * Created by stefan on 30.06.16.
 */
public class QueryCSV {
    private QueryStoreAPI queryStoreAPI;


    public QueryCSV() {
        queryStoreAPI = new QueryStoreAPI();

    }

    public void runQuery(Query query, String directoryPath, String fullExportPath) {
        String queryString = queryStoreAPI.generateQueryStringForGitEvaluation(query);


        try {
            // Load the driver.
            Class.forName("org.relique.jdbc.csv.CsvDriver");


            Connection conn = DriverManager.getConnection("jdbc:relique:csv:" + directoryPath);
            Statement stmt = conn.createStatement();

            ResultSet results = stmt.executeQuery(queryString);


            writeWithResultSetWriter(results, fullExportPath);

            // Clean up
            conn.close();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**
     * An example of writing using CsvResultSetWriter
     */
    private static void writeWithResultSetWriter(ResultSet resultSet, String outputPath) throws Exception {


        ICsvResultSetWriter resultSetWriter = null;
        try {
            resultSetWriter = new CsvResultSetWriter(new FileWriter(outputPath),
                    CsvPreference.STANDARD_PREFERENCE);
            ResultSetMetaData rsmd = resultSet.getMetaData();

            int columnsNumber = rsmd.getColumnCount();

            final CellProcessor[] processors = DataGenerator.getProcessors(columnsNumber);

            // writer csv file from ResultSet
            resultSetWriter.write(resultSet, processors);

        } finally {
            if (resultSetWriter != null) {
                resultSetWriter.close();
            }
        }
    }
}
