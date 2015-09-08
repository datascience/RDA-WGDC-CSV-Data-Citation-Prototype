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


package at.stefanproell;

import at.stefanproell.ResultSetVerification.ResultSetVerificationAPI;

import java.sql.ResultSet;

/**
 * Hello world!
 */
public class ResultSetTestApp {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        ResultSetVerificationAPI api = new ResultSetVerificationAPI();

        String input = "SELECT sensor_id, sensor_name,measurement_value FROM sensors WHERE sensor_name LIKE " +
                "temperature_lab_13";

        String sha1 = api.calculateSHA1HashFromString(input);
        System.out.println( sha1+ " length: " + sha1.length() );
        String sha2 = api.calculateSHA256HashFromString(input);
        System.out.println( sha2+ " length: " + sha2.length() );
        String sha512 = api.calculateSHA512HashFromString(input);
        System.out.println(sha512+ " length: " + sha512.length());

        String md5 = api.calculateMD5HashFromString(input);
        System.out.println( md5+ " length: " + md5.length() );



        System.exit(0);

    }
}
