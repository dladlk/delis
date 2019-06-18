package dk.erst.delis.service.content.document;

import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.Objects;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.entities.document.SendDocumentBytes;
import dk.erst.delis.data.entities.journal.JournalSendDocument;
import dk.erst.delis.data.enums.document.SendDocumentBytesType;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.document.SendDocumentBytesRepository;
import dk.erst.delis.persistence.repository.document.SendDocumentRepository;
import dk.erst.delis.persistence.repository.journal.document.JournalSendDocumentRepository;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.util.SecurityUtil;
import dk.erst.delis.web.document.SendDocumentService;

@Service
public class SendDocumentDelisWebApiServiceImpl implements SendDocumentDelisWebApiService {

    @Value("${delis.download.allow.all:#{false}}")
    private boolean downloadAllowAll;

    private final SendDocumentRepository sendDocumentRepository;
    private final SendDocumentBytesRepository sendDocumentBytesRepository;
    private final JournalSendDocumentRepository journalSendDocumentRepository;
    private final SecurityService securityService;
    private final AbstractGenerateDataService<SendDocumentRepository, SendDocument> abstractGenerateDataService;
    private final SendDocumentService documentService;

    public SendDocumentDelisWebApiServiceImpl(
            AbstractGenerateDataService<SendDocumentRepository, SendDocument> abstractGenerateDataService,
            SendDocumentRepository sendDocumentRepository,
            SendDocumentBytesRepository sendDocumentBytesRepository,
            JournalSendDocumentRepository journalSendDocumentRepository,
            SecurityService securityService,
            SendDocumentService documentService) {
        this.abstractGenerateDataService = abstractGenerateDataService;
        this.sendDocumentRepository = sendDocumentRepository;
        this.sendDocumentBytesRepository = sendDocumentBytesRepository;
        this.journalSendDocumentRepository = journalSendDocumentRepository;
        this.securityService = securityService;
        this.documentService = documentService;
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
        SendDocument sendDocument = getSendDocument(id);
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

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public ListContainer<SendDocumentBytes> findListSendDocumentBytesBySendDocumentId(long id) {
        SendDocument sendDocument = getSendDocument(id);
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (Objects.equals(orgId, sendDocument.getOrganisation().getId())) {
                return new ListContainer<>(sendDocumentBytesRepository.findByDocument(sendDocument));
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        } else {
            return new ListContainer<>(sendDocumentBytesRepository.findByDocument(sendDocument));
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public ListContainer<JournalSendDocument> findListJournalSendDocumentBySendDocumentId(long id) {
        SendDocument sendDocument = getSendDocument(id);
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (Objects.equals(orgId, sendDocument.getOrganisation().getId())) {
                return new ListContainer<>(journalSendDocumentRepository.findByDocumentId(sendDocument.getId()));
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        } else {
            return new ListContainer<>(journalSendDocumentRepository.findByDocumentId(sendDocument.getId()));
        }
    }

    @Override
    public byte[] downloadFile(Long id, Long bytesId) {
        SendDocumentBytes sendDocumentBytes = findByIdAndDocumentId(id, bytesId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.documentService.getDocumentBytesContents(sendDocumentBytes, out);
        return out.toByteArray();
    }

    private SendDocument getSendDocument(long id) {
        SendDocument sendDocument = sendDocumentRepository.findById(id).orElse(null);
        if (sendDocument == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "SendDocument not found")));
        }
        return sendDocument;
    }

    private SendDocumentBytes findByIdAndDocumentId(Long id, Long bytesId) {
        SendDocumentBytes sendDocumentBytes = sendDocumentBytesRepository.findByIdAndDocumentId(bytesId, id);
        if (sendDocumentBytes == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("data", HttpStatus.NOT_FOUND.getReasonPhrase(), "Data not found")));
        }
        if (SecurityUtil.hasRole("ROLE_USER")) {
            if (ObjectUtils.notEqual(securityService.getOrganisation().getId(), sendDocumentBytes.getDocument().getOrganisation().getId())) {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        }
        if (!isDownloadAllowed(sendDocumentBytes)) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.CONFLICT.getReasonPhrase(), "Only RECEIPT bytes are allowed for download, but " + sendDocumentBytes.getType() + " is requested")));
        }

        return sendDocumentBytes;
    }

    private boolean isDownloadAllowed(SendDocumentBytes b) {
        if (downloadAllowAll) {
            return true;
        }
        return b.getType() == SendDocumentBytesType.RECEIPT;
    }
}
