package dk.erst.delis.task.scheduler;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.document.deliver.DocumentCheckDeliveryService;
import dk.erst.delis.task.document.deliver.DocumentDeliverService;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.document.send.forward.SendDocumentFailedProcessService;
import dk.erst.delis.task.identifier.load.IdentifierBatchLoadService;
import dk.erst.delis.task.identifier.load.OrganizationIdentifierLoadReport;
import dk.erst.delis.task.identifier.publish.IdentifierBatchPublishingService;
import dk.erst.delis.web.document.SendDocumentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskScheduler {

	@Autowired
    private ConfigBean configBean;
	@Autowired
	private DocumentLoadService documentLoadService;
	@Autowired
	private DocumentProcessService documentProcessService;
	@Autowired
	private DocumentDeliverService documentDeliverService;
	@Autowired
	private DocumentCheckDeliveryService documentCheckDeliveryService;
	@Autowired
	private IdentifierBatchLoadService identifierBatchLoadService;
	@Autowired
	private IdentifierBatchPublishingService identifierBatchPublishingService;
	@Autowired
	private SendDocumentService sendDocumentService;
	@Autowired
	private SendDocumentFailedProcessService sendDocumentFailedProcessService;


    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void documentLoad() {
        log.info("-- START DOCUMENT LOAD TASK --");
        Path inputFolderPath = configBean.getStorageInputPath().toAbsolutePath();
        File inputFolderFile = inputFolderPath.toFile();
        if (!inputFolderFile.exists() || !inputFolderFile.isDirectory()) {
            log.error("TaskScheduler: documentLoad ==> Document input folder " + inputFolderPath + " does not exist or is not a directory");
        }
        try {
            StatData sd = documentLoadService.loadFromInput(inputFolderPath);
            String loadStatStr = sd.toStatString();
            String message = "Done loading from folder " + inputFolderPath + " in " + sd.toDurationString() + " with next statisics of document status: " + loadStatStr;
            log.info(message);
        } catch (Exception e) {
            log.error("TaskScheduler: documentLoad ==> Failed to invoke documentLoadService.loadFromInput", e);
        }
        log.info("-- DONE DOCUMENT LOAD TASK --");
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void documentValidate() {
        log.info("-- START DOCUMENT VALIDATE TASK --");
        try {
            StatData sd = documentProcessService.processLoaded();
            String message = "Done processing of loaded files in " + sd.toDurationString() + " with result: " + sd.toStatString();
            log.info(message);
        } catch (Exception e) {
            log.error("TaskScheduler: documentValidate ==> Failed to invoke documentListProcessService.processLoaded", e);
        }
        log.info("-- DONE DOCUMENT VALIDATE TASK --");
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void documentDeliver() {
        log.info("-- START DOCUMENT DELIVER TASK --");
        try {
            StatData sd = documentDeliverService.processValidated();
            String message = "Done processing of validated files in " + sd.toDurationString() + " with result: " + sd.toStatString();
            log.info(message);
        } catch (Exception e) {
            log.error("TaskScheduler: documentDeliver ==> Failed to invoke documentDeliveryService.processValidated", e);
        }
        log.info("-- DONE DOCUMENT DELIVER TASK --");
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void documentCheckDelivery() {
        log.info("-- START DOCUMENT DELIVERY CHECK TASK --");
        try {
            StatData sd = documentCheckDeliveryService.checkDelivery();
            String message = "Done processing of exported records in " + sd.toDurationString() + " with result: " + sd.toStatString();
            log.info(message);
        } catch (Exception e) {
            log.error("TaskScheduler: documentDeliveryCheck ==> Failed to invoke documentCheckDeliveryService.checkDelivery", e);
        }
        log.info("-- DONE DOCUMENT DELIVERY CHECK TASK --");
    }

    @Scheduled(fixedDelay = 5000L)
    public void identifierLoad() {
        log.info("-- START IDENTIFIERS LOAD TASK --");
        try {
            List<OrganizationIdentifierLoadReport> loadReports = identifierBatchLoadService.performLoad();
            String reportMessage = identifierBatchLoadService.createReportMessage(loadReports);
            log.info(reportMessage);
        } catch (Exception e) {
            log.error("TaskScheduler: identifierLoad ==> Failed to invoke identifierBatchLoadService.performLoad", e);
        }
        log.info("-- DONE IDENTIFIERS LOAD TASK --");
    }


    @Scheduled(fixedDelay = 5000L)
    public void identifierPublish() {
        log.info("-- START IDENTIFIERS PUBLISH TASK --");
        try {
            List<Long> publishedIds = identifierBatchPublishingService.publishPending();
            log.info("Published ids: " + StringUtils.join(publishedIds, ","));
        } catch (Exception e) {
            log.error("TaskScheduler: identifierLoad ==> Failed to invoke identifierBatchLoadService.performLoad", e);
        }
        log.info("-- DONE IDENTIFIERS PUBLISH TASK --");
    }
    
    @Scheduled(fixedDelay = 5000L)
    public void sendDocumentValidate() {
        log.info("-- START SendDocument Validation -- ");
        try {
            StatData statData = sendDocumentService.validateNewDocuments();
            log.info("Validation stat: " + statData);
        } catch (Exception e) {
            log.error("TaskScheduler: sendDocumentValidate ==> Failed to invoke sendDocumentService.validateNewDocuments", e);
        }
        log.info("-- DONE SendDocument Validation --");
    }
    
    @Scheduled(fixedDelay = 5000L)
    public void sendDocumentFailedProcess() {
        log.info("-- START Failed SendDocument Processing -- ");
        try {
            StatData statData = sendDocumentFailedProcessService.processFailedDocuments();
            log.info("Process stat: " + statData);
        } catch (Exception e) {
            log.error("TaskScheduler: Failed to invoke sendDocumentFailedProcessService.processFailedDocuments", e);
        }
        log.info("-- DONE Failed SendDocument Processing --");
    }
}
