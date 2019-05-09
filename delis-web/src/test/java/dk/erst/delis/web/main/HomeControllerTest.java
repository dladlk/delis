package dk.erst.delis.web.main;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HomeController homeController;

    @Before
    public void setUp() {
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(homeController).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testHome() throws Exception {
        this.mockMvc.perform(get("/home"))
                .andExpect(model().attribute("organisationCount", notNullValue()))
                .andExpect(model().attribute("identifierFailedCount", notNullValue()))
                .andExpect(model().attribute("identifierPendingCount", notNullValue()))
                .andExpect(model().attribute("documentStat", notNullValue()))
                .andExpect(view().name("home"));
    }
}
