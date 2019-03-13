package dk.erst.delis.task.scheduler;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.document.deliver.DocumentDeliverService;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;
import dk.erst.delis.task.identifier.load.IdentifierBatchLoadService;
import dk.erst.delis.task.identifier.load.OrganizationIdentifierLoadReport;
import dk.erst.delis.task.identifier.publish.IdentifierBatchPublishingService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

/**
 * @author funtusthan, created by 05.02.19
 */

@Slf4j
@Service
public class TaskScheduler {

    private final ConfigBean configBean;
    private final DocumentLoadService documentLoadService;
    private final DocumentProcessService documentProcessService;
    private final DocumentDeliverService documentDeliverService;
    private final IdentifierBatchLoadService identifierBatchLoadService;
    private final IdentifierBatchPublishingService identifierBatchPublishingService;

    @Autowired
    public TaskScheduler(
            ConfigBean configBean,
            DocumentLoadService documentLoadService,
            DocumentProcessService documentProcessService,
            DocumentDeliverService documentDeliverService,
            IdentifierBatchLoadService identifierBatchLoadService,
            IdentifierBatchPublishingService identifierBatchPublishingService) {
        this.configBean = configBean;
        this.documentLoadService = documentLoadService;
        this.documentProcessService = documentProcessService;
        this.documentDeliverService = documentDeliverService;
        this.identifierBatchLoadService = identifierBatchLoadService;
        this.identifierBatchPublishingService = identifierBatchPublishingService;
    }

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
}
