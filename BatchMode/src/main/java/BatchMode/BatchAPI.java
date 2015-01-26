package BatchMode;


import CSVTools.Column;
import Database.Helpers.StringHelpers;
import org.apache.commons.io.FilenameUtils;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

public class BatchAPI {
    private Logger logger;
    private HashMap filesList;
    private StringHelpers stringHelpers;

    public List<String> getPrimaryKeys() {
        return primaryKeys;
    }

    public void setPrimaryKeys(List<String> primaryKeys) {
        this.primaryKeys = primaryKeys;
    }

    List<String> primaryKeys;

    public BatchAPI() {
        this.logger = Logger.getLogger(BatchMode_Main.class.getName());
        this.filesList = new HashMap<String, String>();
        this.primaryKeys = new ArrayList<String>();
        this.stringHelpers = new StringHelpers();
    }

    /**
     * Read a file from its path
     *
     * @param path
     * @return
     */
    public File readFileFromPath(String path) {
        File file = new File(path);
        return file;


    }

    /**
     * Print the first line of a file
     *
     * @param file
     * @return
     * @throws IOException
     */
    public String readFirstLinesFromFile(File file, int lines) throws IOException {
        // read as stream
        BufferedReader r = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();

        String line = r.readLine();
        int counter = 0;
        while (counter < lines && line != null) {
            sb.append(line + "\n");
            counter++;
            line = r.readLine();
        }


        return sb.toString();
    }

    /**
     * Read from input and return true ir false depending if user provided yes or y.
     *
     * @return
     */
    public boolean readYesOrNoFromInput() {
        String answer = this.readFromCommandline();
        if (answer.toLowerCase().equals("y") || answer.toLowerCase().equals("yes")) {
            this.logger.info("Answer was YES");
            return true;

        } else {
            this.logger.info("Answer was No");
            return false;
        }
    }

    /**
     * Read a String from command line
     *
     * @return
     */
    public String readFromCommandline() {
        //  open up standard input
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        String inputString = null;

        //  read the username from the command-line; need to use try/catch with the
        //  readLine() method
        try {
            inputString = br.readLine();
        } catch (IOException ioe) {
            System.out.println("IO error trying to read your name!");
            System.exit(1);
        }

        return inputString;
    }

    public void promtMessageToCommandline(String msg) {
        System.out.print(msg);
    }

    public String getTableNameFromFileName(File file) {

        return "table_" + file.getName();


    }

    public HashMap<String, File> addFileToFileList(File file) {


        String tableName = FilenameUtils.removeExtension(this.getTableNameFromFileName(file));


        this.filesList.put(tableName, file);
        this.logger.info("Put file " + file.getAbsolutePath() + " as table " + tableName + " into a list with size " +
                this.filesList
                .size());
        return this.filesList;
    }

    public HashMap getFilesList() {
        return filesList;
    }

    public void setFilesList(HashMap filesList) {
        this.filesList = filesList;
    }

    /**
     * Add a string primary key to the lust
     */

    public List<String> addPrimaryKeyToList(String primaryKey) {
        this.primaryKeys.add(primaryKey);
        return this.primaryKeys;

    }

    public void printColumns(Column[] columns) {
        for (int i = 0; i < columns.length; i++) {
            this.promtMessageToCommandline("[" + i + "] " + columns[i].getColumnName() + "\n");
        }

        this.promtMessageToCommandline("[s] " + " System Generated Sequence Number\n");

    }

    /*Show all columns and ask for primary key
    * 
    * * * */
    public String promptAndSelectPrimaryKey(Column[] columns) {
        String selectedPrimaryKey = null;

        // Display headers and let user specify the primary key
        this.printColumns(columns);


        this.promtMessageToCommandline(">");
        String position = this.readFromCommandline();

        if (position.equals("x")) {
            this.logger.info("All keys have beend added");
            selectedPrimaryKey = null;
        } else if (position.equals("s")) {
            selectedPrimaryKey = "ID_SYSTEM_SEQUENCE";
            this.logger.info("Use the internal system generated primary key");
        } else {
            selectedPrimaryKey = columns[Integer.parseInt(position)].getColumnName();
        }


        return selectedPrimaryKey;


    }

    /*
    * Iterate through directory and add all CSV files to a list
    * Return this list
    */
    public HashMap<String, File> getAllFilesInDirectory(String directoryPath) {
        File dir = new File(directoryPath);
        File[] directoryListing = dir.listFiles();
        HashMap<String, File> directoryFileList = new HashMap<String, File>();


        if (directoryListing != null) {
            for (File child : directoryListing) {
                String currentFileName = child.getAbsoluteFile().toString();

                if (currentFileName.toLowerCase().endsWith(".csv")) {

                    String tableName = this.stringHelpers.removeCSVFileExtention(child.getName());
                    directoryFileList.put(tableName, child);
                    this.logger.info("Added " + currentFileName + " to file list");
                }

            }
        } else {
            // Handle the case where dir is not really a directory.
            // Checking dir.isDirectory() above would not be sufficient
            // to avoid race conditions with another process that deletes
            // directories.
        }

        return directoryFileList;

    }

    /**
     * This method is used to reset the primary key list.
     */
    public void resetPrimaryKeys() {
        this.primaryKeys = new ArrayList<String>();
        this.logger.info("Primary key list is now resetted. Ready for next table...");


    }

}
