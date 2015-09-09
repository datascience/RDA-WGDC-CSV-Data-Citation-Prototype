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

package Helpers;/*
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

import java.io.File;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class FileHelpers {
    
    private Logger logger;


    public FileHelpers() {
        this.logger = Logger.getLogger(this.getClass().getName());
        
    }

    /*
    Delete the temporary file
    * 
    * * * */
    public boolean deleteUploadedFile(String path){
        try{

            File file = new File(path);

            if(file.delete()){
                this.logger.info(file.getName() + " is deleted!");
                return true;
                
            }else{
                this.logger.severe("Delete operation is failed.");

            }

        }catch(Exception e){

            e.printStackTrace();

        }
        return false;
        
    }
}
