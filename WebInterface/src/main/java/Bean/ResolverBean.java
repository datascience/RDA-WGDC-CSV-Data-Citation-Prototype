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

import Controller.ResolverController;
import QueryStore.BaseTable;
import QueryStore.Query;
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
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */

@ManagedBean(name="resolverBean")
@SessionScoped
public class ResolverBean implements Serializable{

    private Logger logger;
    private String selectedBaseTablePID; // +getter +setter
    private String selectedSubset;


    private List<SelectItem> availableBaseTables; // +getter (no setter necessary)
    private List<SelectItem> availableSubsets;
    
    private ResolverController resolverController;
    private String requestPID;
    private String resolveInputPID;
    
    
    


    public ResolverBean() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Resolver Bean");
        this.resolverController = new ResolverController();
        
        
    }

    @PostConstruct
    public void init() {
        this.loadData();


    }


    public void handleDropDownChangeBaseTables() {
        //based on the number provided, change "regions" attribute.
        this.logger.info("change listener. Base table is now " + this.selectedBaseTablePID);
        this.resolverController.setSelectedBaseTable(this.selectedBaseTablePID);
        this.availableSubsets = this.retrieveSubsetsFromDatabase();
        
        

    }

    public void handleDropDownChangeSubsets() {
        QueryStoreAPI queryAPI = new QueryStoreAPI();
        this.logger.info("change subsets. Subset is is now " + this.selectedSubset);

        if(this.selectedSubset==null || this.selectedSubset.equals("")){
            this.logger.info("There was no subset selected");
            this.resolverController.setSelectedSubset(null);

        } else{


            Query query = queryAPI.getQueryByPID(this.selectedSubset);


            this.resolverController.setSelectedSubset(query.getPID());

        }

        


    }

    public String getSelectedBaseTablePID() {
        return selectedBaseTablePID;
    }

    public void setSelectedBaseTablePID(String selectedBaseTablePID) {
        this.logger.info("Selected Basetable " + selectedBaseTablePID);

        this.selectedBaseTablePID = selectedBaseTablePID;
    }

    public List<SelectItem> getAvailableBaseTables() {

        this.availableBaseTables = this.retrieveBaseTablesFromDatabase();
        return this.availableBaseTables;
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
            availableBaseTables.add(new SelectItem(baseTablePid, baseTablePid + "( " + baseTableName + " )"));
        }

        this.logger.info("Found " + availableBaseTables.size() + " base tables");
        return availableBaseTables;


    }

    private List<SelectItem> retrieveSubsetsFromDatabase() {

        QueryStoreAPI queryAPI = new QueryStoreAPI();



        Map<String, String> availableSubsetsMap = queryAPI.getAvailableSubsetsFromBase(this.selectedBaseTablePID);
        List<SelectItem> availableSubsets = new ArrayList<SelectItem>();


        for (Map.Entry<String, String> entry : availableSubsetsMap.entrySet()) {
            String pid = entry.getKey();

            String execDateString = entry.getValue();
            availableSubsets.add(new SelectItem(pid, pid + " ( " + execDateString + " )"));
        }

        this.logger.info("Found " + availableSubsets.size() + " base tables");

        // add an empty entry for unselecting
        availableSubsets.add(new SelectItem("","(No subset - only dataset)"));
        return availableSubsets;


    }

    public void loadData() {
        this.logger.info("Loading data");
        this.availableSubsets = null;
        this.availableBaseTables = this.retrieveBaseTablesFromDatabase();
        this.selectedBaseTablePID = this.availableBaseTables.get(0).getValue().toString();
        this.availableSubsets = this.retrieveSubsetsFromDatabase();
        
        this.resolverController.setSelectedBaseTable(this.selectedBaseTablePID);


    }
    
    public void resolvePID(){

        this.resolverController.resolvePID();
        
    }

    public void resolvePIDfromTextInput(){
        this.logger.info("Resolving: " + this.requestPID);
        this.resolverController.resolvePID(this.requestPID);


    }

    public List<SelectItem> getAvailableSubsets() {

        this.availableSubsets = this.retrieveSubsetsFromDatabase();
        return availableSubsets;
    }

    public void setAvailableSubsets(List<SelectItem> availableSubsets) {
        this.availableSubsets = availableSubsets;
    }

    public ResolverController getResolverController() {
        return resolverController;
    }

    public void setResolverController(ResolverController resolverController) {
        this.resolverController = resolverController;
    }

    public void initPidRequest(){
        this.resolverController.resolvePID(requestPID);
        
    }

    public String getRequestPID() {
        return requestPID;
    }

    public void setRequestPID(String requestPID) {
        this.requestPID = requestPID;
    }

    public String getResolveInputPID() {
        return resolveInputPID;
    }

    public void setResolveInputPID(String resolveInputPID) {
        this.resolveInputPID = resolveInputPID;
    }
}
