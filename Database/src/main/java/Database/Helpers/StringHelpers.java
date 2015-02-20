/*
 * Copyright [2015] [Stefan Pröll]
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
