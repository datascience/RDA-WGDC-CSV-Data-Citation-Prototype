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
import org.supercsv.io.CsvMapWriter;
import org.supercsv.io.ICsvMapWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static Helpers.HelpersCSV.randomString;

/**
 * Created by stefan on 27.06.16.
 */
public class Operations {
    public Operations() {
    }

    public void randomInsert(PersistentIdentifier pid) {
        CSV_Testdata_Writer csvWriter = new CSV_Testdata_Writer();

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


            final CellProcessor[] processors = csvWriter.getProcessors(amountOfColumns);
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
}
