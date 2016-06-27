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

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.joda.time.DateTime;
import org.pmw.tinylog.Logger;

import java.io.*;
import java.net.URL;
import java.util.*;

/**
 * GitCSV
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class GitAPI {


    private String commitMessage;

    Git git = null;

    public GitAPI() {

    }

    private String localRepositoryPath;
    private String remoteRepositoryPath;
    private Repository repo;

    public String getLocalRepositoryPath() {
        return localRepositoryPath;
    }

    public void setLocalRepositoryPath(String localRepositoryPath) {
        this.localRepositoryPath = localRepositoryPath;
    }

    public String getRemoteRepositoryPath() {
        return remoteRepositoryPath;
    }

    public void setRemoteRepositoryPath(String remoteRepositoryPath) {
        this.remoteRepositoryPath = remoteRepositoryPath;
    }

    public Repository getRepo() {
        return repo;
    }

    public void setRepo(Repository repo) {
        this.repo = repo;
    }


    private static HashMap getRevisionsByLog(Repository repository, String filePath) {


        HashMap commitMap = new HashMap<String, DateTime>();


        Git git = new Git(repository);
        LogCommand logCommand = null;
        try {
            logCommand = git.log()
                    .add(git.getRepository().resolve(Constants.HEAD))
                    .addPath(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (RevCommit revCommit : logCommand.call()) {
                DateTime commitDate = new DateTime(1000L * revCommit.getCommitTime());
                // Store commit hash and date
                commitMap.put(revCommit.getName(), commitDate);
            }
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        return commitMap;
    }


    /* Get most recent commit from a hash map
    * */
    private String getMostRecentCommit(HashMap<String, DateTime> commitMap, DateTime execDate) {
        Iterator it = commitMap.entrySet().iterator();
        Map.Entry<String, DateTime> mostRecentCommit = null;
        System.out.println("Number of commits: " + commitMap.size());
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DateTime currentCommitDate = (DateTime) pair.getValue();

            if (mostRecentCommit == null && ((DateTime) pair.getValue()).isBefore(execDate)) {
                System.out.println("most recent war null bei vergleich");
                mostRecentCommit = pair;
            } else if (currentCommitDate.isBefore(execDate)) {
                System.out.println("Current date is before exec");
                if (currentCommitDate.isAfter(mostRecentCommit.getValue())) {
                    System.out.println("Current date is before exec and after the most recent one");
                    mostRecentCommit = pair;
                }
            }

        }

        System.out.println("Current most recent:  " + mostRecentCommit.getKey() + " = " + mostRecentCommit.getValue());
        return mostRecentCommit.getKey();

    }


    /*
    * Get the commit of a given file at an exact known date
    * */
    private void getCommitByExactDate(Repository repo, Date execDate, String path) {
        RevWalk walk = new RevWalk(repo);
        try {
            walk.markStart(walk.parseCommit(repo.resolve(Constants.HEAD)));

            walk.sort(RevSort.COMMIT_TIME_DESC);
            walk.setTreeFilter(PathFilter.create(path));

            for (RevCommit commit : walk) {
                if (commit.getCommitterIdent().getWhen().equals(execDate)) {
                    // this is the commit you are looking for
                    walk.parseCommit(commit);
                    System.out.println("Commit found: " + commit.getName());
                    break;
                }
            }
            walk.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
    * Get all commits of a file before a given date
    * */
    private TreeMap<DateTime, RevCommit> getAllCommitsBefore(Date execDate, String path) {
        RevWalk walk = new RevWalk(this.repo);
        TreeMap<DateTime, RevCommit> commitsByDate = new TreeMap<DateTime, RevCommit>();
        try {
            walk.markStart(walk.parseCommit(this.repo.resolve(Constants.HEAD)));

            walk.sort(RevSort.COMMIT_TIME_DESC);
            walk.setTreeFilter(PathFilter.create(path));

            for (RevCommit commit : walk) {
                if (commit.getCommitterIdent().getWhen().before(execDate)) {
                    DateTime commitTime = new DateTime(commit.getCommitterIdent().getWhen());
                    commitsByDate.put(commitTime, commit);

                }
            }
            walk.close();
            System.out.println("Number of valid commits: " + commitsByDate.size());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return commitsByDate;
    }

    public RevCommit getMostRecentCommit(Date execDate, String path) {
        TreeMap<DateTime, RevCommit> allCommits = this.getAllCommitsBefore(execDate, path);
        RevCommit lastCommit = allCommits.lastEntry().getValue();
        System.out.println("Last entry: " + lastCommit.getName());
        return lastCommit;

    }

    /*
    * Open a local repository. If it does not exist, clone the provided repository
    * */
    private Git openOrCreate(File localDirectory, String remoteRepo) throws IOException, GitAPIException {
        Git git = null;
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.addCeilingDirectory(localDirectory);
        repositoryBuilder.findGitDir(localDirectory);
        if (repositoryBuilder.getGitDir() == null) {
            // git = Git.init().setDirectory( gitDirectory.getParentFile() ).call();
            try {


                Git.cloneRepository().setURI(remoteRepo)
                        .setDirectory(localDirectory).call();
            } catch (GitAPIException e) {
                e.printStackTrace();
            }
        } else {
            git = new Git(repositoryBuilder.build());
        }
        return git;
    }

    private void printMap(HashMap<String, DateTime> commitMap) {
        Iterator it = commitMap.entrySet().iterator();

        Map.Entry<String, DateTime> mostRecentCommit = null;
        System.out.println("Number of commits: " + commitMap.size());
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            System.out.println(pair.getKey() + " --- " + pair.getValue());

        }
    }


    /*
    * print the commit map
    * */
    private void printCommitMap(TreeMap<DateTime, RevCommit> commitMap) {
        Iterator it = commitMap.entrySet().iterator();


        System.out.println("Number of commits here: " + commitMap.size());
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            RevCommit commit = (RevCommit) pair.getValue();
            System.out.println(pair.getKey() + " --- " + commit.getName());

        }
    }

    /*
    * Print all commits for a file
    * */
    public void printAllCommitsForFile(Date execDate, String path) {

        this.printCommitMap(this.getAllCommitsBefore(execDate, path));
    }

    public void retrieveFileFromCommit(String path, RevCommit commit, String outputPath) {
        TreeWalk treeWalk = null;
        try {
            treeWalk = TreeWalk.forPath(this.repo, path, commit.getTree());
            InputStream inputStream = this.repo.open(treeWalk.getObjectId(0), Constants.OBJ_BLOB).openStream();

            this.writeFile(inputStream, outputPath);
            treeWalk.close(); // use release() in JGit < 4.0

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /*
    * Write the stream to the disk
    * */
    private void writeFile(InputStream inputStream, String outputPath) {
        OutputStream outputStream =
                null;
        try {
            outputStream = new FileOutputStream(new File(outputPath));
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, read);
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Done!");

    }

    public void openRepository(File path) {
        this.git = null;
        if (this.isDirectory(path)) {
            try {
                this.git = Git.open(path);
                Logger.info("Open reposirory " + path.getAbsolutePath());
                this.repo = this.git.getRepository();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Initialize a new repository
     *
     * @param path
     * @throws IOException
     */
    public void initRepository(File path) throws IOException {


        // create the directory
        try (Git git = Git.init().setDirectory(path).call()) {
            System.out.println("Having repository: " + git.getRepository().getDirectory());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

    }


    public File newTempFolder() throws IOException {
        // prepare a new folder
        File localPath = File.createTempFile("TestGitRepository", "");
        return localPath;
    }

    public void deleteDirectory(File localPath) throws IOException {
        FileUtils.deleteDirectory(localPath);
    }


    public boolean fileExists(File path) {

        if (path.exists() && path.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isDirectory(File path) {
        if (path.exists() && path.isDirectory()) {
            return true;
        } else {
            return false;
        }
    }


    public void addAndCommit(File file) throws GitAPIException {
        // run the add-call


        Git git = new Git(repo);
        git.status();

        git.add()
                .addFilepattern(file.getName())
                .call();


        // and then commit the changes
        git.commit()
                .setMessage(this.getCommitMessage())
                .call();

        File dir = repo.getDirectory();

        repo.close();

    }


    /**
     * Download the file (an image in this example) into the repository.
     *
     * @param sourceURL
     * @param targetPath
     * @return
     * @throws IOException
     */
    public File copyFileFromURL(URL sourceURL, File targetPath, String filename) throws IOException {
        File targetFile = new File(targetPath.getAbsolutePath() + "/" + filename);
        FileUtils.copyURLToFile(sourceURL, targetFile);
        Logger.info("Downloaded file and copied into repository");
        return targetPath;
    }

    public File getRepoPath() {
        File path = this.repo.getDirectory();
        Logger.info("Path: " + path.getAbsolutePath());
        return path;
    }

    public Repository getRepository() {
        return repo;
    }

    public void setRepository(Repository repository) {
        this.repo = repository;
    }

    public String getCommitMessage() {
        return commitMessage;
    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;
    }

    public Git getGit() {
        return git;
    }

    public void setGit(Git git) {
        this.git = git;
    }


}
