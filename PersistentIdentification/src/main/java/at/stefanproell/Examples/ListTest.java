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

import java.util.LinkedList;
import java.util.List;

/**
 * Created by stefan on 21.11.14.
 */
public class ListTest {

    public static void main(String[] args) {
        System.out.println("Java runtime version is_: " + getVersion());
        LinkedList<String> list = new LinkedList<String>();
        list.addFirst("Test");

        System.exit(0);


    }


    static String getVersion() {
        String version = System.getProperty("java.version");
        int pos = 0, count = 0;
        for (; pos < version.length() && count < 2; pos++) {
            if (version.charAt(pos) == '.') count++;
        }
        return (version.substring(0, pos));
    }
}
