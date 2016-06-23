package DatabaseBackend;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class DatabaseBackendMain {
    public static void main(String[] args) {

        String tableName = "evaluation";
        String repositoryPaTH = "/tmp/Git-Citation";
        String exportCsvFileName = "dataset.csv";


        EvaluationAPI evalAPI = new EvaluationAPI();
        evalAPI.setTableName(tableName);

        evalAPI.setRepositoryPaTH(repositoryPaTH);
        evalAPI.setExportCsvFileName(exportCsvFileName);
        evalAPI.gitRepositoryInit();

        EvaluationRunBean evalRun = evalAPI.createNewEvaluationRun();
        evalAPI.setEvaluationStartTime();


        double selectProportion = 0.4;
        double insertProportion = 0.1;
        double updateProportion = 0.1;
        double deleteProportion = 0.4;

        int amountOfQueries = 20;

        evalAPI.runEvaluation(tableName, amountOfQueries, QueryComplexity.STANDARD, selectProportion,
                insertProportion,
                updateProportion,
                deleteProportion);


        evalAPI.setEvaluationStoppTime();


        System.exit(0);


    }


}

