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

import at.stefanproell.PersistentIdentifierMockup.*;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Enumeration;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by stefan on 19.11.14.
 */
@Path("/resolver")
public class ResolverService {
    private Logger logger;
    private PersistentIdentifierAPI pidAPI = null;
    @Context
    UriInfo uriInfo;

    public ResolverService() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Resolver constructor");
        pidAPI = new PersistentIdentifierAPI();
    }

    /**
     * Create a new identifier. Provide the organizational prefix and thr type: alpha, alphanum, numeric
     *
     * @param organizationPrefix
     * @param identifier
     * @return
     */
    @GET
    @Path("/{organizationPrefix}/{identifier}*")
    @Produces(MediaType.TEXT_PLAIN)


    public String addIdentifier(@PathParam("organizationPrefix") int organizationPrefix, @PathParam("identifier") String identifier) {
        this.logger.info("URI INFO: " + uriInfo.getPath());
        Organization org = null;

        if (organizationPrefix > 0) {
            if (this.pidAPI.checkOrganizationPrefix(organizationPrefix)) {
                this.logger.info("The prefix exists.");
                org = this.pidAPI.getOrganizationObjectByPrefix(organizationPrefix);

                this.logger.info("Provided parameter:" + identifier);
                if (identifier != null) {
                    String URL = this.pidAPI.resolveIdentifierToURIFromFQNIdentifier(organizationPrefix + "/" + identifier);
                    if (URL != null) {
                        return URL;
                    } else {
                        return "This identifier does not exist! Error";
                    }


                } else
                    return "No identifier provided";


            }
            return "Prefix does not exist";


        }
        return "Invalid organizational prefix";


    }


    @GET
    @Path("{ uri: (.+)?}")
    @Produces(MediaType.TEXT_PLAIN)
    public String resolve(@PathParam("uri") String fqn) {
        this.logger.info("URI INFO: " + uriInfo.getPath());
        this.logger.info("FQN INFO: " + fqn);
        if (fqn == null) {
            return "No proper idenfier url provided.";
        }


        int prefix = this.pidAPI.getOrganizationPrefixFromURL(fqn);
        String URLstring = null;
        URLstring = this.pidAPI.resolveIdentifierToURIFromFQNIdentifier(fqn);

        if (URLstring != null) {
            return URLstring;

        } else {
            return "This PID does not exist";
        }


    }


}

