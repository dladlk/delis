package dk.erst.delis.rest.controller

import dk.erst.delis.rest.data.response.PageContainer
import dk.erst.delis.data.entities.document.Document

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

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import kotlin.test.assertEquals

/**
 * @author funtusthan, created by 18.03.19
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class DocumentRestControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun selectDocuments() {
        var mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/document?page=1&size=10&sort=orderBy_Id_Desc"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val doc: PageContainer<Document> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<Document>>() {}.type)

        if (doc.items.isNotEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/document/$id"))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            assertEquals(200, mvcResult.response.status)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/document/$id"))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError).andReturn()

            assertEquals(404, mvcResult.response.status)
        }
    }

    @Test
    fun selectDocumentsError() {

        val mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders
                .get("/rest/document")
                .param("page", "1")
                .param("size", "10")
                .param("sort", "orderBy_Id_Desc")
                .param("flagParamErrorsDocument", "FLAG_ERRORS_DOCUMENT")
                .param("createTime", "1552981734073:1552985334073")
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }
}
