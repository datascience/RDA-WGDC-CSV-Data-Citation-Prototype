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

package Servlet;

import Database.DatabaseOperations.DatabaseTools;
import DatatableModel.DataTablesParamUtility;
import DatatableModel.JQueryDataTableParamModel;
import DatatableModel.TableDataOperations;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
@Path("/tables")
public class DataTablesRESTService {

    Map<String, String> filterMap;
    Map<String, String> sortingMap;
    Map<Integer, String> columnSequenceMap;
    private Logger logger;
    private DataSource dataSource;
    private TableDataOperations tableData;
    private PersistentIdentifierAPI pidAPI = null;
    @Context
    UriInfo uriInfo;

    @Context
    private HttpServletRequest request;


    public DataTablesRESTService() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.tableData = new TableDataOperations();
        this.logger.info("SERVLET");
        filterMap = new HashMap<>();
        sortingMap = new HashMap<>();

    }

    @GET
    @Path("/{data")
    @Produces(MediaType.APPLICATION_JSON)
    public String getData() {
        PrintWriter out = null;

        String json = null;


        // get the map of the latest filters
        if (request.getParameter("lastFilters") != null) {
            this.logger.info("LAST FILTERS");
            Map<String, String> testMap = this.getFilterMap();
            if (testMap == null) {
                this.logger.info("Testmap ist null");
            } else {
                json = this.convertMap2JSON(testMap);
                this.logger.info("JSOOOOOOOOOOOOOOOOOOOOOOOOOOOOON: " + json);
            }


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


            if (param == null || param.sEcho == "") {
                this.logger.severe("Error in method ");
            }


            // Pagination
            int showRows = param.iDisplayLength;
            int offset = param.iDisplayStart;


            int sortingColumnID = this.getSortingStringID(param, request);

            String sortingDirection = this.getSortingDirection(param, request);

            this.columnSequenceMap = this.getColumnSequenceFromRequest(param, request);
            this.logger.info("Sequence map size is " + this.columnSequenceMap.size());

            //todo remove unselected
            //Map<Integer, String> selectedColumnsSequenceMap = this.removeUnselectedColumnsFromQuery(this
            // .columnSequenceMap);


            DatabaseTools dbTools = new DatabaseTools();

            String sortingColumnName = dbTools.getColumnNameByID(sortingColumnID);
            this.sortingMap.put(sortingColumnName, sortingDirection);
            this.logger.info("Servlet: Sorting " + sortingColumnName + " Direction: " + sortingDirection);


            // retrieve filters
            this.setFilterMap(param.filterMap);


            try {

                CachedRowSet cachedRowSet = dbTools.executeQuery(currentTable, columnSequenceMap, sortingColumnID,
                        sortingDirection, filterMap, showRows, offset);

//                this.logger.warning("Cached size ->> " + cachedRowSet.size());

                param.iTotalRecords = dbTools.getRowCount(currentTable);
                param.iTotalDisplayRecords = param.iTotalRecords;//cachedRowSet.size();// value will be set when code
                // filters

                // Before filtering


                //            this.logger.warning("iTotalRecords = " + param.iTotalRecords + " iTotalDisplayRecords
                // " + param.iTotalDisplayRecords);
                json = this.tableData.getJSON(cachedRowSet, param);


                this.logger.warning("Number of Records " + param.iTotalRecords);


                // System.out.println(json);

            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        }
        return json;


    }

    public Map<String, String> getFilterMap() {
        this.logger.info("GET Filter Map.........");
        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {
        this.logger.info("Set the filtermap...");
        this.filterMap = filterMap;
    }


    /* Get the sorting parameters from the request
   * * */
    public int getSortingStringID(JQueryDataTableParamModel param,
                                  HttpServletRequest request) {
        int sortingSQL = 0;
        for (int i = 0; i < param.iSortingCols; i++) {
            String sortingColumns = request.getParameter("iSortCol_" + i);

            sortingSQL = Integer.parseInt(sortingColumns);
        }
        return sortingSQL;
    }

    /*Get the sorting direction from request
    * * */
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

    /* Get the sorting parameters from the request
* * */
    public Map<Integer, String> getColumnSequenceFromRequest(JQueryDataTableParamModel param,
                                                             HttpServletRequest request) {
        // reset map of columns. LinkedHashMap is sorted in insertion order
        Map<Integer, String> columnSequenceMap = new LinkedHashMap<Integer, String>();

        for (int i = 0; i < param.iColumns; i++) {
            String columnName = request.getParameter("mDataProp_" + i);
            columnSequenceMap.put(i, columnName);
            this.logger.info("Column Position " + i + " is " + columnName);


        }
        return columnSequenceMap;
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

    public Map<String, String> getSortingMap() {
        return sortingMap;
    }

    public void setSortingMap(Map<String, String> sortingMap) {
        this.sortingMap = sortingMap;
    }

}

