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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stefan on 24.11.14.
 */
public class RegexTest {

    public static void main(String[] args) {
        System.out.println("API Mini Test");
        RegexTest reg = new RegexTest();
        reg.runRegex("1234/ab2cf/46gsvvvb2/bbsayy");
        System.exit(0);


    }


    public void runRegex(String txt) {


        String re1 = ".*?";    // Non-greedy match on filler
        String regexPrefix = "(\\d{4})";    // Any Single Digit 1
        String regexSlash = "(\\/)";    // Any Single Character 1
        String regexAlphanum = "[a-zA-Z0-9]*";    // Alphanum 1
        String regexSlashOptional = "(\\/)?";    // Any Single Character 2


        //String re6=".*?";	// Non-greedy match on filler
        //String re7="((?:[a-z][a-z]*[0-9]+[a-z0-9]*))";	// Alphanum 2

        Pattern p = Pattern.compile(regexPrefix + regexSlash + "(" + regexAlphanum + regexSlashOptional + ")*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
        Matcher m = p.matcher(txt);


        while (m.find()) {
            for (int i = 1; i <= m.groupCount(); i++) {
                System.out.println("matched text: " + m.group(i));
                //System.out.println("matched start: " + m.start(i));
                //System.out.println("matched end: " + m.end(i));
            }
        }

    }
}
