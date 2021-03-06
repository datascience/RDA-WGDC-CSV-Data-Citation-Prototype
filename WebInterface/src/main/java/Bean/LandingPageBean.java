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

package Bean;

import Database.DatabaseOperations.DatabaseTools;
import QueryStore.BaseTable;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

/**
 * Created by stefan on 12.02.15.
 */
@ManagedBean(name = "landingPageBean")
@SessionScoped
public class LandingPageBean implements Serializable {

    private Logger logger;
    private String selectedBaseTable; // +getter +setter
    private String selectedSubset;

    private List<SelectItem> availableBaseTables; // +getter (no setter necessary)
    private List<SelectItem> availableSubsets;

    private String metaPid;
    private String metaParentPid;
    private String metaExecutionDate;
    private String metaResultSetHash;
    private String metaQueryHash;
    private String metaDescription;
    private String metaSQLString;
    private String metaParentAuthor;
    private String metaAuthor;
    private String metaSuggestedCitationString;
    private String metaTitle;
    private String metaParentTitle;
    private String metaParentURL;
    private String metaSubsetURL;
    
    private int metaParentActiveRecords;
    
    


    private String requestPID;
    private Date metaParentUploadTimestamp;


    public LandingPageBean() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Database Name Bean");

    }

    @PostConstruct
    public void init() {
        this.logger.info("Initializing DatabaseTableNameBean");
        this.availableBaseTables = this.retrieveBaseTablesFromDatabase();
        this.selectedBaseTable = this.availableBaseTables.get(0).getValue().toString();
        this.availableSubsets = this.retrieveSubsetsFromDatabase(this.selectedBaseTable);


    }


    public void handleDropDownChangeBaseTables() {
        //based on the number provided, change "regions" attribute.
        this.logger.info("change listener. Base table is now " + this.selectedBaseTable);
        this.availableSubsets = this.retrieveSubsetsFromDatabase(this.selectedBaseTable);

    }

    public void handleDropDownChangeSubsets() {
        //based on the number provided, change "regions" attribute.
        this.logger.info("change subsets. Subset is is now " + this.selectedSubset);
        QueryStoreAPI queryAPI = new QueryStoreAPI();
        Query query = queryAPI.getQueryByPID(this.selectedSubset);
        BaseTable baseTable = queryAPI.getBaseTableByTableNameOnly(this.selectedBaseTable);
        this.updateMetadataFields(query, baseTable);
        SessionManager sm = new SessionManager();
        sm.setLandingPageSelectedSubset(this.selectedSubset);
        sm.setLandingPageSelectedParent(this.selectedBaseTable);


    }

    public String getSelectedBaseTable() {
        return selectedBaseTable;
    }

    public void setSelectedBaseTable(String selectedBaseTable) {
        this.logger.info("Selected Basetable " + selectedBaseTable);

        this.selectedBaseTable = selectedBaseTable;
    }

    public List<SelectItem> getAvailableBaseTables() {

        return availableBaseTables;
    }

    public void setAvailableBaseTables(List<SelectItem> availableBaseTables) {
        this.availableBaseTables = availableBaseTables;
    }

    public String getSelectedSubset() {
        return selectedSubset;
    }

    public void setSelectedSubset(String selectedSubset) {
        this.logger.info("Selected Subset:  " + selectedSubset);
        this.selectedSubset = selectedSubset;
    }

    private List<SelectItem> retrieveBaseTablesFromDatabase() {
        QueryStoreAPI queryAPI = new QueryStoreAPI();
        Map<String, String> availableBaseTablesMap = queryAPI.getAvailableBaseTables();
        List<SelectItem> availableBaseTables = new ArrayList<SelectItem>();

        for (Map.Entry<String, String> entry : availableBaseTablesMap.entrySet()) {
            String baseTableName = entry.getKey();
            String baseTablePid = entry.getValue();
            availableBaseTables.add(new SelectItem(baseTableName, baseTablePid + "( " + baseTableName + " )"));
        }

        this.logger.info("Found " + availableBaseTables.size() + " base tables");
        return availableBaseTables;


    }

    private List<SelectItem> retrieveSubsetsFromDatabase(String baseTableName) {
        QueryStoreAPI queryAPI = new QueryStoreAPI();
        Map<String, String> availableSubsetsMap = queryAPI.getAvailableSubsetsFromBase(baseTableName);
        List<SelectItem> availableSubsets = new ArrayList<SelectItem>();

        for (Map.Entry<String, String> entry : availableSubsetsMap.entrySet()) {
            String pid = entry.getKey();

            String execDateString = entry.getValue();
            availableSubsets.add(new SelectItem(pid, pid + " ( " + execDateString + " )"));
        }

        this.logger.info("Found " + availableSubsets.size() + " base tables");
        return availableSubsets;


    }

    private void updateMetadataFields(Query query, BaseTable baseTable) {


        if (query != null) {
            PersistentIdentifierAPI pidApi = new PersistentIdentifierAPI();
            
            this.logger.info("Setting metadata fields");
            this.metaPid = query.getPID();
            this.metaSubsetURL = pidApi.getPIDObjectFromPIDString(this.metaPid).getURI();
            this.metaParentPid = baseTable.getBaseTablePID();
            
            this.metaParentURL = pidApi.getPIDObjectFromPIDString(this.metaParentPid).getURI();
            this.metaExecutionDate = query.getExecution_timestamp().toString();
            this.metaResultSetHash = query.getResultSetHash();
            this.metaQueryHash = query.getQueryHash();
            this.metaDescription = query.getQueryDescription();
            this.metaSQLString = query.getQueryString();
            this.metaParentAuthor = baseTable.getAuthor();
            this.metaAuthor = query.getUserName();
            this.metaTitle = query.getSubSetTitle();
            this.metaParentTitle = baseTable.getDataSetTitle();
            this.metaSuggestedCitationString = this.metaAuthor +
                    " (" + this.getYearFromDate(query.getExecution_timestamp()) + ") \"" +
                    this.metaTitle + "\" created at " + this.metaExecutionDate.toString() + ", PID [ark:" + this.metaPid + "]. Subset of "
                    + this.metaParentAuthor + ": \"" + this.getMetaParentTitle() + "\", PID [ark:" + this.metaParentPid + "]";


        } else {
            this.logger.severe("basetable or subset does not exist");
            if (query == null) {
                this.logger.info("This was not a query pid. Checking base tables");

                if (baseTable == null) {
                    this.logger.severe("Not a valid Pid!");
                } else {
                    this.logger.info("Base table found!");
                    this.updateBaseTableFields(baseTable);

                }
            }


        }
    }

    private String getYearFromDate(Date execDate) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(execDate.toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyy");
        String year = df.format(date);
        return year;
    }


    public List<SelectItem> getAvailableSubsets() {
        return availableSubsets;
    }

    public void setAvailableSubsets(List<SelectItem> availableSubsets) {
        this.availableSubsets = availableSubsets;
    }

    public String getMetaPid() {
        return metaPid;
    }

    public void setMetaPid(String metaPid) {
        this.metaPid = metaPid;
    }

    public String getMetaParentPid() {
        return metaParentPid;
    }

    public void setMetaParentPid(String metaParentPid) {
        this.metaParentPid = metaParentPid;
    }

    public String getMetaExecutionDate() {
        return metaExecutionDate;
    }

    public void setMetaExecutionDate(String metaExecutionDate) {
        this.metaExecutionDate = metaExecutionDate;
    }

    public String getMetaResultSetHash() {
        return metaResultSetHash;
    }

    public void setMetaResultSetHash(String metaResultSetHash) {
        this.metaResultSetHash = metaResultSetHash;
    }

    public String getMetaQueryHash() {
        return metaQueryHash;
    }

    public void setMetaQueryHash(String metaQueryHash) {
        this.metaQueryHash = metaQueryHash;
    }

    public String getMetaDescription() {
        return metaDescription;
    }

    public void setMetaDescription(String metaDescription) {
        this.metaDescription = metaDescription;
    }

    public String getMetaSQLString() {
        return metaSQLString;
    }

    public void setMetaSQLString(String metaSQLString) {
        this.metaSQLString = metaSQLString;
    }

    public String getMetaParentAuthor() {
        return metaParentAuthor;
    }

    public void setMetaParentAuthor(String metaParentAuthor) {
        this.metaParentAuthor = metaParentAuthor;
    }

    public String getMetaAuthor() {
        return metaAuthor;
    }

    public void setMetaAuthor(String metaAuthor) {
        this.metaAuthor = metaAuthor;
    }

    public String getMetaSuggestedCitationString() {
        return metaSuggestedCitationString;
    }

    public void setMetaSuggestedCitationString(String metaSuggestedCitationString) {
        this.metaSuggestedCitationString = metaSuggestedCitationString;
    }

    public int countChar(String text) {

        int rows = (int) (text.length() / 60);

        return rows;
    }

    public Date getMetaParentUploadTimestamp() {
        return metaParentUploadTimestamp;
    }

    public void setMetaParentUploadTimestamp(Date metaParentUploadTimestamp) {
        this.metaParentUploadTimestamp = metaParentUploadTimestamp;
    }

    public String getMetaTitle() {
        return metaTitle;
    }

    public void setMetaTitle(String metaTitle) {
        this.metaTitle = metaTitle;
    }

    public String getMetaParentTitle() {
        return metaParentTitle;
    }

    public void setMetaParentTitle(String metaParentTitle) {
        this.metaParentTitle = metaParentTitle;
    }

    public String getRequestPID() {
        return requestPID;
    }

    public void setRequestPID(String requestPID) {

        this.requestPID = requestPID;
    }

    public void initPidRequest() {

        if (requestPID != null) {

            SessionManager sm = new SessionManager();


            this.logger.info("Set request: " + requestPID);
            QueryStoreAPI queryAPI = new QueryStoreAPI();

            Query query = queryAPI.getQueryByPID(requestPID);

            if (query == null) {
                this.logger.info("This was not a subset pid. Checking base tables");
                BaseTable baseTable = queryAPI.getBaseTableByPID(requestPID);
                if (baseTable == null) {
                    this.logger.severe("Not a valid Pid!");
                } else {
                    this.logger.info("Base table found!");
                    this.updateBaseTableFields(baseTable);
                    sm.setLandingPageSelectedParent(requestPID);

                }


            } else {
                BaseTable baseTable = query.getBaseTable();
                this.updateMetadataFields(query, baseTable);
                sm.setLandingPageSelectedParent(baseTable.getBaseTablePID());
                sm.setLandingPageSelectedSubset(requestPID);
            }


        }
    }

    private void updateBaseTableFields(BaseTable baseTable) {
        if (baseTable != null) {
            PersistentIdentifierAPI pidApi = new PersistentIdentifierAPI();
            
            this.logger.info("Setting metadata fields");
            this.metaPid = "";
            this.metaParentPid = baseTable.getBaseTablePID();

            this.metaParentURL = pidApi.getPIDObjectFromPIDString(this.metaParentPid).getURI();
            this.metaExecutionDate = "";
            this.metaResultSetHash = "";
            this.metaQueryHash = "";
            this.metaDescription = baseTable.getDescription();
            this.metaSQLString = "";
            this.metaParentAuthor = baseTable.getAuthor();
            this.metaAuthor = "";
            this.metaTitle = "";
            this.metaParentTitle = baseTable.getDataSetTitle();
            this.metaParentUploadTimestamp = baseTable.getUploadDate();
            this.metaParentActiveRecords = baseTable.getNumberOfActiveRecords();
            this.metaSuggestedCitationString = this.metaParentAuthor + " (" + this.getYearFromDate(this.metaParentUploadTimestamp) + "): \"" + this.getMetaParentTitle() + "\", PID [ark:" + this.metaParentPid + "]";


        } else {
            this.logger.severe("basetable or subset does not exist");
        }


    }

    public void loadData() {
        this.logger.info("Loading data");
        this.availableBaseTables = this.retrieveBaseTablesFromDatabase();
        this.selectedBaseTable = this.availableBaseTables.get(0).getValue().toString();
        this.availableSubsets = this.retrieveSubsetsFromDatabase(this.selectedBaseTable);


    }

    public String getMetaParentURL() {
        this.logger.info("Getting url: " + metaParentURL);
        return metaParentURL;
    }

    public void setMetaParentURL(String metaParentURL) {
        this.metaParentURL = metaParentURL;
    }

    public String getMetaSubsetURL() {
        return metaSubsetURL;
    }

    public void setMetaSubsetURL(String metaSubsetURL) {
        this.metaSubsetURL = metaSubsetURL;
    }

    public int getMetaParentActiveRecords() {
        return metaParentActiveRecords;
    }

    public void setMetaParentActiveRecords(int metaParentActiveRecords) {
        this.metaParentActiveRecords = metaParentActiveRecords;
    }
}
