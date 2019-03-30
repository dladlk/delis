package dk.erst.delis.rest;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Set;

@Api
@RestController
@RequestMapping("/rest/open")
public class IdentifierCheckRestController {

    private static final Logger log = LoggerFactory.getLogger(IdentifierResolverService.class);
    public static final String UTF_8 = "utf-8";
    public static final String OK = "ok";
    public static final String REASON_HEADER = "reason";

    @Autowired
    private IdentifierResolverService identifierResolverService;

    @Autowired
    private OrganisationSetupService organisationSetupService;

    @RequestMapping(value = "/receivercheck/{identifier}/{service}/{action}", method = RequestMethod.GET)
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

        Result result = checkIdentifier(identifier, compoundIdentifier);

        if (result.status == HttpStatus.OK && identifier != null) {
            result = checkServiceAction(identifier, service, action);
        }

        long stopTime = new Date().getTime();
        log.info("Stop checkIdentifier. Execution time: " + (stopTime - startTime) + " ms.");

        ResponseEntity response;
        if (result.status == HttpStatus.OK) {
            response = ResponseEntity.ok().build();
        } else {
            response = ResponseEntity.noContent().header(REASON_HEADER, result.description).build();
        }
        return response;
    }

    private Identifier getIdentifier(String compoundIdentifier) {
        Identifier identifier = null;

        int index = compoundIdentifier.lastIndexOf(":");

        if (index == -1) {
            index = 0;
            log.warn("Passed identifier value does not contain 'type' value - there is no ':' separator found in '" + compoundIdentifier + "'");
        }

        String type = compoundIdentifier.substring(0, index);
        String id = compoundIdentifier.substring(index+1);

        log.info("Identifier type: " + type + " Identifier id: " + id);
        identifier = identifierResolverService.resolve(type, id);
        String idName = identifier == null ? "null" : identifier.getName();
        log.info("Found identifier: " + idName);
        return identifier;
    }

    private Result checkIdentifier(Identifier identifier, String compoundIdentifier) {
        HttpStatus status = HttpStatus.OK;
        String description = OK;
        log.info("Check identifier");
        if (identifier == null) {
            description = "Identifier '"+compoundIdentifier+"' does not exist";
            log.info(description);
            status = getFailedStatus();
        } else if (identifier.getStatus() == IdentifierStatus.DELETED) {
            description = "Identifier " + compoundIdentifier + " marked as deleted";
            log.info(description);
            status = getFailedStatus();
        }
        log.info("Identifier '"+compoundIdentifier+"' check performed. " + description);
        return new Result(status, description);
    }

    private Result checkServiceAction(Identifier identifier, String service, String action) {
        HttpStatus status = HttpStatus.OK;
        String description = OK;
        log.info("Check action: " + action);
        Organisation organisation = identifier.getOrganisation();
        OrganisationSetupData setupData = organisationSetupService.load(organisation);
        Set<OrganisationSubscriptionProfileGroup> profileSet = setupData.getSubscribeProfileSet();

        boolean found = false;
        for(OrganisationSubscriptionProfileGroup profileGroup : profileSet) {
            String processId = profileGroup.getProcessId();
            if (processId.equalsIgnoreCase(service)) {
                log.info("Service found");
                String[] documentIdentifiers = profileGroup.getDocumentIdentifiers();
                for (String documentId : documentIdentifiers) {
                    if (action.endsWith(documentId)) {
                        log.info("Action found");
                        found = true;
                        break;
                    }
                }
            }
            if (found) {
                break;
            }
        }

        if (!found) {
            description = "Service/Action '" + service + " / " + action + "' not found for identifier " + identifier.getValue();
            log.info(description);
            status = getFailedStatus();
        }

        log.info("Action check done");
        return new Result(status, description);
    }

    private HttpStatus getFailedStatus() {
        return HttpStatus.NO_CONTENT;
    }

    private class Result {
        final HttpStatus status;
        final String description;

        public Result(HttpStatus status, String description) {
            this.status = status;
            this.description = description;
        }
    }
}