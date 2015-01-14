package BatchMode;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

public class BatchAPI {


    public BatchAPI() {

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
}
