package at.stefanproell.PersistentIdentifierRestfulService;


import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifier;
import at.stefanproell.PersistentIdentifierMockup.PersistentIdentifierAPI;
import com.google.gson.Gson;

import javax.ws.rs.*;
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
    public String resolveMetadataPage(@PathParam("uri") String fqn, @PathParam("ark") String arkLabel,
                                      @QueryParam("metadataRequestType") String metadataRequestType) {
        this.logger.info("URI INFO: " + uriInfo.getPath());
        this.logger.info("FQN INFO: " + fqn);
        this.logger.info("metadataRequestType " + metadataRequestType);
        this.logger.info("ark label: " + arkLabel);
        PersistentIdentifier pid = null;
        boolean isSimpleMetadataRequest = this.pidAPI.isSimpleMetadataRequest(fqn);
        boolean isExtendedMetadataRequest = this.pidAPI.isExtendedMetadataRequest(fqn);
        fqn = this.pidAPI.removeARKLabelFromString(arkLabel, fqn);
        fqn = this.pidAPI.removeQuestionMarksFromFQN(fqn);

        if (metadataRequestType == null) {
            this.logger.warning("No metadata request type specified. using standar: simplre");
            metadataRequestType = "simple";
        }


        if (fqn == "") {
            return "No proper idenfier url provided.";
        } else {
            if (fqn.endsWith("/")) {
                if (fqn.length() > 0 && fqn.charAt(fqn.length() - 1) == '/') {
                    fqn = fqn.substring(0, fqn.length() - 1);
                }

            }

            if ((metadataRequestType.equals("") == false)) {

                if (metadataRequestType.equals
                        ("simple")) {
                    isSimpleMetadataRequest = true;
                    this.logger.info("Simple metadata request detected");
                } else if (metadataRequestType.equals("extended")) {
                    isExtendedMetadataRequest = true;
                    this.logger.info("Extended metadata request detected");

                }
            }

            pid = this.pidAPI.resolveIdentifierFromFQNIdentifier(fqn);
        }


        if (pid != null) {
            if (isSimpleMetadataRequest) {
                return this.pidAPI.getSimpleMetadataAsJSON(pid);

            } else if (isExtendedMetadataRequest) {
                return this.pidAPI.getExtendedMetadataAsJSON(pid);
            }

        } else {
            Map<String, String> errormap = new HashMap<String, String>();
            errormap.put("error message", "PID does not exist");
            Gson gson = new Gson();
            String json = gson.toJson(errormap);
            return json;
        }

        return null;

    }


}
