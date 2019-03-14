package dk.erst.delis.task.identifier.load;

import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.entities.organisation.SyncOrganisationFact;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static org.junit.Assert.assertEquals;


@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class IdentifierLoadServiceTest {

    @Autowired
    private IdentifierLoadService identifierLoadService;

    @Autowired
    private OrganisationDaoRepository organisationDaoRepository;

    @Test
    public void test() throws IOException {
        Organisation testOrganization = createTestOrganization();
        List<String> lines = createTestIdentifiersLines();
        testOrganization = organisationDaoRepository.save(testOrganization);
        String organizationCode = testOrganization.getCode();
        SyncOrganisationFact result = identifierLoadService.loadCSV(organizationCode, createTestCSVStream(lines), "test.csv");
        //ADD
        assertEquals(lines.size(), result.getAdd());
        int updatedCount = updateIdentifierName(lines);
        result = identifierLoadService.loadCSV(organizationCode, createTestCSVStream(lines), "test.csv");
        //UPDATE
        assertEquals(updatedCount, result.getUpdate());
        //EQUAL
        assertEquals(lines.size() - updatedCount, result.getEqual());
        int expectedDeleted = lines.size();
        lines.clear();
        result = identifierLoadService.loadCSV(organizationCode, createTestCSVStream(lines), "test.csv");
        //DELETE
        assertEquals(expectedDeleted, result.getDelete());

    }

    private int updateIdentifierName(List<String> lines) {
        int updateCount = ThreadLocalRandom.current().nextInt(1, lines.size() + 1);
        for (int i = 0; i < updateCount; i++) {
            String line = lines.remove(0);
            String[] parts = line.split(";");
            lines.add(parts[0] + ";UPDATED NAME-" + i);
        }
        return updateCount;
    }

    private InputStream createTestCSVStream(List<String> lines) {
        List<String> newLines = new ArrayList<>(lines);
        newLines.add(0, "EAN;Name");
        String csvString = StringUtils.join(newLines, "\n");
        return new ByteArrayInputStream(csvString.getBytes());
    }

    private List<String> createTestIdentifiersLines() {
        List<String> lines = new ArrayList<>();
        long timeStamp = System.currentTimeMillis();
        int linesCount = 6;
        for (int i = 0; i < linesCount; i++) {
            long l = timeStamp + i;
            lines.add(String.format("%d;%s", l, "test - " + l));
        }
        return lines;
    }

    private Organisation createTestOrganization() {
        Organisation organisation = new Organisation();
        String organizationCode = "test-" + System.currentTimeMillis();
        organisation.setCode(organizationCode);
        organisation.setName(organizationCode);
        return organisation;
    }
}
