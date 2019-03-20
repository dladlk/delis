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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController mainController;

    @Before
    public void setUp() {
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(mainController).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testMain() throws Exception {
        this.mockMvc.perform(get("/")).andDo(print())
                .andExpect(redirectedUrl("/home"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testLogin() throws Exception {
        this.mockMvc.perform(get("/login")).andDo(print())
                .andExpect(view().name("login"));
    }
}
