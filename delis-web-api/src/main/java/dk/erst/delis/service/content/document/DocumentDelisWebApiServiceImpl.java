package dk.erst.delis.service.content.document;

import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.document.SendDocument;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.exception.model.FieldErrorModel;
import dk.erst.delis.exception.statuses.RestConflictException;
import dk.erst.delis.exception.statuses.RestForbiddenException;
import dk.erst.delis.exception.statuses.RestNotFoundException;
import dk.erst.delis.persistence.repository.document.DocumentBytesRepository;
import dk.erst.delis.persistence.repository.document.DocumentRepository;
import dk.erst.delis.rest.data.response.ListContainer;
import dk.erst.delis.rest.data.response.PageContainer;
import dk.erst.delis.rest.data.response.SuccessData;
import dk.erst.delis.rest.data.response.invoice.InvoiceResponseData;
import dk.erst.delis.service.content.AbstractGenerateDataService;
import dk.erst.delis.service.security.SecurityService;
import dk.erst.delis.task.document.process.log.DocumentProcessStepException;
import dk.erst.delis.task.document.process.validate.result.ErrorRecord;
import dk.erst.delis.task.document.response.ApplicationResponseService;
import dk.erst.delis.util.SecurityUtil;
import dk.erst.delis.web.document.DocumentService;
import dk.erst.delis.web.document.SendDocumentService;
import dk.erst.delis.web.document.ir.InvoiceResponseForm;
import dk.erst.delis.web.document.ir.InvoiceResponseFormControllerConst;
import dk.erst.delis.web.document.ir.MessageLevelResponseConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.WebRequest;

import java.io.*;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class DocumentDelisWebApiServiceImpl implements DocumentDelisWebApiService {

    private final DocumentRepository documentRepository;
    private final DocumentBytesRepository documentBytesRepository;
    private final SecurityService securityService;
    private final DocumentService documentService;
    private final SendDocumentService sendDocumentService;
    private final ApplicationResponseService applicationResponseService;
    private final AbstractGenerateDataService<DocumentRepository, Document> abstractGenerateDataService;
    @Value("${delis.download.allow.all:#{false}}")
    private boolean downloadAllowAll;

    @Autowired
    public DocumentDelisWebApiServiceImpl(
            DocumentRepository documentRepository,
            DocumentBytesRepository documentBytesRepository,
            SecurityService securityService,
            DocumentService documentService,
            AbstractGenerateDataService<DocumentRepository, Document> abstractGenerateDataService,
            SendDocumentService sendDocumentService,
            ApplicationResponseService applicationResponseService) {
        this.documentRepository = documentRepository;
        this.documentBytesRepository = documentBytesRepository;
        this.securityService = securityService;
        this.documentService = documentService;
        this.abstractGenerateDataService = abstractGenerateDataService;
        this.sendDocumentService = sendDocumentService;
        this.applicationResponseService = applicationResponseService;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public PageContainer<Document> getAll(WebRequest webRequest) {
        return abstractGenerateDataService.generateDataPageContainer(Document.class, webRequest, documentRepository);
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public Document getOneById(long id) {
        Document document = documentService.getDocument(id);
        if (document == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("id", HttpStatus.NOT_FOUND.getReasonPhrase(), "Document not found")));
        }
        if (SecurityUtil.hasRole("ROLE_USER")) {
            Long orgId = securityService.getOrganisation().getId();
            if (Objects.equals(orgId, document.getOrganisation().getId())) {
                return document;
            } else {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        } else {
            return document;
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public ListContainer<DocumentBytes> findListDocumentBytesByDocumentId(Long documentId) {
        Document document = abstractGenerateDataService.getOneById(documentId, Document.class, documentRepository);
        long totalElements = documentBytesRepository.countByDocument(document);
        if (totalElements == 0) {
            return new ListContainer<>(Collections.emptyList());
        } else {
            return new ListContainer<>(documentBytesRepository.findByDocument(document));
        }
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public byte[] downloadFile(Long id, Long bytesId) {
        DocumentBytes documentBytes = findByIdAndDocumentId(id, bytesId);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        this.documentService.getDocumentBytesContents(documentBytes, out);
        return out.toByteArray();
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    @Transactional(readOnly = true)
    public InvoiceResponseData getInvoiceResponseDataByDocument(long id) {
        Document document = documentService.getDocument(id);
        if (document == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.NOT_FOUND.getReasonPhrase(), "Document is not found")));
        }

        InvoiceResponseData data = new InvoiceResponseData();

        data.setInvoiceResponseUseCaseList(InvoiceResponseFormControllerConst.useCaseList);
        data.setInvoiceStatusCodeList(InvoiceResponseFormControllerConst.invoiceStatusCodeList);
        data.setStatusActionList(InvoiceResponseFormControllerConst.statusActionList);
        data.setStatusReasonList(InvoiceResponseFormControllerConst.statusReasonList);

        data.setMessageLevelResponseUseCaseList(MessageLevelResponseConst.useCaseList);
        data.setApplicationResponseTypeCodeList(MessageLevelResponseConst.applicationResponseTypeCodeList);
        data.setApplicationResponseLineResponseCodeList(MessageLevelResponseConst.applicationResponseLineResponseCodeList);
        data.setApplicationResponseLineReasonCodeList(MessageLevelResponseConst.applicationResponseLineReasonCodeList);

        return data;
    }

    @Override
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_USER')")
    public Object generateInvoiceResponse(InvoiceResponseForm invoiceResponseForm) {
        log.info("Generating ApplicationResponse for " + invoiceResponseForm);
        Document document = documentService.getDocument(invoiceResponseForm.getDocumentId());
        if (document == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.NOT_FOUND.getReasonPhrase(), "Document is not found")));
        }

        boolean success = false;
        File tempFile = null;
        try {
            tempFile = Files.createTempFile("Generated" + invoiceResponseForm.getDocumentFormatName() + "_", ".xml").toFile();
        } catch (IOException e) {
            log.error("Failed document generated", e);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed document generated: " + e.getMessage())));
        }
        try (OutputStream out = new FileOutputStream(tempFile)) {
            success = applicationResponseService.generateApplicationResponse(document, invoiceResponseForm.getData(), out);
        } catch (ApplicationResponseService.ApplicationResponseGenerationException | IOException e) {
            log.error("Failed document generated", e);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed document generated: " + e.getMessage())));
        }

        if (!success) {
            log.error("Failed to generate " + invoiceResponseForm.getDocumentFormatName());
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to generate " + invoiceResponseForm.getDocumentFormatName())));
        }

        if (invoiceResponseForm.isValidate()) {
            List<ErrorRecord> errorList;
            if (invoiceResponseForm.isMessageLevelResponse()) {
                errorList = applicationResponseService.validateMessageLevelResponse(tempFile.toPath());
            } else {
                errorList = applicationResponseService.validateInvoiceResponse(tempFile.toPath());
            }
            if (!errorList.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                sb.append("Generated ");
                sb.append(invoiceResponseForm.getDocumentFormatName());
                sb.append(" is not valid by schema or schematron, found ");
                sb.append(errorList.size());
                sb.append(" errors");
                log.error(sb.toString());
                throw new RestConflictException(Collections.singletonList(
                        new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), sb.toString())));
            }
        }

        if (invoiceResponseForm.isGenerateWithoutSending()) {
            return tempFile;
        }
        try {
            SendDocument sendDocument = sendDocumentService.sendFile(tempFile.toPath(), "Generated by form on document #" + invoiceResponseForm.getDocumentId(), !invoiceResponseForm.isValidate());
            return new SuccessData(true, invoiceResponseForm.getDocumentFormatName() + " with status " + sendDocument.getDocumentStatus());
        } catch (DocumentProcessStepException se) {
            log.error("Failed document processing", se);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to process file " + tempFile + " with error " + se.getMessage())));
        } catch (Exception e) {
            log.error("Failed to load file " + tempFile, e);
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("ids", HttpStatus.CONFLICT.getReasonPhrase(), "Failed to load file " + tempFile + " with error " + e.getMessage())));
        }
    }

    private DocumentBytes findByIdAndDocumentId(Long id, Long bytesId) {
        DocumentBytes documentBytes = documentBytesRepository.findByIdAndDocumentId(bytesId, id);
        if (documentBytes == null) {
            throw new RestNotFoundException(Collections.singletonList(
                    new FieldErrorModel("data", HttpStatus.NOT_FOUND.getReasonPhrase(), "Data not found")));
        }
        if (SecurityUtil.hasRole("ROLE_USER")) {
            if (ObjectUtils.notEqual(securityService.getOrganisation().getId(), documentBytes.getDocument().getOrganisation().getId())) {
                throw new RestForbiddenException(Collections.singletonList(
                        new FieldErrorModel("id", HttpStatus.FORBIDDEN.getReasonPhrase(), "you do not have permission to view this document.")));
            }
        }
        if (!isDownloadAllowed(documentBytes)) {
            throw new RestConflictException(Collections.singletonList(
                    new FieldErrorModel("RECEIPT", HttpStatus.CONFLICT.getReasonPhrase(), "Only RECEIPT bytes are allowed for download, but " + documentBytes.getType() + " is requested")));
        }
        return documentBytes;
    }

    private boolean isDownloadAllowed(DocumentBytes b) {
        if (downloadAllowAll) {
            return true;
        }
        return b.getType() == DocumentBytesType.IN_AS4;
    }
}
