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
