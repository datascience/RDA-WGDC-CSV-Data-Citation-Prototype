package at.stefanproell.PersistentIdentifierRestfulService;

import at.stefanproell.PersistentIdentifierMockup.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Root resource (exposed at "identifiers" path)
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
     * Display a help page
     */
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String get(@Context HttpServletRequest req, @Context HttpServletResponse resp) throws IOException,
            ServletException {
        req.setAttribute(this.getClass().getName(), this);
        req.getRequestDispatcher("/WEB-INF/jsp/identifiers.jsp").forward(req, resp);
        return null;
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
    public String addIdentifierGET(@PathParam("organizationPrefix") int organizationPrefix, @PathParam("type") String identifierType) {
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

    @POST
    @Path("/new/{organizationPrefix}/{type}")
    @Produces(MediaType.TEXT_PLAIN)
    public String addIdentifierPOST(@PathParam("organizationPrefix") int organizationPrefix, @PathParam("type") String identifierType) {
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

    @PUT
    @Path("/new/{organizationPrefix}/{type}")
    @Produces(MediaType.TEXT_PLAIN)
    public String addIdentifierPUT(@PathParam("organizationPrefix") int organizationPrefix, @PathParam("type") String identifierType) {
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
