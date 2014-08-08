package QueryStore;

import java.util.Date;

/**
 * Interface for time stamps
 */
public interface TimeStamped {
    public Date getCreatedDate();
    public void setCreatedDate(Date createdDate);
    public Date getLastUpdatedDate();
    public void setLastUpdatedDate(Date lastUpdatedDate);


}