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
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.sql.rowset.CachedRowSet;

import Bean.SessionManager;
import CSVTools.CSV_API;
import Database.DatabaseOperations.DatabaseTools;
import Database.DatabaseOperations.ResultSetMetadata;
import QueryStore.BaseTable;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

@ManagedBean
public class DownloadController implements Serializable {

    private final Logger logger;
    private StreamedContent downloadCSVFile;
    private String csvFilePath;
    private String parentCSVPath;
    private StreamedContent downloadParentCSVFile;
    private String latestCSVPath;
    private StreamedContent downloadLatestCSVFile;
    private final String DIRECTORY = "/tmp/CSV-Files/";

    public DownloadController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Download Controller");
        this.createCSVDirectory();


    }

    public StreamedContent getDownloadCSVFile() {

        //InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("images/optimus.jpg");

        InputStream stream = null;
        String fileName = this.getCsvFilePath();

        if (fileName != null) {
            try {
                stream = new FileInputStream(new File(fileName));
                downloadCSVFile = new DefaultStreamedContent(stream, "text/csv", fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            return downloadCSVFile;
        } else {
            this.displayMessage("There was an error!", "Did you select a subset?");
            return null;

        }

    }

    public void subsetCSVAction() {
        this.logger.info("CSV Subset Action");
        SessionManager sm = new SessionManager();
        String subsetPID = sm.getLandingPageSelectedSubset();
        this.logger.info("Retrieving data for: " + subsetPID);


        QueryStoreAPI queryAPI = new QueryStoreAPI();
        Query query = queryAPI.getQueryByPID(subsetPID);
        String downloadFileName = this.getDownloadFileName(query);

        this.setCsvFilePath(downloadFileName);


    }

    public void parentCSVAction() {
        this.logger.info("CSV Subset Action");
        SessionManager sm = new SessionManager();
        String parentPID = sm.getLandingPageSelectedParent();

        this.logger.info("Retrieving data for: " + parentPID);
        String selectedSubset = sm.getLandingPageSelectedSubset();
        QueryStoreAPI queryStoreAPI = new QueryStoreAPI();
        BaseTable baseTable = queryStoreAPI.getBaseTableByTableNameOnly(parentPID);

        DatabaseTools dbTools = new DatabaseTools();

        if (selectedSubset != null) {

            Query query = queryStoreAPI.getQueryByPID(selectedSubset);
            String baseTableQueryString = queryStoreAPI.getParentUnfilteredStringFromQuery(baseTable, query.getExecution_timestamp());
            CachedRowSet resultSet = dbTools.reExecuteQuery(baseTableQueryString);

            this.writeParentCSV(resultSet, query.getExecution_timestamp().toString());
        }


//        this.getDownloadFileName(query);

    }

    private void writeParentCSV(CachedRowSet cachedRowset, String baseTableName) {

        DatabaseTools dbTools = new DatabaseTools();

        ResultSetMetadata rsMetaData = dbTools.getResultSetMetadata();


        this.logger.info("Retrieved " + rsMetaData.getRowCount() + " row from reexecuted dataset.");

        CSV_API csvAPI = new CSV_API();

        String filename = DIRECTORY + baseTableName + ".csv";

        csvAPI.writeResultSetIntoCSVFile(cachedRowset, filename);

        this.setParentCSVPath(filename);

    }


    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    private String getDownloadFileName(Query query) {


        String filename = null;
        SessionManager sm = new SessionManager();
        if (query != null) {
            DatabaseTools dbTools = new DatabaseTools();
            QueryStoreAPI queryAPI = new QueryStoreAPI();
            String currentReExecutionString = queryAPI.generateQueryString(query);


            CachedRowSet resultSet = dbTools.reExecuteQuery(currentReExecutionString);

            try {

                while (resultSet.next()) {
                    this.logger.info(resultSet.getString(1));

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            ResultSetMetadata rsMetaData = dbTools.getResultSetMetadata();

            this.logger.info("Re-executing: " + currentReExecutionString);
            this.logger.info("Retrieved " + rsMetaData.getRowCount() + " row from reexecuted dataset.");

            CSV_API csvAPI = new CSV_API();
            String baseDatabase = query.getBaseTable().getBaseDatabase();
            String baseTableName = query.getBaseTable().getBaseTableName();
            filename = DIRECTORY + baseDatabase + "_" + baseTableName + "_" + query.getPID().replace("/", "-") + ".csv";

            csvAPI.writeResultSetIntoCSVFile(resultSet, filename);


        }
        return filename;
    }

    public StreamedContent getDownloadParentCSVFile() {

        //InputStream stream = ((ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext()).getResourceAsStream("images/optimus.jpg");

        InputStream stream = null;
        String fileName = this.getParentCSVPath();

        if (fileName != null) {
            try {
                stream = new FileInputStream(new File(fileName));
                downloadCSVFile = new DefaultStreamedContent(stream, "text/csv", fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            return downloadCSVFile;
        } else {
            this.displayMessage("There was an error!", "Did you select a subset?");
            return null;

        }


    }

    public void setDownloadParentCSVFile(StreamedContent downloadParentCSVFile) {
        this.downloadParentCSVFile = downloadParentCSVFile;
    }

    public void diffSetAction() {


        if (this.downloadCSVFile == null) {

            this.subsetCSVAction();
        }

        this.displayMessage("Differential CSV", "Not yet implemented ;-)");

    }

    private void displayMessage(String titleString, String message) {
        FacesMessage msg = new FacesMessage(titleString, message);
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public String getParentCSVPath() {
        return parentCSVPath;
    }

    public void setParentCSVPath(String parentCSVPath) {
        this.parentCSVPath = parentCSVPath;
    }

    public void latestSetAction() {
        this.logger.info("CSV Subset Action");
        SessionManager sm = new SessionManager();
        String subsetPID = sm.getLandingPageSelectedSubset();
        this.logger.info("Retrieving data for: " + subsetPID);


        QueryStoreAPI queryAPI = new QueryStoreAPI();
        Query query = queryAPI.getQueryByPID(subsetPID);
        Query latestQuery = query;
        Date currentDate = new Date();
        latestQuery.setExecution_timestamp(currentDate);

        String downloadFileName =
                this.getDownloadFileName(latestQuery);

        this.setLatestCSVPath(downloadFileName);

    }

    public StreamedContent getDownloadLatestCSVFile() {
        InputStream stream = null;
        String fileName = this.getLatestCSVPath();

        if (fileName != null) {
            try {
                stream = new FileInputStream(new File(fileName));
                downloadLatestCSVFile = new DefaultStreamedContent(stream, "text/csv", fileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }


            return downloadLatestCSVFile;
        } else {
            this.displayMessage("There was an error!", "Did you select a subset?");
            return null;

        }


    }

    public void setDownloadLatestCSVFile(StreamedContent downloadLatestCSVFile) {
        this.downloadLatestCSVFile = downloadLatestCSVFile;
    }

    public String getLatestCSVPath() {
        return latestCSVPath;
    }

    public void setLatestCSVPath(String latestCSVPath) {
        this.latestCSVPath = latestCSVPath;
    }


    private void createCSVDirectory() {
        File file = new File(DIRECTORY);

        boolean b = false;

/*
* exists() method tests whether the file or directory denoted by this
* abstract pathname exists or not accordingly it will return TRUE /
* FALSE.
*/

        if (!file.exists()) {
/*
* mkdirs() method creates the directory mentioned by this abstract
* pathname including any necessary but nonexistent parent
* directories.
*
* Accordingly it will return TRUE or FALSE if directory created
* successfully or not. If this operation fails it may have
* succeeded in creating some of the necessary parent directories.
*/
            b = file.mkdirs();
        }
        if (b)
            System.out.println("Directory successfully created");
        else
            System.out.println("Failed to create directory");
    }
}




