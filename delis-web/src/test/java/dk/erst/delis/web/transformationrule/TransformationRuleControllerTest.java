package dk.erst.delis.web.transformationrule;

import dk.erst.delis.dao.RuleDocumentTransformationDaoRepository;
import dk.erst.delis.data.entities.rule.RuleDocumentTransformation;
import dk.erst.delis.data.enums.document.DocumentFormatFamily;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TransformationRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TransformationRuleService service;

    @Autowired
    private RuleDocumentTransformationDaoRepository ruleRepository;

    @Test
    public void contextLoad() {
        assertThat(service).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/transformationrule/list")).andDo(print())
                .andExpect(view().name("setup/index"))
                .andExpect(model().attribute("transformationRuleList", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateNewPost() throws Exception {

        this.mockMvc.perform(post("/transformationrule/save")
                .param("active", "true")
                .param("documentFormatFamilyFrom", "BIS3")
                .param("documentFormatFamilyTo", "OIOUBL")
                .param("rootPath", "rootPath")
                .param("config", "config"))
                .andDo(print())
                .andExpect(redirectedUrl("/setup/index"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateNewGet() throws Exception {
        RuleDocumentTransformation rule = createRule();

        this.mockMvc.perform(get("/transformationrule/create/" + rule.getId()))
                .andDo(print())
                .andExpect(view().name("transformationrule/edit"))
                .andExpect(model().attribute("transformationRule", notNullValue()))
                .andExpect(model().attribute("documentFormatList", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdateRule() throws Exception {
        RuleDocumentTransformation rule = createRule();

        this.mockMvc.perform(get("/transformationrule/update/" + rule.getId()))
                .andDo(print())
                .andExpect(view().name("transformationrule/edit"))
                .andExpect(model().attribute("transformationRule", notNullValue()))
                .andExpect(model().attribute("documentFormatList", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateDefault() throws Exception {
        RuleDocumentTransformation rule = createRule();

        this.mockMvc.perform(get("/transformationrule/createdefault"))
                .andDo(print())
                .andExpect(redirectedUrl("/setup/index"));
    }

    private RuleDocumentTransformation createRule() {
        RuleDocumentTransformation s = new RuleDocumentTransformation();
        s.setActive(true);
        s.setDocumentFormatFamilyFrom(DocumentFormatFamily.BIS3);
        s.setDocumentFormatFamilyTo(DocumentFormatFamily.OIOUBL);
        s.setRootPath("rootPath");
        s.setConfig("config");
        RuleDocumentTransformation save = ruleRepository.save(s);
        return save;
    }
}
