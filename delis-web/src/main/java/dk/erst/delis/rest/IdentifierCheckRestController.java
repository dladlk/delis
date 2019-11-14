package dk.erst.delis.rest;

import java.net.URLDecoder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import dk.erst.delis.task.identifier.IdentifierCheckService;
import dk.erst.delis.task.identifier.IdentifierCheckService.Result;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;

@Api
@RestController
@RequestMapping("/rest/open")
@Slf4j
public class IdentifierCheckRestController {

    public static final String UTF_8 = "utf-8";
    public static final String OK = "ok";
    public static final String REASON_HEADER = "reason";
    public static final String IDENTIFIER_CHECK_STEP_SKIP = "identifier.check.step.skip";
    
    @Autowired
    private IdentifierCheckService identifierCheckService; 

    @RequestMapping(value = "/receivercheck/{identifier}/{service}/{action}", method = RequestMethod.GET)
    public ResponseEntity<?> checkIdentifier(@PathVariable("identifier") String compoundIdentifier,
                                          @PathVariable("service") String service,
                                          @PathVariable("action") String action) {

        boolean skipServiceStep = skipStep(IdentifierCheckStep.SERVICE);
        boolean skipActionStep = skipStep(IdentifierCheckStep.ACTION);

        try {
            compoundIdentifier = URLDecoder.decode(compoundIdentifier, UTF_8);
            service = URLDecoder.decode(service, UTF_8);
            action = URLDecoder.decode(action, UTF_8);
        } catch (Exception e) {
			log.error("Failed to decode URL-encoded values " + compoundIdentifier + ", " + service + ", " + action+": "+e.getMessage(), e);
            throw new RuntimeException(e);
        }
        Result result = identifierCheckService.checkReceivingSupport(compoundIdentifier, service, action, skipServiceStep, skipActionStep);

        ResponseEntity<?> response;
        if (result.isSuccess()) {
            response = ResponseEntity.ok().build();
        } else {
            response = ResponseEntity.noContent().header(REASON_HEADER, result.getDescription()).build();
        }
        return response;
    }

    private boolean skipStep(IdentifierCheckStep step) {
        boolean skip = false;
        String skipEnvVariable = System.getenv(IDENTIFIER_CHECK_STEP_SKIP);

        if (skipEnvVariable == null || skipEnvVariable.length() == 0) {
            skipEnvVariable = System.getProperty(IDENTIFIER_CHECK_STEP_SKIP);
        }

        if (skipEnvVariable != null) {
            skip = skipEnvVariable.toLowerCase().contains(step.name().toLowerCase());
        }
        return skip;
    }

}