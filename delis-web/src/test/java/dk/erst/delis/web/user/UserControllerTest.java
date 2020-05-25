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
import static org.junit.Assert.assertFalse;
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
            user.setPassword("Systest" + i+"_");
            user.setFirstName("Test");
            user.setLastName("Test");
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
        this.mockMvc.perform(get("/user/create")).andExpect(view().name("user/edit"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testList() throws Exception {
        this.mockMvc.perform(get("/user/list")).andExpect(status().isOk())
                .andExpect(content().string(containsString("test_user_1")))
                .andExpect(content().string(containsString("test_user_3")));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testCreatePost() throws Exception {
        this.mockMvc.perform(post("/user/save")
                .param("username", "new_user")
                .param("password", "Systest1_")
                .param("password2", "Systest1_")
                .param("firstName", "First")
                .param("lastName", "Last")
                .param("admin", "true")
                .param("email", "new_user@test.com"))
                .andExpect(redirectedUrl("/user/view/27"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdateGet() throws Exception {
        User test_user = userService.findUserByUsername("test_user_0");
        Long id = test_user.getId();
        this.mockMvc.perform(get("/user/update/" + id))
                .andExpect(view().name("user/edit"));
    }

    @Test
    @WithMockUser(username = "delis")
    public void testUpdatePost() throws Exception {
        User user = userService.findUserByUsername("test_user_0");
        this.mockMvc.perform(post("/user/save")
                .param("id", "" + user.getId())
                .param("username", user.getUsername())
                .param("password", "Systest1_")
                .param("password2", "Systest1_")
                .param("email", user.getEmail())
                .param("admin", "true")
                .param("lastName", "new_not_empty_last_name")
        		.param("firstName", "new_not_empty_first_name"))
                .andExpect(redirectedUrl("/user/view/"+user.getId()));

        User updated = userService.findUserByUsername("test_user_0");
        Assert.assertEquals("new_not_empty_last_name", updated.getLastName());

    }

    @Test
    @WithMockUser(username = "delis")
    public void testDeactivateActivate() throws Exception {
        User userToDelete = userService.findUserByUsername("user_to_delete");
        assertFalse(userToDelete.isDisabled());
        Assert.assertNotNull(userToDelete);

        this.mockMvc.perform(get("/user/deactivate/" + userToDelete.getId())).andExpect(redirectedUrl("/user/view/"+userToDelete.getId()));

        User deletedUser = userService.findUserByUsername("user_to_delete");
        Assert.assertTrue(deletedUser.isDisabled());

        this.mockMvc.perform(get("/user/activate/" + userToDelete.getId())).andExpect(redirectedUrl("/user/view/"+userToDelete.getId()));
        
        deletedUser = userService.findUserByUsername("user_to_delete");
        Assert.assertFalse(deletedUser.isDisabled());
    }

    @After
    public void cleanUp () {
        userRepository.deleteAll();
    }
}
