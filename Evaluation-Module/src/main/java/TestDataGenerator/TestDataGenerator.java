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

    private int numberOfColumns = -1;
    private int numberOfRecords = -1;
    private int averageRecordLength = -1;
    private double variance;
    private Map<String, String> recordMap;
    private String fileName;
    private String[] header;
    private HelpersCSV csvHelpers;

    /**
     * Constructor. Generates test data
     *
     * @param fileName
     * @param amountOfColumns
     * @param numberOfRecords
     * @param averageRecordLength
     * @param variance
     */
    public TestDataGenerator(String fileName, int amountOfColumns, int numberOfRecords, int averageRecordLength,
                             double
                                     variance) {
        this.fileName = fileName;


        this.header = this.generateGenericHeaders(amountOfColumns);
        this.numberOfColumns = header.length;
        this.numberOfRecords = numberOfRecords;
        this.averageRecordLength = averageRecordLength;
        this.variance = variance;
        this.csvHelpers = new HelpersCSV();


        try {
            this.writeWithCsvMapWriter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Write test data to file
     *
     * @throws Exception
     */
    private void writeWithCsvMapWriter() throws Exception {

        ICsvMapWriter mapWriter = null;

        try {
            mapWriter = new CsvMapWriter(new FileWriter(this.fileName),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = csvHelpers.getProcessors(this.numberOfColumns);
            // write the header
            mapWriter.writeHeader(header);

            for (int i = 0; i < this.numberOfRecords; i++) {
                recordMap = new HashMap<String, String>();
                for (int j = 0; j < this.numberOfColumns; j++) {
                    recordMap.put(header[j], HelpersCSV.randomString(this.averageRecordLength, this.variance));
                }
                mapWriter.write(recordMap, header, processors);
            }


        } finally {
            if (mapWriter != null) {
                mapWriter.close();
            }
        }
    }


    private String[] generateGenericHeaders(int amountOfHeaders) {
        String[] newHeaders = header = new String[amountOfHeaders];
        for (int i = 0; i < amountOfHeaders; i++) {
            this.header[i] = "Column_" + (i + 1);
        }
        return newHeaders;
    }
}
