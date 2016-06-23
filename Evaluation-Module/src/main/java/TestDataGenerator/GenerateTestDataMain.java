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

package TestDataGenerator;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class GenerateTestDataMain {

    public static void main(String[] args) {

        int averageStringLength = 5;
        double variance = 2.0;
        int amountOfRecords = 1000;
        int amountOfColumns = 10;

        DateFormat df = new SimpleDateFormat("YYYYMMdd_HHmmss");

// Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();
// Using DateFormat format method we can create a string
// representation of a date with the defined format.
        String reportDate = df.format(today);

// Print what date is today!
        System.out.println("Report Date: " + reportDate);
        String directory = "/tmp/";
        String fileName = "CSV_Evaluation_" + reportDate + "_" + amountOfColumns + "_columns_" +
                amountOfRecords + "_records_" + averageStringLength + "_averageStringLength.csv";


        TestDataGenerator csv = new TestDataGenerator(directory + fileName, amountOfColumns, amountOfRecords,
                averageStringLength, variance);


    }
}
