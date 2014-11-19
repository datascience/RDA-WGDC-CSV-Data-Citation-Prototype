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

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Root resource (exposed at "myresource" path)
 */
@Path("/identifiers")
public class IdentifiersService {

    private Logger logger;
    private PersistentIdentifierAPI pidAPI = null;
    private static String HARDCODED_URL = "http://localhost:8080/pid/service/identifiers/";
    public IdentifiersService() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Identifier constructor");
        pidAPI = new PersistentIdentifierAPI();
    }

    /**
     * Method handling HTTP GET requests. The returned object will be sent
     * to the client as "text/plain" media type.
     *
     * @return String that will be returned as a text/plain response.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String get(@Context UriInfo ui) {
        MultivaluedMap<String, String> queryParams = ui.getQueryParameters();
        MultivaluedMap<String, String> pathParams = ui.getPathParameters();

        printMap(queryParams);
        printMap(pathParams);


        return "ok";

    }

    @GET
    @Path("/sayname")
    @Produces(MediaType.TEXT_PLAIN)
    public String sayHello(@QueryParam("name") String name) {
        if (name != null) {
            // if the query parameter "name" is there
            return "Hello " + name + "!";
        }
        return "Hello World!";
    }

    @GET
    @Path("/details/{identifier}")
    @Produces(MediaType.TEXT_PLAIN)
    public String getIdentifier(@PathParam("identifier") String identifier) {
        if (identifier != null) {
            // if the query parameter "name" is there
            return "Show details for " + identifier;
        }
        return "No id provided";
    }

    /**
     * Create a new identifier. Provide the organizational prefix and thr type: alpha, alphanum, numeric
     *
     * @param organizationPrefix
     * @param identifierType
     * @return
     */
    @GET
    @Path("/new/{organizationPrefix}/{type}")
    @Produces(MediaType.TEXT_PLAIN)
    public String addIdentifier(@PathParam("organizationPrefix") int organizationPrefix, @PathParam("type") String identifierType) {
        Organization org = null;

        if (organizationPrefix > 0) {
            if (this.pidAPI.checkOrganizationPrefix(organizationPrefix)) {
                this.logger.info("The prefix exists.");
                org = this.pidAPI.getOrganizationObjectByPrefix(organizationPrefix);
                ;

                this.logger.info("Provided parameter:" + identifierType);
                if (identifierType != null) {
                    switch (identifierType) {
                        case "alpha":
                            this.logger.info("Create a new alpha identifier");

                            PersistentIdentifierAlpha alphaPID = this.pidAPI.getAlphaPID(org, HARDCODED_URL);
                            this.pidAPI.updateURI(alphaPID.getIdentifier(), HARDCODED_URL + org.getOrganization_prefix() + "/" + alphaPID.getIdentifier());
                            return org.getOrganization_prefix() + "/" + alphaPID.getIdentifier();

                        case "alphanum":
                            this.logger.info("Create a new alpha nummerical identifier");
                            PersistentIdentifierAlphaNumeric alphaNumericPID = this.pidAPI.getAlphaNumericPID(org, HARDCODED_URL);
                            this.pidAPI.updateURI(alphaNumericPID.getIdentifier(), HARDCODED_URL + org.getOrganization_prefix() + "/" + alphaNumericPID.getIdentifier());
                            return org.getOrganization_prefix() + "/" + alphaNumericPID.getIdentifier();

                        case "numeric":
                            this.logger.info("Create a new numeric identifier");
                            PersistentIdentifierNumeric numericPID = this.pidAPI.getNumericPID(org, HARDCODED_URL);
                            this.pidAPI.updateURI(numericPID.getIdentifier(), HARDCODED_URL + org.getOrganization_prefix() + "/" + numericPID.getIdentifier());
                            return org.getOrganization_prefix() + "/" + numericPID.getIdentifier();


                    }

                } else {

                }
                return "No identifier type provided";


            }
            return "Prefix does not exist";


        }
        return "Invalid organizational prefix";


    }


    /**
     * Print parameter map
     *
     * @param mp
     */
    public void printMap(Map mp) {
        Iterator it = mp.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry) it.next();
            this.logger.info("Key: " + pairs.getKey() + " = " + pairs.getValue());
            //     it.remove(); // avoids a ConcurrentModificationException
        }
    }
}
