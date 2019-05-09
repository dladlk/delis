package dk.erst.delis.web.user;

import dk.erst.delis.dao.UserRepository;
import dk.erst.delis.data.entities.user.User;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void setUp() {
        for (int i = 0; i < 4; i++) {
            UserData user = new UserData();
            user.setUsername("test_user_" + i);
            user.setPassword("pass" + i);
            user.setEmail("test" + i + "@test.com");
            userService.saveOrUpdateUser(user);
        }

        UserData user = new UserData();
        user.setUsername("user_to_delete");
        user.setPassword("pass_user_to_delete");
        user.setEmail("user_to_delete@test.com");
        userService.saveOrUpdateUser(user);
    }

    @Test
    public void contextLoad() throws Exception {
        assertThat(userController).isNotNull();
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreateGet() throws Exception {
        this.mockMvc.perform(get("/users/create")).andExpect(view().name("user/edit"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/users/list")).andExpect(status().isOk())
                .andExpect(content().string(containsString("test_user_1")))
                .andExpect(content().string(containsString("test_user_3")));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreatePost() throws Exception {
        this.mockMvc.perform(post("/users/create")
                .param("username", "new_user")
                .param("password", "new_pass")
                .param("email", "new_user@test.com"))
                .andExpect(redirectedUrl("/users/list"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdateGet() throws Exception {
        User test_user = userService.findUserByUsername("test_user_0");
        Long id = test_user.getId();
        this.mockMvc.perform(get("/users/update/" + id))
                .andExpect(view().name("user/update"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdatePost() throws Exception {
        User user = userService.findUserByUsername("test_user_0");
        this.mockMvc.perform(post("/users/update")
                .param("id", "" + user.getId())
                .param("username", user.getUsername())
                .param("password", user.getPassword())
                .param("email", user.getEmail())
                .param("lastName", "new_not_empty_last_name"))
                .andExpect(redirectedUrl("/users/list"));

        User updated = userService.findUserByUsername("test_user_0");
        Assert.assertEquals("new_not_empty_last_name", updated.getLastName());

    }

    @Test
    @WithMockUser(username = "delis")
    public void testDelete() throws Exception {
        User userToDelete = userService.findUserByUsername("user_to_delete");

        Assert.assertNotNull(userToDelete);

        this.mockMvc.perform(get("/users/delete/" + userToDelete.getId())).andExpect(view().name("user/list"));

        User deletedUser = userService.findUserByUsername("user_to_delete");
        Assert.assertNull(deletedUser);
    }

    @After
    public void cleanUp () {
        userRepository.deleteAll();
    }
}
