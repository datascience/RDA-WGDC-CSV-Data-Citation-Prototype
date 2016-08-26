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

    @Column(name="amountOfColumns")
    private int amountOfColumns;
    @Column(name="amountOfRecords")
    private int amountOfRecords;
    @Column(name="amountOfCsvFiles")
    private int amountOfCsvFiles;
    @Column(name="amountOfOperations")
    private int amountOfOperations;

    @Column(name="selectProportion")
    private double selectProportion;
    @Column(name="insertProportion")
    private double insertProportion;
    @Column(name="updateProportion")
    private double updateProportion;
    @Column(name="deleteProportion")
    private double deleteProportion;

    @Column(name="runName")
    private String runName;
    @Column(name="evaluationMachine")
    private String evaluationMachineName;

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

    public int getAmountOfColumns() {
        return amountOfColumns;
    }

    public void setAmountOfColumns(int amountOfColumns) {
        this.amountOfColumns = amountOfColumns;
    }

    public int getAmountOfRecords() {
        return amountOfRecords;
    }

    public void setAmountOfRecords(int amountOfRecords) {
        this.amountOfRecords = amountOfRecords;
    }

    public int getAmountOfCsvFiles() {
        return amountOfCsvFiles;
    }

    public void setAmountOfCsvFiles(int amountOfCsvFiles) {
        this.amountOfCsvFiles = amountOfCsvFiles;
    }

    public int getAmountOfOperations() {
        return amountOfOperations;
    }

    public void setAmountOfOperations(int amountOfOperations) {
        this.amountOfOperations = amountOfOperations;
    }

    public double getSelectProportion() {
        return selectProportion;
    }

    public void setSelectProportion(double selectProportion) {
        this.selectProportion = selectProportion;
    }

    public double getInsertProportion() {
        return insertProportion;
    }

    public void setInsertProportion(double insertProportion) {
        this.insertProportion = insertProportion;
    }

    public double getUpdateProportion() {
        return updateProportion;
    }

    public void setUpdateProportion(double updateProportion) {
        this.updateProportion = updateProportion;
    }

    public double getDeleteProportion() {
        return deleteProportion;
    }

    public void setDeleteProportion(double deleteProportion) {
        this.deleteProportion = deleteProportion;
    }

    public String getRunName() {
        return runName;
    }

    public void setRunName(String runName) {
        this.runName = runName;
    }

    public String getEvaluationMachineName() {
        return evaluationMachineName;
    }

    public void setEvaluationMachineName(String evaluationMachineName) {
        this.evaluationMachineName = evaluationMachineName;
    }
}
