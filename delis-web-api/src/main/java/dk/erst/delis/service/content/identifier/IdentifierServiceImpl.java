package dk.erst.delis.service.content.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.persistence.identifier.IdentifierRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 17.01.19
 */

@Service
public class IdentifierServiceImpl implements IdentifierService {

    private final IdentifierRepository identifierRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public IdentifierServiceImpl(IdentifierRepository identifierRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.identifierRepository = identifierRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<Identifier> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(Identifier.class, webRequest, identifierRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public Identifier getOneById(long id) {
        return (Identifier) abstractGenerateDataService.getOneById(id, Identifier.class, identifierRepository);
    }
}
