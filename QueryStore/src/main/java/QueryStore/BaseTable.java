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

package QueryStore;

import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import org.hibernate.envers.Audited;

import javax.persistence.*;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */

@Entity
@Audited
@Table(name = "filter")
public class BaseTable {
    private int baseID;
    
    private PersistentIdentifier baseTablePID;
    private String author;
    private String description;
    private String baseSchema;
    private String baseTableName;

    

    private int organizationalId;
    
    
    

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "base_id", unique = true, nullable = false)
    public int getBaseID() {
        return baseID;
    }

    public void setBaseID(int baseID) {
        this.baseID = baseID;
    }


    @Column(name = "baseSchema")
    public String getBaseSchema() {
        return baseSchema;
    }

    public void setBaseSchema(String baseSchema) {
        this.baseSchema = baseSchema;
    }
    

    public BaseTable() {
    }

    @Column(name = "base_table_pid", unique = true, nullable = false)
    public PersistentIdentifier getBaseTablePID() {
        return baseTablePID;
    }

    public void setBaseTablePID(PersistentIdentifier baseTablePID) {
        this.baseTablePID = baseTablePID;
    }

    @Column(name = "author", nullable = false)
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Column(name = "description", nullable = false)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "base_table_name", nullable = false)
    public String getBaseTableName() {
        return baseTableName;
    }

    public void setBaseTableName(String baseTableName) {
        this.baseTableName = baseTableName;
    }

    @Column(name = "organizationalId")
    public int getOrganizationalId() {
        return organizationalId;
    }

    public void setOrganizationalId(int organizationalId) {
        this.organizationalId = organizationalId;
    }
}
