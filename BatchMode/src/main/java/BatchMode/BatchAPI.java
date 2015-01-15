package BatchMode;


import org.apache.commons.io.FilenameUtils;

import java.io.*;
import java.util.HashMap;
import java.util.logging.Logger;

public class BatchAPI {
    private Logger logger;
    private HashMap filesList;


    public BatchAPI() {
        this.logger = Logger.getLogger(BatchMode_Main.class.getName());
        this.filesList = new HashMap<String, String>();
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

    public HashMap addFileToFileList(File file) {


        String tableName = FilenameUtils.removeExtension(this.getTableNameFromFileName(file));
        String path = file.getAbsolutePath();

        this.filesList.put(tableName, path);
        this.logger.info("Put file " + path + " as table " + tableName + " into a list with size " + this.filesList
                .size());
        return this.filesList;
    }

    public HashMap getFilesList() {
        return filesList;
    }

    public void setFilesList(HashMap filesList) {
        this.filesList = filesList;
    }
}
