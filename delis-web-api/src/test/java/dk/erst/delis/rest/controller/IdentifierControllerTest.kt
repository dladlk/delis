package dk.erst.delis.rest.controller

import dk.erst.delis.rest.data.response.PageContainer
import dk.erst.delis.data.entities.identifier.Identifier

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
 * @author funtusthan, created by 19.03.19
 */

@RunWith(SpringRunner::class)
@SpringBootTest
@AutoConfigureMockMvc
class IdentifierControllerTest {

    @Autowired
    private lateinit var mvc: MockMvc

    @Test
    fun selectIdentifiers() {
        var mvcResult: MvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/identifier?page=1&size=10&sort=orderBy_Id_Desc"))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        println("in selectIdentifiers: res = " + mvcResult.response.contentAsString)

        val doc: PageContainer<Identifier> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<Identifier>>() {}.type)

        if (!doc.items.isEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/identifier/$id"))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            println("in selectIdentifiers: res one identifier = " + mvcResult.response.contentAsString)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(MockMvcRequestBuilders.get("/rest/identifier/$id"))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError).andReturn()

            assertEquals(404, mvcResult.response.status)
        }
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object: TypeToken<T>() {}.type)
}
