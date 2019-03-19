package dk.erst.delis.web.task;

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
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskController taskController;

    @Test
    public void contextLoad() throws Exception {
        assertThat(taskController).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testIndex() throws Exception {
        this.mockMvc.perform(get("/task/index")).andDo(print())
                .andExpect(view().name("/task/index"))
                .andExpect(model().attribute("message", nullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testIdentifierLoad() throws Exception {
        this.mockMvc.perform(get("/task/identifierLoad")).andDo(print())
                .andExpect(view().name("/task/index"))
                .andExpect(model().attribute("message", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testIdentifierPublish() throws Exception {
        this.mockMvc.perform(get("/task/identifierPublish")).andDo(print())
                .andExpect(view().name("/task/index"))
                .andExpect(model().attribute("message", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testDocumentLoad() throws Exception {
        this.mockMvc.perform(get("/task/documentLoad")).andDo(print())
                .andExpect(view().name("/task/index"))
                .andExpect(model().attribute("message", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testDocumentValidate() throws Exception {
        this.mockMvc.perform(get("/task/documentValidate")).andDo(print())
                .andExpect(view().name("/task/index"))
                .andExpect(model().attribute("message", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testDocumentDeliver() throws Exception {
        this.mockMvc.perform(get("/task/documentDeliver")).andDo(print())
                .andExpect(view().name("/task/index"))
                .andExpect(model().attribute("message", notNullValue()));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testDocumentUnimplemented() throws Exception {
        this.mockMvc.perform(get("/task/unimplemented")).andDo(print())
                .andExpect(view().name("/task/index"))
                .andExpect(model().attribute("message", nullValue()));
    }
}
