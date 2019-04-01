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
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Api
@RestController
@RequestMapping("/rest/open")
public class IdentifierCheckRestController {

    private static final Logger log = LoggerFactory.getLogger(IdentifierResolverService.class);
    public static final String UTF_8 = "utf-8";
    public static final String OK = "ok";
    public static final String REASON_HEADER = "reason";
    public static final String IDENTIFIER_CHECK_STEP_SKIP = "identifier.check.step.skip";

    private final boolean skipServiceStep = skipStep(IdentifierCheckStep.SERVICE);
    private final boolean skipActionStep = skipStep(IdentifierCheckStep.ACTION);

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
        log.info("Skip steps variable status (" + IDENTIFIER_CHECK_STEP_SKIP + ")");
        log.info("Skip Service validation = " + skipServiceStep);
        log.info("Skip Action validation = " + skipActionStep);

        Identifier identifier = getIdentifier(compoundIdentifier);

        Result result = checkIdentifier(identifier, compoundIdentifier);

        if (result.status == HttpStatus.OK && identifier != null) {
            if (skipServiceStep && skipActionStep) {
                log.info("Identifier found. Service and Action validation skipped.");
            } else {
                Set<OrganisationSubscriptionProfileGroup> availableServices = getAvailableServices(identifier, service);
                if (availableServices.size() == 0) {
                    log.info("No Service found for related organisation");
                    result = new Result(HttpStatus.NO_CONTENT, "No Service found for related organisation");
                } else {
                    Set<String> availableActions = getAvailableActions(availableServices, action);
                    if (availableActions.size() == 0) {
                        log.info("No Action found for related organisation");
                        result = new Result(HttpStatus.NO_CONTENT, "No Action found for related organisation");
                    }
                }
            }
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
        String id = compoundIdentifier.substring(index + 1);

        log.info("Identifier type: " + type + " Identifier id: " + id);
        identifier = identifierResolverService.resolve(type, id);
        String idName = identifier == null ? "null" : identifier.getName();
        log.info("Found identifier: " + idName);
        return identifier;
    }

    private Result checkIdentifier(Identifier identifier, String originalIdString) {
        HttpStatus status = HttpStatus.OK;
        String description = OK;
        log.info("Check identifier");
        if (identifier == null) {
            description = "Identifier '" + originalIdString + "' does not exist";
            log.info(description);
            status = getFailedStatus();
        } else if (identifier.getStatus() == IdentifierStatus.DELETED) {
            description = "Identifier " + originalIdString + " marked as deleted";
            log.info(description);
            status = getFailedStatus();
        }
        log.info("Identifier '" + originalIdString + "' check performed. " + description);
        return new Result(status, description);
    }

    private Set<OrganisationSubscriptionProfileGroup> getAvailableServices(Identifier identifier, String service) {
        Set<OrganisationSubscriptionProfileGroup> resultSet = new HashSet<>();

        Organisation organisation = identifier.getOrganisation();
        OrganisationSetupData setupData = organisationSetupService.load(organisation);
        Set<OrganisationSubscriptionProfileGroup> orgSubscribedProfiles = setupData.getSubscribeProfileSet();

        if (skipServiceStep) {
            String allServices = orgSubscribedProfiles.stream().map(OrganisationSubscriptionProfileGroup::getCode).collect(Collectors.joining(","));
            log.info("Check service skip... Return all available services: " + allServices);
            resultSet.addAll(orgSubscribedProfiles);
        } else {
            log.info("Check service");
            resultSet = orgSubscribedProfiles.stream().filter(s -> s.getProcessId().equalsIgnoreCase(service)).collect(Collectors.toSet());
            log.info("Found " + service);

        }
        return resultSet;
    }

    private Set<String> getAvailableActions(Set<OrganisationSubscriptionProfileGroup> services, String action) {
        final Set<String> result = new HashSet<>();
        if (skipActionStep) {
            log.info("Check action skip... Return all available actions for all available services");
            services.stream().map(s -> s.getDocumentIdentifiers()).forEach(strings -> result.addAll(Arrays.asList(strings)));
        } else {
            log.info("Looking for action " + action);
            Set<String> collect = result.stream().filter(s -> action.endsWith(s)).collect(Collectors.toSet());
            result.clear();
            result.addAll(collect);
            log.info("Found " + result.size());
        }

        return result;
    }

    private boolean skipStep(IdentifierCheckStep step) {
        boolean skip = false;
        String skipEnvVariable = System.getenv(IDENTIFIER_CHECK_STEP_SKIP);
        if (skipEnvVariable != null) {
            skip = skipEnvVariable.toLowerCase().contains(step.name().toLowerCase());
        }
        return skip;
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