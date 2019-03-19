package dk.erst.delis.rest.controller

import com.google.gson.Gson

import dk.erst.delis.rest.data.request.login.LoginData

import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * @author funtusthan, created by 18.03.19
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class SecurityRestControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun testLogin() {
        val loginData = LoginData()
        loginData.login = "admin"
        loginData.password = "admin"

        val gson = Gson()
        val body = gson.toJson(loginData)

        mvc.perform(MockMvcRequestBuilders.post("/rest/security/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful)
    }
}
