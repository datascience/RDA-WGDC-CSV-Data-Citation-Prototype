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

import at.stefanproell.DataGenerator.DataGenerator;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevSort;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.joda.time.DateTime;
import org.pmw.tinylog.Logger;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvResultSetWriter;
import org.supercsv.io.ICsvResultSetWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.*;
import java.net.URL;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * GitCSV
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class GitAPI {


    private String commitMessage;

    private Git git = null;

    private java.util.logging.Logger logger;

    public GitAPI() {
        logger = java.util.logging.Logger.getLogger(this.getClass().getName());

    }

    private String localRepositoryPath;
    private String remoteRepositoryPath;
    private Repository repository;
    private DataGenerator dataGenerator;

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

        // this.showLogForFile(this.repository.getDirectory()+path);
        // this.showLog();

        //RevWalk walk = new RevWalk(git.getRepository());
        TreeMap<DateTime, RevCommit> commitsByDate = new TreeMap<DateTime, RevCommit>();

        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().all().call();
            int count = 0;
            for (RevCommit commit : commits) {

                count++;
                DateTime commitTime = new DateTime(commit.getCommitterIdent().getWhen());
                DateTime execTimeDate = new DateTime(execDate);
                // Only add if the timestamp is before
                if (commitTime.compareTo(execTimeDate) <= 0) {
                    commitsByDate.put(commitTime, commit);
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        Iterable<RevCommit> logs = null;

        try {
            logs = git.log()
                    .call();
            logs = git.log()
                    // for all log.all()
                    .addPath(path)
                    .call();
            int count = 0;
            for (RevCommit rev : logs) {
                //System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                count++;
            }


        } catch (GitAPIException e) {
            e.printStackTrace();
        }


        /*



        try {
            walk.markStart(walk.parseCommit(this.repository.resolve(Constants.HEAD)));

            walk.sort(RevSort.COMMIT_TIME_DESC);
         //   walk.setTreeFilter(PathFilter.create(path));

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
        */
        return commitsByDate;
    }

    public RevCommit getMostRecentCommit(Date execDate, String path) {
        TreeMap<DateTime, RevCommit> allCommits = this.getAllCommitsBefore(execDate, path);
        if (allCommits.size() == 0) {
            logger.severe("Error... No commit");
            return null;
        } else {
            RevCommit lastCommit = allCommits.lastEntry().getValue();
            System.out.println("Last entry: " + lastCommit.getName() + " was at " + new DateTime(lastCommit.getCommitterIdent().getWhen()));
            return lastCommit;

        }


    }

    /*
    * Get all commits of a file before a given date
    * */
    public RevCommit getFirstCommit(String path) {

        // this.showLogForFile(this.repository.getDirectory()+path);
        // this.showLog();

        //RevWalk walk = new RevWalk(git.getRepository());
        TreeMap<DateTime, RevCommit> allCommits = new TreeMap<DateTime, RevCommit>();

        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().all().call();
            int count = 0;
            for (RevCommit commit : commits) {

                count++;
                DateTime commitTime = new DateTime(commit.getCommitterIdent().getWhen());
                allCommits.put(commitTime, commit);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return allCommits.firstEntry().getValue();
    }

    /*
    * Get all commits of a file before a given date
    * */
    public Date getFirstCommitDate(String path) {

        // this.showLogForFile(this.repository.getDirectory()+path);
        // this.showLog();

        //RevWalk walk = new RevWalk(git.getRepository());
        TreeMap<DateTime, RevCommit> allCommits = new TreeMap<DateTime, RevCommit>();

        try (Git git = new Git(repository)) {
            Iterable<RevCommit> commits = git.log().all().call();
            int count = 0;
            for (RevCommit commit : commits) {

                count++;
                DateTime commitTime = new DateTime(commit.getCommitterIdent().getWhen());
                allCommits.put(commitTime, commit);

            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        return allCommits.firstEntry().getKey().toDate();
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

        // now we have to get the commit
        RevWalk revWalk = new RevWalk(repository);

// and using commit's tree find the path
        RevTree tree = commit.getTree();
        TreeWalk treeWalk = new TreeWalk(repository);
        try {
            treeWalk.addTree(tree);
            treeWalk.setRecursive(true);
            treeWalk.setFilter(PathFilter.create(path));
            if (!treeWalk.next()) {
                logger.severe("No rev found");
            }
            ObjectId objectId = treeWalk.getObjectId(0);
            ObjectLoader loader = repository.open(objectId);
            InputStream inputStream = loader.openStream();
            this.writeFile(inputStream, outputPath);
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        /*


        if(commit==null){
            logger.severe("Commit was NULL");

        } else {


            TreeWalk treeWalk;
            try {
                treeWalk = TreeWalk.forPath(this.repository, this.repository.getWorkTree().getPath()+path, commit.getTree());
                InputStream inputStream = this.repository.open(treeWalk.getObjectId(0), Constants.OBJ_BLOB).openStream();

                this.writeFile(inputStream, outputPath);
                treeWalk.close(); // use release() in JGit < 4.0

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        */

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
            outputStream.flush();
            outputStream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Done!");

    }

    public Git openRepository(File path) {
        this.git = null;
        if (this.isDirectory(path)) {
            try {
                this.git = Git.open(path);
                Logger.info("Open reposirory " + path.getAbsolutePath());
                this.repository = this.git.getRepository();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return git;

    }

    /**
     * Initialize a new repository
     *
     * @param path
     * @throws IOException
     */
    public void initRepository(String path) throws IOException {
        File localPath = new File(path);
        this.openRepository(localPath);
/*
        // create the directory
        try (Git newGit = Git.init().setDirectory(localPath).call()) {
            this.git = newGit;
            System.out.println("Having repository: " + git.getRepository().getDirectory());
        } catch (GitAPIException e) {
            e.printStackTrace();
        }

        repository = FileRepositoryBuilder.create(new File(localPath.getAbsolutePath(), ".git"));
        */
        try {
            git = Git.init().setDirectory(localPath).call();
            repository = git.getRepository();
            logger.info("Init");

        } catch (GitAPIException e) {
            e.printStackTrace();
        }
        /*
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        repositoryBuilder.setMustExist( true );
        repositoryBuilder.setGitDir(localPath);
        repository = repositoryBuilder.build();
        */



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


    public java.sql.Timestamp addAndCommit(File file, String commitMessage) throws GitAPIException {
        // run the add-call


        // git.status();

        try {
            this.copyFileFromURL(new URL("file://" + file.getAbsolutePath()), new File(repository.getWorkTree().getAbsolutePath()), file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String pattern = file.getName();

        DirCache index = git.add()
                .addFilepattern(pattern)
                .call();


        // and then commit the changes
        RevCommit commit = git.commit()
                .setMessage(commitMessage)
                .call();
        java.sql.Timestamp commitTime = new java.sql.Timestamp(commit.getCommitTime());


        //File dir = repository.getDirectory();
        //    git.status();
        repository.close();
        return commitTime;

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
        File path = this.repository.getDirectory();
        Logger.info("Path: " + path.getAbsolutePath());
        return path;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
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

    public void showLog() {
        Iterable<RevCommit> logs = null;
        try {
            logs = git.log()
                    .call();

            int count = 0;
            for (RevCommit rev : logs) {
                //System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                count++;
            }
            System.out.println("Had " + count + " commits overall on current branch");

            logs = git.log()
                    .add(repository.resolve("HEAD"))
                    .call();
            count = 0;
            for (RevCommit rev : logs) {
                System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                count++;
            }
            System.out.println("Had " + count + " commits overall on test-branch");

            logs = git.log()
                    .all()
                    .call();
            count = 0;
            for (RevCommit rev : logs) {
                //System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                count++;
            }
            System.out.println("Had " + count + " commits overall in repository");

            logs = git.log()
                    // for all log.all()
                    .addPath("readme.me")
                    .call();
            count = 0;
            for (RevCommit rev : logs) {
                //System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                count++;
            }
            System.out.println("Had " + count + " commits on README.md");

            logs = git.log()
                    // for all log.all()
                    .addPath("pom.xml")
                    .call();
            count = 0;
            for (RevCommit rev : logs) {
                //System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                count++;
            }
            System.out.println("Had " + count + " commits on pom.xml");
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IncorrectObjectTypeException e) {
            e.printStackTrace();
        } catch (AmbiguousObjectException e) {
            e.printStackTrace();
        } catch (MissingObjectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showLogForFile(String path) {
        Iterable<RevCommit> logs = null;
        try {
            logs = git.log()
                    .call();

            int count = 0;
            logs = git.log()
                    // for all log.all()
                    .addPath(path)
                    .call();
            count = 0;
            for (RevCommit rev : logs) {
                System.out.println("Commit: " + rev /* + ", name: " + rev.getName() + ", id: " + rev.getId().getName() */);
                count++;
            }
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

    public Date getRandomdateBetweentwoDates(Date start, Date end) {
        DateFormat formatter = new SimpleDateFormat("dd-MMM-yy HH:mm:ss");
        Calendar cal = Calendar.getInstance();


        cal.setTime(start);
        Long value1 = cal.getTimeInMillis();

        cal.setTime(end);
        Long value2 = cal.getTimeInMillis();

        long value3 = (long) (value1 + Math.random() * (value2 - value1));
        cal.setTimeInMillis(value3);
        return cal.getTime();

    }

    public String createSha1(File file) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];

        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        ;

        byte[] mdbytes = md.digest();

        //convert the byte to hex format
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdbytes.length; i++) {
            sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();


    }



}