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

import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */

@Entity
@Audited
@Table(name = "base_table")
public class BaseTable implements Serializable {


    private String baseTablePID;
    private String author;
    private String description;
    private String baseDatabase;
    private String baseTableName;
    private String dataSetTitle;

    private Integer baseTableId;
    private int organizationalId;
    private List<Query> queryList;


    @OneToMany(cascade = CascadeType.ALL, mappedBy = "baseTable")
    public List<Query> getQuery() {
        return queryList;
    }

    public void setQuery(List<Query> list) {
        this.queryList = list;
    }



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "base_table_id", unique = true, nullable = false)
    public Integer getBaseTableId() {
        return this.baseTableId;
    }

    public void setBaseTableId(int baseTableId) {
        this.baseTableId = baseTableId;
    }

    @Column(name = "baseDatabase")
    public String getBaseDatabase() {
        return baseDatabase;
    }

    public void setBaseDatabase(String baseDatabase) {
        this.baseDatabase = baseDatabase;
    }
    

    public BaseTable() {
    }

    @Column(name = "baseTablePid")
    public String getBaseTablePID() {
        return baseTablePID;
    }

    public void setBaseTablePID(String baseTablePID) {
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

    @Column(name = "baseTableName", nullable = false)
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

    @Column(name = "dataSetTitle")
    public String getDataSetTitle() {
        return dataSetTitle;
    }

    public void setDataSetTitle(String dataSetTitle) {
        this.dataSetTitle = dataSetTitle;
    }
}
