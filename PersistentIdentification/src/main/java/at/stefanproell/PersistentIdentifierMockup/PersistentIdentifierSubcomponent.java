package at.stefanproell.PersistentIdentifierMockup;

import org.hibernate.annotations.*;
import org.hibernate.envers.Audited;


import javax.persistence.*;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import java.util.Date;
import java.util.logging.Logger;


/**
 * Generalized Persistent Identifier
 * This is the generalized persistent parentIdentifier class which is inherited by the specialized identifiers.
 * It uses a discriminator value in order to differentiate between the specialized identifiers.
 */
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "IdentifierTypes",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue(value = "subPID")
// Dynamic upate for persistent identifiers
@DynamicUpdate
@Audited
public class PersistentIdentifierSubcomponent extends PersistentIdentifier implements java.io.Serializable, TimeStamped {

    private Logger logger;
    private final String propertiesFileName = "pid.properties";
    private int subPIDlength;

    @ManyToOne(cascade = {CascadeType.ALL})
    @JoinColumn(name = "parent_identifier_id")
    @NotFound(action = NotFoundAction.IGNORE)
    public PersistentIdentifier getParentIdentifier() {
        return parentIdentifier;
    }

    public void setParentIdentifier(PersistentIdentifier parentIdentifier) {
        this.parentIdentifier = parentIdentifier;
    }

    private PersistentIdentifier parentIdentifier;


    public PersistentIdentifierSubcomponent() {
        super();
        this.logger = Logger.getLogger(PersistentIdentifier.class.getName());
    }


    /**
     * Initialize the identifier. If no organization was specified, read from the default property file
     */
    @Override
    public void generateIdentifierString() {


        PIGenerator pig = new PIGenerator();
        if (this.subPIDlength <= 0) {
            this.logger.severe("No length specified. Set organization first!. Reading from property file");
            this.subPIDlength = Helpers.getIntegerParameterFromPropertyFile(propertiesFileName, "subcomponent");

        }
        String identifier = (pig.getRandomAlpaNumericString(this.subPIDlength));
        this.logger.info("new identifier is" + identifier);
        this.setIdentifier(identifier);

    }


}
