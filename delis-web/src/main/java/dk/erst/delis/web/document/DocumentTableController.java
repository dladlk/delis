package dk.erst.delis.web.document;

import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
public class DocumentTableController {
    private DocumentDaoRepository repository;

    public DocumentTableController(DocumentDaoRepository repository) {
        this.repository = repository;
    }

    @RequestMapping(value = "/document/table", method = RequestMethod.POST)
    public DataTablesOutput<Document> listPOST(@Valid @RequestBody DataTablesInput input) {
        return repository.findAll(input);
    }
}
