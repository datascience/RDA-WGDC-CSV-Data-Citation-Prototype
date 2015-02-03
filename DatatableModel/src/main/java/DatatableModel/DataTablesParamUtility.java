/*
 * Copyright [2014] [Stefan Pröll]
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

/*
 * Copyright [2014] [Stefan Pröll]
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

/*
 * Copyright [2014] [Stefan Pröll]
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

/*
 * Copyright [2014] [Stefan Pröll]
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

/*
 * Copyright [2014] [Stefan Pröll]
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

/*
 * Copyright [2014] [Stefan Pröll]
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

package DatatableModel;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Logger;


public class DataTablesParamUtility {

    public static JQueryDataTableParamModel getParam(HttpServletRequest request) {
        Logger logger;
        logger = Logger.getAnonymousLogger();

        if (request.getParameter("sEcho") != null && request.getParameter("sEcho") != "") {

            JQueryDataTableParamModel param = new JQueryDataTableParamModel();
            param.sEcho = request.getParameter("sEcho");
            param.sSearch = request.getParameter("sSearch");
            param.sColumns = request.getParameter("sColumns");
            param.iDisplayStart = Integer.parseInt(request.getParameter("iDisplayStart"));
            param.iDisplayLength = Integer.parseInt(request.getParameter("iDisplayLength"));
            param.iColumns = Integer.parseInt(request.getParameter("iColumns"));
            param.iSortingCols = Integer.parseInt(request.getParameter("iSortingCols"));
            param.iSortColumnIndex = Integer.parseInt(request.getParameter("iSortCol_0"));
            param.sSortDirection = request.getParameter("sSortDir_0");
            param.currentTable = request.getParameter("currentTable");
            param.currentDatabase = request.getParameter("currentDatabase");


            Map<String, String> filterMap = getFilterColumnsMap(request);
            param.filterMap = filterMap;

            return param;
        } else if (request.getParameter("currentDatabase") == null) {
            JQueryDataTableParamModel param = new JQueryDataTableParamModel();
            param.currentTable = request.getParameter("currentTable");
            param.currentDatabase = request.getParameter("currentDatabase");
            return param;
        } else
            return null;
    }

    private static Map<String, String> getFilterColumnsMap(HttpServletRequest request) {
        Logger logger;
        logger = Logger.getAnonymousLogger();

        // Iterate over filter columns

        Map<String, String[]> parameterMap = new HashMap<String, String[]>();
        parameterMap = request.getParameterMap();
        String currentValue;
        Map<String, String> filterMap = new HashMap<String, String>();
        for (String key : parameterMap.keySet()) {
            currentValue = parameterMap.get(key)[0];
            // TODO

            // match all parameters that start with "sSearch_" and end with a digit in order to capture all
            // filterable columns
            // the column names are stored in mDataProp_X and serve as key for the map. X is the same for mDataProp and
            // for sSearch
            if (key.startsWith("sSearch_") && key.matches("^.+?\\d$") && currentValue != "") {

                // get the last digits from the key
                Scanner in = new Scanner(key).useDelimiter("[^0-9]+");
                int columnNumber = in.nextInt();
                String currentColumnAsKey = parameterMap.get("mDataProp_" + columnNumber)[0];
                //	logger.info("Current Column Name ---------> " + currentColumnAsKey);
                filterMap.put(currentColumnAsKey, currentValue);
                logger.info("Parameters set (Amount" + filterMap.size() + " Filter parameter = " + currentColumnAsKey
                        + " - " + currentValue + " columns : " + request.getParameter("sColumns"));

            }
        }
        return filterMap;
    }
}
