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

import at.stefanproell.PersistentIdentifierMockup.Organization;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import com.google.gson.Gson;
import org.glassfish.jersey.server.mvc.Viewable;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


@Path("/organizations")
public class OrganizationsService {

    private Logger logger;
    PersistentIdentifierAPI pidAPI;

    public OrganizationsService() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Identifier constructor");
        pidAPI = new PersistentIdentifierAPI();

    }

    /**
     * Display a help page
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String get(@Context HttpServletRequest req, @Context HttpServletResponse resp) throws IOException,
            ServletException {
        req.setAttribute(this.getClass().getName(), this);
        req.getRequestDispatcher("/WEB-INF/jsp/organization.jsp").forward(req, resp);
        return null;
    }

    /**
     * List all organizations
     *
     * @return
     */
    @GET
    @Path("/list")
    @Produces("application/json")
    public String getIdentifier() {
        Map<Integer, String> listOfOrganizations = null;
        listOfOrganizations = this.pidAPI.listAllOrganizations();

        Gson gson = new Gson();
        String json = gson.toJson(listOfOrganizations);
        return json;
    }

    /**
     * List all pids per otganization
     *
     * @return
     */
    @GET
    @Path("/list/{prefix}")
    @Produces("application/json")
    public String getAllPIDsFromOrganization(@PathParam("prefix") int organizationPrefix) {

        Organization org = null;
        List<PersistentIdentifier> listOfIdentifiers;
        Map<Integer, String> listOfNumberedIdentifiers = new HashMap();
        ;
        if (organizationPrefix > 0) {
            if (this.pidAPI.checkOrganizationPrefix(organizationPrefix)) {
                this.logger.info("The prefix exists.");
                org = this.pidAPI.getOrganizationObjectByPrefix(organizationPrefix);
                listOfIdentifiers = this.pidAPI.listAllPIDsOfOrganization(org);
                this.logger.info("listofidentifiers size " + listOfIdentifiers.size());
                int pidCounter = 0;
                for (PersistentIdentifier pid : listOfIdentifiers) {
                    pidCounter++;
                    listOfNumberedIdentifiers.put(pidCounter, pid.getIdentifier());

                }
                Gson gson = new Gson();
                String json = gson.toJson(listOfNumberedIdentifiers);
                return json;


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
