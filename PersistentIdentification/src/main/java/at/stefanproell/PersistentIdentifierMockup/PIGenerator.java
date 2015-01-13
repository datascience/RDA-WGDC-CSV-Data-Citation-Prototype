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
