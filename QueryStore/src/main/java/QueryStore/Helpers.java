package QueryStore;

import org.apache.commons.lang3.RandomStringUtils;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.hibernate.id.uuid.Helper;

import java.util.logging.Logger;

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
     * @return
     */
    private String calulateHashValue(){
        String hash = Helpers.getRandomAlpaNumericString(40);
        return hash;

    }
}
