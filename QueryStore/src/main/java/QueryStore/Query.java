

package QueryStore;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.logging.Logger;


@Entity
@Audited
@Table(name = "query", uniqueConstraints = {@UniqueConstraint(columnNames = {"PID", "queryHash"})})
public class Query implements Serializable, TimeStamped {


    Map<Integer, String> selectedColumns = null;
    private List<Filter> filters;
    private List<Sorting> sortings;
    private Logger logger;
    private Long queryId;
    private Date execution_timestamp;
    private String userName;
    private String PID;
    private String queryDescription;
    private String datasourcePID;
    private String queryHash;

    private BaseTable baseTable;

    private String queryString;
    private Date createdDate;
    private Date lastUpdatedDate;
    private String resultSetHash;



    public Query() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.selectedColumns = new HashMap<>();
        this.filters = new LinkedList<Filter>();
        this.sortings = new LinkedList<>();


    }


    @ManyToOne(fetch = FetchType.EAGER)
    public BaseTable getBaseTable() {
        return baseTable;
    }

    public void setBaseTable(BaseTable baseTable) {
        this.baseTable = baseTable;
    }

    @Column(name = "queryString", length = 5000)
    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    @ElementCollection
    public Map<Integer, String> getSelectedColumns() {
        return selectedColumns;
    }

    public void setSelectedColumns(Map<Integer, String> selectedColumns) {
        this.logger.info("Setting the selected columns.");
        if (selectedColumns != null) {
            for (Map.Entry<Integer, String> entry : selectedColumns.entrySet()) {
                this.logger.info("SET COL. : Selected columns Key: " + entry.getKey() + "  Value: " + entry.getValue()
                        .toString());
            }
            this.selectedColumns = selectedColumns;
        }

    }


    @Column(name = "resultSetHash", unique = true)
    protected String getResultSetHash() {
        return resultSetHash;
    }

    protected void setResultSetHash(String resultSetHash) {
        this.resultSetHash = resultSetHash;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "queryId")
    protected Long getQueryId() {
        return queryId;
    }


    protected void setQueryId(Long queryId) {
        this.queryId = queryId;
    }


    protected Date getExecution_timestamp() {
        return execution_timestamp;
    }


    public void setExecution_timestamp(Date execution_timestamp) {
        this.execution_timestamp = execution_timestamp;
    }


    @Column(name = "user_name", unique = false, nullable = false)
    public String getUserName() {
        return userName;
    }


    public void setUserName(String userName) {
        this.userName = userName;
    }


    @Column(name = "PID", unique = true, nullable = false)
    protected String getPID() {
        return PID;
    }


    public void setPID(String pID) {
        PID = pID;
    }


    @Column(name = "query_description", unique = false, nullable = true)
    protected String getQueryDescription() {
        return queryDescription;
    }


    protected void setQueryDescription(String queryDescription) {
        this.queryDescription = queryDescription;
    }


    @Column(name = "data_source", unique = false, nullable = true)
    protected String getDatasourcePID() {
        return datasourcePID;
    }


    public void setDatasourcePID(String datasourcePID) {
        this.datasourcePID = datasourcePID;
    }


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "query")
    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "query")
    public List<Sorting> getSortings() {
        return sortings;
    }

    public void setSortings(List<Sorting> sortings) {
        this.sortings = sortings;
    }


    /**
     * Get query hash. The query hash does not necessarily have to be unique, as identical queries can be
     * issued at different times.
     *
     * @return
     */
    @Column(name = "queryHash", unique = false)
    public String getQueryHash() {
        return queryHash;
    }


    public void setQueryHash(String queryHash) {
        this.queryHash = queryHash;
    }



    @Override
    public Date getCreatedDate() {
        return this.createdDate;
    }

    @Override
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;

    }

    @Override
    public Date getLastUpdatedDate() {
        return this.lastUpdatedDate;
    }

    @Override
    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;

    }
}

