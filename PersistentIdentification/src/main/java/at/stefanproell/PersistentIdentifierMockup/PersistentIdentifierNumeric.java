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
            this.numericPIDlength = PropertyHelpers.getIntegerParameterFromPropertyFile(propertiesFileName,
                    "numericPIDlength");

        }
        String identifier = (pig.getRandomNumeric(this.numericPIDlength));
        this.logger.info("new identifier is" + identifier);
        this.setIdentifier(identifier);

    }


}
