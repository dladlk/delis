package dk.erst.delis.task.document.deliver;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentProcessStepType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.task.document.JournalDocumentService;
import dk.erst.delis.task.document.process.log.DocumentProcessLog;
import dk.erst.delis.task.document.process.log.DocumentProcessStep;
import dk.erst.delis.task.document.storage.DocumentBytesStorageService;
import dk.erst.delis.task.organisation.setup.OrganisationSetupService;
import dk.erst.delis.task.organisation.setup.data.OrganisationReceivingMethod;
import dk.erst.delis.task.organisation.setup.data.OrganisationSetupData;
import dk.erst.delis.vfs.service.VFSService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

@Slf4j
@Service
public class DocumentDeliverService {

    private DocumentDaoRepository documentDaoRepository;
    private OrganisationSetupService organisationSetupService;
    private JournalDocumentService journalDocumentService;
    private DocumentBytesStorageService documentBytesStorageService;
    private VFSService vfsService;

    @Autowired
    public DocumentDeliverService(DocumentDaoRepository documentDaoRepository, OrganisationSetupService organisationSetupService,
                                  JournalDocumentService journalDocumentService, DocumentBytesStorageService documentBytesStorageService, VFSService vfsService) {
        this.documentDaoRepository = documentDaoRepository;
        this.organisationSetupService = organisationSetupService;
        this.journalDocumentService = journalDocumentService;
        this.documentBytesStorageService = documentBytesStorageService;
        this.vfsService = vfsService;
    }

    public StatData processValidated() {

        StatData statData = new StatData();
        try {
            List<Organisation> organisations = documentDaoRepository.loadDocumentStatusStat(DocumentStatus.VALIDATE_OK);
            for (Organisation org : organisations) {

                OrganisationSetupData setupData = organisationSetupService.load(org);
                if (setupData.getReceivingMethod() == null) {
                    log.info("No recieving method defined for organization " + org.getName() + ". Documents delivery skipped");
                    statData.increment("Organizations with no recieving method");
                } else {
                    boolean presentValidated;
                    Long previousDocumentId = 0l;
                    do {
                        List<Document> list = documentDaoRepository.findForExport(DocumentStatus.VALIDATE_OK, org, previousDocumentId);
                        presentValidated = !list.isEmpty();

                        for (Document document : list) {
                            previousDocumentId = document.getId();
                            exportDocument(statData, document, setupData);
                        }
                    } while (presentValidated);
                }
            }
        } finally {
            log.info("Done exporting of validated documents in " + (System.currentTimeMillis() - statData.getStartMs()) + " ms");
        }

        return statData;
    }

    public boolean exportDocument(StatData statData, Document document, OrganisationSetupData setupData) {
        document.setDocumentStatus(DocumentStatus.EXPORT_START);
        documentDaoRepository.updateDocumentStatus(document);

        DocumentProcessLog log = moveDocument(document, setupData);

        document.setDocumentStatus(log.isSuccess() ? DocumentStatus.EXPORT_OK : DocumentStatus.VALIDATE_OK);
        documentDaoRepository.updateDocumentStatus(document);


        if (log != null) {
            statData.increment(log.isSuccess() ? "OK" : "ERROR");
            List<DocumentProcessStep> stepList = log.getStepList();
            journalDocumentService.saveDocumentStep(document, stepList);
        }

        return log.isSuccess();
    }

    private DocumentProcessLog moveDocument(Document document, OrganisationSetupData setupData) {
        DocumentProcessLog processLog = new DocumentProcessLog();

        OrganisationReceivingMethod receivingMethod = setupData.getReceivingMethod();
        String receivingMethodSetup = setupData.getReceivingMethodSetup();

        DocumentBytes documentBytes = documentBytesStorageService.find(document, DocumentBytesType.READY);

        if (receivingMethod == null) {
            DocumentProcessStep failStep = new DocumentProcessStep("Can not export - receiving method is not set for organization " +
                    document.getOrganisation().getName(), DocumentProcessStepType.DELIVER);
            failStep.setSuccess(false);
            failStep.done();
            processLog.addStep(failStep);
        } else if (documentBytes == null) {
            DocumentProcessStep failStep = new DocumentProcessStep("Can not export - can not find '" +
                    DocumentBytesType.READY + "' DocumentBytes record for " + document.getId(), DocumentProcessStepType.DELIVER);
            failStep.setSuccess(false);
            failStep.done();
            processLog.addStep(failStep);
        } else {
            String outputFileName = buildOutputFileName(document);
            switch (receivingMethod) {
                case FILE_SYSTEM:
                    File outputFile = new File(receivingMethodSetup, outputFileName);
                    moveToFileSystem(documentBytes, outputFile, processLog);
                    break;
                case AZURE_STORAGE_ACCOUNT:
                    moveToAzure(documentBytes, processLog);
                    break;
                case VFS:
                    moveToVFS(documentBytes, outputFileName, receivingMethodSetup, processLog);
                    break;
                default:
                    DocumentProcessStep failStep = new DocumentProcessStep("Can not export - can not recognize receiving method " +
                            receivingMethod, DocumentProcessStepType.DELIVER);
                    failStep.setSuccess(false);
                    failStep.done();
                    processLog.addStep(failStep);
            }
        }

        return processLog;
    }

    private void moveToVFS(DocumentBytes documentBytes, String outputFileName, String configPath, DocumentProcessLog processLog) {
        DocumentProcessStep step = new DocumentProcessStep("Export to " + outputFileName, DocumentProcessStepType.DELIVER);
        boolean uploaded = false;
        File tempFile = null;
        try {
            tempFile = File.createTempFile("delis", "tmp");
        } catch (IOException e) {
            log.error("Unable to create temp file", e);
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
            boolean loaded = documentBytesStorageService.load(documentBytes, fileOutputStream);
            if (loaded) {
                vfsService.upload(configPath, tempFile.getAbsolutePath(), "/" + outputFileName);
                uploaded = true;
            }
        } catch (Exception e) {
            log.error(String.format("Failed to upload file '%s' using '%s'", outputFileName, configPath), e);
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                log.warn(String.format("Unable to delete temp file '%s'", tempFile.getAbsolutePath()));
            }
        }

        step.setSuccess(uploaded);
        step.done();
        processLog.addStep(step);
    }

    private String getParamValue(String url, String paramName) {
        String paramValuePart = StringUtils.substringAfter(url, paramName + "=");
        return paramValuePart.split("&")[0];
    }

    private String buildOutputFileName(Document document) {
        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss_").format(document.getUpdateTime()));
        sb.append(document.getReceiverIdRaw());
        sb.append("_");
        sb.append(document.getSenderIdRaw());
        sb.append("_");
        sb.append(document.getDocumentId());
        sb.append("_");
        sb.append(document.getDocumentDate());
        sb.append(".xml");
        String s = sb.toString();

        s = s.replaceAll("/", "");
        s = s.replaceAll("\\\\", "");
        s = s.replaceAll("\"", "");
        s = s.replaceAll("'", "");
        s = s.replaceAll(":", "");
        s = s.replaceAll(";", "");
        s = s.replaceAll(" ", "_");


        return s;
    }

    private void moveToFileSystem(DocumentBytes documentBytes, File outputFile, DocumentProcessLog processLog) {
        DocumentProcessStep step = new DocumentProcessStep("Export to " + outputFile, DocumentProcessStepType.DELIVER);
        boolean copied = false;
        outputFile.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(outputFile);) {
            copied = documentBytesStorageService.load(documentBytes, fos);
        } catch (Exception e) {
            log.error("Failed to deliver document " + documentBytes + " to " + outputFile, e);
        }
        step.setSuccess(copied);
        step.done();
        processLog.addStep(step);
    }

    private void moveToAzure(DocumentBytes documentBytes, DocumentProcessLog log) {
        DocumentProcessStep failStep = new DocumentProcessStep("Delivering to Azure storage not implemented yet", DocumentProcessStepType.DELIVER);
        failStep.setSuccess(false);
        failStep.done();
        log.addStep(failStep);
    }
}
