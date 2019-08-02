package dk.erst.delis.service.content.identifier;

import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.identifier.IdentifierRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Objects;

@Service
public class IdentifierDelisWebApiServiceImpl implements IdentifierDelisWebApiService {

    private final SecurityService securityService;
    private final IdentifierRepository identifierRepository;
    private final AbstractGenerateDataService<IdentifierRepository, Identifier> abstractGenerateDataService;

    @Autowired
    public IdentifierDelisWebApiServiceImpl(IdentifierRepository identifierRepository, AbstractGenerateDataService<IdentifierRepository, Identifier> abstractGenerateDataService, SecurityService securityService) {
        this.identifierRepository = identifierRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<Identifier> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(Identifier.class, webRequest, identifierRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public Identifier getOneById(long id) {
        Identifier identifier = identifierRepository.findById(id).orElse(null);
        if (identifier == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "Identifier not found")));
        }
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (Objects.equals(orgId, identifier.getOrganisation().getId())) {
                return identifier;
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        } else {
            return identifier;
        }
    }
}
