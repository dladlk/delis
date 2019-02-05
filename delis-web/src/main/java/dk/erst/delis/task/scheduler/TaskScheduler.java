package dk.erst.delis.task.scheduler;

import dk.erst.delis.common.util.StatData;
import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.task.document.deliver.DocumentDeliverService;
import dk.erst.delis.task.document.load.DocumentLoadService;
import dk.erst.delis.task.document.process.DocumentProcessService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;

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

    @Autowired
    public TaskScheduler(
            ConfigBean configBean,
            DocumentLoadService documentLoadService,
            DocumentProcessService documentProcessService,
            DocumentDeliverService documentDeliverService) {
        this.configBean = configBean;
        this.documentLoadService = documentLoadService;
        this.documentProcessService = documentProcessService;
        this.documentDeliverService = documentDeliverService;
    }

    @Scheduled(fixedRate = 30000)
    public void documentLoad() {
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
    }

    @Scheduled(fixedDelay = 30000)
    public void documentValidate() {
        try {
            StatData sd = documentProcessService.processLoaded();
            String message = "Done processing of loaded files in " + sd.toDurationString() + " with result: " + sd.toStatString();
            log.info(message);
        } catch (Exception e) {
            log.error("TaskScheduler: documentValidate ==> Failed to invoke documentListProcessService.processLoaded", e);
        }
    }

    @Scheduled(fixedDelay = 30000)
    public void documentDeliver() {
        try {
            StatData sd = documentDeliverService.processValidated();
            String message = "Done processing of validated files in " + sd.toDurationString() + " with result: " + sd.toStatString();
            log.info(message);
        } catch (Exception e) {
            log.error("TaskScheduler: documentDeliver ==> Failed to invoke documentDeliveryService.processValidated", e);
        }
    }
}
