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

import com.sun.rowset.CachedRowSetImpl;

import java.sql.*;
import java.util.logging.Logger;

/**
 * Created by stefan on 30.06.16.
 */
public class QueryCSV {
    private QueryStoreAPI queryStoreAPI;
    private Logger logger;


    public QueryCSV() {
        queryStoreAPI = new QueryStoreAPI();
        this.logger = Logger.getLogger(QueryCSV.class.getName());

    }

    public CachedRowSetImpl runQuery(Query query, String directoryPath, String fullExportPath) {
        String queryString = queryStoreAPI.generateQueryStringForGitEvaluation(query);
        ResultSet results = null;
        CachedRowSetImpl crs = null;

        try {
            // Load the driver.
            Class.forName("org.xbib.jdbc.csv.CsvDriver");


            Connection conn = DriverManager.getConnection("jdbc:xbib:csv:" + directoryPath);
            Statement stmt = conn.createStatement();

            this.logger.severe("CSV-Query " + queryString);

            results = stmt.executeQuery(queryString);

            crs = new CachedRowSetImpl();
            crs.populate(results);


            // Clean up
            conn.close();


        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return crs;


    }


}
