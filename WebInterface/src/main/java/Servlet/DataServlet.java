package Servlet;

import Bean.SessionManager;
import Database.DatabaseOperations.DatabaseTools;
import Database.Helpers.StringHelpers;
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
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Handler;
import java.util.logging.Logger;

// import com.sandeep.data.object.DataTableObject;

@SessionScoped
@WebServlet("/csvdata")
public class DataServlet extends HttpServlet {
    Map<String, String> filterMap;
    Map<String, String> sortingMap;
    Map<Integer, String> columnSequenceMap;
    private Logger logger;
    private DataSource dataSource;
    private TableDataOperations tableData;

    public DataServlet() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.tableData = new TableDataOperations();

        filterMap = new HashMap<>();
        sortingMap = new HashMap<>();


    }

    public Map<String, String> getSortingMap() {
        return sortingMap;
    }

    public void setSortingMap(Map<String, String> sortingMap) {
        this.sortingMap = sortingMap;
    }

    public Map<String, String> getFilterMap() {
        this.logger.info("GET Filter Map.........");
        return filterMap;
    }

    public void setFilterMap(Map<String, String> filterMap) {
        this.logger.info("Set the filtermap...");
        this.filterMap = filterMap;
    }

    /**
     * Query data from DB after request
     */

    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response) {
        this.logger.info("S-E-R-V-L-E-T");


        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = null;
        response.setContentType("application/json");


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
            if (param.currentTable == null || param.currentTable.equals("")) {
                this.logger.severe("There is a idscrepancy in the parameter and the session! Using session data.");
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

            this.columnSequenceMap = this.getColumnSequenceFromRequest(param, request);
            this.logger.info("Sequence map size is " + this.columnSequenceMap.size());

            HttpSession session = request.getSession(true);

            //todo sorting must be stored in session
            if (this.columnSequenceMap == null) {
                List<String> columnsFromSession = (List<String>) session.getAttribute("selectedColumnsFromTableMap");
                this.logger.info("There are " + columnsFromSession.size() + " columns selected, the first column is " +
                        columnsFromSession.get(0));


                //todo remove unselected
                Map<Integer, String> selectedColumnsSequenceMap = this.removeUnselectedColumnsFromQuery(this
                        .columnSequenceMap, columnsFromSession);


                this.columnSequenceMap = selectedColumnsSequenceMap;


            }
            SessionManager sm = new SessionManager();
            sm.updateSortingOfSelectedColumnsInSession(this.columnSequenceMap);


            


            DatabaseTools dbTools = new DatabaseTools();

            String sortingColumnName = dbTools.getColumnNameByID(sortingColumnID);
            this.sortingMap.put(sortingColumnName, sortingDirection);
            this.logger.info("Servlet: Sorting " + sortingColumnName + " Direction: " + sortingDirection);


            // retrieve filters
            this.setFilterMap(param.filterMap);




            try {

                CachedRowSet cachedRowSet = dbTools.executeQuery(currentTable, selectedColumnsSequenceMap, 
                        sortingColumnID,
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


    /*The user may unselect colums. Remove unselected columns from the query.
* * */
    private Map<Integer, String> removeUnselectedColumnsFromQuery(Map<Integer, String> columnSequenceMap, List<String
            > columnsFromSession) {


        Iterator it = columnSequenceMap.entrySet().iterator();
        boolean isContained = false;
        while (it.hasNext()) {
            Map.Entry<Integer, String> entry = (Map.Entry<Integer, String>) it.next();

            int sequenceNumber = entry.getKey();
            String columnName = entry.getValue();

            // Iterate over selected colums and remove if not contained
            for (String listItem : columnsFromSession) {
                if (columnName.equals(listItem)) {

                    isContained = true;
                    break;
                } else {

                    isContained = false;

                }

            }
            if (isContained == false) {
                Logger.getGlobal().info("Removeddddddddddddddddddddddddddddd " + entry.getValue());
                it.remove();

            }

        }

        return columnSequenceMap;


    }
}
