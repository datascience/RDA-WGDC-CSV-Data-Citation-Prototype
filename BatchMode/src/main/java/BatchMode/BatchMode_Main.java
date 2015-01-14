package BatchMode;

import java.io.File;
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


    public void runBatch(String arguments) {
        this.logger = Logger.getLogger(BatchMode_Main.class.getName());
        this.batchAPI = new BatchAPI();
        this.filePath = this.getFilePath(arguments);


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
            this.batchAPI.promtMessageToCommandline("Enter the absolute path of the CSV file.\n >: ");
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
