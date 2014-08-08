package at.stefanproell.PersistentIdentifierMockup;

import com.sun.istack.internal.NotNull;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;


/**
 * Generalized Persistent Identifier
 * This is the generalized persistent identifier class which is inherited by the specialized identifiers.
 * It uses a discriminator value in order to differentiate between the specialized identifiers.
 */
@Entity
@Table(name = "persistent_identifier", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "identifier"})
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "IdentifierTypes",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue(value = "PID")
// Dynamic upate for persistent identifiers
@DynamicUpdate
public class PersistentIdentifier implements java.io.Serializable, TimeStamped {
    /**
     *
     */
    private static final long serialVersionUID = -8814833389023520908L;
    private Logger logger;
    private long persitentIdentifier_id;

    private String URI;
    private String identifier;
    private Organization organization;

    private Date createdDate;
    private Date lastUpdatedDate;



   private char wasUpdated;

    public PersistentIdentifier() {
        super();
        this.logger = Logger.getLogger(PersistentIdentifier.class.getName());

    }

    /**
     * The internal id of each identifier.
     * @return
     */
    @Id
    @GeneratedValue
    @Column(name = "persistent_identifier_id")
    public long getPersitentIdentifier_id() {
        return persitentIdentifier_id;
    }

    public void setPersitentIdentifier_id(long persitentIdentifier_id) {
        this.persitentIdentifier_id = persitentIdentifier_id;
    }


    @Column(name = "uri")
    public String getURI() {
        return URI;
    }

    public void setURI(String uRI) {
        URI = uRI;
    }

    @Column(name = "identifier")
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    public Organization getOrganization() {
        return this.organization;
    }

    public void setOrganization(Organization organization) {
        this.organization = organization;
    }


    /**
     * Generate a new identifier.
     */
    public void generateIdentifierString() {
        this.logger.info("Generate a new identifier.");

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

        this.lastUpdatedDate = this.lastUpdatedDate;

    }

    @Override
    public char getWasUpdated() {
        return this.wasUpdated;
    }

    @Override
    public void setWasUpdated(char wasUpdated) {
        this.wasUpdated = wasUpdated;

    }
}
