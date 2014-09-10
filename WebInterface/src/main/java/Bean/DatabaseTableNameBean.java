
package Bean;


import Database.DatabaseTools;
import org.hibernate.Session;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created by stefan on 04.07.14.
 */
@ManagedBean
@SessionScoped
public class DatabaseTableNameBean implements Serializable {

    private Session session = null;
    private Logger logger;

    public DatabaseTableNameBean() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Database Name Bean");
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public List<String> getDatabaseNames() {
        return databaseNames;
    }

    public void setDatabaseNames(List<String> databaseNames) {
        this.databaseNames = databaseNames;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<String> getTableNames() {
        return tableNames;
    }

    public void setTableNames(List<String> tableNames) {
        this.tableNames = tableNames;
    }

    private String databaseName;
    private List<String> databaseNames;
    private String tableName;
    private List<String> tableNames;

    private DatabaseTools dbtools;

    @PostConstruct
    public void init() {
        try {

            dbtools = new DatabaseTools();
            databaseNames = dbtools.getAvailableDatabases();
            //databaseNames = new ArrayList<String>();
            //databaseNames.add("Test hard coedd");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


    }

    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeDatabaseName(ValueChangeEvent event) {
        this.logger.info(event.getComponent().toString() + " " + event.toString());

        String selectedDB = event.getNewValue().toString();
        this.logger.info("Databasename = " + selectedDB);

        this.storeSessionData("currentDatabaseName", selectedDB);

        this.tableNames = this.dbtools.getAvailableTablesFromDatabase(selectedDB);


    }

    /**
     * React on change
     *
     * @param event
     */
    public void handleChangeTableName(ValueChangeEvent event) {
        this.logger.info(event.getComponent().toString() + " " + event.toString());

        String selectedDB = event.getNewValue().toString();
        this.logger.info("Databasename = " + selectedDB);

        this.storeSessionData("currentDatabaseName", selectedDB);

    }


    /**
     * Store details in session
     */
    private void storeSessionData(String key, String value) {
        System.out.println("Session data function");


        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        // schreiben

        session.put(key, value);


    }


}
