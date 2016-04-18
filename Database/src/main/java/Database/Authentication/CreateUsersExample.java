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
     /*   api.addUser("stefan", "Stefan", "Proell", "stefan15", 12345);
        api.addUser("andreas", "Andreas", "Rauber", "andreas15", 12345);
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
*/
        api.addUser("thomas", "Thomas", "Hodge", "thomas15", 12345);
        api.addUser("thaer", "Thaer", "Samar", "thaer15", 12345);
        api.addUser("seamus","Seamus","Ross","seamus15",12345);
        api.addUser("pauline","Pauline","Oddrun","pauline15",12345);
        api.addUser("neil","Neil","Fitzgerald","neil15",12345);
        api.addUser("myriam","Myriam","Traub","myriam15",12345);
        api.addUser("jonathan","Jonathan","Moest","jonathan15",12345);
        api.addUser("mohammed","Mohammed","Minout","mohammed15",12345);
        api.addUser("mijung","Mijung","Lee","mijung15",12345);
        api.addUser("malgorzata","Malgorzata","Rożniakowska-Kłosińska","malgorzata15",12345);
        api.addUser("jaroslaw","Jaroslaw","Gryz","jaroslaw15",12345);
        api.addUser("helene","Helene","de Ribaupierre,","helene15",12345);
        api.addUser("kim","Kim","Hyunsook","kim15",12345);
        api.addUser("dariusz","Dariusz","Paradowski","dariusz15",12345);
        api.addUser("christian","Christian","Jacobs","christian15",12345);
        api.addUser("brigitte","Brigitte","Mathiak","brigitte15",12345);
        api.addUser("benoit","Benoît","Pauwels","benoit15",12345);
        System.out.println("Done");


    }
}
