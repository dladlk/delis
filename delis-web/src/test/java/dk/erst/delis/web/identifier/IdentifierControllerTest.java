package dk.erst.delis.web.identifier;

import dk.erst.delis.dao.IdentifierDaoRepository;
import dk.erst.delis.dao.IdentifierGroupDaoRepository;
import dk.erst.delis.dao.OrganisationDaoRepository;
import dk.erst.delis.data.entities.identifier.Identifier;
import dk.erst.delis.data.entities.identifier.IdentifierGroup;
import dk.erst.delis.data.entities.organisation.Organisation;
import dk.erst.delis.data.enums.identifier.IdentifierStatus;
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

import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class IdentifierControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private IdentifierService identifierService;

    @Autowired
    private IdentifierDaoRepository identifierDaoRepository;

    @Autowired
    private OrganisationDaoRepository organisationDaoRepository;

    @Autowired
    private IdentifierGroupDaoRepository identifierGroupDaoRepository;

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(identifierService).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/identifier/list"))
                .andExpect(view().name("identifier/list"))
                .andExpect(model().attribute("identifierList", notNullValue()))
                .andExpect(model().attribute("selectedIdList", notNullValue()))
                .andExpect(model().attribute("statusList", notNullValue()))
                .andExpect(model().attribute("publishingStatusList", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdateStatuses() throws Exception {
        this.mockMvc.perform(post("/identifier/updatestatuses"))
                .andExpect(redirectedUrl("/identifier/list"));
    }


    @Test
    @WithMockUser(username = "delis")
    public void testView() throws Exception {
        Identifier save = getIdentifier();

        this.mockMvc.perform(get("/identifier/view/" + save.getId()))
                
                .andExpect(view().name("identifier/view"))
                .andExpect(model().attribute("identifierStatusList", notNullValue()))
                .andExpect(model().attribute("identifierPublishingStatusList", notNullValue()))
                .andExpect(model().attribute("identifier", notNullValue()))
                .andExpect(model().attribute("lastJournalList", notNullValue()));
    }

    private Identifier getIdentifier() {
        Organisation org = new Organisation();
        org.setName("name");
        org.setCode("code");
        Organisation organisation = organisationDaoRepository.save(org);
        IdentifierGroup group = new IdentifierGroup();
        group.setCode("code");
        group.setName("name");
        group.setOrganisation(organisation);

        IdentifierGroup identifierGroup = identifierGroupDaoRepository.save(group);
        Identifier identifier = new Identifier();
        identifier.setStatus(IdentifierStatus.ACTIVE);
        identifier.setName("id_name");
        identifier.setType("type");
        identifier.setIdentifierGroup(identifierGroup);
        identifier.setValue("value");
        identifier.setUniqueValueType("UniqueValueType" + new Date().getTime());
        identifier.setOrganisation(organisation);
        return identifierDaoRepository.save(identifier);
    }

    @Test
    @WithMockUser(username = "delis")
    public void testDelete() throws Exception {
        Identifier save = getIdentifier();

        this.mockMvc.perform(get("/identifier/delete/" + save.getId()))
                
                .andExpect(view().name("identifier/view"))
                .andExpect(model().attribute("identifier", notNullValue()));
    }
}
