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

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
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

@ManagedBean
@SessionScoped
public class SelectColumnsController implements Serializable {

    private Logger logger;

    private List<String> selectedColumnsList;
    private List<String> availableColumnsList;


    public List<String> getSelectedColumnsList() {
        return selectedColumnsList;
    }

    public void setSelectedColumnsList(List<String> selectedColumnsList) {
        this.selectedColumnsList = selectedColumnsList;
    }

   


    public List<String> getAvailableColumnsList() {

        return availableColumnsList;
    }

    public void setAvailableColumnsList(List<String> availableColumnsList) {
        this.availableColumnsList = availableColumnsList;
    }



    public SelectColumnsController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.init();

    }

    /*
* Get the colums for the Web interface from the database. Used for building the check boxes
* * * */
    public List<String> getColumnsFromDatabase() {
        
        DatabaseTools dbtools = new DatabaseTools();
        SessionManager sm = new SessionManager();
        String tableName = sm.getCurrentTableNameFromSession();
        this.availableColumnsList = new ArrayList<String>();
        Map<String, String> availableColumnsMap = dbtools.getTableColumnMetadata(tableName);

        for (Map.Entry<String, String> entry : availableColumnsMap.entrySet()) {


            String columnName = entry.getKey();
            this.availableColumnsList.add(columnName);

        }

        return this.availableColumnsList;
    }

    @PostConstruct
    public void init() {
        this.logger.info("Initializign columns");
        this.availableColumnsList = this.getColumnsFromDatabase();
        //@todo
        this.selectedColumnsList = this.availableColumnsList;
        
        SessionManager sm = new SessionManager();
        this.logger.info("Initialization count : " + availableColumnsList.size());
        sm.storeSelectedColumnsFromTableMap(availableColumnsList);


    }

    /**
     * Action button
     */
    public void setSelectedColumnsAction() {

        this.logger.info("Pressed columns button. There are selected columns: " + this.getSelectedColumnsList().size());
        SessionManager sm = new SessionManager();

        sm.storeSelectedColumnsFromTableMap(this.getSelectedColumnsList());


        FacesContext context = FacesContext.getCurrentInstance();
        FacesMessage msg = new FacesMessage("Your selection has been stored.");
        context.addMessage(
                "selectedColumnsForm:selectedColumnsButton", msg
        );


    }

    /*The user may unselect colums. Remove unselected columns from the query.
* * */
    private Map<Integer, String> removeUnselectedColumnsFromQuery(Map<Integer, String> columnSequenceMap) {
        this.logger.info("Removing unselected columns");

        SessionManager sm = new SessionManager();
        List<String> selectedColumns = sm.getSelectedColumnsFromTableMapSession();

        for (Map.Entry<Integer, String> entry : columnSequenceMap.entrySet()) {
            int sequenceNumber = entry.getKey();
            String columnName = entry.getValue();

            this.logger.info("Map Key (Sequence) : " + sequenceNumber + "  Value: " + columnName);
            // Iterate over selected colums and remove if not contained
            for (String listItem : selectedColumns) {
                if (columnName.equals(listItem)) {
                    this.logger.info("The column was selected");
                } else {
                    this.logger.info("Removed column " + listItem + " with seqquence number " + sequenceNumber);
                    columnSequenceMap.remove(sequenceNumber);
                }

            }
        }

        return columnSequenceMap;


    }

}
