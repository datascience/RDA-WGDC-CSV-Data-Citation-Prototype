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

package at.stefanproell.TomcatAuthentication;

/**
 * SCAPE-QueryStore
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class UserTest {
    public static void main(String[] args) {
        System.out.println("Persistent Identifier Mockup");
        UserTest u = new UserTest();
        u.testUsers();
        System.exit(0);


    }

    private void testUsers() {
        UserAPI userapi = new UserAPI();
        UserDetails u1 = userapi.createNewUser("stefanuser", "hallo");
        GroupDetails g = userapi.createnewGroup("arkusers");
        userapi.addUserToGroup(u1, g);


    }

}
