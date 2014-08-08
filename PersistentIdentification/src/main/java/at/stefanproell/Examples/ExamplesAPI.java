package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.*;

import java.util.List;

/**
 * Examples how to use the API. Start reading here.
 *
 */
public class ExamplesAPI
{
    public static void main( String[] args )
    {
        System.out.println( "Persistent Identifier Mockup" );
        ExamplesAPI apiTest = new ExamplesAPI();
        apiTest.run();
        System.exit(0);


    }

    public void run(){
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();


        // create a dummy organization and provide a prefix
        Organization evilOrganization = api.createNewOrganitation("Evil Corp",2345);
        Organization goodOrganization = api.createNewOrganitation("Good Company",6789);
        // set the length for alphanumeric identifiers
        evilOrganization.setAlphanumericPIDlength(20);
        goodOrganization.setAlphanumericPIDlength(12);

        // create identifiers
        api.getAlphaNumericPID(evilOrganization, "www.repository.org/collections/datasets/ResearchData.csv");
        api.getNumericPID(evilOrganization, "www.repository.org/collections/datasets/QuerySet");
        api.getAlphaPID(evilOrganization, "www.repository.org/documentation/manual.pdf");


        // create 100 alphanumeric identifiers
        for (int i = 0; i < 100; i++){
            api.getAlphaNumericPID(goodOrganization,"www.goodorg.com/documents/id"+i+".pdf");

        }

        // List all PIDs per organization
        List<PersistentIdentifier> listOfEvilPIDs = api.listAllPIDsOfOrganization(evilOrganization);
        List<PersistentIdentifier> listOfGoodPIDs = api.listAllPIDsOfOrganization(goodOrganization);

        // get latest added PID
        PersistentIdentifier pid = api.getLatestAddedPID();

       // get latest pid from org
        pid = api.getLatestAddedPIDbyOrganization(evilOrganization);
        pid = api.getLatestAddedPIDbyOrganization(goodOrganization);

       // Resolve PID
       String URI = api.resolveIdentifierToURI(pid.getOrganization().getOrganization_prefix(), pid.getIdentifier());

        api.printRecord(pid);

        // Update URI and print record again.

        // Sleep a little
        try {

            Thread.sleep(2000);
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        api.updateURI(pid.getIdentifier(),"www.uri.org/new");
        api.printRecord(pid);



        // retrieve a PID object
        PersistentIdentifier pidRetrieve = api.getPIDObjectFromPIDString(pid.getOrganization().getOrganization_prefix(), pid.getIdentifier());

        // What kind of identifier is it?
        if(pidRetrieve instanceof PersistentIdentifierAlphaNumeric){
            System.out.println("I am a alphanumeric identifier. See: " + pidRetrieve.getIdentifier());
        } else if(pidRetrieve instanceof PersistentIdentifierAlpha){
            System.out.println("I am a identifier consisting only of letters. See: " + pidRetrieve.getIdentifier());
        } if(pidRetrieve instanceof PersistentIdentifierNumeric){
            System.out.println("I am a numeric identifier. See: " + pidRetrieve.getIdentifier());
        }

    }
}
