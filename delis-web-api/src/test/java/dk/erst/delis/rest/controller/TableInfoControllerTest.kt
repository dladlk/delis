package dk.erst.delis.rest.controller

import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

/**
 * @author funtusthan, created by 18.03.19
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class TableInfoControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun getTableInfoByAllEntitiesTest() {
        val mvcResult: MvcResult = mvc.perform(get("/rest/table-info/enums"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        println("res = " + mvcResult.response.contentAsString)
    }

    @Test
    fun getUniqueOrganizationNameData() {
        val mvcResult: MvcResult = mvc.perform (get("/rest/table-info/organizations"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        println("res = " + mvcResult.response.contentAsString)
    }
}
