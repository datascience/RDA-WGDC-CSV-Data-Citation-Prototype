package at.stefanproell.PersistentIdentifierMockup;

import java.util.Date;

/**
 * Interface for time stamps
 */
public interface TimeStamped {
    public Date getCreatedDate();
    public void setCreatedDate(Date createdDate);
    public Date getLastUpdatedDate();
    public void setLastUpdatedDate(Date lastUpdatedDate);
    public char getWasUpdated();
    public void setWasUpdated(char wasUpdated);

}