/*
 * Copyright [2015] [Stefan Pröll]
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

package Controller;


import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.sql.rowset.CachedRowSet;

import Bean.SessionManager;
import Bean.TableDefinitionBean;
import CSVTools.CSV_API;
import Database.DatabaseOperations.DatabaseTools;
import Database.DatabaseOperations.ResultSetMetadata;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
public class DownloadController implements Serializable {

    private final Logger logger;
    private StreamedContent downloadCSVFile;

    public DownloadController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Download Controller");

    }

    public StreamedContent getDownloadCSVFile() {

        //InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("images/optimus.jpg");

        InputStream stream = null;
        try {
            stream = new FileInputStream(new File("/tmp/test.csv"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        downloadCSVFile = new DefaultStreamedContent(stream, "image/jpg", "downloaded_optimus.jpg");
        return downloadCSVFile;
    }

    public void subsetCSVAction() {
        this.logger.info("CSV Subset Action");
        SessionManager sm = new SessionManager();
        String subsetPID = sm.getLandingPageSelectedSubset();
        this.logger.info("Retrieving data for: " + subsetPID);

        QueryStoreAPI queryAPI = new QueryStoreAPI();
        Query query = queryAPI.getQueryByPID(subsetPID);

        if (query != null) {
            DatabaseTools dbTools = new DatabaseTools();
            String reExecuteSQLString = query.getQueryString();
            CachedRowSet resultSet = dbTools.reExecuteQuery(reExecuteSQLString);

            try {

                while (resultSet.next()) {
                    this.logger.info(resultSet.getString(1));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ResultSetMetadata rsMetaData = dbTools.getResultSetMetadata();

            this.logger.info("Re-executing: " + reExecuteSQLString);
            this.logger.info("Retrieved " + rsMetaData.getRowCount() + " row from reexecuted dataset.");

            CSV_API csvAPI = new CSV_API();
            String filename = subsetPID.replace("/", "-") + ".csv";

            csvAPI.writeResultSetIntoCSVFile(resultSet, filename);
        }
    }

}
