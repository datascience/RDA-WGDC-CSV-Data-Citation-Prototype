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

package QueryStore;

import org.apache.commons.lang3.RandomStringUtils;

/**
 * Helper class
 */
public class Helpers {

    protected Helpers() {

    }

    protected static String getRandomAlpaNumericString(int length) {
        String randomString = RandomStringUtils.randomAlphanumeric(length);
        return randomString;
    }

    /**
     * Dummy method for calculating the hash of a query.
     * It returns a random string with 40 characters.
     *
     * @return
     */
    private String calulateHashValue() {
        String hash = Helpers.getRandomAlpaNumericString(40);
        return hash;

    }
}
