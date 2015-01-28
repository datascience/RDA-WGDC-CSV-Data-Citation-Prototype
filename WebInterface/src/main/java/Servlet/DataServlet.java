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

package Servlet;


import Bean.SessionManager;
import Database.DatabaseOperations.DatabaseTools;
import DatatableModel.DataTablesParamUtility;
import DatatableModel.JQueryDataTableParamModel;
import DatatableModel.TableDataOperations;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.hibernate.metamodel.relational.Database;

import javax.faces.bean.SessionScoped;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Logger;

// import com.sandeep.data.object.DataTableObject;

@SessionScoped
@WebServlet("/csvdata")
public class DataServlet extends HttpServlet {
    private Logger logger;
    private DataSource dataSource;

    private TableDataOperations tableData;
    Map<String, String> filterMap;


    public Map<String, String> getSortingMap() {
        return sortingMap;
    }

    public void setSortingMap(Map<String, String> sortingMap) {
        this.sortingMap = sortingMap;
    }

    Map<String, String> sortingMap;


    public Map<String, String> getFilterMap() {
        this.logger.info("GET Filter Map.........");
        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {
        this.logger.info("Set the filtermap...");
        this.filterMap = filterMap;
    }


    public DataServlet() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.tableData = new TableDataOperations();
        this.logger.info("SERVLET");
        filterMap = new HashMap<>();
        sortingMap = new HashMap<>();


    }

    /**
     * Query data from DB after request
     */

    // TODO rebise row count becaue now calles DB twice
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {

        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = null;
        response.setContentType("application/json");
        System.out.println("GET sent");

        // get the map of the latest filters
        if (request.getParameter("lastFilters") != null) {
            this.logger.info("LAST FILTERS");
            Map<String, String> testMap = this.getFilterMap();
            if (testMap == null) {
                this.logger.info("Testmap ist null");
            } else {
                json = this.convertMap2JSON(testMap);
//
                this.logger.info("JSOOOOOOOOOOOOOOOOOOOOOOOOOOOOON: " + json);
            }
            // get the map of the latest filters
       /*
        if(request.getParameter("lastFilters") != null){
            this.logger.info("LAST FILTERS");
            Map<String, String> filterMap =  this.getFilterMap();
            if(filterMap==null){
                this.logger.info("Testmap ist null");
            }else{
                FilterSortingJSONHelper fsHelper = new FilterSortingJSONHelper();
                fsHelper.setFilters(filterMap);
                fsHelper.setSortings(this.sortingMap);

                json = this.convertMap2JSON(filterMap);
//
                this.logger.info("JSOOOOOOOOOOOOOOOOOOOOOOOOOOOOON: " + json);
            }
*/


        } else if (request.getParameter("lastSortings") != null) {
            this.logger.info("LAST Sorting");
            Map<String, String> sortingsMap = this.getSortingMap();
            if (sortingsMap == null) {
                this.logger.info("SORTINGMAP  ist null");
            } else {
                json = this.convertMap2JSON(sortingsMap);
//
                this.logger.info("Sorting DatatableModel.JSON: " + json);
            }
        }

        // the user is filtering
        else {

            JQueryDataTableParamModel param = DataTablesParamUtility.getParam(request);
            String currentTable = param.currentTable;

            // if there was no table set, get the session table
            if (currentTable == null || currentTable.equals("")) {
                this.logger.warning("Table was null. Using session table");
                SessionManager sm = new SessionManager();
                currentTable = sm.getCurrentTableNameFromSession();

            }
            




            if (param == null || param.sEcho == "") {
                try {
                    response.sendRedirect("error.htm");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


            // Pagination
            int showRows = param.iDisplayLength;
            int offset = param.iDisplayStart;


            int sortingColumnID = this.getSortingStringID(param, request);

            String sortingDirection = this.getSortingDirection(param, request);

            DatabaseTools dbTools = new DatabaseTools();

            String sortingColumnName = dbTools.getColumnNameByID(sortingColumnID);
            this.sortingMap.put(sortingColumnName, sortingDirection);


            // retrieve filters
            this.setFilterMap(param.filterMap);


            // TEST TEST// TEST TEST funktioniert nicht!
            this.printMap(this.getFilterMap());


            try {

                CachedRowSet cachedRowSet = dbTools.executeQuery(currentTable, sortingColumnID,
                        sortingDirection, filterMap, showRows, offset);

//                this.logger.warning("Cached size ->> " + cachedRowSet.size());

                param.iTotalRecords = dbTools.getRowCount(currentTable);
                param.iTotalDisplayRecords = param.iTotalRecords;//cachedRowSet.size();// value will be set when code
                // filters

                // Before filtering


                //            this.logger.warning("iTotalRecords = " + param.iTotalRecords + " iTotalDisplayRecords
                // " + param.iTotalDisplayRecords);
                json = this.tableData.getJSON(cachedRowSet, param);


                this.logger.warning("Number of Records" + param.iTotalRecords);


                // System.out.println(json);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        out.print(json);


    }

    public void addHandler(Handler handler) throws SecurityException {
        logger.addHandler(handler);
    }

    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) {
        System.out.println("POST received");
        doGet(request, response);

    }

    public int getSortingStringID(JQueryDataTableParamModel param,
                                  HttpServletRequest request) {
        int sortingSQL = 0;
        for (int i = 0; i < param.iSortingCols; i++) {
            String sortingColumns = request.getParameter("iSortCol_" + i);

            sortingSQL = Integer.parseInt(sortingColumns);
        }
        return sortingSQL;
    }

    public String getSortingDirection(JQueryDataTableParamModel param,
                                      HttpServletRequest request) {
        String sortingDirection = "";
        for (int i = 0; i < param.iSortingCols; i++) {
            sortingDirection = request.getParameter("sSortDir_" + i);
        }
        return sortingDirection;
    }

    private void printAllRequestParameters(HttpServletRequest request) {

        Enumeration enAttr = request.getAttributeNames();
        while (enAttr.hasMoreElements()) {
            String attributeName = (String) enAttr.nextElement();
            System.out.println("Attribute Name - " + attributeName + ", Value - " + (request.getAttribute
                    (attributeName)).toString());
        }

        System.out.println("To out-put All the request parameters received from request - ");

        Enumeration enParams = request.getParameterNames();
        while (enParams.hasMoreElements()) {
            String paramName = (String) enParams.nextElement();
            System.out.println("Attribute Name - " + paramName + ", Value - " + request.getParameter(paramName));
        }

    }


    private void printMap(Map<String, String> map) {
        if (map == null) {
            this.logger.info("Map was NULL");
        } else {

        }

        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            this.logger.info("Map entry: " + key + " value : " + value);

        }


    }


    private String convertMap2JSON(Map<String, String> map) {
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(map);

        this.logger.info("Converted Map to this DatatableModel.JSON: " + json);
        return json;
    }
}
