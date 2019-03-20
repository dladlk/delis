package dk.erst.delis.web.document;

import dk.erst.delis.dao.AccessPointDaoRepository;
import dk.erst.delis.dao.DocumentDaoRepository;
import dk.erst.delis.data.entities.access.AccessPoint;
import dk.erst.delis.data.entities.document.Document;
import dk.erst.delis.data.enums.access.AccessPointType;
import dk.erst.delis.data.enums.document.DocumentStatus;
import dk.erst.delis.web.accesspoint.AccessPointService;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.rowset.serial.SerialBlob;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class DocumentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DocumentService documentService;

    @Autowired
    private DocumentDaoRepository documentDaoRepository;


    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(documentService).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/document/list")).andDo(print())
                .andExpect(view().name("/document/list"))
                .andExpect(model().attribute("documentList", notNullValue()))
                .andExpect(model().attribute("selectedIdList", notNullValue()))
                .andExpect(model().attribute("statusList", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdateStatus() throws Exception {
        Document document = new Document();
        document.setName("TestDocument1");
        document.setMessageId("messageId");
        document.setDocumentStatus(DocumentStatus.LOAD_OK);
        Document save = documentDaoRepository.save(document);

        save.setDocumentStatus(DocumentStatus.VALIDATE_OK);

        this.mockMvc.perform(post("/document/updatestatus")
                .param("id", "" + save.getId())
                .param("messageId", save.getMessageId())
                .param("documentStatus", save.getDocumentStatus().name()))
                .andDo(print())
                .andExpect(redirectedUrl("/document/view/" + save.getId()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testDocumentView() throws Exception {
        Document document = new Document();
        document.setName("TestDocumentView");
        document.setMessageId("messageId");
        document.setDocumentStatus(DocumentStatus.LOAD_ERROR);
        Document save = documentDaoRepository.save(document);

        this.mockMvc.perform(get("/document/view/" + save.getId())).andDo(print())
                .andExpect(view().name("/document/view"))
                .andExpect(model().attribute("document", notNullValue()))
                .andExpect(model().attribute("documentStatusList", notNullValue()))
                .andExpect(model().attribute("lastJournalList", notNullValue()))
                .andExpect(model().attribute("errorListByJournalDocumentIdMap", notNullValue()))
                .andExpect(model().attribute("documentBytes", notNullValue()));
    }

    @After
    public void cleanUp() {

    }
}
