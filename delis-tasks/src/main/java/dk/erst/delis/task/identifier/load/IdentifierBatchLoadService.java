package dk.erst.delis.task.identifier.load;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class IdentifierBatchLoadService {

    private ConfigBean configBean;
    private IdentifierLoadService identifierLoadService;

    @Autowired
    public IdentifierBatchLoadService(ConfigBean configBean, IdentifierLoadService identifierLoadService) {
        this.configBean = configBean;
        this.identifierLoadService = identifierLoadService;
    }

    public List<OrganizationIdentifierLoadReport> performLoad() {
        List<OrganizationIdentifierLoadReport> result = new ArrayList<>();
        try {
            Path identifierInputPath = configBean.getIdentifierInputPath();
            File identifierInputDir = identifierInputPath.toFile();
            if (!identifierInputDir.exists() || !identifierInputDir.isDirectory()) {
                return result;
            }
            for (File organizationFolder : identifierInputDir.listFiles()) {
                String organizationCode = organizationFolder.getName();
                OrganizationIdentifierLoadReport loadReport = loadOrganizationIdentifiers(organizationFolder.toPath(), organizationCode);
                if (loadReport != null) {
                    result.add(loadReport);
                }
            }
        } catch (Exception e) {
            log.error("Failed to load identifiers", e);
        }
        return result;
    }

    private OrganizationIdentifierLoadReport loadOrganizationIdentifiers(Path orgFolderPath, String organizationCode) throws IOException {
        List<Path> filePathList = Files.list(orgFolderPath)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        List<SyncOrganisationFact> organisationFacts = new ArrayList<>();
        for (Path identifiersFilePath : filePathList) {
            try (FileInputStream fileInputStream = new FileInputStream(identifiersFilePath.toFile())) {
                SyncOrganisationFact syncOrganisationFact =
                        identifierLoadService.loadCSV(organizationCode, fileInputStream, identifiersFilePath.toString());
                organisationFacts.add(syncOrganisationFact);
            } catch (Exception e) {
                log.error("An error occurred during processing file " + identifiersFilePath, e);
            } finally {
                moveToProcessedFolder(identifiersFilePath);
            }
        }
        if (organisationFacts.isEmpty()) {
            return null;
        }
        OrganizationIdentifierLoadReport loadReport = createLoadReport(organisationFacts);
        loadReport.setOrganizationCode(organizationCode);
        return loadReport;
    }

    private void moveToProcessedFolder(Path sourceFilePath) {
        File targetFolder = new File(sourceFilePath.getParent().toString(), "PROCESSED");
        if (!targetFolder.exists() && targetFolder.mkdirs()) {
            throw new RuntimeException("Target folder "+targetFolder+" for processed identifier files not exists and unable to create");
        }
        try {
            DateTimeFormatter timeStampPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            String targetFileNamePrefix = timeStampPattern.format(LocalDateTime.now());
            String targetFileName = targetFileNamePrefix + "_" + sourceFilePath.getFileName().toString();
            Path targetFilePath = Paths.get(targetFolder.toString(), targetFileName);
            Path moved = Files.move(sourceFilePath, targetFilePath, StandardCopyOption.REPLACE_EXISTING);
            log.info(String.format("Processed file '%s' moved to '%s'", sourceFilePath, moved));
        } catch (IOException e) {
            log.error("Failed to move processed file " + sourceFilePath, e);
        }
    }

    private OrganizationIdentifierLoadReport createLoadReport(List<SyncOrganisationFact> organisationFacts) {
        OrganizationIdentifierLoadReport loadReport = new OrganizationIdentifierLoadReport();
        for (SyncOrganisationFact organisationFact : organisationFacts) {
            loadReport.setAdd(loadReport.getAdd() + organisationFact.getAdd());
            loadReport.setUpdate(loadReport.getUpdate() + organisationFact.getUpdate());
            loadReport.setDelete(loadReport.getDelete() + organisationFact.getDelete());
            loadReport.setEqual(loadReport.getEqual() + organisationFact.getEqual());
            loadReport.setFailed(loadReport.getFailed() + organisationFact.getFailed());
        }
        return loadReport;
    }

    public String createReportMessage(List<OrganizationIdentifierLoadReport> loadReports) {
        List<String> messages = loadReports.stream()
                .map(object -> Objects.toString(object, null))
                .collect(Collectors.toList());
        return StringUtils.join(messages, "\n");
    }

}
