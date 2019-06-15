package dk.erst.delis.web.setup;

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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class SetupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SetupController setupController;

    @Before
    public void setUp() {
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(setupController).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testIndex() throws Exception {
        this.mockMvc.perform(get("/setup/index"))
                .andExpect(view().name("/setup/index"))
                .andExpect(model().attribute("configValuesList", notNullValue()))
                .andExpect(model().attribute("configBean", notNullValue()))
                .andExpect(model().attribute("configList", notNullValue()))
                .andExpect(model().attribute("validationRuleList", notNullValue()))
                .andExpect(model().attribute("transformationRuleList", notNullValue()));
    }

    //todo
//    @Test
//    @WithMockUser(username = "delis")
//    public void testCreate() throws Exception {
//        this.mockMvc.perform(get("/setup/config/create"))
//                .andExpect(view().name("/setup/config_value_edit"))
//                .andExpect(model().attribute("configValueTypeList", notNullValue()))
//                .andExpect(model().attribute("configValue", notNullValue()));
//    }

    @Test
    @WithMockUser(username = "delis")
    public void testSave() throws Exception {
        this.mockMvc.perform(post("/setup/config/save")
                .param("configValueType", "ENDPOINT_FORMAT")
                .param("value", "test"))
                .andExpect(redirectedUrl("/setup/index"))
                .andExpect(model().attribute("errorMessage", nullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdate() throws Exception {
        this.mockMvc.perform(get("/setup/config/dbupdate"))
                .andExpect(redirectedUrl("/setup/index"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testEdit() throws Exception {
        this.mockMvc.perform(get("/setup/config/edit/ENDPOINT_FORMAT"))
                .andExpect(view().name("/setup/config_value_edit"))
                .andExpect(model().attribute("configValue", notNullValue()));
    }

}
