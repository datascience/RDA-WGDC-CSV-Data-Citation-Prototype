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

package Helpers;


import EvaluationRun_Old.EvaluationRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.supercsv.cellprocessor.Optional;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.prefs.CsvPreference;

import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class HelpersCSV {
    private static Random fRandom = new Random();
    private static final Logger logger =
            Logger.getLogger(HelpersCSV.class.getName());

    public HelpersCSV() {

    }

    /**
     * Generate cell processors based on the amount of columns
     *
     * @param amountOfColumns
     * @return
     */
    public CellProcessor[] getProcessors(int amountOfColumns) {
        CellProcessor[] processors = new CellProcessor[amountOfColumns];

        for (int i = 0; i < amountOfColumns; i++) {
            processors[i] = new Optional();

        }
        return processors;
    }

    public Map<String, EvaluationRecord> readEvaluationQueries(String fileName) throws Exception {

        ICsvMapReader mapReader = null;
        Map<String, Object> evaluationCSVLine;
        // Treemap keeps the records sorted
        Map<String, EvaluationRecord> evaluationMap = new TreeMap<String, EvaluationRecord>();
        try {
            mapReader = new CsvMapReader(new FileReader(fileName), CsvPreference.STANDARD_PREFERENCE);

            // the header columns are used as the keys to the Map
            final String[] header = mapReader.getHeader(true);

            // create  processors that fit the evaluation record
            final CellProcessor[] processors = new CellProcessor[]{new Optional(), new Optional(), new Optional(), new
                    Optional(),
                    new Optional(), new Optional(), new Optional(), new Optional(), new Optional(), new Optional()};


            while ((evaluationCSVLine = mapReader.read(header, processors)) != null) {
                System.out.println(String.format("lineNo=%s, rowNo=%s, customerMap=%s", mapReader.getLineNumber(),
                        mapReader.getRowNumber(), evaluationCSVLine));

                //    EvaluationRecord evaluationRecord = new EvaluationRecord(
                //           evaluationCSVLine.get("QueryID").toString(),
                //        evaluationCSVLine.get("SQL-System-Query").toString(),
                //    evaluationCSVLine.get("Git-System-Query").toString(),
                //            evaluationCSVLine.get("Query-Type").toString(),

                //            evaluationCSVLine.get("Affected-Id-System-Sequence").toString(),
                //            null,null,null,null

                //  );

                //evaluationMap.put(evaluationRecord.getQueryID(),evaluationRecord);
                logger.info("Map size: " + evaluationMap.size());
            }

        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }

        return evaluationMap;
    }


    public String[] getHeaders(String path) throws Exception {
        final String[] header;
        ICsvMapReader mapReader = null;
        try {
            mapReader = new CsvMapReader(new FileReader(path), CsvPreference.STANDARD_PREFERENCE);

            // the header columns are used as the keys to the Map
            header = mapReader.getHeader(true);

            final CellProcessor[] processors = getProcessors(header.length);
        } finally {
            if (mapReader != null) {
                mapReader.close();
            }
        }
        return header;
    }

    /**
     * Creates upper case strings
     * @param length
     * @param variance
     * @return
     */
    public static String randomString(int length, double variance) {
        int rand = (int) Math.abs(Math.round(length + fRandom.nextGaussian() * variance));
        String randomString = RandomStringUtils.randomAlphanumeric(rand).toUpperCase();
        return randomString;
    }

    /**
     * Pick random headers
     *
     * @param headers
     * @return
     */
    public static String pickRandomColumn(String[] headers) {
        Random random = new Random();
        int index = random.nextInt(headers.length);
        String randomColumn = headers[index];
        return randomColumn;

    }

    public static String[] pickRandomColumns(String[] headers, int n) {
        String[] randomHeaders = new String[n];

        for (int i = 0; i < n; i++) {
            randomHeaders[i] = pickRandomColumn(headers);

        }
        return randomHeaders;
    }

    public static String joinArrayAsString(String[] array) {
        return StringUtils.join(array, ',');

    }

    public static String joinArrayListAsString(ArrayList<String> arrayList) {
        return StringUtils.join(arrayList, ',');

    }

    public static String joinArrayListAsEscapedString(ArrayList<String> arrayList) {
        ArrayList<String> escapedList = new ArrayList<String>(arrayList.size());
        for (int i = 0; i < arrayList.size(); i++) {
            escapedList.add("\"" + arrayList.get(i) + "\"");
        }
        return StringUtils.join(escapedList, ',');

    }

    public String trimExtension(String fileName) {

        String ext = ".csv";
        if (fileName.endsWith(ext)) {
            return fileName.substring(0, fileName.length() - ext.length());
        } else {
            return fileName;
        }


    }

    /**
     * Remove last comma
     *
     * @param input
     * @return
     */
    public static String trimLastComma(String input) {


        if (input.endsWith(",")) {
            return input.substring(0, input.length() - 1);
        } else {
            return input;
        }


    }

    public String getFileNameFromPath(String inputPath) {
        Path p = Paths.get(inputPath);
        return p.getFileName().toString();
    }


}
