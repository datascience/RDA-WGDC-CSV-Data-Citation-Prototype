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

import CSVTools.CsvToolsApi;
import at.stefanproell.CSV_Testdata_Generator.CSV_Testdata_Writer;
import at.stefanproell.CSV_Tools.CSV_Analyser;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvMapReader;
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapReader;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import static Helpers.HelpersCSV.randomString;

/**
 * Created by stefan on 27.06.16.
 */
public class Operations {
    private final CSV_Testdata_Writer csvDataWriter;
    private final CSV_Analyser csvAnalyzer;
    private final Logger logger;

    public Operations() {
        csvAnalyzer = new CSV_Analyser();
        csvDataWriter = new CSV_Testdata_Writer();
        logger = Logger.getLogger(Operations.class.getName());
    }

    public void randomInsert(PersistentIdentifier pid) {


        CsvToolsApi csvToolsApi = new CsvToolsApi();
        String currentPath = pid.getURI();

        String[] headers = csvToolsApi.getArrayOfHeadersCSV(currentPath);
        int amountOfColumns = headers.length;


        Map<String, Object> newRecord = new HashMap<String, Object>();

        for (int i = 0; i < amountOfColumns; i++) {
            newRecord.put(headers[i], randomString(10, 2));
        }

        ICsvMapWriter mapWriter = null;

        try {
            mapWriter = new CsvMapWriter(new FileWriter(currentPath, true),
                    CsvPreference.STANDARD_PREFERENCE);


            final CellProcessor[] processors = csvDataWriter.getProcessors(amountOfColumns);
            mapWriter.write(newRecord, headers, processors);

            // write the header
       /*
            mapWriter.writeHeader(headers);

            for(int i=0; i < amountOfRecordsInFile;i++){
                Map<String, Object> record = csvMap.get(i);
                mapWriter.write(record,headers, processors);
            }
           */


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (mapWriter != null) {
                try {
                    mapWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    public void randomDelete(PersistentIdentifier pid) {
        ICsvMapReader mapReader = null;
        CsvToolsApi csvToolsApi = new CsvToolsApi();
        String currentPath = pid.getURI();

        String[] headers = csvToolsApi.getArrayOfHeadersCSV(currentPath);
        int amountOfColumns = headers.length;


        ICsvMapWriter mapWriter = null;
        try {

            // read existing file
            Map<Integer, Map<String, Object>> csvMap = csvAnalyzer.readCSV(new File(currentPath));
            int amountOfRecords = csvMap.size();

            Random rand = new Random();
            int randomRecord = rand.nextInt((amountOfRecords) + 1);
            final CellProcessor[] processors = csvDataWriter.getProcessors(amountOfColumns);


            mapWriter = new CsvMapWriter(new FileWriter(currentPath),
                    CsvPreference.STANDARD_PREFERENCE);
            mapWriter.writeHeader(headers);


            int counter = 0;
            for (Map.Entry<Integer, Map<String, Object>> csvRowMap : csvMap.entrySet()) {

                Map<String, Object> csvRow = csvRowMap.getValue();
                if (randomRecord != counter) {
                    mapWriter.write(csvRow, headers, processors);
                } else {
                    logger.info("Deleting record: " + counter);

                }

                counter++;


            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                mapWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
}
