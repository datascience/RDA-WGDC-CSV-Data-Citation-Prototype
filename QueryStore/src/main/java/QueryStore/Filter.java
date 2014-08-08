package QueryStore;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Table(name = "filter")
class Filter implements Serializable {
    private Long filterId;
    private Query query;
    private String filterName;
    private String filterValue;
    private int filterSequence;


    protected Filter() {

    }

    protected Filter(Query q) {
        this.query = q;
    }

    protected Filter(Query q, String filterName, String filterValue) {
        this.query = q;
        this.filterName = filterName;
        this.filterValue = filterValue;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "filter_id", unique = true, nullable = false)
    protected Long getFilterId() {
        return filterId;
    }

    protected void setFilterId(Long filterId) {
        this.filterId = filterId;
    }


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "query_id")
    protected Query getQuery() {
        return query;
    }

    protected void setQuery(Query query) {
        this.query = query;
    }

    @Column(name = "filterName")
    protected String getFilterName() {
        return filterName;
    }

    protected void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    @Column(name = "filterValue")
    protected String getFilterValue() {
        return filterValue;
    }

    protected void setFilterValue(String filterValue) {
        this.filterValue = filterValue;
    }

    @Column(name = "filter_sequence")
    protected int getFilterSequence() {
        return filterSequence;
    }

    protected void setFilterSequence(int filterSequence) {
        this.filterSequence = filterSequence;
    }
}
