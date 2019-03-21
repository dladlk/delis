package dk.erst.delis.util

import dk.erst.delis.persistence.specification.EntitySpecification

import org.junit.Test
import org.junit.runner.RunWith

import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.web.context.request.ServletWebRequest

import kotlin.test.assertEquals

/**
 * @author funtusthan, created by 21.03.19
 */

@RunWith(SpringRunner::class)
class WebRequestUtilTest {

    @Test
    fun generatePageAndSizeModelTest() {
        val httpRequest = MockHttpServletRequest()
        httpRequest.setParameter("page", "1")
        httpRequest.setParameter("size", "10")
        httpRequest.setParameter("flagParam", "FLAG_ERRORS_DOCUMENT")
        val request = ServletWebRequest(httpRequest)
        val pageAndSizeModel = WebRequestUtil.generatePageAndSizeModel(request)
        assertEquals(1, pageAndSizeModel.page)
        assertEquals(10, pageAndSizeModel.size)
        assertEquals(EntitySpecification.FLAG_ERRORS_DOCUMENT.name, httpRequest.getParameter("flagParam"))
    }
}
