package dk.erst.delis.rest.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import dk.erst.delis.DelisWebApiApplication
import dk.erst.delis.rest.controller.info.TableInfoController

import dk.erst.delis.rest.data.response.ListContainer
import dk.erst.delis.rest.data.response.info.TableInfoData
import dk.erst.delis.service.info.TableInfoService

import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.test.context.TestPropertySource
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.setup.MockMvcBuilders

import kotlin.test.assertEquals

/**
 * @author funtusthan, created by 18.03.19
 */

@RunWith(SpringRunner::class)
@SpringBootTest(classes = [DelisWebApiApplication::class])
@ComponentScan(basePackages = ["dk.erst.delis"])
@TestPropertySource(properties = ["spring.jpa.hibernate.ddl-auto=update"])
@AutoConfigureMockMvc
class TableInfoControllerTest {

    private lateinit var mvc: MockMvc
    private lateinit var tableInfoController: TableInfoController

    @Autowired
    private lateinit var tableInfoService: TableInfoService

    @Before
    fun init() {
        this.tableInfoController = TableInfoController(tableInfoService)

        this.mvc = MockMvcBuilders
                .standaloneSetup(this.tableInfoController)
                .build()
    }

    @Test
    fun getTableInfoByAllEntitiesTest() {
        val mvcResult: MvcResult = mvc.perform(get("/rest/open/table-info/enums"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val info: ListContainer<TableInfoData> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<ListContainer<TableInfoData>>() {}.type)
        assertEquals(true, info.items.isNotEmpty())
    }

    @Test
    fun getUniqueOrganizationNameData() {
        val mvcResult: MvcResult = mvc.perform (get("/rest/open/table-info/organizations"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }
}
