package dk.erst.delis.rest.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import dk.erst.delis.data.entities.journal.JournalDocument
import dk.erst.delis.rest.data.response.PageContainer

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
 * @author funtusthan, created by 19.03.19
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class JournalDocumentControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun selectJournalDocuments() {
        var mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/journal/document?page=1&size=10&sort=orderBy_Id_Desc"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val doc: PageContainer<JournalDocument> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<JournalDocument>>() {}.type)

        if (doc.items.isNotEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/journal/document/$id"))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            assertEquals(200, mvcResult.response.status)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/journal/document/$id"))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError).andReturn()

            assertEquals(404, mvcResult.response.status)
        }
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)
}
