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

    public IdentifiersService() {
        this.logger = Logger.getLogger(this.getClass().getName());
        this.logger.info("Identifier constructor");
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
