package dk.erst.delis.service.content.journal.identifier;

import dk.erst.delis.data.entities.journal.JournalIdentifier;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.journal.identifier.JournalIdentifierRepository;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.ClassLoaderUtil;
import dk.erst.delis.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

@Service
public class JournalIdentifierDelisWebApiServiceImpl implements JournalIdentifierDelisWebApiService {

    private final SecurityService securityService;
    private final JournalIdentifierRepository journalIdentifierRepository;
    private final AbstractGenerateDataService<JournalIdentifierRepository, JournalIdentifier> abstractGenerateDataService;

    @Autowired
    public JournalIdentifierDelisWebApiServiceImpl(
            JournalIdentifierRepository journalIdentifierRepository,
            AbstractGenerateDataService<JournalIdentifierRepository, JournalIdentifier> abstractGenerateDataService, SecurityService securityService) {
        this.journalIdentifierRepository = journalIdentifierRepository;
        this.abstractGenerateDataService = abstractGenerateDataService;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<JournalIdentifier> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(JournalIdentifier.class, webRequest, journalIdentifierRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public JournalIdentifier getOneById(long id) {
        JournalIdentifier journalIdentifier = journalIdentifierRepository.findById(id).orElse(null);
        if (journalIdentifier == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "JournalIdentifier not found")));
        }
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (Objects.equals(orgId, journalIdentifier.getOrganisation().getId())) {
                return journalIdentifier;
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        } else {
            return journalIdentifier;
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public ListContainer<JournalIdentifier> getByIdentifier(WebRequest webRequest, long identifierId) {
        long collectionSize = journalIdentifierRepository.countByIdentifierId(identifierId);
        if (collectionSize == 0) {
            return new ListContainer<>(Collections.emptyList());
        }
        String[] strings = Objects.requireNonNull(webRequest.getParameter("sort")).split("_");
        for ( Field field : ClassLoaderUtil.getAllFieldsByEntity(JournalIdentifier.class) ) {
            if (Modifier.isPrivate(field.getModifiers())) {
                if (Objects.equals(strings[1].toUpperCase(), field.getName().toUpperCase())) {
                    if (Objects.equals(strings[2], "Asc")) {
                        return new ListContainer<>(journalIdentifierRepository.findAllByIdentifierId(identifierId, Sort.by(field.getName()).ascending()));
                    } else {
                        return new ListContainer<>(journalIdentifierRepository.findAllByIdentifierId(identifierId, Sort.by(field.getName()).descending()));
                    }
                }
            }
        }
        return  new ListContainer<>(journalIdentifierRepository.findAllByIdentifierId(identifierId, Sort.by("id").ascending()));
    }
}
