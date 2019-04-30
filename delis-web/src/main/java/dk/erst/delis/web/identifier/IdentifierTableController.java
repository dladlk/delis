package dk.erst.delis.web.identifier;

import dk.erst.delis.dao.IdentifierTableRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class IdentifierTableController {
    private IdentifierTableRepository repository;

    public IdentifierTableController (IdentifierTableRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/identifier/table", method = RequestMethod.POST)
    public DataTablesOutput<Identifier> listPOST(@Valid @RequestBody DataTablesInput input) {
        return repository.findAll(input);
    }
}
