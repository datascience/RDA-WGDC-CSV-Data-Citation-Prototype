package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;

/**
 * Created by stefan on 20.11.14.
 */
public class UpdateTest {

    public static void main(String[] args) {
        System.out.println("API Mini Test");
        UpdateTest apiTest = new UpdateTest();

        apiTest.one();

        System.exit(0);


    }

    private void one() {
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();
        Organization org = api.getOrganizationObjectByPrefix(56789);
        PersistentIdentifierAlphaNumeric newpid = api.getAlphaNumericPID(org, "URL1");
        // PersistentIdentifierAlphaNumeric newpid2 = api.getAlphaNumericPID(org, "URL2");
        // PersistentIdentifierAlphaNumeric newpid3 = api.getAlphaNumericPID(org, "URL3");

        try {
            Thread.sleep(5000);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        api.updateURI(newpid.getIdentifier(), "URL1-1");

        try {
            Thread.sleep(5000);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        api.updateURI(newpid.getIdentifier(), "URL1-2");


        try {
            Thread.sleep(100);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        api.updateURI(newpid.getIdentifier(), "URL1-3");

    }

}
