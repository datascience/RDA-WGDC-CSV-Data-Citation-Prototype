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

package at.stefanproell.PersistentIdentifierRestfulService;


import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import com.google.gson.Gson;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Path("/landing")
public class LandingPageService {

    private Logger logger;
    private PersistentIdentifierAPI pidAPI = null;
    @Context
    UriInfo uriInfo;

    public LandingPageService() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Landing page constructor");
        pidAPI = new PersistentIdentifierAPI();
    }

    /**
     * Landing page
     * <p/>
     * If a URL starts with an ark label, this label gets removed.
     *
     * @param fqn
     * @return
     */
    @GET
    @Path("{ark:(/ark:/[^/]+?)?}{ uri: (.+)?}")
    @Produces("application/json")
    public String resolveMetadataPage(@PathParam("uri") String fqn, @PathParam("ark") String arkLabel) {
        this.logger.info("URI INFO: " + uriInfo.getPath());
        this.logger.info("FQN INFO: " + fqn);
        PersistentIdentifier pid = null;
        boolean isSimpleMetadataRequest = this.pidAPI.isSimpleMetadataRequest(fqn);
        boolean isExtendedMetadataRequest = this.pidAPI.isExtendedMetadataRequest(fqn);
        fqn = this.pidAPI.removeARKLabelFromString(arkLabel, fqn);
        fqn = this.pidAPI.removeQuestionMarksFromFQN(fqn);

        if (fqn == "") {
            return "No proper idenfier url provided.";
        } else {

            pid = this.pidAPI.resolveIdentifierFromFQNIdentifier(fqn);
        }


        if (pid != null) {
            if (isSimpleMetadataRequest) {
                return this.pidAPI.getSimpleMetadataAsJSON(pid);

            } else if (isExtendedMetadataRequest) {
                return this.pidAPI.getExtendedMetadataAsJSON(pid);
            }

        } else {
            Map<String, String> errormap = null;
            errormap.put("error message", "PID does not exist");
            Gson gson = new Gson();
            String json = gson.toJson(errormap);
            return json;
        }

        return null;

    }


}
