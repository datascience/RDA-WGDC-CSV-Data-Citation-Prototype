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

package at.stefanproell.PersistentIdentifierRestfulService;

import at.stefanproell.PersistentIdentifierMockup.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.net.URI;
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

    private static String SIMPLE = "simple";
    private static String EXTENDED = "extended";

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


    /**
     * Resolver for standard and hierarchical PIDs. The ark label is optional. Both provided arks would resolve to the
     * same url: http://localhost:8080/pid/service/resolver/6789/7WwLdBc7Jvwh and
     * http://localhost:8080/pid/service/resolver/ark:/6789/7WwLdBc7Jvwh
     * <p/>
     * If a URL starts with an ark label, this label gets removed.
     *
     * @param fqn
     * @return
     */
    @GET
    @Path("{ark:(/ark:/[^/]+?)?}{ uri: (.+)?}")
    @Produces(MediaType.TEXT_PLAIN)
    public String resolve(@PathParam("uri") String fqn, @PathParam("ark") String arkLabel, @Context
    UriInfo ui, @Context HttpServletRequest hsr, @Context final HttpServletResponse response) {
        this.logger.info("URI INFO: " + uriInfo.getPath() + " uri host " + uriInfo.getBaseUri());
        this.logger.info("FQN INFO: " + fqn);

        String currentMetadataType = null;
        boolean forwardRequestToLandingPage = false;


        if (hsr.getQueryString() == null) {
            this.logger.info("Normal link");

        } else if (hsr.getQueryString().equals("")) {
            this.logger.info("one ?");
            currentMetadataType = SIMPLE;
            forwardRequestToLandingPage = true;


        } else if (hsr.getQueryString().equals("?")) {
            this.logger.info("Two ??");
            currentMetadataType = EXTENDED;
            forwardRequestToLandingPage = true;
        } else {
            this.logger.info("None of the above");
        }

        if (forwardRequestToLandingPage) {
            this.logger.info("Redirecting! ");
            // Get the base uri, e.g.http://localhost:8080/pid/service/
            UriBuilder baseURIbuilder = uriInfo.getBaseUriBuilder();
            baseURIbuilder.queryParam("metadataRequestType", "extended");
            baseURIbuilder.path("landing/" + fqn);
            URI newURI = baseURIbuilder.build();

            this.logger.info("New URI is: " + newURI.toString());


            try {
                response.sendRedirect(newURI.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return "forwarded";

        }


        if (fqn == "") {
            return "No proper idenfier url provided.";
        } else {
            fqn = this.pidAPI.removeARKLabelFromString(arkLabel, fqn);
            if (fqn.endsWith("?")) {
                this.logger.info("This is a metadata request!");
            }
        }


        String prefix = null;
        prefix = this.pidAPI.getOrganizationPrefixFromURL(fqn);
        this.logger.info("The prefix parsed was: " + prefix);

        String URLstring = null;
        // Get the URL from the PID
        URLstring = this.pidAPI.resolveIdentifierToURIFromFQNIdentifier(fqn);

        if (URLstring != null) {
            return URLstring;

        } else {
            // If there is a prefix, check if it actually exists
            if (prefix != null) {
                if (this.pidAPI.checkOrganizationPrefix(Integer.parseInt(prefix)) == false) {
                    return "This organization prefix is not registered.";
                } else {

                }
            }
            return "This PID does not exist for this organization or your provided identifier is not formated " +
                    "correctly. ";
        }


    }


}

