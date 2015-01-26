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

package Database.DatabaseOperations;

import com.sun.rowset.CachedRowSetImpl;

import javax.sql.rowset.CachedRowSet;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Logger;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DatabaseQueries {

    private Logger logger;
    private DataBaseConnectionPool dbcp;
    private DatabaseTools dbtools;

    public DatabaseQueries() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.dbcp = new DataBaseConnectionPool();
        this.dbtools = new DatabaseTools(this.dbcp.getDataBaseName());

    }


    /**
     * Query the database and retrieve the records. Rewrite Statement for most recent version only
     *
     * @param tableName
     * @param sortingColumnsID
     * @param sortingDirection
     * @param filterMap
     * @param startRow
     * @param offset
     * @return
     */
    public CachedRowSet queryDatabase(String tableName, int sortingColumnsID,
                                      String sortingDirection, Map<String, String> filterMap,
                                      int startRow, int offset) {
        Connection connection = null;
        this.logger.warning("TABLE NAME in query : " + tableName);

        ResultSet rs = null;
        CachedRowSet cachedResultSet = null;
        String[] tableHeaders = null;
        Statement stat = null;

        // result set has to be sorted

        if (sortingColumnsID == 0) {
            this.logger.warning("sorting colum war null!");
        }
        if (sortingColumnsID >= 0) {
            this.logger.warning("sortingColumnsID == " + sortingColumnsID);

            Map<String, String> tableMetadata;

            try {

                cachedResultSet = new CachedRowSetImpl();
                // get column names
                tableMetadata = this.dbtools.getTableColumnMetadata(tableName);
                this.logger.warning("Table metadata: " + tableMetadata.size());

                String sortColumn = "outerGroup." + (new ArrayList<String>(
                        tableMetadata.keySet())).get(sortingColumnsID);

                String whereClause = "";
                if (this.hasFilters(filterMap)) {
                    whereClause = this.getWhereString(filterMap);
                }

                connection = this.dbcp.getConnection();

                // get primary key from the table

                String primaryKey = dbtools.getPrimaryKeyFromTableWithoutMetadataColumns(tableName).get(0);


                this.logger.info("Primary key is: " + primaryKey);
                String selectSQL = "SELECT * ";
                selectSQL += this.getMostRecentVersionSQLString(primaryKey, tableName);
                selectSQL += whereClause + " ORDER BY " + sortColumn + " "
                        + sortingDirection
                        + this.getPaginationStringWithLIMIT(startRow, offset);

                stat = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);
                System.out.println("TEEEEEE: " + selectSQL);

                ResultSet sortedResultSet = stat.executeQuery(selectSQL);


                this.logger.info("NATIVE SQL STRING "
                        + connection.nativeSQL(selectSQL));
                cachedResultSet.populate(sortedResultSet);
                stat.close();
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }

                    if (stat != null) {
                        stat.close();
                    }
                } catch (SQLException sqlee) {
                    sqlee.printStackTrace();
                }
            }

        } else {
            try {
                connection = this.dbcp.getConnection();


                stat = connection.createStatement(
                        ResultSet.TYPE_SCROLL_INSENSITIVE,
                        ResultSet.CONCUR_READ_ONLY);

                // String query = "SELECT * FROM  "+this.tableName;

                // stat = this.getConnection().createStatement();
                String query = "SELECT * FROM  " + tableName;
                System.out.println(query);
                rs = stat.executeQuery(query);
                cachedResultSet.populate(rs);
                stat.close();
                connection.close();
            } catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                try {
                    if (connection != null) {
                        connection.close();
                    }
                    if (stat != null) {
                        stat.close();
                    }
                } catch (SQLException sqlee) {
                    sqlee.printStackTrace();
                }
            }
        }
        // TODO how to close connection?

        return cachedResultSet;

    }

    /**
     * Creates the INNER JOIN part of the Query which retrieves only the latest record.
     * Example: SELECT * FROM addresses outerGroup INNER JOIN (
     * SELECT email, max(LAST_UPDATE) AS mostRecent
     * FROM addresses WHERE (RECORD_STATUS = 'inserted' OR RECORD_STATUS = 'updated') GROUP BY email
     * ) grouped
     * ON adr.email = grouped.email AND adr.LAST_UPDATE = grouped.mostRecent
     *
     * @param primaryKey
     * @param tableName
     * @return
     */
    private String getMostRecentVersionSQLString(String primaryKey, String tableName) {
        String innerJoinSQLString = "FROM " + tableName + " AS outerGroup INNER JOIN ( SELECT " + primaryKey + ", " +
                "max(LAST_UPDATE) AS mostRecent FROM " +
                tableName + " WHERE (RECORD_STATUS = 'inserted' OR RECORD_STATUS = 'updated') GROUP BY " + primaryKey
                + ") innerGroup ON outerGroup." + primaryKey + " = innerGroup." + primaryKey + " AND outerGroup" +
                ".LAST_UPDATE = innerGroup.mostRecent ";
        this.logger.info("Rewritten INNER JOIN SQL: " + innerJoinSQLString);
        return innerJoinSQLString;

    }

    /**
     * Check if there are filters
     *
     * @param filterMap
     * @return
     */
    private boolean hasFilters(Map<String, String> filterMap) {
        if (filterMap.size() > 0) {
            this.logger.info("There are filters");
            return true;
        } else
            return false;
    }


    // iterate over the filters and buils WHERE clauses
    // the pagination string has the WHERE keyword
    private String getWhereString(Map<String, String> filtersMap) {
        // String whereString = "  AND ";
        // use this string if LIMIT is active
        String whereString = " WHERE ";
        int filterCounter = -1;
        for (Map.Entry<String, String> entry : filtersMap.entrySet()) {

            filterCounter++;

            String column = entry.getKey();
            String clause = entry.getValue();

            if (filterCounter == 0) {
                whereString += "UPPER(" + column + ") LIKE UPPER(\'%" + clause
                        + "%\') ";
            }
            if (filterCounter >= 1) {
                whereString += "AND UPPER(" + column + ") LIKE UPPER(\'%"
                        + clause + "%\') ";
            }

        }

        this.logger.info("WHERE Clause: " + whereString);
        return whereString;

    }

    /*
 * All rows have a sequence number with and index. Use this sequence number
 * in the WHERE clause in order to retrieve only the required rows.
 *
 * @return
 */
    private String getPaginationStringWithLIMIT(int showRows, int offset) {
        String paginationString = null;
        if (showRows < 0 || offset < 0) {
            this.logger.warning("Pagination error!!");

        }
        // paginationString= " WHERE ID_SYSTEM_SEQUENCE BETWEEN \'" + startRow +
        // "\' AND \'" + (startRow + offset) + "\'";
        paginationString = " LIMIT " + showRows + " OFFSET " + offset;
        return paginationString;
    }

}
