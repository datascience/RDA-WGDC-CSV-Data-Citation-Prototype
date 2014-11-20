/*
 * Copyright [2014] [Stefan Pröll]
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

import org.mindrot.jbcrypt.BCrypt;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import static javax.persistence.GenerationType.IDENTITY;

/**
 * Created by stefan on 18.11.14.
 */
@Entity
@Table(name = "user", uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})})
public class User implements TimeStamped {
    private int user_id;
    private String username;
    private String password;
    private Date createdDate;
    private Date lastUpdatedDate;
    private char wasUpdated;
    private Set<Organization> organizations = new HashSet<Organization>(0);
    private Logger logger;

    public User(String username, String plaintextPassword) {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.username = username;
        this.password = BCrypt.hashpw(plaintextPassword, BCrypt.gensalt());
        this.logger.warning("Stored password: " + plaintextPassword + " as hash: " + this.password);


    }

    public User() {
        super();
    }

    /**
     * Check the password
     *
     * @param candidatePassword
     * @return
     */
    private boolean checkpassword(String candidatePassword) {
        if (BCrypt.checkpw(candidatePassword, this.getPassword())) {
            this.logger.info("It matches");
            return true;
        } else {
            this.logger.info("It does NOT match");
            return false;
        }
    }

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id", unique = true, nullable = false)
    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    @Column(name = "username", unique = true, nullable = false)
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Column(name = "password", unique = false, nullable = false)
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "user")
    public Set<Organization> getOrganizations() {
        return organizations;
    }

    public void setOrganizations(Set<Organization> organizations) {
        this.organizations = organizations;
    }


}
