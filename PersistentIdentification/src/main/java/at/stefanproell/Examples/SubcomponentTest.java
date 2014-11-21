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

package at.stefanproell.Examples;

import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAlphaNumeric;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierSubcomponent;

/**
 * Created by stefan on 20.11.14.
 */
public class SubcomponentTest {

    public static void main(String[] args) {
        System.out.println("API Mini Test");
        SubcomponentTest apiTest = new SubcomponentTest();

        apiTest.one();

        System.exit(0);


    }

    private void one() {
        PersistentIdentifierAPI api = new PersistentIdentifierAPI();
        Organization org = api.getOrganizationObjectByPrefix(6789);
        PersistentIdentifierAlphaNumeric newpid = api.getAlphaNumericPID(org, "URL1");
        PersistentIdentifierAlphaNumeric newpid2 = api.getAlphaNumericPID(org, "URL2");
        PersistentIdentifierSubcomponent sub = api.getSubComponentAlphaNummeric(newpid, "http://subcomponent");
        PersistentIdentifierSubcomponent sub2 = api.getSubComponentAlphaNummeric(newpid, "http://other_subcomponent");
        PersistentIdentifierSubcomponent subsub = api.getSubComponentAlphaNummeric(sub, "http://subsub");
    }
}
