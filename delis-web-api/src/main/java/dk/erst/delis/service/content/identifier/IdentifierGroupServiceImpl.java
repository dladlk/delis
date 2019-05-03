package dk.erst.delis.service.content.identifier;

import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.persistence.repository.identifier.IdentifierGroupRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

/**
 * @author funtusthan, created by 17.01.19
 */

@Service
public class IdentifierGroupServiceImpl implements IdentifierGroupService {

    private final IdentifierGroupRepository identifierGroupRepository;
    private final AbstractGenerateDataService<IdentifierGroupRepository, IdentifierGroup> abstractGenerateDataService;

    @Autowired
    public IdentifierGroupServiceImpl(IdentifierGroupRepository identifierGroupRepository, AbstractGenerateDataService<IdentifierGroupRepository, IdentifierGroup> abstractGenerateDataService) {
        this.identifierGroupRepository = identifierGroupRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public PageContainer<IdentifierGroup> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(IdentifierGroup.class, webRequest, identifierGroupRepository);
    }

    @Override
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Transactional(readOnly = true)
    public IdentifierGroup getOneById(long id) {
        return abstractGenerateDataService.getOneById(id, IdentifierGroup.class, identifierGroupRepository);
    }
}
