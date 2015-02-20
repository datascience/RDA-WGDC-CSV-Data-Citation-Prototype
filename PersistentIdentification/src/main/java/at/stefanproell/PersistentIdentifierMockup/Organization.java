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

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Organization entity.
 * An organization has a name and a prefix. The prefix needs to be unique within the database.
 * Each organization can set the default values for the length of the identifiers individually.
 * If no values are specified, the default values are read from the property files.
 */
@Entity
@Table(name = "organization", uniqueConstraints = {@UniqueConstraint(columnNames = {"organization_id",
        "organization_prefix"})})
@Audited
public class Organization implements TimeStamped {


    private int organization_id;
    private int organization_prefix;
    private int alphaPIDlength;
    private int alphanumericPIDlength;
    private int numericPIDlength;


    private Date createdDate;
    private Date lastUpdatedDate;
    private char wasUpdated;

    private String organization_name;
    private Set<PersistentIdentifier> persistentIdentifiers = new HashSet<PersistentIdentifier>(0);
    private Logger logger;

    /**
     *
     */
    public Organization() {
        super();
        this.logger = Logger.getLogger(Organization.class.getName());
    }

    /**
     * @param organization_prefix
     * @param organization_name
     */
    public Organization(String organization_name, int organization_prefix) {
        super();
        System.out.println("Org: " + organization_name + " prefix " + organization_prefix);
        this.organization_prefix = organization_prefix;
        this.organization_name = organization_name;
        this.logger = Logger.getLogger(Organization.class.getName());
        // set default values
        this.setParametersFromPropertiesFile();
    }


    /**
     * @param organization_prefix
     * @param alphaPIDlength
     * @param alphanumericPIDlength
     * @param numericPIDlength
     * @param organization_name
     * @param persistentIdentifiers
     */
    public Organization(int organization_prefix,
                        int alphaPIDlength, int alphanumericPIDlength,
                        int numericPIDlength, String organization_name,
                        Set<PersistentIdentifier> persistentIdentifiers) {
        super();
        this.logger = Logger.getLogger(Organization.class.getName());
        this.organization_prefix = organization_prefix;
        this.alphaPIDlength = alphaPIDlength;
        this.alphanumericPIDlength = alphanumericPIDlength;
        this.numericPIDlength = numericPIDlength;
        this.organization_name = organization_name;
        this.persistentIdentifiers = persistentIdentifiers;
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "organization_id", unique = true, nullable = false)
    public int getOrganization_id() {
        return organization_id;
    }

    public void setOrganization_id(int organization_id) {
        this.organization_id = organization_id;
    }

    @Column(name = "organization_prefix", unique = true, nullable = false)
    public int getOrganization_prefix() {
        return organization_prefix;
    }

    public void setOrganization_prefix(int organization_prefix) {
        this.organization_prefix = organization_prefix;
    }

    @Column(name = "organization_name", unique = false, nullable = false)
    public String getOrganization_name() {
        return organization_name;
    }

    public void setOrganization_name(String organization_name) {
        this.organization_name = organization_name;
    }

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "organization")
    public Set<PersistentIdentifier> getPersistentIdentifiers() {
        return persistentIdentifiers;
    }

    public void setPersistentIdentifiers(
            Set<PersistentIdentifier> persistentIdentifiers) {
        this.persistentIdentifiers = persistentIdentifiers;
    }

    @Column(name = "alphaPIDlength", unique = false, nullable = false)
    public int getAlphaPIDlength() {
        return alphaPIDlength;
    }

    public void setAlphaPIDlength(int alphaPIDlength) {
        this.alphaPIDlength = alphaPIDlength;
    }

    @Column(name = "alphanumericPIDlength", unique = false, nullable = false)
    public int getAlphanumericPIDlength() {
        return alphanumericPIDlength;
    }

    public void setAlphanumericPIDlength(int alphanumericPIDlength) {
        this.alphanumericPIDlength = alphanumericPIDlength;
    }

    @Column(name = "numericPIDlength", unique = false, nullable = false)
    public int getNumericPIDlength() {
        return numericPIDlength;
    }

    public void setNumericPIDlength(int numericPIDlength) {
        this.numericPIDlength = numericPIDlength;
    }

    /**
     * Read the parameters for the standard settings from the property files.
     */
    private void setParametersFromPropertiesFile() {

        String filename = "pid.properties";
        Properties prop = null;

        prop = PropertyHelpers.readPropertyFile(filename);

        if (prop == null) {
            this.logger.severe("Property filew as null");
        } else {
            PropertyHelpers.printPropertiesFile(filename);
            this.alphanumericPIDlength = Integer.parseInt(prop.getProperty("alphaNumericPIDlength"));
            this.numericPIDlength = Integer.parseInt(prop.getProperty("numericPIDlength"));
            this.alphaPIDlength = Integer.parseInt(prop.getProperty("alphaPIDlength"));
            this.logger.info("Read property file. Values: " + this.alphanumericPIDlength + "  " + this
                    .numericPIDlength + " " + this.alphaPIDlength);
        }


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
        this.lastUpdatedDate = lastUpdatedDate;

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
