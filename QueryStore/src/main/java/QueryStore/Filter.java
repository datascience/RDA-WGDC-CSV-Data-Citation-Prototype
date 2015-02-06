
package QueryStore;

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Audited
@Table(name = "filter")
public class Filter implements Serializable {
    private Long filterId;

    @ManyToOne(fetch = FetchType.LAZY)
    public Query getQuery() {
        return query;
    }

    public void setQuery(Query query) {
        this.query = query;
    }

    private Query query;
    private String filterName;
    private String filterValue;
    private int filterSequence;


    public Filter() {

    }

    public Filter(Query q) {
        this.query = q;
    }

    public Filter(Query q, String filterName, String filterValue) {
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
