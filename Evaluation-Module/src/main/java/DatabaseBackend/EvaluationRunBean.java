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
import java.util.HashSet;
import java.util.Set;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
@Entity
@Table(name = "EvaluationRunBean")
public class EvaluationRunBean implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "runID")
    private Integer runID;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "startDate")
    private Date startDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "endDate")
    private Date endDateDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "evaluationRunBean")
    private Set<EvaluationRecordBean> evaluationRecordSet;

    public EvaluationRunBean() {
        this.startDate = new Date();
        this.evaluationRecordSet = new HashSet<EvaluationRecordBean>();
    }

    public EvaluationRunBean(Integer runID, Date startDate, Date endDateDate) {
        this.runID = runID;
        this.startDate = startDate;
        this.endDateDate = endDateDate;
        this.evaluationRecordSet = new HashSet<EvaluationRecordBean>();
    }

    public Integer getRunID() {
        return runID;
    }

    public void setRunID(Integer runID) {
        this.runID = runID;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDateDate() {
        return endDateDate;
    }

    public void setEndDateDate(Date endDateDate) {
        this.endDateDate = endDateDate;
    }


    public Set<EvaluationRecordBean> getEvaluationRecordSet() {
        if (evaluationRecordSet == null) {
            evaluationRecordSet = new HashSet<EvaluationRecordBean>();
        }
        return evaluationRecordSet;
    }

    public void setEvaluationRecordSet(Set<EvaluationRecordBean> evaluationRecordSet) {
        this.evaluationRecordSet = evaluationRecordSet;
    }

}
