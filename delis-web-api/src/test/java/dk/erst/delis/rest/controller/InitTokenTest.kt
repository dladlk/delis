package dk.erst.delis.rest.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import dk.erst.delis.DelisWebApiApplication
import dk.erst.delis.dao.UserRepository
import dk.erst.delis.data.entities.user.User
import dk.erst.delis.rest.data.response.DataContainer
import dk.erst.delis.rest.data.response.auth.AuthData

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.http.MediaType
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import kotlin.test.assertEquals

/**
 * @author funtusthan, created by 24.03.19
 */

@ActiveProfiles("test")
@RunWith(SpringRunner::class)
@SpringBootTest(classes = [DelisWebApiApplication::class])
@ComponentScan(basePackages = ["dk.erst.delis"])
@AutoConfigureMockMvc
open class InitTokenTest {

    private val username = "admin"
    private val password = "admin"

    lateinit var auth: AuthData

    @Autowired lateinit var mvc: MockMvc
    @Autowired private lateinit var userRepository: UserRepository

    @Before
    fun initUser() {
        var user = userRepository.findByUsernameIgnoreCase(username)
        if (user == null) {
            user = User()
            user.username = username
            user.password = BCryptPasswordEncoder().encode(password)
            user = userRepository.save(user)
            assertEquals(user.username, username)
        }
    }

    @Before
    fun setUp() {
        val mvcResult: MvcResult = this.mvc.perform(
                MockMvcRequestBuilders
                        .post("/oauth/token")
                        .param("grant_type", "password")
                        .param("username", "admin")
                        .param("password", "admin")
                        .accept(MediaType.APPLICATION_JSON_UTF8)
                        .contentType(MediaType.APPLICATION_JSON_UTF8).header("Authorization", "Basic dGVzdDp0ZXN0"))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()
        val res: DataContainer<AuthData> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<DataContainer<AuthData>>() {}.type)
        this.auth = res.data
    }

    @Test
    fun testLogout() {
        val mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .delete("/rest/logout")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }
}
