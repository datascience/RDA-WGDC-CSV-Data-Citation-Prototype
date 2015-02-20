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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;

/**
 * This is a test class
 */
@Path("/segments")
public class PathSegments {
    @Context
    UriInfo uriInfo;


    public PathSegments() {

    }

    @GET
    @Path("{ uri: (.+)?}")
    @Produces(MediaType.TEXT_PLAIN)
    public String resolve() {
        String hierarchy = "";
        for (int i = 0; i < uriInfo.getPathSegments().size(); i++) {
            hierarchy += "[" + i + "] " + uriInfo.getPathSegments().get(i);
            if (i < uriInfo.getPathSegments().size() - 1) {
                hierarchy += " --> ";
            }

        }
        return hierarchy;
    }
}