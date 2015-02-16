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

package Database.Authentication;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class CreateUsersExample {
    public static void main(String[] args) {

        CreateUsersExample userTest = new CreateUsersExample();
        userTest.run();
        System.exit(0);


    }

    public void run() {
        UserAPI api = new UserAPI();
        api.addUser("stefan", "Stefan", "Proell", "stefan15", 12345);
        api.addUser("andreas", "Andreas", "Rauber", "andreas15", 12345);
        api.addUser("natalie", "Natalie", "Harrower", "natalie15", 12345);
        api.addUser("kathrin", "Kathrin", "Beck", "katrin15", 12345);
        api.addUser("catherine", "Catherine", "Jones", "catherine15", 12345);
        api.addUser("martti", "Martti", "Louhivouri", "martti15", 12345);
        api.addUser("odile", "Odile", "Francoise", "odile15", 12345);
        api.addUser("dimitris", "Dimitris", "Gavrilis", "dimitris15", 12345);
        api.addUser("thomas", "thomas", "thomas", "thomas15", 12345);
        api.addUser("pavel", "Pavel", "Stranak", "pavel15", 12345);
        api.addUser("fabrizio", "fabrizio", "fabrizio", "fabrizio15", 12345);
        api.addUser("pilar", "Pilar", "Callau", "pilar15", 12345);
        api.addUser("mark", "mark", "mark", "mark15", 12345);
        api.addUser("patrice", "Patrice", "Ajai-Ajagbe", "patrice15", 12345);

        api.addUser("user1", "firstname1", "lastname1", "user15", 12345);
        api.addUser("user2", "firstname2", "lastname2", "user15", 12345);
        api.addUser("user3", "firstname3", "lastname3", "user15", 12345);
        api.addUser("user4", "firstname4", "lastname4", "user15", 12345);
        api.addUser("user5", "firstname5", "lastname5", "user15", 12345);
        api.addUser("user6", "firstname6", "lastname6", "user15", 12345);
        api.addUser("user7", "firstname7", "lastname7", "user15", 12345);
        api.addUser("user8", "firstname8", "lastname8", "user15", 12345);
        api.addUser("user9", "firstname9", "lastname9", "user15", 12345);
        api.addUser("user10", "firstname10", "lastname10", "user15", 12345);

        api.addUser("artur", "Artur", "Kulmukhametov", "artur15", 12345);
        api.addUser("kresimir", "Kresimir", "Duretec", "kresimir15", 12345);
        api.addUser("thomas", "Thomas", "Lidy", "thomas15", 12345);
        api.addUser("david", "David", "Wang", "david15", 12345);
        

        System.out.println("Done");



    }
}
