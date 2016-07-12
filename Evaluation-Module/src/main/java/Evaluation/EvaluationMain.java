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
import java.util.concurrent.TimeUnit;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by stefan on 27.06.16.
 */
public class EvaluationMain {
    public static void main(String[] args) {

        Logger log = LogManager.getLogManager().getLogger("");
        for (Handler h : log.getHandlers()) {
            h.setLevel(Level.INFO);
        }

        // Drop existing data
        DatabaseTools dbTools = new DatabaseTools();
        dbTools.dropAndRecreateCitationDatabase();
        dbTools = null;



        // Take care that strings are not too short, because then there will be primary key duplicates!
        int averageStringLength = 50;
        double variance = 5.0;
        int amountOfRecords = 10000;
        int amountOfColumns = 10;

        int amountOfCsvFiles = 10;
        double selectProportion = 0.3;
        double insertProportion = 0.5;
        double updateProportion = 0.2;
        double deleteProportion = 0.1;
        QueryComplexity complexity = QueryComplexity.EASY;
        int amountOfOperations = 20;

        String csvFolder = "/tmp/Evaluation";
        String gitRepoPath = "/tmp/Evaluation_Git_Repo";
        String exportPath = "/tmp/Evaluation_Results/";

        CsvToolsApi csvApi = new CsvToolsApi();
        csvApi.deleteCSVDirectory(csvFolder);
        csvApi.deleteCSVDirectory(gitRepoPath);
        csvApi.deleteCSVDirectory(exportPath);

        csvApi.createCSVDirectory(csvFolder);
        csvApi.createCSVDirectory(exportPath);


        EvaluationAPI api = new EvaluationAPI(9999, csvFolder, gitRepoPath, exportPath, selectProportion, insertProportion, updateProportion, deleteProportion, complexity, amountOfOperations);
        log.severe("Creating files...");
        List<PersistentIdentifier> list = api.createCsvFiles(amountOfCsvFiles, amountOfRecords, amountOfColumns, averageStringLength, variance);
        log.severe("Importing files...");
        api.uploadListOfCsvFiles(list);
        log.severe("Run operations on files...");
        api.runOperations(list);


        System.exit(0);


    }
}