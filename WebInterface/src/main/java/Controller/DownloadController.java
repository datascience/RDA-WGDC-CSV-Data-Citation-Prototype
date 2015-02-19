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

    public DownloadController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Download Controller");

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

        this.getResultSetFromQuery(query);

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


//        this.getResultSetFromQuery(query);

    }

    private void writeParentCSV(CachedRowSet cachedRowset, String baseTableName) {

        DatabaseTools dbTools = new DatabaseTools();

        ResultSetMetadata rsMetaData = dbTools.getResultSetMetadata();


        this.logger.info("Retrieved " + rsMetaData.getRowCount() + " row from reexecuted dataset.");

        CSV_API csvAPI = new CSV_API();

        String filename = "/tmp/CSV-Files" + baseTableName + ".csv";

        csvAPI.writeResultSetIntoCSVFile(cachedRowset, filename);

        this.setParentCSVPath(filename);

    }



    public String getCsvFilePath() {
        return csvFilePath;
    }

    public void setCsvFilePath(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    private String getResultSetFromQuery(Query query) {


        String filename = null;
        SessionManager sm = new SessionManager();
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
            String baseDatabase = query.getBaseTable().getBaseDatabase();
            String baseTableName = query.getBaseTable().getBaseTableName();
            filename = "/tmp/CSV-Files/" + baseDatabase + "_" + baseTableName + "_" + query.getPID().replace("/", "-") + ".csv";

            csvAPI.writeResultSetIntoCSVFile(resultSet, filename);

            this.setCsvFilePath(filename);


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
}
