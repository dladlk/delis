package dk.erst.delis.web.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
import dk.erst.delis.task.identifier.resolve.IdentifierResolverService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("check")
public class IdendifierCheckRestController {

    @Autowired
    private IdentifierResolverService identifierResolverService;

    @RequestMapping(value = "/receiver/{identifier}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity checkIdentifier (@PathVariable("identifier") String compoundIdentifier) {

        String[] split = compoundIdentifier.split(":");
        String type = split[0];
        String id = split[1];

        Identifier identifier = identifierResolverService.resolve(type, id);

        HttpStatus status = HttpStatus.OK;

        if (identifier == null || identifier.getStatus() == IdentifierStatus.DELETED) {
            status = HttpStatus.NO_CONTENT;
        }

        return new ResponseEntity(status);
    }
}
