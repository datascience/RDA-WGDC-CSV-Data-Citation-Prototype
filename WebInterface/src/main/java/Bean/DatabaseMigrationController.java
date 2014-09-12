

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

package Bean;


import CSVTools.CSVHelper;

import CSVTools.Column;
import Database.MigrateCSV2SQL;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 18.06.14.
 */

@ManagedBean
@SessionScoped
public class DatabaseMigrationController implements Serializable {
    //resource injection
//    @Resource(name="jdbc/citationdatabase")
    private DataSource dataSource;
    private HashMap<String, String> filesList;
    private Logger logger;

    private String primaryKey;

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public DatabaseMigrationController() {

        this.logger = Logger.getLogger(this.getClass().getName());
        System.out.println("DB controller");

        try {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/citationdatabase");
            System.out.println("Datasource added");
        } catch (NamingException e) {
            e.printStackTrace();
        }

        // Init file list
        this.filesList = new HashMap<String, String>();


    }

    public String viewTable() {

        this.logger.info("Opening table view");
        return "table.xhtml?faces-redirect=true";
    }

    /**
     * @TODO needs to be rewritten!!
     */

    public void migrate() {
        System.out.println("Doing the migration");
        boolean calulateHashColumn = false;

        this.logger.info("Calculate Hash Columns is OFF");
        // retrieve file names
        this.filesList = this.getFileListFromSession();

        System.out.println("Retrieved  " + filesList.size() + " file names");

        //
        Iterator it = this.filesList.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();

            this.logger.info("TableName = " + pairs.getKey().toString() + " Path: " + pairs.getValue().toString());

            CSVHelper csv;
            csv = new CSVHelper();
            String currentTableName = csv.replaceSpaceWithDash(pairs.getKey().toString());
            String currentPath = pairs.getValue().toString();
            // Read headers
            String[] headers = csv.getArrayOfHeadersCSV(currentPath);
            try {
                csv.readWithCsvListReaderAsStrings(currentPath);
                // get column metadata
                Column[] meta = csv.analyseColumns(true, currentPath);

                // read CSV file
                csv.readWithCsvListReaderAsStrings(currentPath);
                MigrateCSV2SQL migrate = new MigrateCSV2SQL();


                // Create DB schema
                migrate.createSimpleDBFromCSV(meta, currentTableName, this.getPrimaryKey(), calulateHashColumn);
                // Import CSV Data
                migrate.insertCSVDataIntoDB(currentPath, currentTableName, true, calulateHashColumn);

                // add indices
                migrate.addDatabaseIndicesToMetadataColumns(currentTableName);

            } catch (Exception e) {
                e.printStackTrace();
            }

            try {

            } catch (Exception e) {
                e.printStackTrace();
            }


            it.remove(); // avoids a ConcurrentModificationException
        }


    }


    /**
     * Read the name of the table to be created from the session variables
     *
     * @return
     */
    private HashMap<String, String> getFileListFromSession() {
        System.out.println("Read session data");
        // lesen
        Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        return (HashMap<String, String>) sessionMAP.get("fileListHashMap");
    }


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

    /**
     * Action button
     */
    public void setPrimarKeyAction() {
        this.logger.info("Primary key is " + this.getPrimaryKey());
        //FacesContext.getCurrentInstance().addMessage("primaryKeyform:primaryKeyButton", new FacesMessage("yayyayyay"));
        FacesMessage msg = new FacesMessage("The primary key is " + this.getPrimaryKey(), "The primary key must be unique");
        FacesContext.getCurrentInstance().addMessage(null, msg);

    }

}
