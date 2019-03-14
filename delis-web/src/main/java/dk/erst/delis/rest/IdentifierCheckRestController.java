package dk.erst.delis.rest;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.document.sbdh.Main;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.task.organisation.setup.data.OrganisationSubscriptionProfileGroup;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Set;

@Api
@RestController
@RequestMapping("rest")
public class IdentifierCheckRestController {

    private static final Logger log = LoggerFactory.getLogger(IdentifierResolverService.class);
    public static final String UTF_8 = "utf-8";

    @Autowired
    private IdentifierResolverService identifierResolverService;

    @Autowired
    private OrganisationSetupService organisationSetupService;

    @RequestMapping(value = "/receivercheck/{identifier}/{service}/{action}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity checkIdentifier(@PathVariable("identifier") String compoundIdentifier,
                                          @PathVariable("service") String service,
                                          @PathVariable("action") String action) {

        long startTime = new Date().getTime();
        log.info("Start checkIdentifier.");

        try {
            compoundIdentifier = URLDecoder.decode(compoundIdentifier, UTF_8);
            service = URLDecoder.decode(service, UTF_8);
            action = URLDecoder.decode(action, UTF_8);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
        log.info("Start checkIdentifier. CompoundIdentifier=" + compoundIdentifier + " Service=" + service + " Action=" + action);

        Identifier identifier = getIdentifier(compoundIdentifier);

        HttpStatus status = HttpStatus.OK;

        status = checkIdentifier(identifier, status);

        status = checkService(identifier, service, status);

        status = checkAction(identifier, action, status);

        long stopTime = new Date().getTime();
        log.info("Stop checkIdentifier. Execution time: " + (stopTime - startTime) * 1000 + " seconds.");

        return new ResponseEntity(status);
    }

    private Identifier getIdentifier(@PathVariable("identifier") String compoundIdentifier) {
        Identifier identifier = null;

        int index = compoundIdentifier.lastIndexOf(":");
        String type = compoundIdentifier.substring(0, index);
        String id = compoundIdentifier.substring(index+1);

        identifier = identifierResolverService.resolve(type, id);
        return identifier;
    }

    private HttpStatus checkIdentifier(Identifier identifier, HttpStatus status) {
        log.info("Check identifier");
        if (identifier == null) {
            log.info("Identifier does not exists");
            status = setFailedStatus();
        } else if (identifier.getStatus() == IdentifierStatus.DELETED) {
            log.info("Identifier marked as deleted");
            status = setFailedStatus();
        }
        log.info("Identifier ok");
        return status;
    }

    private HttpStatus setFailedStatus() {
        return HttpStatus.NO_CONTENT;
    }

    private HttpStatus checkService(Identifier identifier, String service, HttpStatus status) {
        log.info("Check service");
        //todo
        log.info("Service ok");
        return status;
    }

    private HttpStatus checkAction(Identifier identifier, String action, HttpStatus status) {
        log.info("Check action: " + action);
        Organisation organisation = identifier.getOrganisation();
        OrganisationSetupData setupData = organisationSetupService.load(organisation);
        Set<OrganisationSubscriptionProfileGroup> profileSet = setupData.getSubscribeProfileSet();

        boolean found = false;
        for(OrganisationSubscriptionProfileGroup profileGrout : profileSet) {
            String[] documentIdentifiers = profileGrout.getDocumentIdentifiers();
            for (String documentId : documentIdentifiers) {
                if (documentId.equalsIgnoreCase(action)) {
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }

        if (!found) {
            log.info("Action '" + action + "' not found");
            status = setFailedStatus();
        }

        log.info("Action check done");
        return status;
    }
}