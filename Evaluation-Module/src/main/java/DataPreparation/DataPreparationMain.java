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

package DataPreparation;

import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DataPreparationMain {

    public static void main(String[] args) {
        String inputCSV = "/home/stefan/Development/workspaceIDEA/CSV-DataCitation/Evaluation-Module/additional_configuration/evaluation.csv";
        String outputCSV = "/tmp/evaluation_output.csv";
        PersistentIdentifierAPI pidAPI = new PersistentIdentifierAPI();

        Organization evaluationOrganization = pidAPI.getOrganizationObjectByPrefix(9999);
        if (evaluationOrganization == null) {
            evaluationOrganization = pidAPI.createNewOrganitation("Evaluation Organization", 9999);
        }
        DataPreparation prep = new DataPreparation(evaluationOrganization);
        String tableName = prep.uploadNewCSVFile(inputCSV);
        prep.createNewBaseTableRecord(tableName);

        prep.writeBaseTableToCSVFileIncludingSequenceNumbers(tableName, outputCSV);

        System.exit(0);

    }
}
