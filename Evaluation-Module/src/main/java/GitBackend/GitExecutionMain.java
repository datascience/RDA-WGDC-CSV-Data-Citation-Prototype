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

package GitBackend;

import org.eclipse.jgit.revwalk.RevCommit;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class GitExecutionMain {

    public static void main(String[] args) {
        GitAPI api = new GitAPI();

        String repoPath = "/home/stefan/Test/Bild-Git";
        String localPath = "/tmp/localgit";
        String dataSetFileName = "testimage.jpg";


        // api.setLocalRepositoryPath(localPath);
        // api.setRemoteRepositoryPath(repoPath);
        // api.initRepository();


        DateTime dt = new DateTime("2015-08-27T18:21:40");
        Date execDate = dt.toDate();

//    api.printAllCommitsForFile(execDate,dataSetFileName);
        RevCommit commit = api.getMostRecentCommit(execDate, dataSetFileName);
        //api.retrieveFileFromCommit(dataSetFileName,commit,"/tmp/yayyay");

    }

}


