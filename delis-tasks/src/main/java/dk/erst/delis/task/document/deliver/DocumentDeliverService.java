package dk.erst.delis.task.document.deliver;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.dao.DocumentExportDaoRepository;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.entities.document.DocumentBytes;
import dk.erst.delis.data.entities.document.DocumentExport;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.document.DocumentBytesType;
import dk.erst.delis.data.enums.document.DocumentExportStatus;
import dk.erst.delis.data.enums.document.DocumentFormat;
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

@Slf4j
@Service
public class DocumentDeliverService {

    public static final String DELIVER_FOLDER_BIS3 = "BIS3";
	public static final String DELIVER_FOLDER_OIOUBL = "OIOUBL";
    
	private DocumentDaoRepository documentDaoRepository;
    private DocumentExportDaoRepository documentExportDaoRepository;
    private OrganisationSetupService organisationSetupService;
    private JournalDocumentService journalDocumentService;
    private DocumentBytesStorageService documentBytesStorageService;
    private VFSService vfsService;

    @Autowired
    public DocumentDeliverService(DocumentDaoRepository documentDaoRepository, OrganisationSetupService organisationSetupService,
    							DocumentExportDaoRepository documentExportDaoRepository,
                                  JournalDocumentService journalDocumentService, DocumentBytesStorageService documentBytesStorageService, VFSService vfsService) {
        this.documentDaoRepository = documentDaoRepository;
        this.documentExportDaoRepository = documentExportDaoRepository;
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
                if (setupData.getReceivingMethod() == null || StringUtils.isBlank(setupData.getReceivingMethodSetup())) {
                    log.info("No recieving method or setup is configured for organization " + org.getName() + ". Documents delivery skipped");
                    statData.increment("Organizations with no recieving setup");
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
            long duration = System.currentTimeMillis() - statData.getStartMs();
            if (duration > 500) {
            	log.info("Done exporting of validated documents in " + duration + " ms");
            }
        }

        return statData;
    }

    public boolean exportDocument(StatData statData, Document document, OrganisationSetupData setupData) {
        document.setDocumentStatus(DocumentStatus.EXPORT_START);
        documentDaoRepository.updateDocumentStatus(document);

        DocumentProcessLog log = moveDocument(document, setupData);
        
        document.setDocumentStatus(log.isSuccess() ? DocumentStatus.EXPORT_OK : DocumentStatus.EXPORT_ERROR);
        documentDaoRepository.updateDocumentStatus(document);


        if (log != null) {
            statData.increment(log.isSuccess() ? "OK" : "ERROR");
            List<DocumentProcessStep> stepList = log.getStepList();
            journalDocumentService.saveDocumentStep(document, stepList);
        }

        return log.isSuccess();
    }

    private DocumentProcessLog moveDocument(Document document, OrganisationSetupData setupData) {
    	long start = System.currentTimeMillis();
        DocumentProcessLog processLog = new DocumentProcessLog();

		DocumentBytes documentBytes = documentBytesStorageService.find(document, DocumentBytesType.READY);
		if (documentBytes == null) {
			DocumentProcessStep failStep = new DocumentProcessStep("Can not export - can not find '" + DocumentBytesType.READY + "' DocumentBytes record for " + document.getId(), DocumentProcessStepType.DELIVER);
			failStep.setSuccess(false);
			failStep.done();
			processLog.addStep(failStep);
		} else {
        	
        	boolean doDoubleSending = setupData.isReceiveBothOIOUBLBIS3() && documentBytes.getFormat().isOIOUBL();
            
        	String outputFileName = buildOutputFileName(document, doDoubleSending ? DELIVER_FOLDER_OIOUBL : null);
        	
        	boolean somethingSent = false;
        	try {
	            boolean uploaded = uploadDocument(setupData, processLog, documentBytes, outputFileName);
	            
	            if (uploaded && setupData.isCheckDeliveredConsumed()) {
	            	saveDocumentExport(document, documentBytes.getSize(), outputFileName);
	            	somethingSent = true;
	            }
            
	            if (doDoubleSending) {
	            	DocumentFormat addReceivingFormat;
	        		if (documentBytes.getFormat() == DocumentFormat.OIOUBL_CREDITNOTE) {
	        			addReceivingFormat = DocumentFormat.BIS3_CREDITNOTE;
	        		} else {
	        			addReceivingFormat = DocumentFormat.BIS3_INVOICE;
	        		}
	        		DocumentBytes addDocumentBytes = documentBytesStorageService.find(document, addReceivingFormat);
	        		
	        		if (addDocumentBytes != null) {
	        			outputFileName = buildOutputFileName(document, DELIVER_FOLDER_BIS3);
	        			uploaded = uploadDocument(setupData, processLog, addDocumentBytes, outputFileName);
	        			if (uploaded && setupData.isCheckDeliveredConsumed()) {
	        				saveDocumentExport(document, addDocumentBytes.getSize(), outputFileName);
	        			}
	        		}
	            }
        	} catch (UploadFailureException ufe) {
        		log.error("Failed delivery of "+document, ufe);
    			DocumentProcessStep failureStep = new DocumentProcessStep(ufe.getMessage(), DocumentProcessStepType.DELIVER);
    			failureStep.done();
    			failureStep.setSuccess(false);
    			failureStep.setDuration(System.currentTimeMillis() - start);
				processLog.addStep(failureStep);

				if (somethingSent) {
					/*
					 * It is BIS3 copy which is failed - keep status success, but add error message to journal about issue with delivery
					 */
					processLog.setSuccess(true);
        		}
        	}
        }

        return processLog;
    }

	private void saveDocumentExport(Document document, long size, String outputFileName) {
		DocumentExport documentExport = new DocumentExport();
		documentExport.setOrganisation(document.getOrganisation());
		documentExport.setDocument(document);
		documentExport.setStatus(DocumentExportStatus.PENDING);
		
		documentExport.setExportFileName(outputFileName);
		documentExport.setExportDate(Calendar.getInstance().getTime());
		documentExport.setExportSize(size);
		
		documentExportDaoRepository.save(documentExport);
	}

	private boolean uploadDocument(OrganisationSetupData setupData, DocumentProcessLog processLog, DocumentBytes documentBytes, String outputFileName) throws UploadFailureException {
		OrganisationReceivingMethod receivingMethod = setupData.getReceivingMethod();
		String receivingMethodSetup = setupData.getReceivingMethodSetup();
		
		boolean success = false;
		switch (receivingMethod) {
		    case FILE_SYSTEM:
		        File outputFile = new File(receivingMethodSetup, outputFileName);
		        success = moveToFileSystem(documentBytes, outputFile, processLog);
		        break;
		    case VFS:
		        success = moveToVFS(documentBytes, outputFileName, receivingMethodSetup, processLog);
		        break;
		    default:
		        DocumentProcessStep failStep = new DocumentProcessStep("Can not export - can not recognize receiving method " + receivingMethod, DocumentProcessStepType.DELIVER);
		        failStep.setSuccess(false);
		        failStep.done();
		        processLog.addStep(failStep);
		}
		return success;
	}

    private boolean moveToVFS(DocumentBytes documentBytes, String outputFileName, String configPath, DocumentProcessLog processLog) throws UploadFailureException {
        DocumentProcessStep step = new DocumentProcessStep("Export to " + outputFileName, DocumentProcessStepType.DELIVER);
        
        boolean uploaded = false;
        try {
			uploaded = moveToVFS(documentBytes, outputFileName, configPath);
        } finally {
	        step.setSuccess(uploaded);
	        step.done();
	        processLog.addStep(step);
        }
        return uploaded;
    }

	private boolean moveToVFS(DocumentBytes documentBytes, String outputFileName, String configPath) throws UploadFailureException {
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
                return true;
            }
        } catch (Exception e) {
            log.error(String.format("Failed to upload file '%s' using '%s'", outputFileName, configPath), e);
            throw new UploadFailureException(e.getMessage(), documentBytes.getDocument(), outputFileName);
        } finally {
            if (tempFile != null && !tempFile.delete()) {
                log.warn(String.format("Unable to delete temp file '%s'", tempFile.getAbsolutePath()));
            }
        }
		return false;
	}

    private String buildOutputFileName(Document document, String subfolder) {
        StringBuilder sb = new StringBuilder();
        sb.append(new SimpleDateFormat("yyyyMMdd_HHmmss").format(document.getUpdateTime()));
        sb.append("_REF");
        sb.append(document.getId());
        sb.append("_");
        sb.append(removePrefix(document.getReceiverIdRaw()));
        sb.append("_");
        sb.append(removePrefix(document.getSenderIdRaw()));
//        sb.append("_");
//        sb.append(document.getDocumentId());
//        sb.append("_");
//        sb.append(document.getDocumentDate());
        sb.append(".xml");
        String s = sb.toString();

        s = s.replaceAll("/", "");
        s = s.replaceAll("\\\\", "");
        s = s.replaceAll("\"", "");
        s = s.replaceAll("'", "");
        s = s.replaceAll(":", "");
        s = s.replaceAll(";", "");
        s = s.replaceAll(" ", "_");

        if (subfolder != null) {
        	s = subfolder +"/" + s;
        }

        return s;
    }

	private String removePrefix(String idRaw) {
		if (StringUtils.isNotBlank(idRaw)) {
			int delimStart = idRaw.indexOf("::");
			if (delimStart > 0) {
				return idRaw.substring(delimStart + 2);
			}
		}
		return idRaw;
	}

	private boolean moveToFileSystem(DocumentBytes documentBytes, File outputFile, DocumentProcessLog processLog) throws UploadFailureException {
        DocumentProcessStep step = new DocumentProcessStep("Export to " + outputFile, DocumentProcessStepType.DELIVER);
        boolean copied = false;
        File parentFile = outputFile.getParentFile();
        if (!parentFile.exists() || !parentFile.isDirectory()) {
        	throw new UploadFailureException("Folder "+parentFile.getAbsolutePath()+" does not exist or is not a folder", documentBytes.getDocument(), outputFile.getAbsolutePath());
        }
        try (FileOutputStream fos = new FileOutputStream(outputFile);) {
            copied = documentBytesStorageService.load(documentBytes, fos);
        } catch (Exception e) {
            log.error("Failed to deliver document " + documentBytes + " to " + outputFile, e);
            throw new UploadFailureException(e.getMessage(), documentBytes.getDocument(), outputFile.getAbsolutePath());
        } finally {
	        step.setSuccess(copied);
	        step.done();
	        processLog.addStep(step);
        }
        return copied;
    }

}
