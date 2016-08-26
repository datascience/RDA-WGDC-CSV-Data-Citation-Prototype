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

package at.stefanproell.DataGenerator;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class GenerateTestDataMain {

    public static void main(String[] args) {

        int averageStringLength=5;
        double variance = 2.0;
        int amountOfRecords = 10;
        String[] headers = {"COLUMN_1", "COLUMN_2", "COLUMN_3", "COLUMN_4", "COLUMN_5"};

        DataGenerator csv = new DataGenerator("/tmp/testfile.csv", headers, amountOfRecords,
                averageStringLength,variance);






    }
}
