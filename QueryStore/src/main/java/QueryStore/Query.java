package QueryStore;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.logging.Logger;


@Entity
@Audited
@Table(name = "query", uniqueConstraints = {@UniqueConstraint(columnNames = {"PID","queryHash"})})
public class Query implements Serializable, TimeStamped {

    private Logger logger;

    private Long queryId;
    private Date execution_timestamp;
    private String userName;
    private String PID;
    private String queryDescription;
    private String datasourcePID;
    private String queryHash;

    private Date createdDate;
    private Date lastUpdatedDate;



    @Column(name = "resultSetHash", unique = true)
    protected String getResultSetHash() {
        return resultSetHash;
    }

    protected void setResultSetHash(String resultSetHash) {
        this.resultSetHash = resultSetHash;
    }

    private String resultSetHash;


    private Set<Filter> filters = new LinkedHashSet<Filter>();
    private Set<Sorting> sortings = new LinkedHashSet<Sorting>();


    protected Query() {
        this.logger = Logger.getLogger(this.getClass().getName());

    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "query_id")
    protected Long getQueryId() {
        return queryId;
    }


    protected void setQueryId(Long queryId) {
        this.queryId = queryId;
    }


    protected Date getExecution_timestamp() {
        return execution_timestamp;
    }


    protected void setExecution_timestamp(Date execution_timestamp) {
        this.execution_timestamp = execution_timestamp;
    }


    @Column(name = "user_name", unique = false, nullable = false)
    protected String getUserName() {
        return userName;
    }


    protected void setUserName(String userName) {
        this.userName = userName;
    }


    @Column(name = "PID", unique = true, nullable = false)
    protected String getPID() {
        return PID;
    }


    protected void setPID(String pID) {
        PID = pID;
    }


    protected String getQueryDescription() {
        return queryDescription;
    }


    protected void setQueryDescription(String queryDescription) {
        this.queryDescription = queryDescription;
    }


    @Column(name = "data_source", unique = false)
    protected String getDatasourcePID() {
        return datasourcePID;
    }


    protected void setDatasourcePID(String datasourcePID) {
        this.datasourcePID = datasourcePID;
    }


    /**
     * Get query hash. The query hash does not necessarily have to be unique, as identical queries can be
     * issued at different times.
     * @return
     */
    @Column(name = "queryHash", unique = false)
    protected String getQueryHash() {
        return queryHash;
    }


    protected void setQueryHash(String queryHash) {
        this.queryHash = queryHash;
    }


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "query")
    protected Set<Filter> getFilters() {
        return this.filters;
    }

    protected void setFilters(Set<Filter> filters) {
        this.filters = filters;
    }

    protected void setStockDailyRecords(Set<Filter> filters) {
        this.filters = filters;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "query")
    protected Set<Sorting> getSortings() {
        return sortings;
    }

    protected void setSortings(Set<Sorting> sortings) {
        this.sortings = sortings;
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

