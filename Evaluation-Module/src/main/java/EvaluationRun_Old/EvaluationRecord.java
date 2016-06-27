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

package EvaluationRun_Old;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class EvaluationRecord {

    private String QueryID;
    private String SQLSystemQuery;
    private String GitSystemQuery;
    private String QueryType;
    private String AffectedIdSystemSequence;
    private String SQLSystemQueryStart;
    private String SQLSystemQueryStop;
    private String GitSystemQueryStart;
    private String GitSystemQueryStop;

    public EvaluationRecord(String SQLSystemQuery, String gitSystemQuery, String queryType, String
            affectedIdSystemSequence, String SQLSystemQueryStart, String SQLSystemQueryStop, String
                                    gitSystemQueryStart, String gitSystemQueryStop) {

        this.SQLSystemQuery = SQLSystemQuery;
        GitSystemQuery = gitSystemQuery;
        QueryType = queryType;
        AffectedIdSystemSequence = affectedIdSystemSequence;
        this.SQLSystemQueryStart = SQLSystemQueryStart;
        this.SQLSystemQueryStop = SQLSystemQueryStop;
        GitSystemQueryStart = gitSystemQueryStart;
        GitSystemQueryStop = gitSystemQueryStop;
    }


    public String getQueryID() {
        return QueryID;
    }

    public void setQueryID(String queryID) {
        QueryID = queryID;
    }

    public String getSQLSystemQuery() {
        return SQLSystemQuery;
    }

    public void setSQLSystemQuery(String SQLSystemQuery) {
        this.SQLSystemQuery = SQLSystemQuery;
    }

    public String getGitSystemQuery() {
        return GitSystemQuery;
    }

    public void setGitSystemQuery(String gitSystemQuery) {
        GitSystemQuery = gitSystemQuery;
    }

    public String getQueryType() {
        return QueryType;
    }

    public void setQueryType(String queryType) {
        QueryType = queryType;
    }

    public String getAffectedIdSystemSequence() {
        return AffectedIdSystemSequence;
    }

    public void setAffectedIdSystemSequence(String affectedIdSystemSequence) {
        AffectedIdSystemSequence = affectedIdSystemSequence;
    }

    public String getSQLSystemQueryStart() {
        return SQLSystemQueryStart;
    }

    public void setSQLSystemQueryStart(String SQLSystemQueryStart) {
        this.SQLSystemQueryStart = SQLSystemQueryStart;
    }

    public String getSQLSystemQueryStop() {
        return SQLSystemQueryStop;
    }

    public void setSQLSystemQueryStop(String SQLSystemQueryStop) {
        this.SQLSystemQueryStop = SQLSystemQueryStop;
    }

    public String getGitSystemQueryStart() {
        return GitSystemQueryStart;
    }

    public void setGitSystemQueryStart(String gitSystemQueryStart) {
        GitSystemQueryStart = gitSystemQueryStart;
    }

    public String getGitSystemQueryStop() {
        return GitSystemQueryStop;
    }

    public void setGitSystemQueryStop(String gitSystemQueryStop) {
        GitSystemQueryStop = gitSystemQueryStop;
    }
}
