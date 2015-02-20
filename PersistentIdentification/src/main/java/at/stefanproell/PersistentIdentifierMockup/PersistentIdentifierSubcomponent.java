/*
 * Copyright [2015] [Stefan Pröll]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package at.stefanproell.PersistentIdentifierMockup;

import GenericTools.PropertyHelpers;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;

import javax.persistence.*;
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
            this.subPIDlength = PropertyHelpers.getIntegerParameterFromPropertyFile(propertiesFileName, "subcomponent");

        }
        String identifier = (pig.getRandomAlpaNumericString(this.subPIDlength));
        this.logger.info("new identifier is" + identifier);
        this.setIdentifier(identifier);

    }


}
