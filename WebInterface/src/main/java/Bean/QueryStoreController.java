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


import Database.Authentication.User;
import QueryStore.BaseTable;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 21.06.14.
 */

@ManagedBean(name = "queryStoreController")
@SessionScoped
public class QueryStoreController implements Serializable {

    private Logger logger;
    private QueryStoreAPI queryStoreAPI;
    private String subSetTitle;


    public String getDataSetDescription() {

        return dataSetDescription;
    }

    public void setDataSetDescription(String dataSetDescription) {
        this.dataSetDescription = dataSetDescription;
    }

    private String dataSetDescription;




    public QueryStoreAPI getQueryStoreAPI() {
        if (this.queryStoreAPI == null) {
            this.queryStoreAPI = new QueryStoreAPI();
        }
        return this.queryStoreAPI;
    }

    public void setQueryStoreAPI(QueryStoreAPI queryStoreAPI) {
        this.queryStoreAPI = queryStoreAPI;
    }

    private PersistentIdentifierAPI pidAPI;

    public QueryStoreController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.queryStoreAPI = new QueryStoreAPI();
        this.pidAPI = new PersistentIdentifierAPI();



    }
    
    
    public Query getQuery() {

        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    Query query;


    public Map<String, String> getFilterMap() {


        if (filterMap != null) {
            this.logger.info("Filter map size: " + filterMap.size());
        } else {
            this.logger.warning("Filter map is NULL");
        }


        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {

        this.logger.info("Setting filtermap inside the bean!!! ");
        this.filterMap = filterMap;
    }

    private Map<String, String> filterMap;



    /* Finalize the query.
    * */
    public void finalizeDataSet() {

        // init



        SessionManager sm = new SessionManager();
        User user = sm.getLogedInUserObject();


        int prefix = user.getOrganizational_id();

        Organization org = this.pidAPI.getOrganizationObjectByPrefix(prefix);
        this.logger.info("Retrieving ORG by prefix: " + prefix + " Organizationa name (from object) :  " + org
                .getOrganization_name());

        //@todo real landing page
        PersistentIdentifierAlphaNumeric pid = this.pidAPI.getAlphaNumericPID(org,
                "http://localhost:8080/landingpages/XXX");

        String userName = user.getUsername();
        String currentPID = pid.getFQNidentifier();
        Date creationDate = new Date();


        this.query = this.queryStoreAPI.createNewQuery(user.getFirstName() + " " + user.getLastName(), currentPID);

        this.query.setCreatedDate(creationDate);
        this.query.setExecution_timestamp(creationDate);
        this.query.setDatasourcePID(sm.getCurrentDatabaseNameFromSession() + "." + sm.getCurrentTableNameFromSession());

        TableDefinitionBean tableDefinitionBean = sm.getTableDefinitionBean();

        if (tableDefinitionBean.getBaseTablePID() == null) {
            BaseTable baseTable = this.queryStoreAPI.getBaseTableByDatabaseAndTableName(sm.getCurrentDatabaseNameFromSession(), sm.getCurrentTableNameFromSession());
            tableDefinitionBean.setBaseTablePID(baseTable.getBaseTablePID());
            sm.updateTableDefinitionBean(tableDefinitionBean);
        }

        String baseTablePid = sm.getTableDefinitionBean().getBaseTablePID();


        BaseTable baseTable = this.queryStoreAPI.getBaseTableByPID(baseTablePid);

        this.query.setBaseTable(baseTable);


        //@todo current databasename und table name sind null vom session manager.

        this.queryStoreAPI.persistQuery(this.query);


        this.logger.info("Created query: " + this.query.getCreatedDate());
        String pidString = this.queryStoreAPI.getQueryPID(this.query);


        FacesContext.getCurrentInstance().addMessage("queryStoreMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "The QueryStore is now initialized", "The generated PID is: " + pidString));
        this.logger.info("Completed.");

        ///// Store filter

        this.logger.info("Store selection");
        String filterMapJSON = this.getJSONFromWebService("?lastFilters=1");
        Map<String, String> filterMap = this.convertJSON2Map(filterMapJSON);
        // this.printMap(filterMap);

        // persist the filters
        if (filterMap != null) {
            this.queryStoreAPI.addFilters(query, filterMap);
        }


        String sortingsMapJSON = this.getJSONFromWebService("?lastSortings=1");
        Map<String, String> sortingsMap = this.convertJSON2Map(sortingsMapJSON);
        this.printMap(sortingsMap);


        // persist the sortings
        this.queryStoreAPI.addSortings(query, sortingsMap);

        // save
        this.queryStoreAPI.persistQuery(query);
        String queryHash = this.queryStoreAPI.getQueryHash(this.query);
        FacesContext.getCurrentInstance().addMessage("queryStoreMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Stored current selection", "Updated query hash:" + queryHash));
        this.logger.info("Completed.");


        //////////////// finalize


        this.logger.info("finalize ");
        this.query = this.getQuery();


        this.query.setSelectedColumns(sm.getColumnNamesFromDataTablesSession());
        this.query.setQueryDescription(this.dataSetDescription);
        this.query.setSubSetTitle(this.subSetTitle);
        this.query.setResultSetRowCount(sm.getRowCount());


        this.queryStoreAPI.updateExecutiontime(this.query);
        this.queryStoreAPI.finalizeQuery(this.query);


        queryHash = this.queryStoreAPI.getQueryHash(this.query);


        String resultSetHash = this.queryStoreAPI.calculateResultSetHashShort(this.query);


        boolean persisted = this.queryStoreAPI.persistResultSetHash(this.query, resultSetHash);
        if (persisted) {
            FacesContext.getCurrentInstance().addMessage("queryStoreMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "ResultSet Hash", "The result set has this hash: " + resultSetHash));
        } else {
            Query q = (Query) this.queryStoreAPI.getQueryByResultSetHash(resultSetHash);
            String existingPID = this.queryStoreAPI.getQueryPID(q);
            FacesContext.getCurrentInstance().addMessage("queryStoreMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "ResultSet Hash", "The same hash already exists with PID " + existingPID));
            if (this.queryStoreAPI.deleteQuery(this.query)) {
                FacesContext.getCurrentInstance().addMessage("queryStoreMessage", new FacesMessage(FacesMessage.SEVERITY_WARN, "Query deleted", "Deleted this query as an identical one already exists"));

            }

        }



        FacesContext.getCurrentInstance().addMessage("queryStoreMessage", new FacesMessage(FacesMessage.SEVERITY_INFO, "Finalized query", "The query PID is: " + pidString + " and the query hash is " + queryHash));
        this.logger.info("Completed.");
    }




    private void printMap(Map<String, String> map) {
        if (map == null) {
            this.logger.info("Map was NULL");
        } else {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                this.logger.info("Map entry: " + key + " value : " + value);

            }

        }


    }

    /*
        Converts the DatatableModel.JSON string into a Map
     */
    private Map<String, String> convertJSON2Map(String json) {
        Gson gson = new Gson();
        Type typeOfHashMap = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> newMap = gson.fromJson(json, typeOfHashMap); // This type must match TypeToken

        return newMap;
    }

    private String getJSONFromWebService(String parameterRequest) {
        String line;
        StringBuilder builder = new StringBuilder(2048);
        // call Web Service and retrieve json
        String str = "http://localhost:8080/cite/csvdata" + parameterRequest;

        URLConnection urlc = null;
        BufferedReader bfr = null;
        try {
            URL url = null;
            url = new URL(str);

            urlc = url.openConnection();
            bfr = new BufferedReader(new InputStreamReader(urlc.getInputStream()));

            while ((line = bfr.readLine()) != null) {
                builder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        String jsonString = builder.toString();

        return jsonString;

    }

    public String getSubSetTitle() {
        return subSetTitle;
    }

    public void setSubSetTitle(String subSetTitle) {
        this.subSetTitle = subSetTitle;
    }
}