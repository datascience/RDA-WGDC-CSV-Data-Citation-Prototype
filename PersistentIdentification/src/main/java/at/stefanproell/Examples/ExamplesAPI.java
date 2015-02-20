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

import at.stefanproell.PersistentIdentifierMockup.*;

/**
 * Examples how to use the API. Start reading here.
 */
public class ExamplesAPI {
    public static void main(String[] args) {
        System.out.println("Persistent Identifier Mockup");
        ExamplesAPI apiTest = new ExamplesAPI();
        apiTest.run();
        System.exit(0);


    }

    public void run() {
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();


        // create a dummy organization and provide a prefix
        Organization evilOrganization = api.createNewOrganitation("Evil Corp", 334455);
        Organization theOrganization = api.createNewOrganitation("The Company", 10101);
        Organization goodOrganization = api.createNewOrganitation("Good Company", 55667);
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
