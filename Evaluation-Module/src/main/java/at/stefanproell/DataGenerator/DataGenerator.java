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

import org.apache.commons.lang3.RandomStringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.constraint.UniqueHashCode;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.io.FileWriter;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DataGenerator {


    private static final Logger logger =
            Logger.getLogger(DataGenerator.class.getName());

    private int numberOfColumns=-1;
    private int numberOfRecords=-1;
    private int averageRecordLength=-1;
    private double variance;
    private Random fRandom = new Random();
    private Map<String, String> recordMap;
    private String fileName;
    private String[] header;

    public DataGenerator(String fileName, String[] header, int numberOfRecords, int averageRecordLength, double
            variance ) {
        this.fileName=fileName;
        this.header=header;
        this.numberOfColumns=header.length;
        this.numberOfRecords = numberOfRecords;
        this.averageRecordLength = averageRecordLength;
        this.variance = variance;

        try {
            this.writeWithCsvMapWriter();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DataGenerator() {

    }
    /**
     * An example of reading using CsvMapWriter.
     */
    private void writeWithCsvMapWriter() throws Exception {

        ICsvMapWriter mapWriter = null;

        try {
            mapWriter = new CsvMapWriter(new FileWriter(this.fileName),
                    CsvPreference.STANDARD_PREFERENCE);

            final CellProcessor[] processors = getProcessors(this.numberOfColumns);
            // write the header
            mapWriter.writeHeader(header);

                for(int i = 0; i < this.numberOfRecords;i++){
                    recordMap = new HashMap<String, String>();
                    for(int j = 0; j < this.numberOfColumns;j++) {
                        String randomString = this.randomString(this.averageRecordLength, this.variance);

                        recordMap.put(header[j], randomString);
                    }
                    mapWriter.write(recordMap, header, processors);
                }






        }
        finally {
            if( mapWriter != null ) {
                mapWriter.close();
            }
        }
    }

    /**
     * Get Cell Processors. Verify that the first column is unique
     *
     * @param amountOfColumns
     * @return
     */
    public static CellProcessor[] getProcessors(int amountOfColumns) {
        CellProcessor[] processors=new CellProcessor[amountOfColumns];

        for(int i = 0; i< amountOfColumns; i++) {
            if (i == 0) {
                processors[i] = new UniqueHashCode();
            }
            processors[i]=new Optional();

        }
        return  processors;
    }

    private String randomString(int length, double variance){
        int rand = (int) Math.round(length + fRandom.nextGaussian() * variance);
        String randomString = RandomStringUtils.randomAlphanumeric(rand);

        return randomString;
    }


    /**
     * Generate header array
     *
     * @param amountOfColumns
     * @return
     */
    private String[] generadeHeaderArray(int amountOfColumns) {
        String[] headers = new String[amountOfColumns];
        for (int i = 0; i < amountOfColumns; i++) {
            headers[i] = "Column_" + i;
        }
        return headers;


    }

}
