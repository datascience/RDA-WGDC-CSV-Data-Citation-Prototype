/*
 * Copyright [2016] [Stefan Pröll]
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

/*
 * Copyright [2016] [Stefan Pröll]
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

package DatabaseBackend;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
@Entity
@Table(name = "EvaluationRecordBean")
public class EvaluationRecordBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "evalRecordId")
    private Integer evalRecordId;

    @Id
    @Column(name="operationCount", nullable = false)
    private Integer operationCount;

    @Column(name = "sqlQuery", length = 5000)
    private String sqlQuery;


    @Column(name = "gitQuery", length = 1000)
    private String gitQuery;

    @Column(name = "queryType")
    private String queryType;

    @Column(name = "queryComplexity")
    private String queryComplexity;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startTimestampSQL",columnDefinition="DATETIME(6) NOT NULL")
    private Date startTimestampSQL;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endTimestampSQL",columnDefinition="DATETIME(6) NOT NULL")
    private Date endTimestampSQL;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startTimestampGit",columnDefinition="DATETIME(6) NOT NULL")
    private Date startTimestampGit;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endTimestampGit",columnDefinition="DATETIME(6) NOT NULL")
    private Date endTimestampGit;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "runID", nullable = false)
    private EvaluationRunBean evaluationRunBean;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "reExecutionDate",columnDefinition="DATETIME(6)")
    private Date reExecutionDate;

    @Column(name = "gitFolderSizeInBytes")
    private int gitFolderSizeInBytes;

    @Column(name = "sqlDBSizeInBytes")
    private int sqlDBSizeInBytes;


    public EvaluationRecordBean() {
    }

    public EvaluationRecordBean(String sqlQuery, String gitQuery, String queryType, Date
            startTimestampSQL, Date endTimestampSQL, Date startTimestampGit, Date endTimestampGit) {
        this.evalRecordId = evalRecordId;
        this.sqlQuery = sqlQuery;
        this.gitQuery = gitQuery;
        this.queryType = queryType;
        this.startTimestampSQL = startTimestampSQL;
        this.endTimestampSQL = endTimestampSQL;
        this.startTimestampGit = startTimestampGit;
        this.endTimestampGit = endTimestampGit;
    }

    public Integer getEvalRecordId() {
        return evalRecordId;
    }

    public void setEvalRecordId(Integer evalRecordId) {
        this.evalRecordId = evalRecordId;
    }

    public String getSqlQuery() {
        return sqlQuery;
    }

    public void setSqlQuery(String sqlQuery) {
        this.sqlQuery = sqlQuery;
    }

    public String getGitQuery() {
        return gitQuery;
    }

    public void setGitQuery(String gitQuery) {
        this.gitQuery = gitQuery;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public Date getStartTimestampSQL() {
        return startTimestampSQL;
    }

    public void setStartTimestampSQL(Date startTimestampSQL) {
        this.startTimestampSQL = startTimestampSQL;
    }

    public Date getEndTimestampSQL() {
        return endTimestampSQL;
    }

    public void setEndTimestampSQL(Date endTimestampSQL) {
        this.endTimestampSQL = endTimestampSQL;
    }

    public Date getStartTimestampGit() {
        return startTimestampGit;
    }

    public void setStartTimestampGit(Date startTimestampGit) {
        this.startTimestampGit = startTimestampGit;
    }

    public Date getEndTimestampGit() {
        return endTimestampGit;
    }

    public void setEndTimestampGit(Date endTimestampGit) {
        this.endTimestampGit = endTimestampGit;
    }

    public String getQueryComplexity() {
        return queryComplexity;
    }

    public void setQueryComplexity(String queryComplexity) {
        this.queryComplexity = queryComplexity;
    }


    public Date getReExecutionDate() {
        return reExecutionDate;
    }

    public void setReExecutionDate(Date reExecutionDate) {
        this.reExecutionDate = reExecutionDate;
    }

    public EvaluationRunBean getEvaluationRunBean() {
        return evaluationRunBean;
    }




    public int getGitFolderSizeInBytes() {
        return gitFolderSizeInBytes;
    }

    public void setGitFolderSizeInBytes(int gitFolderSizeInBytes) {
        this.gitFolderSizeInBytes = gitFolderSizeInBytes;
    }

    public int getSqlDBSizeInBytes() {
        return sqlDBSizeInBytes;
    }

    public void setSqlDBSizeInBytes(int sqlDBSizeInBytes) {
        this.sqlDBSizeInBytes = sqlDBSizeInBytes;
    }

    public Integer getOperationCount() {
        return operationCount;
    }

    public void setOperationCount(Integer operationCount) {
        this.operationCount = operationCount;
    }

    public void setEvaluationRunBean(EvaluationRunBean evaluationRunBean) {
        this.evaluationRunBean = evaluationRunBean;
    }
}
