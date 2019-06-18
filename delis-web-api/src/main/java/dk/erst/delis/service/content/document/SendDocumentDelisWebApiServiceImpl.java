package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.document.SendDocumentRepository;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.SecurityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.util.Collections;
import java.util.Objects;

@Slf4j
@Service
public class SendDocumentDelisWebApiServiceImpl implements SendDocumentDelisWebApiService {

    private final SendDocumentRepository sendDocumentRepository;
    private final SecurityService securityService;
    private final AbstractGenerateDataService<SendDocumentRepository, SendDocument> abstractGenerateDataService;

    public SendDocumentDelisWebApiServiceImpl(AbstractGenerateDataService<SendDocumentRepository, SendDocument> abstractGenerateDataService, SendDocumentRepository sendDocumentRepository, SecurityService securityService) {
        this.abstractGenerateDataService = abstractGenerateDataService;
        this.sendDocumentRepository = sendDocumentRepository;
        this.securityService = securityService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<SendDocument> getAll(WebRequest request) {
        return abstractGenerateDataService.generateDataPageContainer(SendDocument.class, request, sendDocumentRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public SendDocument getOneById(long id) {
        SendDocument sendDocument = sendDocumentRepository.findById(id).orElse(null);
        if (sendDocument == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "SendDocument not found")));
        }

        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (Objects.equals(orgId, sendDocument.getOrganisation().getId())) {
                return sendDocument;
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        } else {
            return sendDocument;
        }
    }
}
