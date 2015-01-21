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

package Bean;

import javax.faces.context.FacesContext;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * CSV-DataCitation
 * Created by stefan
 * {MONTH_NAME_FULL} {YEAR}
 */
public class SessionManager {
    private Logger logger;


    public SessionManager() {
        this.logger = Logger.getLogger(this.getClass().getName());
    }

    public void printSessionVariables() {
        Map<String, Object> sessionMap = this.getSessionMap();
        this.logger.info("Stored session variables (keys)");

        if (sessionMap != null) {
            for (Map.Entry<String, Object> entry : sessionMap.entrySet()) {
                this.logger.info(entry.getKey());
            }
        }


    }

    /**
     * Store details in session
     */
    protected void storeSessionData(String key, String value) {
        System.out.println("Writing data into session: Key " + key + "  Value:  " + value);

        if (FacesContext.getCurrentInstance() != null) {
            Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

            // schreiben

            session.put(key, value);

        }


    }

    /*
    * Get session data
    * * * */
    protected Map<String, Object> getSessionMap() {
        Map<String, Object> sessionMAP = null;
        if (FacesContext.getCurrentInstance() != null) {
            sessionMAP = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
            if (sessionMAP == null) {
                this.logger.severe("No session data");
                return sessionMAP;
            }
        }
        return sessionMAP;


    }
}
