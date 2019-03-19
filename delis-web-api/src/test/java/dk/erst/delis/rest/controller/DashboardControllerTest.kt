package dk.erst.delis.rest.controller

import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import kotlin.test.assertEquals

/**
 * @author funtusthan, created by 18.03.19
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class DashboardControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun generateDashboardDataTest() {
        val mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/dashboard"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }

    @Test
    fun generateDashboardChartData() {
        val mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/chart"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }
}
