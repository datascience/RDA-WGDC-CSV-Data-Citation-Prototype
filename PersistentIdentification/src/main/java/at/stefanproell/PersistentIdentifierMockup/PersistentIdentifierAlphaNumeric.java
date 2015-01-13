package at.stefanproell.PersistentIdentifierMockup;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Identifier consisting of alpha numeric symbols
 */
@Entity
@Table(name = "persistent_identifier")
@DiscriminatorValue("AN")
@Audited
public class PersistentIdentifierAlphaNumeric extends PersistentIdentifier implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int alphanumericPIDlength;
    private Logger logger;
    private final String propertiesFileName = "pid.properties";


    /**
     *
     */
    public PersistentIdentifierAlphaNumeric() {

        this.logger = Logger.getLogger(PersistentIdentifierAlphaNumeric.class.getName());


    }

    @Override
    public void setOrganization(Organization organization) {
        super.setOrganization(organization);
        this.alphanumericPIDlength = organization.getAlphanumericPIDlength();

    }


    /**
     * Initialize the identifier. If no organization was specified, read from the default property file
     */
    @Override
    public void generateIdentifierString() {


        PIGenerator pig = new PIGenerator();
        if (this.alphanumericPIDlength <= 0) {
            this.logger.severe("No length specified. Set organization first!. Reading from property file");
            this.alphanumericPIDlength = Helpers.getIntegerParameterFromPropertyFile(propertiesFileName,
                    "alphaNumericPIDlength");

        }
        String identifier = (pig.getRandomAlpaNumericString(this.alphanumericPIDlength));
        this.logger.info("new identifier is" + identifier);
        this.setIdentifier(identifier);

    }


}
