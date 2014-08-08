package at.stefanproell.PersistentIdentifierMockup;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;
import java.util.logging.Logger;

/**
 * Identifier consisting of letters
 */
@Entity
@Table(name = "persistent_identifier")
@DiscriminatorValue("A")
public class PersistentIdentifierAlpha extends PersistentIdentifier implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int alphaPIDlength;
    private Logger logger;
    private final String propertiesFileName="pid.properties";


    /**
     *
     */
    public PersistentIdentifierAlpha() {

        super();
        this.logger = logger.getLogger(PersistentIdentifierAlpha.class.getName());


        // TODO Auto-generated constructor stub
    }

    @Override
    public void setOrganization(Organization organization) {
        super.setOrganization(organization);
        this.alphaPIDlength = organization.getAlphaPIDlength();

    }

    /**
     * Initialize the identifier. If no organization was specified, read from the default property file
     */
    @Override
    public void generateIdentifierString() {


        PIGenerator pig = new PIGenerator();
        this.setIdentifier(pig.getRandomAlpaString(this.alphaPIDlength));
        if(this.alphaPIDlength<=0){
            this.logger.severe("No length specified. Set organization first!. Reading from property file");
            this.alphaPIDlength = Helpers.getIntegerParameterFromPropertyFile(propertiesFileName,"alphaPIDlength");

        }
        String identifier =(pig.getRandomAlpaNumericString(this.alphaPIDlength));
        this.logger.info("new identifier is" + identifier);
        this.setIdentifier(identifier);

    }




}
