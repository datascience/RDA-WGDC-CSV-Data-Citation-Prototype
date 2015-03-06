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

package Controller;

import Bean.SessionManager;
import QueryStore.BaseTable;
import QueryStore.Query;
import QueryStore.QueryStoreAPI;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class ResolverController implements Serializable {
    private Logger logger;
    private String selectedBaseTable; // +getter +setter
    private String selectedSubset;
    

    public ResolverController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Resolver Controller ");
        
    }
    
    


    public void resolvePID(String requestPID) {

        if (requestPID != null) {
            ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
            
            if(this.isBaseTable(requestPID)){
                this.logger.info("This is a basetable... Forward to base table landing page");
                try {
                    context.redirect(context.getRequestContextPath() + "/dataset-landingpage.xhtml?requestPID="
                                +requestPID);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if(this.isSubsetPID(requestPID)){
                this.logger.info("This is a subset ... Forward to subsetlanding page");
                try {
                    context.redirect(context.getRequestContextPath() + "/subset-landingpage.xhtml?requestPID="
                            +requestPID);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                
            }
        }


    }
    


    public void resolvePID() {
        this.logger.info("Resolving pid");
        
        if(this.getSelectedSubset()!= null ){
            this.resolvePID(this.getSelectedSubset());
            
        } else if(this.getSelectedBaseTable()!=null){
            this.resolvePID(this.getSelectedBaseTable());
            
        } else{
            this.logger.info("Nothing selected");
        }
    }
    
    /*
    * Check if PID resolved to base table
    * * * */
    private boolean isBaseTable(String requestPID){
        this.logger.info("Validate if "+requestPID+ " is a basetable");
        if (requestPID != null) {


            QueryStoreAPI queryAPI = new QueryStoreAPI();

            Query query = queryAPI.getQueryByPID(requestPID);

            if (query == null) {
                this.logger.info("This was not a query pid. Checking base tables");
                BaseTable baseTable = queryAPI.getBaseTableByPID(requestPID);
                if (baseTable == null) {
                    this.logger.severe("Not a valid Pid!");
                    return false;
                } else {
                    this.logger.info("Base table found!");
                    return true;


                }


            }
        }
        return false;
        
    }
    
    /*
    check if PID belongs to subset
     */
    private boolean isSubsetPID(String requestPID){
        this.logger.info("Validate if "+requestPID+ " is a subset");
        
        if (requestPID != null) {
            QueryStoreAPI queryAPI = new QueryStoreAPI();
            Query query = queryAPI.getQueryByPID(requestPID);
            if(query != null){
                return true;        
            }
        }
        return false;
        
    }
    
    

    public String getSelectedBaseTable() {
        return selectedBaseTable;
    }

    public void setSelectedBaseTable(String selectedBaseTable) {
        this.selectedBaseTable = selectedBaseTable;
    }

    public String getSelectedSubset() {
        return selectedSubset;
    }

    public void setSelectedSubset(String selectedSubset) {
        this.selectedSubset = selectedSubset;
    }
}
