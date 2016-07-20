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

package Helpers;

import Evaluation.EvaluationAPI;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.core.Logger;

import java.io.File;
import java.io.IOException;

/**
 * GitCSV
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class FileHelper {
    private java.util.logging.Logger logger;

    public FileHelper() {
        logger = java.util.logging.Logger.getLogger(FileHelper.class.getName());
    }


    public int getFileFolderSize(File dir) {
        int size = 0;
        if (dir.isDirectory()) {
            for (File file : dir.listFiles()) {

                if (file.isFile()) {
                    size += file.length();
                } else {
                    size += getFileFolderSize(file);
                }

            }
        } else if (dir.isFile()) {
            size += dir.length();
        }
        if(size<0){
            logger.severe("Negative size");
        }
        return size;
    }

    public int getFileSize(File file) {
        int size = 0;
        if (file.isFile()) {
            size = (int) file.length();
        }
        return size;
    }

    public void copyFile(File source, File dest) throws IOException {
        FileUtils.copyFile(source, dest);
    }

    public void deleteDirectory(File path) {
        if (path.exists()) {
            try {
                File f = path;
                FileUtils.cleanDirectory(f); //clean out directory (this is optional -- but good know)
                FileUtils.forceDelete(f); //delete directory
                FileUtils.forceMkdir(f); //create directory
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void createDirectory(File path) {


        File theDir = path;

// if the directory does not exist, create it
        if (!theDir.exists()) {

            boolean result = false;

            try {
                theDir.mkdir();
                result = true;
            } catch (SecurityException se) {
                //handle it
            }
            if (result) {
                System.out.println("DIR created");
            }
        }


    }
}



