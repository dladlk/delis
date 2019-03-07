package dk.erst.delis.task.identifier.load;

import dk.erst.delis.config.ConfigBean;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
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
                System.out.println("organizationCode = " + organizationCode);
                OrganizationIdentifierLoadReport loadReport = loadOrganizationIdentifiers(organizationFolder.toPath(), organizationCode);
                if (loadReport != null) {
                    result.add(loadReport);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private OrganizationIdentifierLoadReport loadOrganizationIdentifiers(Path orgFolderPath, String organizationCode) throws IOException {
        List<Path> filePathList = Files.list(orgFolderPath)
                .filter(Files::isRegularFile)
                .collect(Collectors.toList());
        List<SyncOrganisationFact> organisationFacts = new ArrayList<>();
        for (Path identifiersFilePath : filePathList) {
            try {
                FileInputStream fileInputStream = new FileInputStream(identifiersFilePath.toFile());
                SyncOrganisationFact syncOrganisationFact =
                        identifierLoadService.loadCSV(organizationCode, fileInputStream, identifiersFilePath.toString());
                System.out.println("syncOrganisationFact = " + syncOrganisationFact);
                organisationFacts.add(syncOrganisationFact);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        OrganizationIdentifierLoadReport loadReport = createLoadReport(organisationFacts);
        loadReport.setOrganizationCode(organizationCode);
        return loadReport;
    }

    private OrganizationIdentifierLoadReport createLoadReport(List<SyncOrganisationFact> organisationFacts) {
        OrganizationIdentifierLoadReport loadReport = new OrganizationIdentifierLoadReport();
        for (SyncOrganisationFact organisationFact : organisationFacts) {
            //TODO populate load report
        }
        return loadReport;
    }


}
