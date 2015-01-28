package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;

/**
 * Created by stefan on 05.08.14.
 */
public class MinimalTest {

    public static void main(String[] args) {
        System.out.println("API Mini Test");
        MinimalTest apiTest = new MinimalTest();

        apiTest.one();

        System.exit(0);


    }

    public void one() {
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();


        // create a dummy organization and provide a prefix
        Organization evilOrganization = api.createNewOrganitation("Evil Corp", 45678);
        System.out.println("Organization created");

        // create identifiers
        PersistentIdentifierAlphaNumeric pid = api.getAlphaNumericPID(evilOrganization, "www.dertest.org");
        System.out.println("PID created");

        // Validate the PID. Returns true
        api.validatePID(evilOrganization.getOrganization_prefix(), pid.getIdentifier());

        // Validate wrong prefix. Returns false
        api.validatePID(1111, pid.getIdentifier());

        // resolve
        String URI = api.resolveIdentifierToURI(pid.getOrganization().getOrganization_prefix(), pid.getIdentifier());

        // print details
        api.printRecord(pid);

        PersistentIdentifierAlphaNumeric newpid = api.getAlphaNumericPID(evilOrganization, "www.dertest.org");

        try {
            Thread.sleep(1000);                 //1000 milliseconds is one second.
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        api.updateURI(newpid.getIdentifier(), "this is updated");


    }
}
