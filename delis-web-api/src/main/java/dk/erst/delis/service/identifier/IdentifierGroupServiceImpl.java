package dk.erst.delis.service.identifier;

import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.persistence.identifier.IdentifierGroupRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 17.01.19
 */

@Service
public class IdentifierGroupServiceImpl implements IdentifierGroupService {

    private final IdentifierGroupRepository identifierGroupRepository;
    private final AbstractGenerateDataService abstractGenerateDataService;

    @Autowired
    public IdentifierGroupServiceImpl(IdentifierGroupRepository identifierGroupRepository, AbstractGenerateDataService abstractGenerateDataService) {
        this.identifierGroupRepository = identifierGroupRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @Transactional(readOnly = true)
    public PageContainer<IdentifierGroup> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(IdentifierGroup.class, webRequest, identifierGroupRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public IdentifierGroup getOneById(long id) {
        return (IdentifierGroup) abstractGenerateDataService.getOneById(id, IdentifierGroup.class, identifierGroupRepository);
    }
}
