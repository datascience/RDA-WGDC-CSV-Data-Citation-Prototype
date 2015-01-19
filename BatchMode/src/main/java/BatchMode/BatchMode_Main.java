package BatchMode;

import Bean.DatabaseMigrationController;
import CSVTools.CSV_API;
import CSVTools.Column;
import Database.Helpers.StringHelpers;
import Database.MigrationTasks;

import java.io.File;
import java.io.IOException;
import java.util.*;
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

        boolean isSingleFileMode = false;

        MigrationTasks migrationTasks = new MigrationTasks();

        HashMap<String, File> filesList = null;

        // only use one test file
        if (isSingleFileMode) {

            String first10 = "/media/Data/Datasets/CSV-Datasets/csv-citation-test/addresses_first10.csv";
            String first10one = "/media/Data/Datasets/CSV-Datasets/csv-citation-test/addresses_first10_one_change.csv";
            String updated10 = "/media/Data/Datasets/CSV-Datasets/csv-citation-test/addresses_first10_changed.csv";


            //arguments = first10;
            //arguments = updated10;
            arguments = first10one;
            this.filePath = this.getFilePath(arguments);


            File csvFile = this.batchAPI.readFileFromPath(this.getFilePath());

            filesList = this.batchAPI.addFileToFileList(csvFile);

            isNewFile = this.isItANewFile(csvFile);
        } else {

            String path = "/media/Data/Datasets/CSV-Datasets/Collection-Test";
            filesList = this.batchAPI.getAllFilesInDirectory(path);


        }
       

            





        /*
        * Create a new database
        * **/
        if (isNewFile) {


            this.migrateNewFile(filesList);


        } else {

            this.batchAPI.promtMessageToCommandline("Does this file only contain new rows which are not yet contained" +
                    " in the corresponding CSV dataset? \n");
            this.batchAPI.promtMessageToCommandline(">");
            containsOnlyNewRows = this.batchAPI.readYesOrNoFromInput();

            /*File only contains new records to append
            *
            * */
            if (containsOnlyNewRows == true) {


                migrationTasks.insertNewCSVDataToExistingTable(filesList, null, containsHeaders, false);


            }


            /*
            * All the records in this file might be changed. Update existing rows and append new rows.
            * */
            else {
                this.batchAPI.promtMessageToCommandline("Does this file contain changed records which should be " +
                        "updated? If there are new rows, should they be appended?\n ");
                this.batchAPI.promtMessageToCommandline(">");
                containsChangedRecords = this.batchAPI.readYesOrNoFromInput();

                /*
                * Check for updates, update existing records and append new records
                * */

                String tableName = "table_addresses_first10";
                if (containsChangedRecords == true) {
                    migrationTasks.updateDataInExistingTable(filesList, tableName, containsHeaders, false);

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

    /**
     * Migrate a new file into the database*
     *
     * @param
     */
    private void migrateNewFile(HashMap<String, File> filesList) {
        CSV_API csvAPI = new CSV_API();

        String currentFileName = "";
        File currentFile = null;

        Iterator<Map.Entry<String, File>> entries = filesList.entrySet().iterator();

        while (entries.hasNext()) {
            Map.Entry<String, File> entry = entries.next();
            currentFileName = entry.getKey().toString();
            currentFile = (File) entry.getValue();


            // Read the columns
            Column[] columns = null;
            try {
                boolean containsHeaders = this.doesThisFileContainHeaders(currentFile);
                columns = csvAPI.analyseColumns(containsHeaders, currentFile.getAbsolutePath().toString());

            } catch (IOException e) {
                e.printStackTrace();
            }


            MigrationTasks migrationTasks = new MigrationTasks();
            String answer = "";

            while (answer != null) {
                this.batchAPI.promtMessageToCommandline("This is the list of potential primary keys. Add a key by " +
                        "pressing the corresponding number\n");

                answer = this.batchAPI.promptAndSelectPrimaryKey(columns);
                if (answer == null) {
                    this.logger.info("No more primary keys added... proceeding");
                    if (this.batchAPI.getPrimaryKeys().size() == 0) {
                        this.logger.warning("No primary key was set. Usign hard coded default");
                        this.batchAPI.addPrimaryKeyToList("ID_SYSTEM_SEQUENCE");
                    }
                } else {
                    this.logger.info("Adding " + answer + " to list");
                    this.batchAPI.addPrimaryKeyToList(answer);
                    answer = this.batchAPI.promptAndSelectPrimaryKey(columns);
                }


            }

            StringHelpers stringHelpers = new StringHelpers();
            String keys = stringHelpers.getCommaSeperatedListofPrimaryKeys(this.batchAPI.getPrimaryKeys());
            this.logger.info("You entered these keys: " + keys);
            migrationTasks.migrate(filesList, this.batchAPI.getPrimaryKeys());


        }


    }

    /**
     * Read the first 5 lines of the file and ask the user if it contains headers
     *
     * @return
     */
    private boolean doesThisFileContainHeaders(File csvFile) {
        boolean containsHeaders = false;
        // launch CSV api

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

        return containsHeaders;

    }

    private boolean isItANewFile(File csvFile) {
       

        /* Is is a completely new file which is not stored in the database
        * */
        this.batchAPI.promtMessageToCommandline("Is this a new CSV file which is not already in the database?\n");
        this.batchAPI.promtMessageToCommandline(">");
        return this.batchAPI.readYesOrNoFromInput();
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
