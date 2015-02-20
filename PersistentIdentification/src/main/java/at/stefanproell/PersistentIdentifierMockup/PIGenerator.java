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

package at.stefanproell.PersistentIdentifierMockup;

import org.apache.commons.lang3.RandomStringUtils;

import java.util.logging.Logger;

/**
 * The Persistent Identifier Generator
 *
 * @author stefan
 */
public class PIGenerator {
    private RandomStringUtils randomUtil;


    private Logger logger;

    public PIGenerator() {
        this.randomUtil = new RandomStringUtils();
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("New PIGenerator created");
    }

    public String getRandomAlpaString(int length) {
        String randomString = RandomStringUtils.random(length, true, false);

        logger.info("Generated PID " + randomString);
        return randomString;
    }

    public String getRandomAlpaNumericString(int length) {
        logger.info("Length is " + length);
        String randomString = RandomStringUtils.randomAlphanumeric(length);
        logger.info("Generated PID " + randomString);

        return randomString;
    }

    public String getRandomNumeric(int length) {
        String randomString = RandomStringUtils.randomNumeric(length);

        logger.info("Generated PID " + randomString);

        return randomString;
    }


}
