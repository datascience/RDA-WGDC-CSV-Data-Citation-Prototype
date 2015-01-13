package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.*;

/**
 * Examples how to use the API. Start reading here.
 */
public class ExamplesAPI {
    public static void main(String[] args) {
        System.out.println("Persistent Identifier Mockup Test");
        ExamplesAPI apiTest = new ExamplesAPI();
        apiTest.run();
        System.exit(0);


    }

    public void run() {
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();


        // create a dummy organization and provide a prefix
        Organization evilOrganization = api.createNewOrganitation("Evil Corp", 23456);
        Organization goodOrganization = api.createNewOrganitation("Good Company", 56789);
        // set the length for alphanumeric identifiers
        evilOrganization.setAlphanumericPIDlength(20);
        goodOrganization.setAlphanumericPIDlength(12);

        // create identifiers and print details
        PersistentIdentifierAlphaNumeric alphaNum = api.getAlphaNumericPID(evilOrganization,
                "www.repository.org/collections/datasets/ResearchData.csv");
        api.printRecord(alphaNum);

        PersistentIdentifierNumeric numeric = api.getNumericPID(evilOrganization,
                "www.repository.org/collections/datasets/QuerySet");
        api.printRecord(numeric);

        PersistentIdentifierAlpha alpha = api.getAlphaPID(evilOrganization, "www.repository.org/documentation/manual" +
                ".pdf");
        api.printRecord(alpha);

    }
}
