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

package at.stefanproell.Examples;

/**
 * Created by stefan on 13.02.15.
 */
public class StringTest {

    public static void main(String[] args) {

        String url = "http://localhost:8080/cite/uploadNewCSV.xhtml";
        int start = url.indexOf("uploadNewCSV.xhtml");
        int end = url.lastIndexOf("uploadNewCSV.xhtml");
        System.out.println(start + " " + end + " " + url.substring(start, end));
        System.out.println(url.replace("uploadNewCSV", "landingpage"));


    }


//    landingpage.xhtml?requestPID=null/GOJKBCcAZr
}
