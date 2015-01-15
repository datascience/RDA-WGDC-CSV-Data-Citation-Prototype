package BatchMode;

import Bean.DatabaseMigrationController;
import CSVTools.CSV_API;
import CSVTools.Column;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Hello world!
 */
public class BatchMode_Main {
    private File inputFile;


    private String filePath = null;
    private BatchAPI batchAPI;
    private Logger logger;

    public static void main(String[] args) {
        BatchMode_Main batch = new BatchMode_Main();
        String arguments = null;


        System.out.println("Testmodule for manual CSV data conversion.");

        if (args.length > 0) {
            arguments = args[0];
        }

        batch.runBatch(arguments);


    }

    /**
     * Get the path either from command line parameters or standard input
     *
     * @param arguments
     */
    public void runBatch(String arguments) {
        this.logger = Logger.getLogger(BatchMode_Main.class.getName());
        this.batchAPI = new BatchAPI();
        boolean containsHeaders = false;
        boolean isNewFile = true;
        boolean containsOnlyNewRows = false;
        boolean containsChangedRecords = false;
        boolean calulateHashColumn = false;
        HashMap filesList;

        arguments = "/media/Data/Datasets/CSV-Datasets/addresses_small.csv";
        this.filePath = this.getFilePath(arguments);


        File csvFile = this.batchAPI.readFileFromPath(this.getFilePath());

        filesList = this.batchAPI.addFileToFileList(csvFile);

        // launch CSV api
        CSV_API csvAPI = new CSV_API();
        Column[] columns;
        try {
            columns = csvAPI.analyseColumns(containsHeaders, csvFile.getAbsolutePath().toString());

        } catch (IOException e) {
            e.printStackTrace();
        }



        /* Does the file contain headers?
        * */
        try {
            int numberOfLines = 5;
            String firstFiveLinesLine = this.batchAPI.readFirstLinesFromFile(csvFile, numberOfLines);
            this.batchAPI.promtMessageToCommandline("This are the " + numberOfLines + "line of the file. Does " +
                    "the first line contain headers?\n");
            this.batchAPI.promtMessageToCommandline(firstFiveLinesLine);
            this.batchAPI.promtMessageToCommandline(">");
            containsHeaders = this.batchAPI.readYesOrNoFromInput();



        } catch (IOException e) {
            e.printStackTrace();
        }

        /* Is is a completely new file which is not stored in the database
        * */
        this.batchAPI.promtMessageToCommandline("Is this a new CSV file which is not already in the database?");
        this.batchAPI.promtMessageToCommandline(">");
        isNewFile = this.batchAPI.readYesOrNoFromInput();

        /*
        * Create a new database
        * **/
        if (isNewFile) {
            DatabaseMigrationController dbMigrate = new DatabaseMigrationController();

        } else {

            this.batchAPI.promtMessageToCommandline("Does this file only contain new rows which are not yet contained" +
                    " in the corresponding CSV dataset? ");
            this.batchAPI.promtMessageToCommandline(">");
            containsOnlyNewRows = this.batchAPI.readYesOrNoFromInput();

            /*File only contains new records to append
            *
            * */
            if (containsOnlyNewRows == true) {


            }


            /*
            * All the records in this file might be changed. Update existing rows and append new rows.
            * */
            else {
                this.batchAPI.promtMessageToCommandline("Does this file contain changed records which should be " +
                        "updated? If there are new rows, should they be appended? ");
                this.batchAPI.promtMessageToCommandline(">");
                containsChangedRecords = this.batchAPI.readYesOrNoFromInput();

                /*
                * Check for updates, update existing records and append new records
                * */
                if (containsChangedRecords == true) {

                } else {
                    this.batchAPI.promtMessageToCommandline("Your selection is not valid! Exiting");
                    System.exit(0);


                }

            }


        }


    }


    /**
     * Overwritten method
     *
     * @param commandlineArgument
     * @return
     */
    private String getFilePath(String commandlineArgument) {
        String path = null;
        if (commandlineArgument == null || commandlineArgument.length() == 0) {
            this.batchAPI.promtMessageToCommandline("Enter the absolute path of the CSV file.\n");
            path = this.batchAPI.readFromCommandline();
        } else {
            path = commandlineArgument;

        }

        this.filePath = path;
        this.logger.info("Path is: " + filePath);
        return filePath;
    }


    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public File getInputFile() {
        return inputFile;
    }

    public void setInputFile(File inputFile) {
        this.inputFile = inputFile;
    }

    public BatchAPI getBatchAPI() {
        return batchAPI;
    }

    public void setBatchAPI(BatchAPI batchAPI) {
        this.batchAPI = batchAPI;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }

}
