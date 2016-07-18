/*
 * Copyright [2016] [Stefan Pröll]
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

package Database.InitializeDatabase;


import Database.Authentication.UserAPI;
import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;

/**
 * Created by stefan on 20.06.16.
 */
public class InitializeMain {
    public static void main(String[] args) {
        UserAPI userAPI = new UserAPI();
        userAPI.addUser("stefan", "Stefan", "Proell", "stefan15", 12345);
        userAPI.addUser("andreas", "Andreas", "Rauber", "andreas15", 12345);

        PersistentIdentifierAPI pidAPI = new PersistentIdentifierAPI();


        // create a dummy organization and provide a prefix
        Organization org = pidAPI.createNewOrganitation("Demo Organization", 12345);


    }
}
