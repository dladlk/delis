package dk.erst.delis.rest.controller

import com.fasterxml.jackson.databind.ObjectMapper

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import dk.erst.delis.DelisWebApiApplication
import dk.erst.delis.data.entities.user.User
import dk.erst.delis.exception.handler.RestExceptionHandler
import dk.erst.delis.rest.controller.auth.SecurityRestController
import dk.erst.delis.rest.data.request.login.LoginData
import dk.erst.delis.rest.data.response.DataContainer
import dk.erst.delis.rest.data.response.auth.AuthData
import dk.erst.delis.service.auth.AuthService
import dk.erst.delis.service.auth.AuthTokenFilter
import dk.erst.delis.service.auth.AuthUserProviderService

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MvcResult

import kotlin.test.assertEquals
import kotlin.test.assertTrue

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder

/**
 * @author funtusthan, created by 19.03.19
 */

@RunWith(SpringRunner::class)
@DataJpaTest
@SpringBootTest(classes = [DelisWebApiApplication::class])
@ComponentScan(basePackages = ["dk.erst.delis"])
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=update"])
@AutoConfigureMockMvc
open class SecurityRestControllerTest {

    private val loginUrl = "/rest/security/signin"
    private val logoutUrl = "/rest/security/logout"
    private val username = "admin"
    private val password = "admin"

    private lateinit var mvc: MockMvc
    private lateinit var src: SecurityRestController
    private lateinit var authTokenFilter: AuthTokenFilter
    private lateinit var restExceptionHandler: RestExceptionHandler

    @Autowired
    private lateinit var em: TestEntityManager
    @Autowired
    private lateinit var authService: AuthService
    @Autowired
    private lateinit var authUserProviderService: AuthUserProviderService


    @Before
    fun init() {
        this.src = SecurityRestController(authService, authUserProviderService)
        this.authTokenFilter = AuthTokenFilter()
        this.restExceptionHandler = RestExceptionHandler()
        this.mvc = MockMvcBuilders
                .standaloneSetup(this.src)
                .setControllerAdvice(this.restExceptionHandler)
                .addFilters<StandaloneMockMvcBuilder>(this.authTokenFilter)
                .build()
    }

    @Before
    fun testUser() {
        var user = User()
        user.username = username
        user.password = BCryptPasswordEncoder().encode(password)
        em.persist(user)
        user = em.find(User::class.java, 1L)
        assertEquals(user.username, username)
    }

    @Test
    fun testLogin() {

        val loginData = LoginData()
        loginData.login = username
        loginData.password = password

        var mvcResult: MvcResult = this.mvc.perform(MockMvcRequestBuilders.post(loginUrl)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(ObjectMapper().writeValueAsString(loginData)))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        assertEquals(200, mvcResult.response.status)

        val data: DataContainer<AuthData> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<DataContainer<AuthData>>() {}.type)

        val authData: AuthData = data.data

        assertEquals(authData.username, username)

        val regex = """\b[0-9a-f]{8}\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\b[0-9a-f]{12}\b""".toRegex() // UUIDs regex

        assertTrue(regex.containsMatchIn(authData.token))

        mvcResult = this.mvc.perform(
                MockMvcRequestBuilders
                        .delete(logoutUrl)
                        .header("Authorization", authData.token))
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andReturn()

        val dataLogout: DataContainer<Boolean> = Gson().fromJson(mvcResult.response.contentAsString,
        object : TypeToken<DataContainer<Boolean>>() {}.type)

        assertEquals(true, dataLogout.data)
    }
}
