package Database.Helpers;

import java.util.List;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class StringHelpers {
    private Logger logger;


    public StringHelpers() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    /**
     * Iterate over the list of keys and return a comma seperated list of keys*
     *
     * @param primaryKeyColumns
     * @return
     */
    public String getCommaSeperatedListofPrimaryKeys(List<String> primaryKeyColumns) {
        String listOfKeys = "";

        for (String key : primaryKeyColumns) {
            listOfKeys += key + ",";
        }

        if (listOfKeys.endsWith(",")) {
            listOfKeys = listOfKeys.substring(0, listOfKeys.lastIndexOf(","));
        }

        this.logger.info("The list of primary keys is: " + listOfKeys);

        return listOfKeys;

    }


    /**
     * Remove the extention CSV from the string
     * * * @param inputString
     *
     * @return
     */
    public String removeCSVFileExtention(String inputString) {
        String noExtention = inputString.substring(0, inputString.lastIndexOf(".csv"));
        this.logger.info("Removed CSV Extention: " + noExtention);
        return noExtention;


    }
}
