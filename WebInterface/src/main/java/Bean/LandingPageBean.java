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
import QueryStore.QueryStoreAPI;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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


    public void handleDropDownChange() {
        //based on the number provided, change "regions" attribute.
        this.logger.info("change listener. Base table is now " + this.selectedBaseTable);
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


}
