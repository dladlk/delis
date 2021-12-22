package dk.erst.delis.task.scheduler;

import java.io.File;
import java.nio.file.Path;

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
    public StatData documentLoad() {
        TaskResult task = taskSchedulerMonitor.build("documentLoad");
        Path inputFolderPath = configBean.getStorageInputPath().toAbsolutePath();
        File inputFolderFile = inputFolderPath.toFile();
        if (!inputFolderFile.exists() || !inputFolderFile.isDirectory()) {
            log.error("TaskScheduler: documentLoad ==> Document input folder " + inputFolderPath + " does not exist or is not a directory");
			return StatData.error("Input folder " + inputFolderPath + " does not exist or is not a directory");
        } else {
	        try {
	            StatData sd = documentLoadService.loadFromInput(inputFolderPath);
	            if (!sd.isEmpty()) {
		            String message = "Done loading from folder " + inputFolderPath + " in " + sd.toDurationString() + " with next statisics of document status: " + sd.toStatString();
		            log.info(message);
	            }
	            task.success(sd);
	            return sd;
	        } catch (Exception e) {
	            log.error("TaskScheduler: documentLoad ==> Failed to invoke documentLoadService.loadFromInput", e);
	            task.failure(e);
	            return StatData.error(e.getMessage());
	        }
        }
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public StatData documentValidate() {
        TaskResult task = taskSchedulerMonitor.build("documentValidate");
        try {
            StatData sd = documentProcessService.processLoaded();
            if (!sd.isEmpty()) {
	            String message = "Done processing of loaded files in " + sd.toDurationString() + " with result: " + sd.toStatString();
	            log.info(message);
            }
            task.success(sd);
            return sd;
        } catch (Exception e) {
            log.error("TaskScheduler: documentValidate ==> Failed to invoke documentListProcessService.processLoaded", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public StatData documentDeliver() {
        TaskResult task = taskSchedulerMonitor.build("documentDeliver");
        try {
            StatData sd = documentDeliverService.processValidated();
            if (!sd.isEmpty()) {
	            String message = "Done processing of validated files in " + sd.toDurationString() + " with result: " + sd.toStatString();
	            log.info(message);
            }
            task.success(sd);
            return sd;
        } catch (Exception e) {
            log.error("TaskScheduler: documentDeliver ==> Failed to invoke documentDeliveryService.processValidated", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }

    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public StatData documentCheckDelivery() {
        TaskResult task = taskSchedulerMonitor.build("documentCheckDelivery");
        try {
            StatData sd = documentCheckDeliveryService.checkDelivery();
            if (!sd.isEmpty()) {
	            String message = "Done processing of exported records in " + sd.toDurationString() + " with result: " + sd.toStatString();
	            log.info(message);
            }
            task.success(sd);
            return sd;
        } catch (Exception e) {
            log.error("TaskScheduler: documentDeliveryCheck ==> Failed to invoke documentCheckDeliveryService.checkDelivery", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }

    @Scheduled(fixedDelay = 5000L)
    public StatData identifierLoad() {
        TaskResult task = taskSchedulerMonitor.build("identifierLoad");
        try {
            StatData sd = identifierBatchLoadService.performLoad();
            if (!sd.isEmpty()) {
            	log.info("identifierLoad: "+ sd);
            }
            task.success(sd);
            return sd;
        } catch (Exception e) {
            log.error("TaskScheduler: identifierLoad ==> Failed to invoke identifierBatchLoadService.performLoad", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }


    @Scheduled(fixedDelay = 5000L)
    public StatData identifierPublish() {
        TaskResult task = taskSchedulerMonitor.build("identifierPublish");
        try {
            StatData statData = identifierBatchPublishingService.publishPending();
            if (!statData.isEmpty()) {
            	log.info("identifierPublish: " + statData);
            }
            task.success(statData);
            return statData;
        } catch (Exception e) {
            log.error("TaskScheduler: identifierLoad ==> Failed to invoke identifierBatchLoadService.performLoad", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }
    
    @Scheduled(fixedDelay = 5000L)
    public StatData sendDocumentLoad() {
        TaskResult task = taskSchedulerMonitor.build("sendDocumentLoad");
        try {
            StatData statData = sendDocumentService.loadNewDocuments();
            if (!statData.isEmpty()) {
            	log.info("sendDocumentLoad: " + statData);
            }
            task.success(statData);
            return statData;
        } catch (Exception e) {
            log.error("TaskScheduler: sendDocumentLoad ==> Failed to invoke sendDocumentService.loadNewDocuments", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }    
    
    @Scheduled(fixedDelay = 5000L)
    public StatData sendDocumentValidate() {
        TaskResult task = taskSchedulerMonitor.build("sendDocumentValidate");
        try {
            StatData statData = sendDocumentService.validateNewDocuments();
            if (!statData.isEmpty()) {
            	log.info("sendDocumentValidate: " + statData);
            }
            task.success(statData);
            return statData;
        } catch (Exception e) {
            log.error("TaskScheduler: sendDocumentValidate ==> Failed to invoke sendDocumentService.validateNewDocuments", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }
    
    @Scheduled(fixedDelay = 5000L)
    public StatData sendDocumentFailedProcess() {
        TaskResult task = taskSchedulerMonitor.build("sendDocumentFailedProcess");
        try {
            StatData statData = sendDocumentFailedProcessService.processFailedDocuments();
            if (!statData.isEmpty()) {
            	log.info("sendDocumentFailedProcess: " + statData);
            }
            task.success(statData);
            return statData;
        } catch (Exception e) {
            log.error("TaskScheduler: Failed to invoke sendDocumentFailedProcessService.processFailedDocuments", e);
            task.failure(e);
            return StatData.error(e.getMessage());
        }
    }
}
