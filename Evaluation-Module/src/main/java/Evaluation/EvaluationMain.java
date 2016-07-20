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
import Database.DatabaseOperations.DatabaseTools;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;

import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by stefan on 27.06.16.
 */
public class EvaluationMain {
    public static void main(String[] args) {
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);
        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.SEVERE);
        }

       // run("SMP-S3-R100","EvaluationMachine-VM2",5,1000,1,100,10,0.01,0.99,0.0,0.0);
        run("AVG-S3-R1000","EvaluationMachine-VM2",25,10000,1,1000,25,0.01,0.99,0.0,0.0);
        /*

        run("AVG-S4-R100","EvaluationMachine-VM2",25,10000,1,100,25,0.1,0.3,0.3,0.3);
        run("AVG-S4-R1000","EvaluationMachine-VM2",25,10000,1,1000,25,0.1,0.3,0.3,0.3);

        run("AVG-S5-R100","EvaluationMachine-VM2",25,10000,1,100,25,0.2,0.2,0.4,0.2);
        run("AVG-S5-R1000","EvaluationMachine-VM2",25,10000,1,1000,25,0.2,0.2,0.4,0.2);

        run("CPX-S1-R100","EvaluationMachine-VM2",25,100000,1,100,50,1.0,0.0,0.0,0.0);
        run("CPX-S1-R1000","EvaluationMachine-VM2",25,100000,1,1000,50,1.0,0.0,0.0,0.0);

        run("CPX-S2-R100","EvaluationMachine-VM2",25,100000,1,100,50,0.8,0.05,0.15,0.0);
        run("CPX-S2-R1000","EvaluationMachine-VM2",25,100000,1,1000,50,0.8,0.05,0.15,0.0);

        run("CPX-S3-R100","EvaluationMachine-VM2",25,100000,1,100,50,0.01,0.99,0.0,0.0);
        run("CPX-S3-R100","EvaluationMachine-VM2",25,100000,1,1000,50,0.01,0.99,0.0,0.0);

        run("CPX-S4-R100","EvaluationMachine-VM2",25,100000,1,100,50,0.1,0.3,0.3,0.3);
        run("CPX-S4-R1000","EvaluationMachine-VM2",25,100000,1,1000,50,0.1,0.3,0.3,0.3);

        run("CPX-S5-R100","EvaluationMachine-VM2",25,100000,1,100,50,0.2,0.2,0.4,0.1);
        run("CPX-S5-R1000","EvaluationMachine-VM2",25,100000,1,1000,50,0.2,0.2,0.4,0.1);

*/


        System.exit(0);


    }

    private static void run(String runName,      String evaluationMachine,        int amountOfColumns,  int amountOfRecords,  int amountOfCsvFiles, int amountOfOperations , int averageStringLength ,      double selectProportion ,
            double insertProportion,
            double updateProportion,
            double deleteProportion  ){
        // Drop existing data
        DatabaseTools dbTools = new DatabaseTools();
        dbTools.dropAndRecreateCitationDatabase();
        dbTools = null;





        // Take care that strings are not too short, because then there will be primary key duplicates!

        double variance = 2.0;






        double qEasyProbability=0.6;
        double qStandardProbability=0.3;
        double qComplexProbability=0.1;

        String csvFolder = "/tmp/Evaluation";
        String gitRepoPath = "/tmp/Evaluation_Git_Repo";
        String exportPath = "/tmp/Evaluation_Results/";

        CsvToolsApi csvApi = new CsvToolsApi();
        csvApi.deleteCSVDirectory(csvFolder);
        csvApi.deleteCSVDirectory(gitRepoPath);
        csvApi.deleteCSVDirectory(exportPath);

        csvApi.createCSVDirectory(csvFolder);
        csvApi.createCSVDirectory(exportPath);


        EvaluationAPI api = new EvaluationAPI(9999, csvFolder, gitRepoPath, exportPath, selectProportion, insertProportion, updateProportion, deleteProportion, amountOfOperations,qEasyProbability,qStandardProbability,qComplexProbability);

        List<PersistentIdentifier> list = api.createCsvFiles(amountOfCsvFiles, amountOfRecords, amountOfColumns, averageStringLength, variance);

        api.uploadListOfCsvFiles(list);


        api.setRunDetails(runName,evaluationMachine,amountOfColumns,amountOfCsvFiles,amountOfOperations,amountOfRecords,selectProportion,deleteProportion,updateProportion,insertProportion);
        api.runOperations(list);
        api.setRunBeanStopTime();
    }
}
