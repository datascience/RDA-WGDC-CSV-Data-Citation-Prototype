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
        api.addUser("carl", "carl");
        api.addUser("stefan", "stefan");
        api.addUser("andi", "andi");


    }
}
