package dk.erst.delis.web.validationrule;

import dk.erst.delis.dao.RuleDocumentValidationDaoRepository;
import dk.erst.delis.data.entities.rule.RuleDocumentValidation;
import dk.erst.delis.data.enums.document.DocumentFormat;
import dk.erst.delis.data.enums.rule.RuleDocumentValidationType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ValidationRuleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ValidationRuleService service;

    @Autowired
    private RuleDocumentValidationDaoRepository ruleRepository;

    @Test
    public void contextLoad() {
        assertThat(service).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/validationrule/list"))
                .andExpect(view().name("setup/index"))
                .andExpect(model().attribute("validationRuleList", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateNewPost() throws Exception {

        this.mockMvc.perform(post("/validationrule/save")
                .param("active","true")
                .param("priority","1")
                .param("documentFormat","CII")
                .param("validationType","SCHEMATRON")
                .param("rootPath","rootPath")
                .param("config","config"))
                
                .andExpect(redirectedUrl("/setup/index"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateNewGet() throws Exception {
        RuleDocumentValidation rule = createRule();

        this.mockMvc.perform(get("/validationrule/create/" + rule.getId()))
                
                .andExpect(view().name("validationrule/edit"))
                .andExpect(model().attribute("validationRule", notNullValue()))
                .andExpect(model().attribute("documentFormatList", notNullValue()))
                .andExpect(model().attribute("validationTypeList", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdateRule() throws Exception {
        RuleDocumentValidation rule = createRule();

        this.mockMvc.perform(get("/validationrule/update/" + rule.getId()))
                
                .andExpect(view().name("validationrule/edit"))
                .andExpect(model().attribute("validationRule", notNullValue()))
                .andExpect(model().attribute("documentFormatList", notNullValue()))
                .andExpect(model().attribute("validationTypeList", notNullValue()));
    }

    private RuleDocumentValidation createRule () {
        RuleDocumentValidation s = new RuleDocumentValidation();
        s.setActive(true);
        s.setDocumentFormat(DocumentFormat.CII);
        s.setPriority(0);
        s.setRootPath("");
        s.setValidationType(RuleDocumentValidationType.XSD);
        s.setConfig("config");
        RuleDocumentValidation save = ruleRepository.save(s);
        return save;
    }
}
