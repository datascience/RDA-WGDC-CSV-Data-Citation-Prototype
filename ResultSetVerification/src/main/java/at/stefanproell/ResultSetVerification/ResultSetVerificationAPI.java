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

package at.stefanproell.ResultSetVerification;

import com.sun.rowset.CachedRowSetImpl;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.*;
import java.util.logging.Logger;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class ResultSetVerificationAPI {
    private Logger logger;

    /**
     * Constructor
     *
     * @return
     */
    public DataBaseConnectionPool getDcp() {

        if (this.dcp == null) {
            this.dcp = new DataBaseConnectionPool();
        }
        return this.dcp;
    }

    /**
     * Get row count from table name
     *
     * @param tableName
     * @return
     * @throws java.sql.SQLException
     */
    public int getRowCount(String tableName) throws SQLException {
        // TODO SQL injection
        String sql = "SELECT COUNT(*) FROM " + tableName;
        int numberOfRecords = -1;
        Connection connection = this.dcp.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        ResultSet rs = preparedStatement.executeQuery();
        while (rs.next()) {
            numberOfRecords = rs.getInt(1);
        }

        this.logger.warning("Row Set Size:  " + numberOfRecords);
        connection.close();

        return numberOfRecords;
    }

    public void setDcp(DataBaseConnectionPool dcp) {
        this.dcp = dcp;
    }

    private DataBaseConnectionPool dcp = null;

    public ResultSetVerificationAPI() {
        this.logger = Logger.getLogger(ResultSetVerificationAPI.class.getName());
        if (this.dcp == null) {
            this.dcp = new DataBaseConnectionPool();
        }
    }

    /**
     * Get a map of <ColumnName, ColumnType>
     */
    public Map<String, String> getTableColumnMetadata(String tableName)
            throws SQLException {
        Connection connection = this.dcp.getConnection();
        DatabaseMetaData meta = connection.getMetaData();
        CachedRowSetImpl cachedResultSet = new CachedRowSetImpl();
        String catalog = null;
        String schemaPattern = null;
        String tableNamePattern = tableName;
        this.logger.warning("Getting metadata for table: " + tableName);
        String columnNamePattern = null;

        ResultSet result = meta.getColumns(catalog, schemaPattern,
                tableNamePattern, columnNamePattern);
        System.out.println(result.getFetchSize());
        cachedResultSet.populate(result);
        connection.close();

        Map<String, String> columnMetadataMap = new LinkedHashMap<String, String>();
        while (cachedResultSet.next()) {


            // ColumnName
            String columnName = cachedResultSet.getString(4);

            // ColumnType
            String columnType = cachedResultSet.getString(6);

            columnMetadataMap.put(columnName, columnType);

        }

        return columnMetadataMap;

    }

    public List<String> getListOfColumnNames(String tableName) {
        Map<String, String> columnMetadataMap = null;
        try {
            columnMetadataMap = this.getTableColumnMetadata(tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        List<String> listOfColumns = new ArrayList<String>(columnMetadataMap.keySet());
        return listOfColumns;

    }

    /**
     * Get the number of columns in a table
     *
     * @param tableName
     * @return
     * @throws SQLException
     */
    public int getNumberofColumnsPerTable(String tableName) throws SQLException {
        Connection connection = this.dcp.getConnection();
        int columncCount = 0;
        DatabaseMetaData meta = connection.getMetaData();
        String catalog = null;
        String schemaPattern = null;
        String tableNamePattern = tableName;
        String columnNamePattern = null;

        ResultSet result = meta.getColumns(catalog, schemaPattern,
                tableNamePattern, columnNamePattern);

        Map<String, String> columnMetadataMap = new HashMap<String, String>();
        while (result.next()) {
            columncCount++;

        }

        connection.close();
        return columncCount;

    }


    /**
     * Calculate the CRC checksum of a complete table. Return a SH1 hash of the appended CRC string
     *
     * @param tableName
     * @return
     */
    public String calculateCRCofTable(String tableName) {
        String appendedChecksum = "";
        String hashedCRC = "";
        String currentDatabaseName = this.dcp.getDataBaseName();
        List<String> listOfColumns = this.getListOfColumnNames(tableName);
        String sql = "SELECT ";
        String prefix = "sum(crc32(" + currentDatabaseName + "." + tableName + ".";
        String suffix = "))";
        String checkSumSQLString = Helpers.commaSeparatedStringWithPrefixAndSuffix(listOfColumns, prefix, suffix);
        sql += checkSumSQLString;
        sql += " FROM " + currentDatabaseName + "." + tableName;
        String primaryKeysString = Helpers.commaSeparatedString(this.getPrimaryKeyFromTable(tableName));
        sql += " ORDER BY " + primaryKeysString;
        this.logger.info(sql);

        Connection connection = this.dcp.getConnection();

        ResultSet rs = null;
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            rs = preparedStatement.executeQuery();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    appendedChecksum += rs.getString(i);
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.logger.info(appendedChecksum);
        try {
            hashedCRC = this.calculateSHA1HashFromString(appendedChecksum);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        this.logger.info("Hashed checksum " + hashedCRC);
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hashedCRC;
    }

    /**
     * Get the primary key from the table
     * MySQL Specific!!
     *
     * @return
     */
    public List<String> getPrimaryKeyFromTable(String tableName) {
        List<String> primaryKeys;
        String primaryKey;
        primaryKeys = new ArrayList<String>();

        String currentDatabaseName = this.dcp.getDataBaseName();
        String sql = "SHOW KEYS FROM " + currentDatabaseName + "." + tableName + " WHERE Key_name = 'PRIMARY'";
        int numberOfRecords = -1;
        Connection connection = this.dcp.getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                primaryKey = rs.getString("Column_name");
                primaryKeys.add(primaryKey);
                this.logger.info("new primary key added to list: " + primaryKey);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return primaryKeys;

    }

    /**
     * Calculate SHA1 hash from input
     *
     * @param inputString
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String calculateSHA1HashFromString(String inputString)
            throws NoSuchAlgorithmException {
        MessageDigest crypto = null;
        try {
            crypto = MessageDigest.getInstance("SHA-1");
            crypto.reset();
            crypto.update(inputString.getBytes("utf8"));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        String hash = DigestUtils.sha1Hex(crypto.digest());
        return hash;

    }

    /**
     * Check the table metadata if there is a column with the name "rowHash"
     *
     * @param tableName
     * @return
     */
    public boolean hasAppendedHashColumn(String tableName) {
        List<String> listOfColumns = this.getListOfColumnNames(tableName);
        if (listOfColumns.contains((String) "rowHash")) {
            this.logger.info("Table already has a row hash column");
            return true;
        } else {
            this.logger.info("Table already has no hash column");
            return false;
        }

    }

    /**
     * Execute a simple query
     *
     * @param sqlString
     * @return
     */
    public ResultSet executeQuery(String sqlString) {
        Connection connection = this.dcp.getConnection();
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;
        try {
            preparedStatement = connection.prepareStatement(sqlString);
            rs = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return rs;

        }

    }
}
