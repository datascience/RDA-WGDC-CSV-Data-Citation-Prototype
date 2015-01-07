package at.stefanproell.PersistentIdentifierMockup;

import org.hibernate.envers.Audited;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.logging.Logger;

/**
 * Identifier consisting of numbers
 */
@Entity
@Table(name = "persistent_identifier")
@DiscriminatorValue("N")
@Audited
public class PersistentIdentifierNumeric extends PersistentIdentifier implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int numericPIDlength;
    private Logger logger;
    private final String propertiesFileName = "pid.properties";

    /**
     *
     */
    public PersistentIdentifierNumeric() {
        super();
        this.logger = Logger.getLogger(PersistentIdentifierNumeric.class.getName());


    }

    @Override
    public void setOrganization(Organization organization) {
        super.setOrganization(organization);
        this.numericPIDlength = organization.getNumericPIDlength();

    }


    /**
     * Initialize the identifier. If no organization was specified, read from the default property file
     */
    @Override
    public void generateIdentifierString() {


        PIGenerator pig = new PIGenerator();
        this.setIdentifier(pig.getRandomAlpaString(this.numericPIDlength));
        if (this.numericPIDlength <= 0) {
            this.logger.severe("No length specified. Set organization first!. Reading from property file");
            this.numericPIDlength = Helpers.getIntegerParameterFromPropertyFile(propertiesFileName, "numericPIDlength");

        }
        String identifier = (pig.getRandomNumeric(this.numericPIDlength));
        this.logger.info("new identifier is" + identifier);
        this.setIdentifier(identifier);

    }


}
