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
import dk.erst.delis.task.scheduler.TaskSchedulerMonitor.TaskResult;
import dk.erst.delis.web.document.SendDocumentService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TaskScheduler {

	@Autowired
	private TaskSchedulerMonitor taskSchedulerMonitor;

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
        TaskResult task = taskSchedulerMonitor.build("documentLoad");
        Path inputFolderPath = configBean.getStorageInputPath().toAbsolutePath();
        File inputFolderFile = inputFolderPath.toFile();
        if (!inputFolderFile.exists() || !inputFolderFile.isDirectory()) {
            log.error("TaskScheduler: documentLoad ==> Document input folder " + inputFolderPath + " does not exist or is not a directory");
        } else {
	        try {
	            StatData sd = documentLoadService.loadFromInput(inputFolderPath);
	            if (!sd.isEmpty()) {
		            String message = "Done loading from folder " + inputFolderPath + " in " + sd.toDurationString() + " with next statisics of document status: " + sd.toStatString();
		            log.info(message);
	            }
	            task.success(sd);
	        } catch (Exception e) {
	            log.error("TaskScheduler: documentLoad ==> Failed to invoke documentLoadService.loadFromInput", e);
	            task.failure(e);
	        }
        }
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void documentValidate() {
        TaskResult task = taskSchedulerMonitor.build("documentValidate");
        try {
            StatData sd = documentProcessService.processLoaded();
            if (!sd.isEmpty()) {
	            String message = "Done processing of loaded files in " + sd.toDurationString() + " with result: " + sd.toStatString();
	            log.info(message);
            }
            task.success(sd);
        } catch (Exception e) {
            log.error("TaskScheduler: documentValidate ==> Failed to invoke documentListProcessService.processLoaded", e);
            task.failure(e);
        }
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void documentDeliver() {
        TaskResult task = taskSchedulerMonitor.build("documentDeliver");
        try {
            StatData sd = documentDeliverService.processValidated();
            if (!sd.isEmpty()) {
	            String message = "Done processing of validated files in " + sd.toDurationString() + " with result: " + sd.toStatString();
	            log.info(message);
            }
            task.success(sd);
        } catch (Exception e) {
            log.error("TaskScheduler: documentDeliver ==> Failed to invoke documentDeliveryService.processValidated", e);
            task.failure(e);
        }
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void documentCheckDelivery() {
        TaskResult task = taskSchedulerMonitor.build("documentCheckDelivery");
        try {
            StatData sd = documentCheckDeliveryService.checkDelivery();
            if (!sd.isEmpty()) {
	            String message = "Done processing of exported records in " + sd.toDurationString() + " with result: " + sd.toStatString();
	            log.info(message);
            }
            task.success(sd);
        } catch (Exception e) {
            log.error("TaskScheduler: documentDeliveryCheck ==> Failed to invoke documentCheckDeliveryService.checkDelivery", e);
            task.failure(e);
        }
    }

    @Scheduled(fixedDelay = 5000L)
    public void identifierLoad() {
        TaskResult task = taskSchedulerMonitor.build("identifierLoad");
        try {
            List<OrganizationIdentifierLoadReport> loadReports = identifierBatchLoadService.performLoad();
            String reportMessage = identifierBatchLoadService.createReportMessage(loadReports);
            if (!reportMessage.isEmpty()) {
            	log.info(reportMessage);
            }
            task.success(reportMessage);
        } catch (Exception e) {
            log.error("TaskScheduler: identifierLoad ==> Failed to invoke identifierBatchLoadService.performLoad", e);
            task.failure(e);
        }
    }


    @Scheduled(fixedDelay = 5000L)
    public void identifierPublish() {
        TaskResult task = taskSchedulerMonitor.build("identifierPublish");
        try {
            List<Long> publishedIds = identifierBatchPublishingService.publishPending();
            if (publishedIds != null && !publishedIds.isEmpty()) {
            	log.info("Published ids: " + StringUtils.join(publishedIds, ","));
            }
            task.success(publishedIds);
        } catch (Exception e) {
            log.error("TaskScheduler: identifierLoad ==> Failed to invoke identifierBatchLoadService.performLoad", e);
            task.failure(e);
        }
    }
    
    @Scheduled(fixedDelay = 5000L)
    public void sendDocumentValidate() {
        TaskResult task = taskSchedulerMonitor.build("sendDocumentValidate");
        try {
            StatData statData = sendDocumentService.validateNewDocuments();
            if (!statData.isEmpty()) {
            	log.info("sendDocumentValidate: " + statData);
            }
            task.success(statData);
        } catch (Exception e) {
            log.error("TaskScheduler: sendDocumentValidate ==> Failed to invoke sendDocumentService.validateNewDocuments", e);
            task.failure(e);
        }
    }
    
    @Scheduled(fixedDelay = 5000L)
    public void sendDocumentFailedProcess() {
        TaskResult task = taskSchedulerMonitor.build("sendDocumentFailedProcess");
        try {
            StatData statData = sendDocumentFailedProcessService.processFailedDocuments();
            if (!statData.isEmpty()) {
            	log.info("sendDocumentFailedProcess: " + statData);
            }
            task.success(statData);
        } catch (Exception e) {
            log.error("TaskScheduler: Failed to invoke sendDocumentFailedProcessService.processFailedDocuments", e);
            task.failure(e);
        }
    }
}
