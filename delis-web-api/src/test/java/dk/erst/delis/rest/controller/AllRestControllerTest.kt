package dk.erst.delis.rest.controller

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import dk.erst.delis.data.entities.document.Document
import dk.erst.delis.data.entities.identifier.Identifier
import dk.erst.delis.data.entities.journal.JournalDocument
import dk.erst.delis.data.entities.journal.JournalIdentifier
import dk.erst.delis.data.entities.journal.JournalOrganisation
import dk.erst.delis.rest.data.response.PageContainer

import org.junit.Test

import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

import kotlin.test.assertEquals

/**
 * @author funtusthan, created by 25.03.19
 */

class AllRestControllerTest : InitTokenTest() {

    @Test
    fun generateDashboardDataTest() {
        val mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .get("/rest/dashboard")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }

    @Test
    fun generateDashboardChartData() {
        val mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .get("/rest/chart")
                        .param("timeZone", "Europe/Copenhagen")
                        .param("startDate", "1554152400000")
                        .param("endDate", "1554204260630")
                        .param("defaultChart", "true")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }

    @Test
    fun selectDocuments() {
        var mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .get("/rest/document?page=1&size=10&sort=orderBy_Id_Desc")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val doc: PageContainer<Document> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<Document>>() {}.type)

        if (doc.items.isNotEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/document/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            assertEquals(200, mvcResult.response.status)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/document/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
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
                .header("Authorization", "Bearer " + auth.accessToken)
        )
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)
    }

    @Test
    fun selectIdentifiers() {
        var mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .get("/rest/identifier?page=1&size=10&sort=orderBy_Id_Desc")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val doc: PageContainer<Identifier> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<Identifier>>() {}.type)

        if (doc.items.isNotEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/identifier/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            assertEquals(200, mvcResult.response.status)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/identifier/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError).andReturn()

            assertEquals(404, mvcResult.response.status)
        }
    }

    @Test
    fun selectJournalDocuments() {
        var mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .get("/rest/journal/document?page=1&size=10&sort=orderBy_Id_Desc")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val doc: PageContainer<JournalDocument> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<JournalDocument>>() {}.type)

        if (doc.items.isNotEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/journal/document/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            assertEquals(200, mvcResult.response.status)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/journal/document/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError).andReturn()

            assertEquals(404, mvcResult.response.status)
        }
    }

    @Test
    fun selectJournalIdentifiers() {
        var mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .get("/rest/journal/identifier?page=1&size=10&sort=orderBy_Id_Desc")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val doc: PageContainer<JournalIdentifier> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<JournalIdentifier>>() {}.type)

        if (doc.items.isNotEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/journal/identifier/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            assertEquals(200, mvcResult.response.status)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/journal/identifier/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError).andReturn()

            assertEquals(404, mvcResult.response.status)
        }
    }

    @Test
    fun selectJournalOrganisations() {
        var mvcResult: MvcResult = mvc.perform(
                MockMvcRequestBuilders
                        .get("/rest/journal/organisation?page=1&size=10&sort=orderBy_Id_Desc")
                        .header("Authorization", "Bearer " + auth.accessToken))
                .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
        assertEquals(200, mvcResult.response.status)

        val doc: PageContainer<JournalOrganisation> = Gson().fromJson(mvcResult.response.contentAsString,
                object : TypeToken<PageContainer<JournalOrganisation>>() {}.type)

        if (doc.items.isNotEmpty()) {
            var id = doc.items.first().id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/journal/organisation/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is2xxSuccessful).andReturn()
            assertEquals(200, mvcResult.response.status)

            val sort = doc.items.sortedWith(compareBy({it.id}))

            id = sort.last().id
            ++id
            mvcResult = mvc.perform(
                    MockMvcRequestBuilders
                            .get("/rest/journal/organisation/$id")
                            .header("Authorization", "Bearer " + auth.accessToken))
                    .andExpect(MockMvcResultMatchers.status().is4xxClientError).andReturn()

            assertEquals(404, mvcResult.response.status)
        }
    }
}
