package Evaluation;

import CSVTools.CsvToolsApi;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;

import java.util.List;

/**
 * Created by stefan on 13.07.16.
 */
public class DataGeneratorTest {
    public static void main(String[] args) {
        int amountOfColumns = 50;
        int amountOfRecords = 100000;
        int amountOfCsvFiles = 1;

        // Take care that strings are not too short, because then there will be primary key duplicates!
        int averageStringLength = 50;
        double variance = 1.0;




        double selectProportion = 0.3;
        double insertProportion = 0.5;
        double updateProportion = 0.2;
        double deleteProportion = 0.1;
        double qEasyProbability=0.6;
        double qStandardProbability=0.3;
        double qComplexProbability=0.1;
        int amountOfOperations = 100;

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

    }
}
