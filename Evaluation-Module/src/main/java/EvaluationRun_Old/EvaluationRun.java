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

import Database.DatabaseOperations.DatabaseTools;
import Helpers.HelpersCSV;
import com.google.common.base.Stopwatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class EvaluationRun {
    private Map<String, EvaluationRecord> evaluationRecordMap = null;
    private HelpersCSV csv;
    private DatabaseTools dbTools;
    private Stopwatch stopwatch;
    private String tableName;
    private static final Logger logger =
            Logger.getLogger(EvaluationRecord.class.getName());


    public EvaluationRun(String evaluationFileName, String targetTableName) {
        csv = new HelpersCSV();
        dbTools = new DatabaseTools();
        tableName = targetTableName;

        try {
            evaluationRecordMap = csv.readEvaluationQueries(evaluationFileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Loaded " + evaluationRecordMap.size() + " evaluation queries");
    }

    public void run() {
        Iterator it = evaluationRecordMap.entrySet().iterator();
        while (it.hasNext()) {

            Map.Entry pair = (Map.Entry) it.next();
            it.remove(); // avoids a ConcurrentModificationException
            EvaluationRecord evaluationRecord = (EvaluationRecord) pair.getValue();

            process(evaluationRecord);

        }


    }

    private void process(EvaluationRecord evaluationRecord) {
        logger.info("Processing evaluation record with query id " + evaluationRecord.getQueryID());

        if (evaluationRecord.getQueryType().equalsIgnoreCase("INSERT")) {
            logger.info("INSERT");

        } else if (evaluationRecord.getQueryType().equalsIgnoreCase("UPDATE")) {
            logger.info("UPDATE");

        } else if (evaluationRecord.getQueryType().equalsIgnoreCase("DELETE")) {
            logger.info("DELETE");

            deleteSQL(evaluationRecord);


        } else if (evaluationRecord.getQueryType().equalsIgnoreCase("SELECT")) {
            logger.info("SELECT");

        }

    }

    /**
     * Delete record in the MySQL implementation
     *
     * @param evaluationRecord
     */
    private void deleteSQL(EvaluationRecord evaluationRecord) {

        List<Integer> sequenceNumber = new ArrayList<Integer>(1);
        sequenceNumber.add(Integer.parseInt(evaluationRecord.getAffectedIdSystemSequence()));
        this.dbTools.deleteMarkedRecords(sequenceNumber, tableName);

    }


}
