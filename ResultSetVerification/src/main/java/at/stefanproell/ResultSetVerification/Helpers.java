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

package at.stefanproell.ResultSetVerification;

import java.util.List;
import java.util.logging.Logger;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class Helpers {
    private static final Logger logger = Logger.getLogger(String.valueOf(Helpers.class));

    /**
     * Iterates over a list of strings and appends them. The last item is not followed by a comma
     *
     * @param items
     * @return
     */
    public static String commaSeparatedString(List<String> items) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (String item : items) {
            sb.append(sep);
            sb.append(item);
            sep = ",";
        }
        return sb.toString();
    }

    /**
     * Iterates over a list of strings and appends for each a prefix and a suffix.. The last item is not followed
     * by a comma
     *
     * @param items
     * @return
     */
    public static String commaSeparatedStringWithPrefixAndSuffix(List<String> items, String prefix, String suffix) {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (String item : items) {
            sb.append(sep);
            sb.append(prefix);
            sb.append(item);
            sb.append(suffix);
            sep = ",";
        }
        String builtString = sb.toString();
        logger.info("new string: " + builtString);
        return builtString;
    }
}
