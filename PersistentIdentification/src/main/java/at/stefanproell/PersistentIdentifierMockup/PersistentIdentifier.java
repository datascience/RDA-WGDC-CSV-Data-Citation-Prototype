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

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;


/**
 * Generalized Persistent Identifier
 * This is the generalized persistent identifier class which is inherited by the specialized identifiers.
 * It uses a discriminator value in order to differentiate between the specialized identifiers.
 */
@Entity
@Table(name = "persistent_identifier", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"organization_id", "identifier", "parent_identifier_id"})
})
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(
        name = "IdentifierTypes",
        discriminatorType = DiscriminatorType.STRING
)
@DiscriminatorValue(value = "PID")
// Dynamic upate for persistent identifiers
@DynamicUpdate
@Audited
public class PersistentIdentifier implements java.io.Serializable, TimeStamped {
    /**
     *
     */
    private Logger logger;
    private long persitentIdentifier_id;

    private String URI;
    private String identifier;

    /**
     * Get the fully qualified identifier name. This is a redundant field and denormalizes the database for
     * performance reasons
     *
     * @return
     */
    @Column(name = "fqn_identifier", unique = true)
    public String getFQNidentifier() {
        return FQNidentifier;
    }

    public void setFQNidentifier(String FQNidentifier) {
        this.FQNidentifier = FQNidentifier;
    }

    // fully qualified identifier
    private String FQNidentifier;
    private Organization organization;

    private Date createdDate;
    private Date lastUpdatedDate;

    @OneToMany(mappedBy = "parentIdentifier")
    @NotFound(action = NotFoundAction.IGNORE)
    public List<PersistentIdentifierSubcomponent> getSubcomponents() {
        return subcomponents;
    }

    public void setSubcomponents(List<PersistentIdentifierSubcomponent> subcomponents) {
        this.subcomponents = subcomponents;
    }

    private List<PersistentIdentifierSubcomponent> subcomponents;

    private char wasUpdated;

    public PersistentIdentifier() {
        super();
        this.logger = Logger.getLogger(PersistentIdentifier.class.getName());

    }

    /**
     * The internal id of each identifier.
     *
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
