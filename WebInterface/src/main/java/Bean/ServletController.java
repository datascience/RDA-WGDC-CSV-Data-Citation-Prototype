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


import DatatableModel.TableMetadata;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 20.06.14.
 */

@ManagedBean(name = "servletController")
@SessionScoped
public class ServletController implements Serializable {
    private Logger logger;
    private String emptyTableHeaders;
    private String tableFooterAsHTML;
    private String tableHeadersAsHTML;

    private String columnFilterColumns = null;
    private String currentTableName = null;
    private String mDataString;


    private List<String> selectedColumnsFromWebInterfaceViaSession = null;


    public ServletController() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Servlet controller");
        this.selectedColumnsFromWebInterfaceViaSession = this.getSelectedColumnsFromWebInterfaceViaSession();
        



    }

    public String getEmptyTableHeaders() {


        String htmlString = null;
        try {
            htmlString = TableMetadata.getEmptyTableHeaders(this.getCurrentTableName());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return htmlString;
    }

    public void setEmptyTableHeaders(String emptyTableHeaders) {
        this.emptyTableHeaders = emptyTableHeaders;
    }

    public String getTableFooterAsHTML() {
        String htmlString = null;
        try {


            htmlString = TableMetadata.getTableFooterAsHTML(this.getSelectedColumnsFromWebInterfaceViaSession());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return htmlString;
    }

    public void setTableFooterAsHTML(String tableFooterAsHTML) {
        this.tableFooterAsHTML = tableFooterAsHTML;
    }

    public String getTableHeadersAsHTML() {
        String htmlString = null;
        try {
            htmlString = TableMetadata.getTableHeadersAsHTML(this.getSelectedColumnsFromWebInterfaceViaSession());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return htmlString;
    }

    public void setTableHeadersAsHTML(String tableHeadersAsHTML) {
        this.tableHeadersAsHTML = tableHeadersAsHTML;
    }

    public String getColumnFilterColumns() {

        String filterColumns = null;
        try {
            filterColumns = TableMetadata.getColumnFilterColumnns(this.getSelectedColumnsFromWebInterfaceViaSession());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return filterColumns;

    }

    public void setColumnFilterColumns(String columnFilterColumns) {
        this.columnFilterColumns = columnFilterColumns;
    }

    public String getCurrentTableName() {

        //@todo check if that works

        //Map<String, Object> sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        //String tableName = (String) sessionMAP.get("currentTableName");
        SessionManager sm = new SessionManager();
        TableDefinitionBean tableBean = sm.getTableDefinitionBean();
        String tableName = tableBean.getTableName();
        return tableName;
    }

    //@todo
    public List<String> getSelectedColumnsFromWebInterfaceViaSession() {

        SessionManager sm = new SessionManager();

        List<String> selectedColumnsSessionData = sm.getSelectedColumnsFromTableSessionAsList();

        ;
        if (selectedColumnsSessionData == null) {
            this.logger.info("The session was not yet set. ");
            sm.initializeSelectedColumns();
            selectedColumnsSessionData = sm.getSelectedColumnsFromTableSessionAsList();

            

        }
        return selectedColumnsSessionData;
    }

    public void setCurrentTableName(String currentTableName) {
        this.currentTableName = currentTableName;
    }

    public String getmDataString() {

        return this.dataTablesMDataProp();
    }

    public void setmDataString(String mDataString) {
        this.mDataString = mDataString;
    }

    public String dataTablesMDataProp() {
        this.logger.info("Servlet DATA PROP");

        String tableName = this.getCurrentTableName();
        if (tableName == null) {
            this.logger.info("There is no table available");


        } else {
            String tableMetaString = null;

            try {

                List<String> selectedColumnsSessionData = this.getSelectedColumnsFromWebInterfaceViaSession();
                if (selectedColumnsSessionData != null && selectedColumnsSessionData.size() >= 1) {
                    tableMetaString = TableMetadata.getDataTablesMDataProp(selectedColumnsSessionData);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            return tableMetaString;
            
        }

        return null;
    }
}
