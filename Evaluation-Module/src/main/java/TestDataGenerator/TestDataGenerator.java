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

import Helpers.HelpersCSV;

import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class TestDataGenerator {


    private static final Logger logger =
            Logger.getLogger(TestDataGenerator.class.getName());


    private HelpersCSV csvHelpers;


    public TestDataGenerator() {
        csvHelpers = new HelpersCSV();

    }


    /**
     * Write test data to file
     *
     * @throws Exception
     */
    public String writeWithCsvMapWriter(String fileName, int amountOfColumns, int numberOfRecords, int averageRecordLength,
                                        double
                                                variance) throws Exception {

        ICsvMapWriter mapWriter = null;
        String[] headers = generateGenericHeaders(amountOfColumns);

        Map<String, String> recordMap;

        try {
            mapWriter = new CsvMapWriter(new FileWriter(fileName),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = csvHelpers.getProcessors(amountOfColumns);
            // write the header
            mapWriter.writeHeader(headers);

            for (int i = 0; i < numberOfRecords; i++) {
                recordMap = new HashMap<String, String>();
                for (int j = 0; j < amountOfColumns; j++) {
                    String randomString = HelpersCSV.randomString(averageRecordLength, variance);

                    // The first column is used as primary key. It must not contain null values
                    while ((randomString.equals(null) || randomString.equals("") || randomString.equals("null")) && j == 0) {
                        randomString = HelpersCSV.randomString(averageRecordLength, variance);
                        logger.info("Value was null... getting a new one.");
                    }
                    recordMap.put(headers[j], randomString);
                }
                mapWriter.write(recordMap, headers, processors);
            }


        } finally {
            if (mapWriter != null) {
                mapWriter.close();
            }
        }
        return fileName;
    }


    /**
     * @param amountOfHeaders
     * @return
     */
    private String[] generateGenericHeaders(int amountOfHeaders) {
        String[] newHeaders = new String[amountOfHeaders];
        for (int i = 0; i < amountOfHeaders; i++) {
            newHeaders[i] = "COLUMN_" + (i + 1);
        }
        return newHeaders;
    }
}
