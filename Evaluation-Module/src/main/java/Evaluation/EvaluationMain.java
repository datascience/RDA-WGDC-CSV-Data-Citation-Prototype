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

package Evaluation;

import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;

import java.util.List;

/**
 * Created by stefan on 27.06.16.
 */
public class EvaluationMain {
    public static void main(String[] args) {
        int averageStringLength = 5;
        double variance = 2.0;
        int amountOfRecords = 100;
        int amountOfColumns = 5;

        int amountOfCsvFiles = 1;


        EvaluationAPI api = new EvaluationAPI(9999, "/tmp/Evaluation");
        List<PersistentIdentifier> list = api.createCsvFiles(amountOfCsvFiles, amountOfRecords, amountOfColumns, averageStringLength, variance);
        api.uploadListOfCsvFiles(list);
        api.runOperations(list);


        System.exit(0);


    }
}
